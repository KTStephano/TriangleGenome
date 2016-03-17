package cs351.utility;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the WorkerThread class used by the job system.
 */
public final class WorkerThread extends Thread
{
  //private final ReentrantLock LOCK;
  private final int THREAD_ID;
  private final ParallelJobSystem JOB_SYSTEM;
  private final AtomicBoolean IS_RUNNING;
  private final ConcurrentLinkedQueue<ParallelJobGroup> JOB_QUEUE;

  public WorkerThread(int threadID, ParallelJobSystem jobSystem)
  {
    THREAD_ID = threadID;
    JOB_SYSTEM = jobSystem;
    IS_RUNNING = new AtomicBoolean(true);
    JOB_QUEUE = new ConcurrentLinkedQueue<>();
  }

  @Override
  public void run()
  {
    //System.out.println("Thread " + THREAD_ID + " started");
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
    //System.out.println("Thread " + THREAD_ID + " terminated successfully");
  }

  public void terminate()
  {
    IS_RUNNING.getAndSet(false);
  }

  public void addJobGroup(ParallelJobGroup group)
  {
    JOB_QUEUE.add(group);
  }
}