package cs351.core.Engine;

import cs351.project2.OrderedGenomeList;
import cs351.utility.ParallelJobSystem;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Contains a minimal amount of items that needs to persist throughout a single
 * execution.
 *
 * @author Justin, George
 */
public class Globals
{
  // Make sure to lock onto the reentrant lock before accessing CONCURRENT_GENOME_LIST
  public static final ReentrantLock LOCK = new ReentrantLock();
  public static final OrderedGenomeList CONCURRENT_GENOME_LIST = new OrderedGenomeList(2000);
}