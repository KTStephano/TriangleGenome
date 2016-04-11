package cs351.project2.crossover;

import cs351.core.Cross;
import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import cs351.core.Genome;
import cs351.core.TriangleManager;
import cs351.project2.Engine;

import java.util.Iterator;
import java.util.Random;

/**
 * CITATION: http://chriscummins.cc/s/genetics/#
 */
public class UniformCrossMutate implements Cross
{
  protected static final Random RAND = new Random();
  protected int dnaLength = 10;
  protected float mutationChance = 0.006f;
  protected float mutateAmount = 0.1f;
  protected boolean shouldMutate = true;

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
    GUI gui = engine.getGUI();
    TriangleManager manager = new TriangleManager();

    for (int tries = 0; tries < 2; tries++)
    {
      Genome offspring = new Genome();
      Iterator<float[]> itrFirst = first.getTriangles().iterator();
      Iterator<float[]> itrSecond = second.getTriangles().iterator();
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

        geneSequenceCross(firstGenes, secondGenes, newGenes);

        offspring.add(manager.revertNormalization(newGenes));
      }

      offspring.setFitness(engine.getPopulation().getFitnessFunction().generateFitness(engine, offspring));
      ((Engine) engine).incrementGenerationCount();
      ((Engine)engine).incrementCrossCount();
      if (best == null) best = offspring;
      else best = engine.getPopulation().getFitnessFunction().compare(best, offspring);
    }
    return best;
  }

  /**
   * Performs some form of crossover between the first and second gene sequences. The results
   * are put in newSequence.
   * @param firstSequence first sequence of genes
   * @param secondSequence second sequence of genes
   * @param newSequence the new sequence of genes (result of cross)
   */
  protected void geneSequenceCross(float[] firstSequence, float[] secondSequence, float[] newSequence)
  {
    for (int i = 0; i < dnaLength; i++)
    {
      newSequence[i] = (RAND.nextFloat() < 0.5f) ? firstSequence[i] : secondSequence[i];
      newSequence[i] = checkForMutation(newSequence[i]);
    }
  }

  /**
   * Includes the chance that the input gene will mutate. If there is a mutation, the
   * return will reflect the change, and if not the return will be equal to the input gene.
   * @param geneVal normalized gene value
   * @return the new/old gene (depending on if a mutation occurred)
   */
  protected float checkForMutation(float geneVal)
  {
    if (RAND.nextFloat() < mutationChance && shouldMutate)
    {
      float mutation = RAND.nextFloat() * mutateAmount * 2;// - mutateAmount;
      if (RAND.nextFloat() < 0.5f) mutation *= -1;
      geneVal += mutation;

      if (geneVal < 0) geneVal = 0.0f;
      else if (geneVal > 1) geneVal = 1.0f;
    }
    return geneVal;
  }
}
