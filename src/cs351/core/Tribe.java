package cs351.core;

/**
 * See slide 29.
 *
 * A tribe is a useful structure for managing and manipulating
 * a large set of genomes. The efficiency of this class is very important
 * as its functions will be called very frequently during the generation
 * process.
 *
 * The genomes in a tribe should be ordered according to their
 * fitness values, where the first element is the most fit and the last
 * element is the least fit.
 */
public interface Tribe
{
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
   * Gets the number of genomes that are part of the tribe.
   *
   * @return number of genomes
   */
  int size();

  /**
   * Gets the genome at the specified index. Index 0 should be the most
   * fit genome.
   *
   * @param index index of the genome
   * @return genome at the specified index
   */
  Genome get(int index);

  /**
   * Runs through all genomes in the tribe and reorders them based on their fitnesses.
   *
   * This is needed since the genomes in a population might mutate at some point, but
   * the tribe won't know about these mutations.
   */
  void recalculate();
}