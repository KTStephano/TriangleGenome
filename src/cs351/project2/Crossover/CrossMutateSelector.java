package cs351.project2.crossover;

import cs351.core.Cross;
import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.Globals;
import cs351.core.Genome;
import cs351.core.Tribe;
import cs351.project2.Engine;
import cs351.project2.OrderedGenomeList;
import cs351.utility.Job;

import java.util.ArrayList;
import java.util.Random;

public class CrossMutateSelector implements Job
{
  private final EvolutionEngine ENGINE;
  private final Tribe TRIBE;
  private final Cross CROSS;
  private final int MAX_GENOMES = 10_000;
  private int sampleSize = 100;
  private float selectionCutoff = 0.25f;
  private float globalSubmitChance = 0.1f;
  private float crossWithGlobalChance = 0.1f;
  private final Random RAND = new Random();

  public CrossMutateSelector(Engine engine, Tribe tribe, Cross cross)
  {
    ENGINE = engine;
    TRIBE = tribe;
    CROSS = cross;
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
    int selectCount = (int) (sampleSize / 2 * selectionCutoff);
    int randCount = (int) Math.ceil(1 / selectionCutoff);
    boolean shouldCrossWithGlobal = (RAND.nextFloat() < crossWithGlobalChance);
    //CROSS.setShouldMutate(false); // for pure crossover
    for (int i = 0; i < selectCount; i++)
    {
      //int choice = Math.abs(RAND.nextInt(size) - RAND.nextInt(size));
      for (int j = 0; j < randCount; j++)
      {
        try
        {
          int chosenTribeSize;
          if (shouldCrossWithGlobal)
          {
            Globals.LOCK.lock();
            if (Globals.CONCURRENT_GENOME_LIST.size() == 0) addTopGenomesToGlobal();
            chosenTribeSize = Globals.CONCURRENT_GENOME_LIST.size();
          }
          else chosenTribeSize = size;
          int randTriangle = Math.abs(RAND.nextInt(chosenTribeSize) - RAND.nextInt(chosenTribeSize));
          //while (randTriangle == choice) randTriangle = Math.abs(RAND.nextInt(size) - RAND.nextInt(size));

          Genome randGenome = shouldCrossWithGlobal ? Globals.CONCURRENT_GENOME_LIST.get(randTriangle) :
                                                      tribe.get(randTriangle);
          OFFSPRING.add(CROSS.cross(ENGINE, tribe.get(i), randGenome));
          ++numCreated;
        }
        finally
        {
          if (shouldCrossWithGlobal) Globals.LOCK.unlock();
        }
      }
    }
    //CROSS.setShouldMutate(true);

    if (TRIBE.size() > MAX_GENOMES)
    {
      for (int i = 0; i < numCreated; i++)
      {
        if (tribe.size() - 1 < 0) break;
        tribe.removeAt(tribe.size() - 1);
      }
    }

    checkForGenomeSubmission();

    for (Genome genome : OFFSPRING) tribe.add(genome);
    TRIBE.sort();
    //System.out.println(TRIBE.size());
  }

  protected void checkForGenomeSubmission()
  {
    if (RAND.nextFloat() < globalSubmitChance)
    {
      addTopGenomesToGlobal();
    }
  }

  protected void addTopGenomesToGlobal()
  {
    OrderedGenomeList tribe = (OrderedGenomeList)TRIBE;
    int selectCount = (int) (sampleSize / 2 * selectionCutoff);
    try
    {
      Globals.LOCK.lock();
      // If the concurrent genome list has gone over 10_000, then remove some genomes before adding more
      if (Globals.CONCURRENT_GENOME_LIST.size() > MAX_GENOMES)
      {
        for (int i = 0; i < selectCount; i++) Globals.CONCURRENT_GENOME_LIST.removeAt(Globals.CONCURRENT_GENOME_LIST.size() - 1);
      }
      for (int i = 0; i < selectCount; i++) Globals.CONCURRENT_GENOME_LIST.add(copyGenome(tribe.get(i)));
      //System.out.println("Globals size: " + Globals.CONCURRENT_GENOME_LIST.size());
      Globals.CONCURRENT_GENOME_LIST.sort();
    }
    finally
    {
      Globals.LOCK.unlock();
    }
  }

  protected Genome copyGenome(Genome genome)
  {
    Genome copy = new Genome();
    copy.setFitness(genome.getFitness());
    for (float[] triangle : genome.getTriangles()) copy.add(triangle);
    return copy;
  }
}
