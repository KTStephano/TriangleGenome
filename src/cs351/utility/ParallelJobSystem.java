package cs351.utility;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A ParallelJobSystem provides an easy way to manage up to 256 threads. The best way to
 * use it is to have only one job system for an entire program but many different separate
 * job lists (see the JobList class).
 *
 * Each separate job list can interact with the same job system so that its jobs can be
 * submitted to the job system's worker threads.
 */
public final class ParallelJobSystem
{
  private final ReentrantLock LOCK;
  private final int NUM_WORKER_THREADS;
  private final WorkerThread[] WORKER_THREADS;
  private final AtomicInteger RUNNING_THREADS;
  // The back buffer represents jobs that are only visible to the job system
  private final ConcurrentHashMap<Integer, ConcurrentLinkedDeque<ParallelJobGroup>> JOB_BACK_BUFFER;
  // The front buffer represents jobs that are available to every active worker thread
  // maintained by the given job system
  private final PriorityBlockingQueue<ParallelJobGroup> JOB_FRONT_BUFFER;
  private final AtomicBoolean IS_STARTED;
  private final AtomicBoolean WAS_DESTROYED;

  /**
   * Creates a new job system with the specified number of worker threads. This number is used
   * when the init() function is called in order to create and start that number of workers.
   *
   * Note that the current max number of worker threads is 256.
   *
   * @param numWorkerThreads number of worker threads on the range of [1, 256]
   */
  public ParallelJobSystem(int numWorkerThreads)
  {
    LOCK = new ReentrantLock();
    // Establish the min/max values and then make sure numWorkerThreads
    // does not fall outside of them
    final int MIN_THREADS = 1;
    final int MAX_THREADS = 256;
    if (numWorkerThreads < MIN_THREADS) numWorkerThreads = MIN_THREADS;
    else if (numWorkerThreads > MAX_THREADS) numWorkerThreads = MAX_THREADS;
    NUM_WORKER_THREADS = numWorkerThreads;
    WORKER_THREADS = new WorkerThread[NUM_WORKER_THREADS];
    RUNNING_THREADS = new AtomicInteger(0);
    // Set up the two buffers
    JOB_BACK_BUFFER = new ConcurrentHashMap<>(NUM_WORKER_THREADS);
    // The blocking queue uses a comparator to decide what goes ahead of what
    // which will be higher priority job lists in this case going ahead of lower
    // priority job lists
    JOB_FRONT_BUFFER = new PriorityBlockingQueue<>(10, (g1, g2) -> g2.getPriority() - g1.getPriority());
    IS_STARTED = new AtomicBoolean(false);
    WAS_DESTROYED = new AtomicBoolean(false);
  }

  /**
   * Initializes the job system for use. Once this is called, all worker threads are created and started.
   * This function is meant to be called exactly once per job system object.
   *
   * @throws IllegalStateException thrown if the job system is used after destruction or initialized twice
   */
  public void init() throws IllegalStateException
  {
    if (WAS_DESTROYED.get() || IS_STARTED.get()) throw new IllegalStateException("Job system was used after destruction or initialized twice");
    // Print some info
    System.out.println("--- ParallelJobSystem Initializing ---");
    System.out.println("Logical Cores Available: " + Runtime.getRuntime().availableProcessors());
    System.out.println("Requested Worker Threads: " + NUM_WORKER_THREADS);
    // Create and start each worker thread
    RUNNING_THREADS.set(NUM_WORKER_THREADS);
    for (int i = 0; i < NUM_WORKER_THREADS; i++)
    {
      WORKER_THREADS[i] = new WorkerThread(i, this);
      WORKER_THREADS[i].start();
    }
    IS_STARTED.getAndSet(true);
  }

  /**
   * Destroys the job system. After this the object should not be used again.
   */
  public void destroy()
  {
    if (WAS_DESTROYED.get() || !IS_STARTED.get()) throw new IllegalStateException("Job system was destroyed twice or never initialized");
    for (WorkerThread thread : WORKER_THREADS) thread.terminate();
    JOB_BACK_BUFFER.clear();
    JOB_FRONT_BUFFER.clear();
  }

  /**
   * Submits a job list to the job system. The job system makes no guarantee that the worker
   * threads will see the list immediately (if ever in the event that all worker threads are stuck
   * with infinite-loop jobs for some reason).
   *
   * Instead, when worker threads are signalling that more work is needed, the job system will
   * at that point try to push previously-submitted jobs to the front buffer.
   *
   * @param jobs list of jobs to execute
   * @param priority their priority (lower numbers being higher priority)
   * @return an atomic integer representing a counter - when the counter is 0, the job list
   *         has been completed by the worker threads
   * @throws IllegalStateException thrown if used after destruction or before init
   */
  public AtomicInteger submit(Collection<Job> jobs, int priority, boolean clearGivenJobList) throws IllegalStateException
  {
    if (WAS_DESTROYED.get() || !IS_STARTED.get()) throw new IllegalStateException("Job system used before init/after destruction");
    ParallelJobGroup group = new ParallelJobGroup(priority);
    AtomicInteger counter = group.addJobs(jobs);
    try
    {
      LOCK.lock();
      if (!JOB_BACK_BUFFER.containsKey(priority)) JOB_BACK_BUFFER.put(priority, new ConcurrentLinkedDeque<>());
      JOB_BACK_BUFFER.get(priority).add(group);
    }
    finally
    {
      LOCK.unlock();
    }
    if (clearGivenJobList) jobs.clear();
    return counter;
  }

  /**
   * Allows the worker threads to let the job system know that previous job lists were
   * completed. At this point the job system will try to push previously-submitted jobs
   * to the front buffer (if no jobs are left in the queue) and give more work to the
   * worker threads.
   *
   * Note that if one worker thread asks for more work and the job system makes the effort
   * to accommodate the request, *all* worker threads will receive more work in the hopes
   * that the job system won't be asked for more work immediately after a request.
   *
   * @param threadID id of the thread that needs more work
   * @throws RuntimeException thrown if an unknown worker signals for more work which should
   *                          never happen
   */
  public void signalWork(int threadID) throws RuntimeException
  {
    if (threadID < 0 || threadID >= NUM_WORKER_THREADS) throw new RuntimeException("Invalid thread id");
    if (JOB_FRONT_BUFFER.size() == 0) dispatchJobs(); // see if the back buffer has anything new
    // TODO I don't think this needs a lock since every thread signals constantly, but double check at some point
    ParallelJobGroup group = JOB_FRONT_BUFFER.poll();
    if (group == null) return;
    for (WorkerThread thread : WORKER_THREADS) thread.addJobGroup(group);
  }

  /**
   * This is used by worker threads to let the job system know that they have stopped
   * execution. Once all threads have finished, the job system sets a flag letting
   * itself know that it is done and then prints a message to the console.
   *
   * @param threadID id of the thread - used to make sure the thread is legit
   * @throws RuntimeException thrown if the thread id is invalid for this job system
   */
  public void notifyOfThreadTermination(int threadID) throws RuntimeException
  {
    if (threadID < 0 || threadID >= NUM_WORKER_THREADS) throw new RuntimeException("Invalid thread id");
    RUNNING_THREADS.getAndDecrement();
    // All threads have finished
    if (RUNNING_THREADS.get() == 0)
    {
      WAS_DESTROYED.getAndSet(true);
      System.out.println("--- ParallelJobSystem Shutdown Successfully ---");
    }
  }

  /**
   * Gets the number of worker threads.
   *
   * @return number of worker threads
   */
  public int getNumActiveThreads()
  {
    return NUM_WORKER_THREADS;
  }

  private void dispatchJobs()
  {
    try
    {
      LOCK.lock();
      // Once the job groups are submitted, they need to be cleared out
      LinkedList<ConcurrentLinkedDeque<ParallelJobGroup>> listsToClear = new LinkedList<>();
      for (Map.Entry<Integer, ConcurrentLinkedDeque<ParallelJobGroup>> entry : JOB_BACK_BUFFER.entrySet())
      {
        //if (entry.getValue().size() == 0) continue;
        listsToClear.add(entry.getValue());
        for (ParallelJobGroup group : entry.getValue()) JOB_FRONT_BUFFER.add(group);
      }
      // Clear all lists
      for (ConcurrentLinkedDeque<ParallelJobGroup> list : listsToClear) list.clear();
    }
    finally
    {
      LOCK.unlock();
    }
  }
}