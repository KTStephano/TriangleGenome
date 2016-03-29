package cs351.core;

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
   * @param data array of 10 elements
   */
  public void setTriangleData(float[] data)
  {
    if (data.length != 10) throw new IllegalArgumentException("Invalid triangle data");
    this.data = data;
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
   * @param newVal new value for the coordinate
   */
  public void mutateCoordinate(Coordinate coordinate, float newVal)
  {
    data[coordinate.ordinal()] = newVal;
  }

  /**
   * Mutates one of the color values.
   * @param colorVal which coordinate to mutate (ex: ColorValue.RED);
   * @param newVal new value for the coordinate
   */
  public void mutateColorValue(ColorValue colorVal, float newVal)
  {
    int colorOffset = 6;
    data[colorVal.ordinal() + colorOffset] = newVal;
  }
}