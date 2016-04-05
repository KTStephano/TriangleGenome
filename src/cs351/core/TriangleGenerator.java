package cs351.core;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import java.util.Random;

/**
 * This class is used to create triangles that are represented by a single
 * float array containing 10 genes.
 */
public class TriangleGenerator
{
  public static float[] createTriangle(final Random RAND, final EvolutionEngine ENGINE)
  {
    float x1 = 0.0f, x2 = 0.0f, x3 = 0.0f, y1 = 0.0f, y2 = 0.0f, y3 = 0.0f, alpha = 0.0f; // coordinates and alpha value
    int red, green, blue; // RGB values
    float[] geneCollection = new float[10];

    GUI mainGUI = ENGINE.getGUI();

    boolean reCalculate = true;
    // Create x values, check to make sure all values are valid
    int width = mainGUI.getImageWidth();
    int height = mainGUI.getImageHeight();

    x1 = RAND.nextFloat() * width;
    x2 = RAND.nextFloat() * width;
    x3 = RAND.nextFloat() * width;
    //x2 = x1 + RAND.nextInt(width / 4);
    //x3 = x1 + RAND.nextInt(width / 4);

    y1 = RAND.nextFloat() * height;
    y2 = RAND.nextFloat() * height;
    y3 = RAND.nextFloat() * height;
    //y2 = y1 + RAND.nextInt(height / 4);
    //y3 = y1 + RAND.nextInt(height / 4);

    // Create rgb values
    red = RAND.nextInt(255);
    green = RAND.nextInt(255);
    blue = RAND.nextInt(255);

    // Create alpha value
    alpha = Math.max(RAND.nextFloat() * RAND.nextFloat(), .2f);

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

    return geneCollection;
  }

  public static float[] createBaseTriangle(final Random RAND, final EvolutionEngine ENGINE)
  {
    float x1 = 0.0f, x2 = 0.0f, x3 = 0.0f, y1 = 0.0f, y2 = 0.0f, y3 = 0.0f, alpha = 0.0f; // coordinates and alpha value
    int red, green, blue; // RGB values
    float[] geneCollection = new float[10];

    GUI mainGUI = ENGINE.getGUI();

    boolean reCalculate = true;
    // Create x values, check to make sure all values are valid
    while(reCalculate)
    {
      x1 = RAND.nextInt(mainGUI.getImageWidth());
      x2 = RAND.nextInt(mainGUI.getImageWidth());
      x3 = RAND.nextInt(mainGUI.getImageWidth());
      if(x1 != x2 && x2 != x3 && x3 != x1) reCalculate = false;
    }

    // Create y values, check to make sure all values are valid
    reCalculate = true;
    while(reCalculate)
    {
      y1 = RAND.nextInt(mainGUI.getImageHeight());
      y2 = RAND.nextInt(mainGUI.getImageHeight());
      y3 = RAND.nextInt(mainGUI.getImageHeight());
      if(y1 != y2 && y2 != y3 && y3 != y1) reCalculate = false;
    }

    // Create rgb values
    red = RAND.nextInt(255);
    green = RAND.nextInt(255);
    blue = RAND.nextInt(255);

    // Create alpha value
    alpha = RAND.nextFloat();

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

    return geneCollection;
  }
}
