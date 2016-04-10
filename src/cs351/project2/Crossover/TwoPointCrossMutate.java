package cs351.project2.crossover;

import cs351.core.Cross;

/**
 * Almost identical to SinglePointCrossMutate except that it chooses two
 * cross points.
 */
public class TwoPointCrossMutate extends UniformCrossMutate implements Cross
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
    int firstCrossPoint = RAND.nextInt(dnaLength - 2); // max value: 7
    int secondCrossPoint = RAND.nextInt(dnaLength - firstCrossPoint - 2) + 1; // min value: 1
    int i = 0;
    // [0, firstCrossPoint], copy from firstSequence
    for (; i <= firstCrossPoint; i++)
    {
      newSequence[i] = firstSequence[i];
      newSequence[i] = checkForMutation(newSequence[i]);
    }
    // [i, dnaLength - secondCrossPoint), copy from secondSequence
    for (; i < dnaLength - secondCrossPoint; i++)
    {
      newSequence[i] = secondSequence[i];
      newSequence[i] = checkForMutation(newSequence[i]);
    }
    // run to the end, copy from firstSequence again
    for (; i < dnaLength; i++)
    {
      newSequence[i] = firstSequence[i];
      newSequence[i] = checkForMutation(newSequence[i]);
    }
  }
}
