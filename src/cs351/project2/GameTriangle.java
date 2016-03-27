package cs351.project2;

import cs351.core.Engine.GUI;
import cs351.core.Gene;
import cs351.core.GeneTypes;
import cs351.core.Triangle;
import cs351.utility.Vector2f;
import cs351.utility.Vector4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * GameTriangle is initially called by GamePopulation. It is initially called when a new genome is being generated
 * and by default needs to be assigned a triangle. When created GameTriangle will randomly generate genes
 */
public class GameTriangle implements Triangle
{
  private int maxSize = 50; // max distance a vertex can be from another vertex in this triangle
  private int minSize = 20; // min distance a vertex can be from another vertex in this triangle
  private double x1,x2,x3,y1,y2,y3,alpha; // coordinates and alpha value
  private int red, green, blue; // RGB values
  private Collection<Gene> geneCollection;
  private Collection<Vector2f> vector2fCollection;
  private Vector4f triangleColor;
  /**
   * Returns an ordered list of vertices. No matter how many times
   * this is called, it should always return a list ordered in the form
   * of { vertex 1, vertex 2, vertex 3 }.
   *
   * @return ordered list of vertices
   */
  @Override
  public Collection<Vector2f> getVertices()
  {
    return vector2fCollection;
  }

  /**
   * Returns a Vector4 representing the color of this triangle. The x value
   * represents red, y represents green, z represents blue and w represents
   * the transparency value.
   *
   * @return color of the triangle
   */
  @Override
  public Vector4f getColor()
  {
    return triangleColor;
  }

  /**
   * An ordered list of genes for this Triangle. See slide 25 for the order
   * that the genes should be in.
   *
   * @return ordered list of genes
   */
  @Override
  public Collection<Gene> getGenes()
  {
    return geneCollection;
  }

  /**
   * @return maximum distance between any vertex of the triangle
   */
  @Override
  public int getMaxSize()
  {
    return maxSize;
  }

  /**
   * @return minimum distance between any vertex of the triangle
   */
  @Override
  public int getMinSize()
  {
    return minSize;
  }

  /**
   * set the maximum distance between any vertex of the triangle
   */
  @Override
  public void setMaxSize(int maxSize)
  {
    this.maxSize = maxSize;
  }

  /**
   * set the maximum distance between any vertex of the triangle
   */
  @Override
  public void setMinSize(int minSize)
  {
    this.minSize = minSize;
  }

  /**
   * Randomly generates 10 genes for this triangle. Remember that a triangle cannot have all three x vertices with the
   * same value. In the same aspect, a triangle cannot have all three y vertices with the same value
   */
  @Override
  public void init(Random numGenerator, GUI mainGUI)
  {
    geneCollection = new ArrayList<>();
    vector2fCollection = new ArrayList<>();

    boolean reCalculate = true;

    // Create x values, check to make sure all values are valid
    reCalculate = true;
    while(reCalculate == true)
    {
      x1 = (double) numGenerator.nextInt((int)mainGUI.getImageWidth());
      x2 = (double) numGenerator.nextInt((int)mainGUI.getImageWidth());
      x3 = (double) numGenerator.nextInt((int)mainGUI.getImageWidth());
      if(x1 != x2 && x2 != x3 && x3 != x1) reCalculate = false;
    }

    // Create y values, check to make sure all values are valid
    reCalculate = true;
    while(reCalculate == true)
    {
      y1 = (double) numGenerator.nextInt((int)mainGUI.getImageHeight());
      y2 = (double) numGenerator.nextInt((int)mainGUI.getImageHeight());
      y3 = (double) numGenerator.nextInt((int)mainGUI.getImageHeight());
      if(y1 != y2 && y2 != y3 && y3 != y1) reCalculate = false;
    }

    // Create rgb values
    red = numGenerator.nextInt(255);
    green = numGenerator.nextInt(255);
    blue = numGenerator.nextInt(255);

    // Create alpha value
    alpha = numGenerator.nextDouble();

    // Create new genes with created values, add genes to geneCollection
    geneCollection.add(new Gene(x1, 0, mainGUI.getImageWidth(), GeneTypes.TRIANGLE_VERTEX_X));
    geneCollection.add(new Gene(y1, 0, mainGUI.getImageWidth(), GeneTypes.TRIANGLE_VERTEX_Y));

    geneCollection.add(new Gene(x2, 0, mainGUI.getImageWidth(), GeneTypes.TRIANGLE_VERTEX_X));
    geneCollection.add(new Gene(y2, 0, mainGUI.getImageWidth(), GeneTypes.TRIANGLE_VERTEX_Y));

    geneCollection.add(new Gene(x3, 0, mainGUI.getImageWidth(), GeneTypes.TRIANGLE_VERTEX_X));
    geneCollection.add(new Gene(y3, 0, mainGUI.getImageWidth(), GeneTypes.TRIANGLE_VERTEX_Y));

    geneCollection.add(new Gene(red, 0, 255, GeneTypes.TRIANGLE_COLOR_RED));
    geneCollection.add(new Gene(green, 0, 255, GeneTypes.TRIANGLE_COLOR_GREEN));
    geneCollection.add(new Gene(blue, 0, 255, GeneTypes.TRIANGLE_COLOR_BLUE));

    geneCollection.add(new Gene(alpha, 0, 1, GeneTypes.TRIANGLE_COLOR_TRANSPARENT));

    // Create vector2fs and add to vector2f collection
    vector2fCollection.add(new Vector2f(x1,y1));
    vector2fCollection.add(new Vector2f(x2,y2));
    vector2fCollection.add(new Vector2f(x3,y3));

    // create vector4f for RGBA values
    triangleColor = new Vector4f(red, green, blue, alpha);
  }

}
