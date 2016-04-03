package cs351.core;

import cs351.core.Engine.EvolutionEngine;

/**
 * See slide 25.
 *
 * The whole purpose of this class is to take two separate genomes and mix them
 * together. The result will be a brand new genome that inherits traits
 * from both of its parents.
 *
 * Different implementations of Cross might define different ways of mixing
 * the two parent genomes.
 */
public interface Cross
{
  /**
   * This should cross the genes from this genome and another genome, creating a brand
   * new genome. The new genome should have all its own genes (copied from the parents)
   * with all of its own triangles. This will prevent issues with genomes sharing
   * references to genes/triangles in other genomes.
   *
   * @param engine game engine
   * @param first first of the two parent genomes
   * @param second second of the two parent genomes
   * @return a brand new genome inheriting traits from the two parents
   */
  Genome cross(EvolutionEngine engine, Genome first, Genome second);
}
