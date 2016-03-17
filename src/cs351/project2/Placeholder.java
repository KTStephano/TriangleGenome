package cs351.project2;

import cs351.utility.Job;
import cs351.utility.ParallelJobSystem;

import java.util.Random;

/**
 * Placeholder class
 */
public class Placeholder
{
  private static final Random RAND = new Random();

  /**
   * Really terrible job (probably shouldn't ever be static but not sure)
   */
  private static class JobTypeOne implements Job
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
   * Really terrible job (probably shouldn't ever be static but not sure)
   */
  private static class JobTypeTwo implements Job
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

  public static void main(String[] args)
  {
    // I've gotten one exception so far but I think I fixed it...
    new ParallelProgrammingDemo().runDemo();

    // Kill the job system
    Globals.JOB_SYSTEM.destroy();
  }
}
