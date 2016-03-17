package cs351.project2;

import cs351.utility.ParallelJobSystem;

/**
 * Hopefully we can find a better way to do this later
 */
public class Globals
{
  public static final int NUM_THREADS = 10;
  public static final ParallelJobSystem JOB_SYSTEM;

  static
  {
    JOB_SYSTEM = new ParallelJobSystem(NUM_THREADS);
    JOB_SYSTEM.init();
  }
}