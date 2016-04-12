package cs351.core;

import cs351.core.Engine.EvolutionEngine;

/**
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
   * At this point the mutator should decide on a mutation(s) and
   * perform them.
   *
   * @param function fitness function to use
   * @param engine reference to the current engine
   */
  void mutate(FitnessFunction function, EvolutionEngine engine);
}
