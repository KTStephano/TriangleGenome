package cs351.core;

/**
 * See slide 19.
 *
 * A mutator should manage one Genome from generation to generation.
 * It is up to the mutator to decide not only what type of mutation
 * to perform on the genome, but which mutations should be favored
 * in the future (ex: if a mutation is particularly successful).
 */
public interface Mutator
{
  /**
   * Sets the genome. Should clear out all data maintained for the
   * previous genome if there is any.
   *
   * @param genome new genome
   */
  void setGenome(Genome genome);

  /**
   * Sets the fitness function to be used by this genome
   * to decide the fitness after mutations are made.
   *
   * @param function fitness function to use
   */
  void setFitnessFunction(FitnessFunction function);

  /**
   * At this point the mutator should decide on a mutation(s) and
   * perform them.
   */
  void mutate();
}
