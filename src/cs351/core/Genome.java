package cs351.core;

import java.util.Collection;
import java.util.LinkedList;

public final class Genome
{
  private final static int ZERO_HASH = 341940517;
  private static int ID = 0;
  private int id;
  protected Double fitness = 0.0;
  protected final LinkedList<Triangle> TRIANGLES = new LinkedList<>();
  //protected final LinkedList<Integer> GENES = new LinkedList<>();

  {
    id = ++ID;
  }

  @Override
  public int hashCode()
  {
    return (int)(TRIANGLES.hashCode() *
                 (fitness > 0.0 || fitness < 0.0 ? fitness * 100 : ZERO_HASH));
    //return TRIANGLES.hashCode() + GENES.hashCode() + fitness.hashCode();
  }

  @Override
  public boolean equals(Object other)
  {
    if (this == other) return true;
    else if (!(other instanceof Genome)) return false;
    Genome genome = (Genome)other;
    return this.TRIANGLES.equals(genome.TRIANGLES) &&
            this.fitness.equals(genome.fitness);
  }

  /**
   * Adds a triangle to the genome. The order that TRIANGLES are added
   * should be maintained.
   */
  public void add(Triangle triangle)
  {
    TRIANGLES.add(triangle);
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
    if (TRIANGLES.contains(triangle)) TRIANGLES.remove(triangle);
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
   * For each float array that is returned, that represents a complete triangle. The
   * order of the genes within the float array is the same as the order that they were
   * when the float array was added to the genome.
   *
   * @return ordered list of TRIANGLES
   */
  public Collection<Triangle> getTriangles()
  {
    return TRIANGLES;
  }
}
