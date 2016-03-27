package cs351.core;

import java.util.Collection;
import java.util.LinkedList;

public class Genome
{
  private final static int ZERO_HASH = 341940517;
  protected Double fitness = 0.0;
  protected final LinkedList<Triangle> TRIANGLES = new LinkedList<>();
  protected final LinkedList<Gene> GENES = new LinkedList<>();

  @Override
  public int hashCode()
  {
    //return TRIANGLES.hashCode() * GENES.hashCode() * fitness > 0.0 || fitness < 0.0 ? (int)fitness * 100 : ZERO_HASH;
    return TRIANGLES.hashCode() + GENES.hashCode() + fitness.hashCode();
  }

  @Override
  public boolean equals(Object other)
  {
    return this == other || (other instanceof Genome &&
            ((Genome)other).TRIANGLES.equals(TRIANGLES) &&
            ((Genome)other).GENES.equals(GENES));
  }

  /**
   * Adds a triangle to the genome. The order that TRIANGLES are added
   * should be maintained.
   */
  public void add(Triangle triangle)
  {
    TRIANGLES.add(triangle);
    GENES.addAll(triangle.getGenes());
  }

  /**
   * Removes a triangle from the genome. The result should be like removing
   * an element from a linked list where both neighbors of the removed
   * triangle become neighbors of each other and no other orderings
   * are disturbed.
   *
   * @param triangle triangle to remove
   */
  public void remove(Triangle triangle)
  {
    if (TRIANGLES.contains(triangle))
    {
      TRIANGLES.remove(triangle);
      GENES.removeAll(triangle.getGenes());
    }
  }

  /**
   * Gets the normalized fitness that was assigned to this genome.
   *
   * @return normalzed fitness
   */
  public double getFitness()
  {
    return fitness;
  }


  /**
   * Sets the fitness for this genome.
   *
   * @param fitness normalized fitness value
   */
  public void setFitness(double fitness)
  {
    this.fitness = fitness;
  }

  /**
   * This should return an ordered list of TRIANGLES.
   *
   * @return ordered list of TRIANGLES
   */
  public Collection<Triangle> getTriangles()
  {
    return TRIANGLES;
  }

  /**
   * The output from this should be the same as calling getTriangles()
   * and then getGenes() on each triangle in order.
   *
   * Example output: {triangle 1's genes}, {triangle 2's genes}, etc.
   *
   * The size of this list should be equal to the number of TRIANGLES * number of genes per triangle.
   *
   * @return ordered list of genes
   */
  public Collection<Gene> getGenes()
  {
    return GENES;
  }
}
