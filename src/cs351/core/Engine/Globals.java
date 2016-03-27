package cs351.core.Engine;

import cs351.utility.ParallelJobSystem;

/**
 * Hopefully we can find a better way to do this later
 */
public class Globals
{
  public static final int NUM_THREADS = 10;
  public static final ParallelJobSystem JOB_SYSTEM;

  // These are the data tags the engine will use for its statistics system -
  // we can add to this as we add more stats
  public static final String AVERAGE_TIME_PER_GENERATION = "Average Seconds Per Generation";
  private static final String TOTAL_GENERATIONS = "Number of Generations";

  static
  {
    JOB_SYSTEM = new ParallelJobSystem(NUM_THREADS);
    JOB_SYSTEM.init();
  }
}