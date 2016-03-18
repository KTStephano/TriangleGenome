package cs351.core;

import java.util.Collection;

/**
 * See slide 20.
 *
 * Given a tribe, a selector uses some algorithm to create a list of
 * genomes that can be crossed with each other.
 */
public interface Selector
{
  /**
   * Takes a tribe and selects a set number of genomes from the pool. It is up
   * to the selector's algorithm to pick them, but each genome should have some
   * chance of getting selected.
   *
   * @param tribe tribe to sample from
   * @param numGenomes number of genomes to pull from the tribe
   * @return list of selected genomes
   */
  Collection<Genome> select(Tribe tribe, int numGenomes);
}