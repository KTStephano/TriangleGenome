package cs351.project2;

import cs351.core.Cross;

/**
 * Performs single point crossover between two genomes, including the possibility
 * for individual genes to mutate in the process.
 */
public class SinglePointCrossMutate extends UniformCrossMutate implements Cross
{
  /**
   * Performs some form of crossover between the first and second gene sequences. The results
   * are put in newSequence.
   * @param firstSequence first sequence of genes
   * @param secondSequence second sequence of genes
   * @param newSequence the new sequence of genes (result of cross)
   */
  @Override
  protected void geneSequenceCross(float[] firstSequence, float[] secondSequence, float[] newSequence)
  {
    int crossPoint = RAND.nextInt(dnaLength - 1);
    for (int i = 0; i < dnaLength; i++)
    {
      newSequence[i] = (i <= crossPoint ? firstSequence[i] : secondSequence[i]);
      newSequence[i] = checkForMutation(newSequence[i]);
    }
  }
}
