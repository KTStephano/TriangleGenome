package cs351.core.Engine;

import cs351.core.*;

import java.util.Collection;

/**
 * A population is used by an evolution engine to set the initial state
 * of a group of genomes and then manage any changes/removals as the engine
 * moves from generation to generation.
 *
 * Another use of the population is to maintain a reference to the active
 * fitness function, selector function, cross function, and whenever a new genome is added
 * to the population, the population object should assign a mutator to that genome.
 *
 * @author Justin
 */
public interface Population
{
  /**
   * Gets the fitness function that the population wants to be used
   * for all genomes.
   *
   * @return active fitness function
   */
  FitnessFunction getFitnessFunction();

  /**
   * Gets the active cross object being used for genomes that are
   * apart of the given population.
   *
   * @return Cross object
   */
  @Deprecated
  Cross getCrossObject();

  /**
   * Returns the selector that is being used to perform random genome selections.
   *
   * @return selector object
   */
  @Deprecated
  Selector getSelector();

  /**
   * Returns the working tribes for the population.
   *
   * @return working tribe
   */
  Collection<Tribe> getTribes();

  /**
   * Returns the best genome out of *every* available tribe.
   * @return best genome out of all tribes
   */
  Genome getOverallBest();

  /**
   * This function should clear the existing tribe and reinitialize it
   * to some starting state. Along with this, a fitness function should be generated
   * in order to be used not only by this class, but by all other classes that
   * want to create genomes.
   *
   * @param engine engine object for callbacks
   * @param numTribes number of tribes to initialize
   */
  void generateStartingState(EvolutionEngine engine, int numTribes);

  /**
   * This should only ever be called by an evolution engine. The purpose is to allow
   * the population to make sure that the Tribe it maintains does not contain
   * genomes that the population doesn't know about. This will only happen is another
   * part of the simulation directly added to the population's tribe without
   * running it through the population first.
   *
   * The reason this is important is because the population won't be able to assign
   * mutator objects to genomes it doesn't know about.
   *
   * @throws IllegalStateException thrown if the Tribe and population have fallen out of sync
   */
  void validatePopulationData() throws IllegalStateException;
}
