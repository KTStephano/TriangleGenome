package cs351.project2;

import cs351.core.Cross;
import cs351.core.Genome;
import cs351.core.Tribe;
import cs351.utility.Job;

import java.util.*;

/**
 * TODO this class is pretty bad
 */
public class CrossPhase implements Job
{
  private final Engine ENGINE;
  private final Tribe TRIBE;
  private final Cross CROSS;
  private final Random RAND = new Random();
  private final LinkedList<Genome> SELECTED_GENOMES = new LinkedList<>();
  private final TreeSet<Integer> RANDOM_SELECTIONS = new TreeSet<>();

  public CrossPhase(Engine engine, Tribe tribe)
  {
    ENGINE = engine;
    TRIBE = tribe;
    CROSS = new GameCross();
  }

  /**
   * This is called exactly once for every time the job is submitted to
   * the job system. The threadID represents the number of the thread
   * that the job is executing on.
   *
   * @param threadID thread number for reference
   */
  @Override
  public void start(int threadID)
  {
    SELECTED_GENOMES.clear();
    SELECTED_GENOMES.add(TRIBE.getBest());
    ArrayList<Genome> list = new ArrayList<>(TRIBE.getGenomes());
    // select 9 other random genomes
    for (int i = 0; i < 9; i++)
    {
      SELECTED_GENOMES.add(list.get(RAND.nextInt(TRIBE.size())));
    }
    Iterator<Genome> itr = SELECTED_GENOMES.iterator();
    while (itr.hasNext())
    {
      // this should be safe as long as itr has an even number of elements
      TRIBE.add(CROSS.cross(ENGINE, itr.next(), itr.next()));
      ENGINE.incrementGenerationCount();
    }
  }
}
