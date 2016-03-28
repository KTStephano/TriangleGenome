package cs351.project2;

import cs351.core.Engine.GUI;
import cs351.core.Triangle;
import java.util.Random;

/**
 * GameTriangle is initially called by GamePopulation. It is initially called when a new genome is being generated
 * and by default needs to be assigned a triangle. When created GameTriangle will randomly generate genes
 */
public class GameTriangle implements Triangle
{
  private int maxSize = 50; // max distance a vertex can be from another vertex in this triangle
  private int minSize = 20; // min distance a vertex can be from another vertex in this triangle
  private float x1,x2,x3,y1,y2,y3,alpha; // coordinates and alpha value
  private int red, green, blue; // RGB values
  private float[] geneCollection;

  {
    geneCollection = new float[10];
  }

  /**
   * Returns an ordered list of vertices. No matter how many times
   * this is called, it should always return a list ordered in the form
   * of { vertex 1, vertex 2, vertex 3 }.
   *
   * @return ordered list of x-vertices
   */
  public float[] getXVertices()
  {
    float[] vertices = new float[3];
    vertices[0] = x1;
    vertices[1] = x2;
    vertices[2] = x3;
    return vertices;
  }

  /**
   * Returns an ordered list of vertices. No matter how many times
   * this is called, it should always return a list ordered in the form
   * of { vertex 1, vertex 2, vertex 3 }.
   *
   * @return ordered list of x-vertices
   */
  public float[] getYVertices()
  {
    float[] vertices = new float[3];
    vertices[0] = y1;
    vertices[1] = y2;
    vertices[2] = y3;
    return vertices;
  }

  /**
   * Returns a Vector4 representing the color of this triangle. The x value
   * represents red, y represents green, z represents blue and w represents
   * the transparency value.
   *
   * @return color of the triangle
   */
  @Override
  public float[] getColor()
  {
    float[] color = new float[4];
    color[0] = red;
    color[1] = green;
    color[2] = blue;
    color[3] = alpha;
    return color;
  }

  /**
   * An ordered list of genes for this Triangle. See slide 25 for the order
   * that the genes should be in.
   *
   * @return ordered list of genes
   */
  @Override
  public float[] getGenes()
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
    boolean reCalculate = true;

    // Create x values, check to make sure all values are valid
    reCalculate = true;
    while(reCalculate == true)
    {
      x1 = numGenerator.nextInt(mainGUI.getImageWidth());
      x2 = numGenerator.nextInt(mainGUI.getImageWidth());
      x3 = numGenerator.nextInt(mainGUI.getImageWidth());
      if(x1 != x2 && x2 != x3 && x3 != x1) reCalculate = false;
    }

    // Create y values, check to make sure all values are valid
    reCalculate = true;
    while(reCalculate == true)
    {
      y1 = numGenerator.nextInt(mainGUI.getImageHeight());
      y2 = numGenerator.nextInt(mainGUI.getImageHeight());
      y3 = numGenerator.nextInt(mainGUI.getImageHeight());
      if(y1 != y2 && y2 != y3 && y3 != y1) reCalculate = false;
    }

    // Create rgb values
    red = numGenerator.nextInt(255);
    green = numGenerator.nextInt(255);
    blue = numGenerator.nextInt(255);

    // Create alpha value
    alpha = numGenerator.nextFloat();

    /**
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
     */

    // Build the collection of genes in the following order { x1, y1, x2, y2, x3, y3, r, g, b, a }
    geneCollection[0] = x1;
    geneCollection[1] = y1;

    geneCollection[2] = x2;
    geneCollection[3] = y2;

    geneCollection[4] = x3;
    geneCollection[5] = y3;

    geneCollection[6] = red;
    geneCollection[7] = green;
    geneCollection[8] = blue;
    geneCollection[9] = alpha;
  }

}
