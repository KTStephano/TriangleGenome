package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Genome;
import cs351.core.Tribe;
import cs351.utility.Job;

import java.util.ArrayList;
import java.util.Random;

public class CrossMutateSelection implements Job
{
  private final EvolutionEngine ENGINE;
  private final Tribe TRIBE;
  private final CrossMutate CROSS;
  private float selectionCutoff = 0.15f;
  private final Random RAND = new Random();

  public CrossMutateSelection(Engine engine, Tribe tribe)
  {
    ENGINE = engine;
    TRIBE = tribe;
    CROSS = new CrossMutate();
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
    int size = TRIBE.size();
    OrderedGenomeList tribe = (OrderedGenomeList)TRIBE;
    final ArrayList<Genome> OFFSPRING = new ArrayList<>(10);
    int numCreated = 0;
    int selectCount = (int) (size * selectionCutoff);
    int randCount = (int) Math.ceil(1 / selectionCutoff);
    //CROSS.setShouldMutate(false); // for pure crossover
    for (int i = 0; i < 1; i++)
    {
      for (int j = 0; j < randCount + selectCount; j++)
      {
        int randTriangle = i;
        while (randTriangle == i) randTriangle = RAND.nextInt(size);

        OFFSPRING.add(CROSS.cross(ENGINE, tribe.get(i), tribe.get(randTriangle)));
        ++numCreated;
      }
    }
    //CROSS.setShouldMutate(true);

    for (int i = 0; i < numCreated; i++)
    {
      if (tribe.size() - 1 < 0) break;
      tribe.removeAt(tribe.size() - 1);
    }

    for (Genome genome : OFFSPRING) tribe.add(genome);
    TRIBE.sort();
    //System.out.println(TRIBE.size());
  }
}
