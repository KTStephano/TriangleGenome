package cs351.project2;

import cs351.core.Engine.Globals;

/**
 * Placeholder class
 */
public class Placeholder
{
  public static void main(String[] args)
  {
    // I've gotten one exception so far but I think I fixed it...
    new ParallelProgrammingDemo().runDemo();

    // Kill the job system
    Globals.JOB_SYSTEM.destroy();
  }
}
