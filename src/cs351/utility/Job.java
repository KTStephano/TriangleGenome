package cs351.utility;

/**
 * A job can be added to any job list and submitted to a job system's worker
 * threads for execution. No job should run infinitely as it will prevent
 * the job system from being able to schedule any new jobs.
 */
public interface Job
{
  /**
   * This is called exactly once for every time the job is submitted to
   * the job system. The threadID represents the number of the thread
   * that the job is executing on.
   *
   * @param threadID thread number for reference
   */
  void start(int threadID);
}