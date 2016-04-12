package cs351.utility;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the WorkerThread class used by the job system. Its threads
 * are managed internally and there is no handle to get a reference to them
 * from the job system itself.
 *
 * @author Justin
 */
public final class WorkerThread extends Thread
{
  //private final ReentrantLock LOCK;
  private final int THREAD_ID;
  private final ParallelJobSystem JOB_SYSTEM;
  private final AtomicBoolean IS_RUNNING;
  private final ConcurrentLinkedQueue<ParallelJobGroup> JOB_QUEUE;

  /**
   * Creates a new WorkerThread object with the given threadID and a parallel job system
   * reference for callbacks.
   * @param threadID id to associate with this worker
   * @param jobSystem parallel job system reference for callbacks
   */
  public WorkerThread(int threadID, ParallelJobSystem jobSystem)
  {
    super("ParallelWorkerThread-" + threadID);
    THREAD_ID = threadID;
    JOB_SYSTEM = jobSystem;
    IS_RUNNING = new AtomicBoolean(true);
    JOB_QUEUE = new ConcurrentLinkedQueue<>();
  }

  @Override
  public void run()
  {
    while(IS_RUNNING.get())
    {
      if (JOB_QUEUE.size() == 0)
      {
        // Let the job system know that we're idle and then sleep a little
        try
        {
          JOB_SYSTEM.signalWork(THREAD_ID);
          Thread.sleep(1);
        }
        catch (InterruptedException e)
        {
          // Do nothing
        }
      }
      else
      {
        final ParallelJobGroup GROUP = JOB_QUEUE.poll();
        ParallelJobGroup.JobWrapper currentJob;
        // Multiple threads might be working on this same group, so just keep getting
        // the next job and checking for null - ParallelJobGroup guarantees that no
        // two calls to getNextJob will return the same value
        while ((currentJob = GROUP.getNextJob()) != null)
        {
          currentJob.getJob().start(THREAD_ID);
          currentJob.markCompleted();
        }
      }
    }
    JOB_SYSTEM.notifyOfThreadTermination(THREAD_ID);
  }

  /**
   * Lets the thread know it needs to terminate. It will not immediately kill itself
   * as it may be in the middle of working on some jobs, but at the next check it will
   * see that it needs to quit.
   */
  public void terminate()
  {
    IS_RUNNING.getAndSet(false);
  }

  /**
   * Adds a job group to its internal list of jobs that it needs to work on in parallel
   * with other worker threads.
   * @param group job group to add to its internal list
   */
  public void addJobGroup(ParallelJobGroup group)
  {
    JOB_QUEUE.add(group);
  }
}