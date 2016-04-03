package cs351.project2;
import cs351.core.*;

import java.util.*;

/**
 * Created by Owner on 4/1/2016.
 */
public class GameCross implements Cross
{

  Genome childGenome = new Genome();
  private int coordLimit = 5;
  private int colorLimit = 9;

  /**
   * This should cross the genes from this genome and another genome, creating a brand
   * new genome. The new genome should have all its own genes (copied from the parents)
   * with all of its own triangles. This will prevent issues with genomes sharing
   * references to genes/triangles in other genomes.
   *
   * @param function fitness function to use to calculate the fitness of the new genome
   * @param first    first of the two parent genomes
   * @param second   second of the two parent genomes
   * @return a brand new genome inheriting traits from the two parents
   */

  private static final Random RAND = new Random();

  @Override
  public Genome cross(FitnessFunction function, Genome first, Genome second)
  {
    childGenome = singleCross(function, first, second);

    // Generate childGenome fitness

    // If child is greater than either parent genome, just return the child genome
    if(childGenome == function.compare(childGenome, first) || childGenome == function.compare(childGenome, second))
    {
      return childGenome;
    }

    // Otherwise use the double cross, and just return the child genome
    childGenome = doubleCross(function, first, second);
    return childGenome;
  }

  private Genome singleCross(FitnessFunction function, Genome first, Genome second)
  {
    Genome singleCrossGenome = new Genome();

    int crossLimit = 0;
    int index = 0;

    Collection<float[]> firstGenome = new LinkedList<>();
    Collection<float[]> secondGenome = new LinkedList<>();

    firstGenome = first.getTriangles();
    secondGenome = second.getTriangles();

    Iterator<float[]> fIterator = firstGenome.iterator();
    Iterator<float[]> sIterator = secondGenome.iterator();

    float[] firstTriangle = new float[10];
    float[] secondTriangle = new float[10];
    float[] currentTriangle = new float[10];

    // Create 200 triangles
    for(int i = 0; i < firstGenome.size(); i++)
    {
      firstTriangle = fIterator.next();
      secondTriangle = sIterator.next();

      crossLimit = RAND.nextInt(10); // set single crossover limit
      index = 0;

      // Get Values up to the single cross limit
      for(; index < crossLimit; index++)
      {
        currentTriangle[index] = firstTriangle[index];
      }

      // Add remaining values from second triangle
      for(; index < 10; index ++)
      {
        currentTriangle[index] = secondTriangle[index];
      }

      singleCrossGenome.add(currentTriangle);
    }
    return singleCrossGenome;
  }

  private Genome doubleCross(FitnessFunction function, Genome first, Genome second)
  {
    Genome doubleCrossGenome = new Genome();

    int coordCrossLimit = 0;
    int colorCrossLimit = 6;
    int index = 0;

    Collection<float[]> firstGenome = new LinkedList<>();
    Collection<float[]> secondGenome = new LinkedList<>();

    firstGenome = first.getTriangles();
    secondGenome = second.getTriangles();

    Iterator<float[]> fIterator = firstGenome.iterator();
    Iterator<float[]> sIterator = secondGenome.iterator();

    float[] firstTriangle = new float[10];
    float[] secondTriangle = new float[10];
    float[] currentTriangle = new float[10];

    for(int i = 0; i < firstGenome.size(); i++)
    {
      firstTriangle = fIterator.next();
      secondTriangle = sIterator.next();

      coordCrossLimit = RAND.nextInt(6); // set single crossover limit
      colorCrossLimit = RAND.nextInt(4) + 6;
      index = 0;

      // Get Coordinate Values
      for(; index < coordCrossLimit; index++)
      {
        currentTriangle[index] = firstTriangle[index];
      }
      for(; index < 6; index ++)
      {
        currentTriangle[index] = secondTriangle[index];
      }

      // Get Color Values
      for(; index < colorCrossLimit; index++)
      {
        currentTriangle[index] = firstTriangle[index];
      }
      for(; index < 10; index++)
      {
        currentTriangle[index] = secondTriangle[index];
      }


      doubleCrossGenome.add(currentTriangle);
    }
    return doubleCrossGenome;
  }

  /**
   * Implements the uniform cross over function. In theory, both genomes will be able to contribute equally to
   * the child
   * @param function
   * @param first
   * @param second
   * @return
   */
  private Genome uniformCross(FitnessFunction function, Genome first, Genome second)
  {
    return null;
  }


}
