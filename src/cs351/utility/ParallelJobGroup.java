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
 *
 * @author Justin
 */
public final class ParallelJobGroup
{
  private final ReentrantLock LOCK;
  private final int PRIORITY;
  private final AtomicInteger BACK_COUNTER; // Kept hidden from outside classes
  private final AtomicInteger FRONT_COUNTER; // This is returned to other classes
  private ArrayList<Job> availableJobs;

  /**
   * Maps a Job object to an internal AtomicInteger representing the number
   * of jobs left to execute from a (potentially) larger list of jobs.
   *
   * @author Justin
   */
  public final class JobWrapper
  {
    private final Job JOB;
    private final AtomicInteger COUNTER;

    /**
     * Creates a new JobWrapper with the given Job and AtomicCounter.
     * @param job job
     * @param counter AtomicCounter (associated with a (potentially) larger list that the job is part of)
     */
    public JobWrapper(Job job, AtomicInteger counter)
    {
      JOB = job;
      COUNTER = counter;
    }

    /**
     * Gets the job associated with this wrapper.
     * @return job
     */
    public Job getJob()
    {
      return JOB;
    }

    /**
     * Decrements the overall job counter by 1.
     */
    public void markCompleted()
    {
      COUNTER.getAndDecrement();
    }
  }

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
    BACK_COUNTER = new AtomicInteger(0);
    FRONT_COUNTER = new AtomicInteger(0);
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
   * If you are trying to get the next job from this group, it is best
   * to just call this function and then check to see if the result is null. Calling
   * hasJobs() will return a value that is consistent at that instant, but from
   * the time it returns and the time you call getNextJob() some other thread
   * could have come in and taken the last job.
   *
   * @return a valid job if there is one and null if not
   */
  public JobWrapper getNextJob()
  {
    if (!hasJobs()) return null;
    try
    {
      LOCK.lock();
      // The - 1 since the counter represents the length of the list, not
      // a valid index into the list
      int current = BACK_COUNTER.getAndDecrement() - 1;
      return current >= 0 ? new JobWrapper(availableJobs.get(current), FRONT_COUNTER) : null;
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
    try
    {
      LOCK.lock();
      BACK_COUNTER.set(BACK_COUNTER.get() + jobs.size());
      FRONT_COUNTER.set(BACK_COUNTER.get());
      availableJobs.addAll(jobs);
      return FRONT_COUNTER;
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
    return BACK_COUNTER.get() > 0;
  }
}
