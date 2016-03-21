package cs351.project2;

import cs351.core.Engine.Globals;
import cs351.utility.Job;
import cs351.utility.JobList;

import java.util.Random;

/**
 * Pretty terrible example but it kind of shows what you can do with the
 * current system. I still need to do a lot more testing to see if it
 * breaks anywhere, so let me know if you run into any problems when you
 * are trying it out and stuff.
 */
public class ParallelProgrammingDemo
{
  private final Random RAND = new Random();

  /**
   * Simple job. Picks a random number and adds to it 10 thousand times.
   */
  private class JobTypeOne implements Job
  {
    private String id;
    private int startNum;

    public JobTypeOne(String id)
    {
      this.id = id;
      startNum = RAND.nextInt();
    }

    @Override
    public void start(int threadID)
    {
      System.out.println(id + " started on thread " + threadID);
      for (int i = 0; i < 10_000; i++) startNum++;
    }
  }

  /**
   * Simple job. Picks a random number and subtracts 2 from it 10 thousand times.
   */
  private class JobTypeTwo implements Job
  {
    private String id;
    private int startNum;

    public JobTypeTwo(String id)
    {
      this.id = id;
      startNum = RAND.nextInt();
    }

    @Override
    public void start(int threadID)
    {
      System.out.println(id + " started on thread " + threadID);
      for (int i = 0; i < 10_000; i++) startNum -= 2;
    }
  }

  public void runDemo()
  {
    // Create 2 local lists to manage jobs before submitting them
    JobList list0 = new JobList(Globals.JOB_SYSTEM);
    JobList list1 = new JobList(Globals.JOB_SYSTEM);

    // Adds 20 jobs to each list
    for (int i = 0; i < 20; i++)
    {
      // Each job has a separate id, and 1 is used as the priority
      list0.add(new JobTypeOne("list0, job " + i), 1);
      list1.add(new JobTypeTwo("list1, job " + i), 1);
    }

    // Submit the first list and wait - waiting stalls whichever thread it runs
    // on so we should probably be careful
    list0.submitJobs(true);
    list0.waitForCompletion();
    // Submit the second list and wait
    System.out.println("Jobs from the first list completed");
    list1.submitJobs(true);
    list1.waitForCompletion();
    System.out.println("Jobs from the second list completed");
  }
}
