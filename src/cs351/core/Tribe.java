package cs351.core;

import cs351.core.Engine.EvolutionEngine;

import java.util.Collection;

/**
 * A tribe is a useful structure for managing and manipulating
 * a large set of genomes. The efficiency of this class is very important
 * as its functions will be called very frequently during the generation
 * process.
 *
 * The genomes in a tribe should be ordered according to their
 * fitness values, where the first element is the most fit and the last
 * element is the least fit.
 *
 * @author Justin
 */
public interface Tribe
{
  /**
   * Gets the mutator object that was assigned to the genome when
   * it was added to the tribe.
   *
   * @param genome genome object that is apart of the population
   * @return Mutator object for the given genome
   * @throws RuntimeException thrown if the given genome is not apart of the population
   */
  Mutator getMutatorForGenome(Genome genome) throws RuntimeException;

  /**
   * Adds a genome to the tribe.
   *
   * @param genome genome to add
   */

  void add(Genome genome);

  /**
   * Removes a genome from the tribe.
   *
   * @param genome genome to remove
   */
  void remove(Genome genome);

  /**
   * Clears the tribe of all genomes.
   */
  void clear();

  /**
   * Checks to see if the genome is part of the tribe.
   *
   * @param genome genome to check for
   * @return true if it exists and false if not
   */
  boolean contains(Genome genome);

  /**
   * Gets the number of genomes that are part of the tribe.
   *
   * @return number of genomes
   */
  int size();

  /**
   * Gets the best genome in the tribe.
   *
   * @return best genome
   */
  Genome getBest();

  /**
   * Returns an ordered list of genomes. The first is the most fit and the last
   * is the least fit.
   *
   * @return ordered list of genomes
   */
  Collection<Genome> getGenomes();

  /**
   * Runs through all genomes in the tribe and reorders them based on their fitness.
   *
   * This is needed since the genomes in a population might mutate at some point, but
   * the tribe won't know about these mutations.
   */
  void sort();

  /**
   * Passes the address of the engine to this tribe
   * @param engine EvolutionEngine reference for callbacks
   */
  void init(EvolutionEngine engine);
}