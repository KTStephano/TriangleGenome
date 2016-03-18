package cs351.core;

import java.util.Collection;

public interface Genome
{
  /**
   * Adds a triangle to the genome. The order that triangles are added
   * should be maintained.
   */
  void add(Triangle triangle);

  /**
   * Removes a triangle from the genome. The result should be like removing
   * an element from a linked list where both neighbors of the removed
   * triangle become neighbors of each other and no other orderings
   * are disturbed.
   *
   * @param triangle triangle to remove
   */
  void remove(Triangle triangle);

  /**
   * Gets the normalized fitness that was assigned to this genome.
   *
   * @return normalzed fitness
   */
  double getFitness();

  /**
   * Sets the fitness for this genome.
   *
   * @param fitness normalized fitness value
   */
  void setFitness(double fitness);

  /**
   * This should return an ordered list of triangles.
   *
   * @return ordered list of triangles
   */
  Collection<Triangle> getTriangles();

  /**
   * The output from this should be the same as calling getTriangles()
   * and then getGenes() on each triangle in order.
   *
   * Example output: {triangle 1's genes}, {triangle 2's genes}, etc.
   *
   * The size of this list should be equal to the number of triangles * number of genes per triangle.
   *
   * @return ordered list of genes
   */
  Collection<Gene> getGenes();
}
