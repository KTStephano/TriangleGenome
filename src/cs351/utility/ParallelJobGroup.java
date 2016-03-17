package cs351.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Note :: This is only to be used by the job system - see the JobList class
 *         which should be used to manage individual lists before submitting
 *         them to the job system.
 *
 * A job group represents a collection of jobs associated with an atomic
 * integer that points to the current job in the list that needs to be executed.
 * This way a group of threads can work on the same job list.
 */
public final class ParallelJobGroup
{
  private final ReentrantLock LOCK;
  private final int PRIORITY;
  private AtomicInteger counter;
  private ArrayList<Job> availableJobs;

  /**
   * Creates a ParallelJobGroup of the specified priority. Only jobs of the same
   * priority should be added to the group afterwards.
   *
   * @param priority priority of the group (lower is higher priority)
   */
  public ParallelJobGroup(int priority)
  {
    LOCK = new ReentrantLock();
    PRIORITY = priority;
    counter = new AtomicInteger(0);
    availableJobs = new ArrayList<>(25);
  }

  /**
   * Gets the priority of the group.
   *
   * @return job group priority
   */
  public int getPriority()
  {
    return PRIORITY;
  }

  /**
   * This will check to see if there is another job in the list and return
   * it if there is. If no jobs are left, null is returned.
   *
   * @return a valid job if there is one and null if not
   */
  public Job getNextJob()
  {
    if (!hasJobs()) return null;
    // The - 1 since the counter represents the length of the list, not
    // a valid index into the list
    int current = counter.getAndDecrement() - 1;
    try
    {
      LOCK.lock();
      return current >= 0 ? availableJobs.get(current) : null;
    }
    finally
    {
      LOCK.unlock();
    }
  }

  /**
   * Adds a list of jobs to the group.
   *
   * @param jobs list of valid jobs
   * @return a counter that represents the current state of the list - 0  means all
   *         jobs completed
   */
  public AtomicInteger addJobs(Collection<Job> jobs)
  {
    counter.set(counter.get() + jobs.size());
    try
    {
      LOCK.lock();
      availableJobs.addAll(jobs);
      return counter;
    }
    finally
    {
      LOCK.unlock();
    }
  }

  /**
   * Returns true if there are jobs left in the list.
   *
   * @return true if jobs are left and false if not
   */
  public boolean hasJobs()
  {
    return counter.get() > 0;
  }
}
