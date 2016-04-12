package cs351.core;

import cs351.core.Engine.GUI;
import java.util.Random;

/**
 * A triangle maintains a single float[] array representing its genes and
 * provides some useful methods for extracting data from it.
 *
 * @author Justin
 */
@Deprecated
public interface Triangle
{
  /**
   * Returns an ordered list of vertices. No matter how many times
   * this is called, it should always return a list ordered in the form
   * of { vertex 1, vertex 2, vertex 3 }.
   *
   * @return ordered list of x-vertices
   */
  float[] getXVertices();

  /**
   * Returns an ordered list of vertices. No matter how many times
   * this is called, it should always return a list ordered in the form
   * of { vertex 1, vertex 2, vertex 3 }.
   *
   * @return ordered list of x-vertices
   */
  float[] getYVertices();

  /**
   * Returns a Vector4 representing the color of this triangle. The x value
   * represents red, y represents green, z represents blue and w represents
   * the transparency value.
   *
   * @return color of the triangle
   */
  float[] getColor();

  /**
   * An ordered list of genes for this Triangle. See slide 25 for the order
   * that the genes should be in.
   *
   * @return ordered list of genes
   */
  float[] getGenes();

  /**
   * @return maximum distance between any vertex of the triangle
   */
  int getMaxSize();

  /**
   * @return minimum distance between any vertex of the triangle
   */
  int getMinSize();

  /**
   * set the maximum distance between any vertex of the triangle
   */
  void setMaxSize(int maxSize);

  /**
   * set the maximum distance between any vertex of the triangle
   */
  void setMinSize(int minSize);

  /**
   * Randomly generates 10 genes for this triangle. Uses passed generator for any randomization
   *
   * @return a float array representing
   */
  void init(Random numGenerator, GUI mainGUI);

}
