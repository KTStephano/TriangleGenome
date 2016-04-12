package cs351.core;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A Genome maintains an ordered list of triangles that are stored as float arrays.
 *
 * @author Justin
 */
public final class Genome
{
  private final static int ZERO_HASH = 341940517;
  private static int ID = 0;
  private int id;
  protected Double fitness = 0.0;
  protected final LinkedList<float[]> TRIANGLES = new LinkedList<>();
  protected Tribe tribe;

  {
    id = ++ID;
  }

  @Override
  public int hashCode()
  {
    return (int)(TRIANGLES.hashCode() *
                 (fitness > 0.0 || fitness < 0.0 ? fitness * 100 : ZERO_HASH));
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
   * @param triangle triangle data to add
   */
  public void add(float[] triangle)
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
  public void remove(float[] triangle)
  {
    if (TRIANGLES.contains(triangle)) TRIANGLES.remove(triangle);
  }

  /**
   * Removes all triangle data associated with this genome.
   */
  public void clear()
  {
    TRIANGLES.clear();
  }


  /**
   * Gets the size in terms of number of triangles.
   * @return number of triangles
   */
  public int size()
  {
    return TRIANGLES.size();
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
   * Sets the tribe that this genome belongs to (should be done by the tribe itself
   * when the genome is added).
   * @param tribe tribe to associate with this genome
   */
  public void setTribe(Tribe tribe)
  {
    this.tribe = tribe;
  }

  /**
   * Gets the tribe that this genome belongs to (should be set by the tribe when added).
   * @return tribe
   */
  public Tribe getTribe()
  {
    return tribe;
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
  public Collection<float[]> getTriangles()
  {
    return TRIANGLES;
  }

}
