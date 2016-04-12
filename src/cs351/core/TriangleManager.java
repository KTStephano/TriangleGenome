package cs351.core;

import cs351.core.Engine.GUI;

/**
 * The TriangleManager takes a float array from a TriangleGenerator and allows
 * you to extract data from it in a much more convenient way.
 *
 * Note :: Be sure to call setTriangleData(float[] data) before using and each time
 *         you want to interpret a different set of triangle data
 *
 * @author Justin
 */
public class TriangleManager
{
  protected float[] data;
  protected float[] normalizedData;
  protected float maxColor = 255.0f;
  protected float maxAlpha = 1.0f;
  protected float maxXValue; // pulled from GUI
  protected float maxYValue; // pulled from GUI

  /**
   * Enum representing the different coordinate possibilities.
   *
   * @author Justin
   */
  public enum Coordinate
  {
    X1,
    Y1,
    X2,
    Y2,
    X3,
    Y3
  }

  /**
   * Enum representing the different color values.
   *
   * @author Justin
   */
  public enum ColorValue
  {
    RED,
    GREEN,
    BLUE,
    ALPHA
  }

  /**
   * Sets the triangle data to interpret.
   * @param gui gui to pull constraints from
   * @param data array of 10 elements
   */
  public void setTriangleData(GUI gui, float[] data)
  {
    if (data.length != 10) throw new IllegalArgumentException("Invalid triangle data");
    this.data = data;
    normalizedData = new float[10];
    maxXValue = gui.getImageWidth();
    maxYValue = gui.getImageHeight();

    normalizedData[0] = data[0] / maxXValue;
    normalizedData[1] = data[1] / maxYValue;
    normalizedData[2] = data[2] / maxXValue;
    normalizedData[3] = data[3] / maxYValue;
    normalizedData[4] = data[4] / maxXValue;
    normalizedData[5] = data[5] / maxYValue;
    normalizedData[6] = data[6] / 255;
    normalizedData[7] = data[7] / 255;
    normalizedData[8] = data[8] / 255;
    normalizedData[9] = data[9];
  }

  /**
   * Sets the triangle data when used for crossover (called by GameCross.java)
   * @param data array of 10 elements
   */
  public void setTriangleData(float[] data)
  {
    if (data.length != 10) throw new IllegalArgumentException("Invalid triangle data");
    this.data = data;
  }

  public float[] getNormalizedDNA()
  {
    return normalizedData;
  }

  public float[] revertNormalization(float[] dna)
  {
    dna[0] = dna[0] * maxXValue;
    dna[1] = dna[1] * maxYValue;
    dna[2] = dna[2] * maxXValue;
    dna[3] = dna[3] * maxYValue;
    dna[4] = dna[4] * maxXValue;
    dna[5] = dna[5] * maxYValue;
    dna[6] = dna[6] * 255;
    dna[7] = dna[7] * 255;
    dna[8] = dna[8] * 255;
    dna[9] = dna[9];
    return dna;
  }

  /**
   * Format :: { x1, x2, x3 }
   * @return float array with the formatted data
   */
  public float[] getXCoordinates()
  {
    return new float[] { data[0], data[2], data[4] };
  }

  /**
   * Format :: { y1, y2, y3 }
   * @return float array with the formatted data - lines up with getXCoordinates()
   */
  public float[] getYCoordinates()
  {
    return new float[] { data[1], data[3], data[5] };
  }

  /**
   * Format :: { red, green, blue, alpha }
   * @return float array with the formatted data
   */
  public float[] getColor()
  {
    return new float[] { data[6], data[7], data[8], data[9] };
  }

  /**
   * Mutates one of the coordinates.
   * @param coordinate which coordinate to mutate (ex: Coordinate.X1)
   * @param offset mutation amount
   */
  public void mutateCoordinate(Coordinate coordinate, float offset)
  {
    float newVal = data[coordinate.ordinal()] + offset;
    if (coordinate.ordinal() % 2 == 0) newVal = constrain(newVal, 0.0f, maxXValue);
    else newVal = constrain(newVal, 0.0f, maxYValue);
    data[coordinate.ordinal()] = newVal;
  }

  /**
   * Mutates one of the color values.
   * @param colorVal which coordinate to mutate (ex: ColorValue.RED);
   * @param offset mutation amount
   */
  public void mutateColorValue(ColorValue colorVal, float offset)
  {
    int colorOffset = 6;
    float newVal = data[colorVal.ordinal() + colorOffset] + offset;
    // 9 = alpha value
    if (colorVal.ordinal() + colorOffset == 9) newVal = constrain(newVal, 0.0f, maxAlpha);
    else newVal = constrain(newVal, 0.0f, maxColor);
    data[colorVal.ordinal() + colorOffset] = newVal;
  }

  private float constrain(float num, float min, float max)
  {
    if (num < min) num = min;
    else if (num > max) num = max;
    return num;
  }
}