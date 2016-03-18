package cs351.core;

import cs351.utility.Vector2f;
import cs351.utility.Vector4f;
import java.util.Collection;

public interface Triangle
{
  /**
   * Returns an ordered list of vertices. No matter how many times
   * this is called, it should always return a list ordered in the form
   * of { vertex 1, vertex 2, vertex 3 }.
   *
   * @return ordered list of vertices
   */
  Collection<Vector2f> getVertices();

  /**
   * Returns a Vector4 representing the color of this triangle. The x value
   * represents red, y represents green, z represents blue and w represents
   * the transparency value.
   *
   * @return color of the triangle
   */
  Vector4f getColor();

  /**
   * An ordered list of genes for this Triangle. See slide 25 for the order
   * that the genes should be in.
   *
   * @return ordered list of genes
   */
  Collection<Gene> getGenes();
}
