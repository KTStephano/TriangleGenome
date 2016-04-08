package cs351.project2;

import cs351.core.Cross;
import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import cs351.core.Genome;
import cs351.core.TriangleManager;

import java.util.Iterator;
import java.util.Random;

/**
 * CITATION: http://chriscummins.cc/s/genetics/#
 */
public class CrossMutate implements Cross
{
  private static final Random RAND = new Random();
  private int dnaLength = 10;
  private float mutationChance = 0.006f;
  private float mutateAmount = 0.1f;
  private boolean shouldMutate = true;

  public void setShouldMutate(boolean value)
  {
    shouldMutate = value;
  }

  /**
   * This should cross the genes from this genome and another genome, creating a brand
   * new genome. The new genome should have all its own genes (copied from the parents)
   * with all of its own triangles. This will prevent issues with genomes sharing
   * references to genes/triangles in other genomes.
   *
   * @param engine game engine
   * @param first  first of the two parent genomes
   * @param second second of the two parent genomes
   * @return a brand new genome inheriting traits from the two parents
   */
  @Override
  public Genome cross(EvolutionEngine engine, Genome first, Genome second)
  {
    Genome best = null;

    for (int tries = 0; tries < 1; tries++)
    {
      Genome offspring = new Genome();
      Iterator<float[]> itrFirst = first.getTriangles().iterator();
      Iterator<float[]> itrSecond = second.getTriangles().iterator();
      GUI gui = engine.getGUI();
      TriangleManager manager = new TriangleManager();
      //mutateAmount = 1.0f - (float)((first.getFitness() > second.getFitness()) ? first.getFitness() : second.getFitness());

      //if (mutateAmount <= 0.1) mutateAmount = 1.0f - mutateAmount;
      //mutateAmount = RAND.nextFloat() < 0.5f ? RAND.nextFloat() : 0.1f;
      //mutateAmount = mutateAmount < 0.1f ? RAND.nextFloat() : mutateAmount;

      while (itrFirst.hasNext())
      {
        manager.setTriangleData(gui, itrFirst.next());
        float[] firstGenes = manager.getNormalizedDNA();
        manager.setTriangleData(gui, itrSecond.next());
        float[] secondGenes = manager.getNormalizedDNA();
        float[] newGenes = new float[10];

        for (int i = 0; i < dnaLength; i++)
        {
          newGenes[i] = (RAND.nextFloat() < 0.5f) ? firstGenes[i] : secondGenes[i];

          if (RAND.nextFloat() < mutationChance && shouldMutate)
          {
            float mutation = RAND.nextFloat() * mutateAmount * 2;// - mutateAmount;
            if (RAND.nextFloat() < 0.5f) mutation *= -1;
            newGenes[i] += mutation;

            if (newGenes[i] < 0) newGenes[i] = 0.0f;
            else if (newGenes[i] > 1) newGenes[i] = 1.0f;
          }
        }

        offspring.add(manager.revertNormalization(newGenes));
      }

      offspring.setFitness(engine.getPopulation().getFitnessFunction().generateFitness(engine, offspring));
      ((Engine) engine).incrementGenerationCount();
      if (best == null) best = offspring;
      else best = engine.getPopulation().getFitnessFunction().compare(best, offspring);
    }
    return best;
  }
}
