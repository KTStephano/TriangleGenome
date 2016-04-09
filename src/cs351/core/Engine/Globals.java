package cs351.core.Engine;

import cs351.project2.OrderedGenomeList;
import cs351.utility.ParallelJobSystem;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Hopefully we can find a better way to do this later
 */
public class Globals
{
  public static final int NUM_THREADS = 10;
  //public static final ParallelJobSystem JOB_SYSTEM;

  // These are the data tags the engine will use for its statistics system -
  // we can add to this as we add more stats
  public static final String AVERAGE_TIME_PER_GENERATION = "Average Seconds Per Generation";
  public static final String TOTAL_GENERATIONS = "Number of Generations";

  // Make sure to lock onto the reentrant lock before accessing CONCURRENT_GENOME_LIST
  public static final ReentrantLock LOCK = new ReentrantLock();
  public static final OrderedGenomeList CONCURRENT_GENOME_LIST = new OrderedGenomeList(2000);

  /**
  static
  {
    JOB_SYSTEM = new ParallelJobSystem(NUM_THREADS);
    JOB_SYSTEM.init();
  }
   */
}