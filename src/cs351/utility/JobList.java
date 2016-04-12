package cs351.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Note :: This class is not safe to be used by multiple threads at once. It is
 *         better to create segregated lists for each thread and let the job
 *         system deal with managing jobs concurrently.
 *
 * This class can be used to manage local copies of a job list
 * before submitting them to the job system. The benefit to this is
 * that you can build up a list of jobs to be executed but have strict
 * control over when the job system gets to see them.
 *
 * Each list is capable of managing various layers of different-priority job lists.
 *
 * Keep in mind that once you tell the list to submit the jobs it is managing,
 * it will submit all jobs to the job system for immediate execution and clear
 * its local list. This includes all job lists for all priorities.
 *
 * @author Justin
 */
public class JobList
{
  private final ParallelJobSystem JOB_SYSTEM;
  private final HashMap<Integer, LinkedList<Job>> JOBS;
  private final ArrayList<AtomicInteger> ACTIVE_COUNTERS;
  private int size = 0;

  public JobList(ParallelJobSystem jobSystem)
  {
    JOB_SYSTEM = jobSystem;
    JOBS = new HashMap<>();
    ACTIVE_COUNTERS = new ArrayList<>(10);
  }

  /**
   * Adds a job of the given priority to the list. A job list will separate its jobs
   * by priority, but when submit is called, all jobs of all priorities are submitted
   * and the job system is notified of which job lists are of what priority.
   *
   * @param job job to submit
   * @param priority priority of the job (determines the list it goes in)
   */
  public void add(Job job, int priority)
  {
    if (!JOBS.containsKey(priority)) JOBS.put(priority, new LinkedList<>());
    JOBS.get(priority).add(job);
    size++;
  }

  /**
   * Removes a job from the list. This should only be called before submitting since
   * once submit is called, all job lists are wiped.
   *
   * @param job job to remove
   * @param priority its priority (used to find it)
   */
  public void remove(Job job, int priority)
  {
    if (!JOBS.containsKey(priority)) return;
    JOBS.get(priority).remove(job);
    size--;
  }

  /**
   * Gets the size of all job lists that it is maintaining.
   *
   * @return size of all job lists combined
   */
  public int size()
  {
    return size;
  }

  public void clear()
  {
    JOBS.clear();
  }


  /**
   * Submits all jobs to the given job system. All lists are wiped afterwards
   * so future calls to submitJobs will have no effect unless jobs are
   * re-added.
   *
   * @param clearExistingData true if the job list should clear out the submitted jobs
   *                          and false if it should keep them around for re-submission
   */
  public void submitJobs(boolean clearExistingData)
  {
    for (Map.Entry<Integer, LinkedList<Job>> entry : JOBS.entrySet())
    {
      // 'true' tells the job system to clear the list it is being given after it
      // is finished using it
      ACTIVE_COUNTERS.add(JOB_SYSTEM.submit(entry.getValue(), entry.getKey(), clearExistingData));
    }
    // Reset the size
    if (clearExistingData) size = 0;
    //System.out.println("Here I am");
  }

  /**
   * Checks to see if there are still jobs related to this job list that are pending
   * completion with the multithreading system.
   *
   * @return true if jobs exist and false if all jobs have completed
   */
  public boolean containsActiveJobs()
  {
    for (AtomicInteger counter : ACTIVE_COUNTERS)
    {
      if (counter.get() > 0) return true; // at least 1 job is left
    }
    // Clear the counters since by now they're all 0
    ACTIVE_COUNTERS.clear();
    return false; // all jobs completed
  }

  /**
   * Returns only when the job system has completed all jobs that were associated with
   * this list before being submitted.
   */
  public void waitForCompletion()
  {
    try
    {
      boolean isComplete = false;
      while (!isComplete)
      {
        isComplete = true;
        if (containsActiveJobs())
        {
          isComplete = false;
          Thread.sleep(1);
        }
      }
      // handled by containsActiveJobs() function - commented out
      // Clear the counters since by now they're all 0
      //ACTIVE_COUNTERS.clear();
    }
    catch (InterruptedException e)
    {
      // Do nothing
    }
  }
}
