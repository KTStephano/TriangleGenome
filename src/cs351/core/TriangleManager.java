package cs351.core;

import cs351.core.Engine.GUI;

/**
 * The TriangleManager takes a float array from a TriangleGenerator and allows
 * you to extract data from it in a much more convenient way.
 *
 * Note :: Be sure to call setTriangleData(float[] data) before using and each time
 *         you want to interpret a different set of triangle data
 */
public class TriangleManager
{
  protected float[] data;
  protected float maxColor = 255.0f;
  protected float maxAlpha = 1.0f;
  protected float maxXValue; // pulled from GUI
  protected float maxYValue; // pulled from GUI

  public enum Coordinate
  {
    X1,
    Y1,
    X2,
    Y2,
    X3,
    Y3
  }

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
    maxXValue = gui.getImageWidth();
    maxYValue = gui.getImageHeight();
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