package cs351.utility;

/**
 * Vector2f can maintain double values for x/y components and perform some vector
 * operations with them.
 *
 * @author Justin
 */
public class Vector2f
{
  Vector4f vec4;

  /**
   * Creates a new Vector2f with x/y components to be the given value.
   *
   * @param value value to set x/y coordinates to
   */
  public Vector2f(double value)
  {
    vec4 = new Vector4f(value);
  }

  /**
   * Creates a new Vector2f with x/y/ components to be the given values.
   *
   * @param x x value
   * @param y y value
   */
  public Vector2f(double x, double y)
  {
    vec4 = new Vector4f(x, y, 0.0, 0.0);
  }

  /**
   * Copy constructor - creates a new Vector2f with the values of the given
   * Vector2f.
   *
   * @param other Vector2f to copy
   */
  public Vector2f(Vector2f other)
  {
    vec4 = new Vector4f(other.vec4);
  }

  /**
   * Custom hash code.
   *
   * @return hash code
   */
  @Override
  public int hashCode()
  {
    return vec4.hashCode();
  }

  /**
   * Custom equals.
   *
   * @param other object to compare to
   * @return true if equal and false if not
   */
  @Override
  public boolean equals(Object other)
  {
    if (this == other) return true;
    else if (!(other instanceof Vector2f)) return false;
    Vector2f vec2i = (Vector2f)other;
    return vec4.equals(vec2i.vec4);
  }

  /**
   * Sets the x and y values for this vector.
   *
   * @param x x value
   * @param y y value
   */
  public void set(double x, double y)
  {
    vec4.set(x, y, 0.0, 0.0);
  }

  /**
   * Sets the x and y values for this vector to be that of another vector.
   *
   * @param other vector to pull values from
   */
  public void set(Vector2f other)
  {
    vec4.set(other.vec4);
  }

  /**
   * Sets the x value of this vector.
   *
   * @param x x value
   */
  public void setX(double x)
  {
    vec4.setX(x);
  }

  /**
   * Sets the y value of this vector.
   *
   * @param y y value
   */
  public void setY(double y)
  {
    vec4.setY(y);
  }

  /**
   * Gets the x value of this vector.
   *
   * @return x value
   */
  public double getX()
  {
    return vec4.getX();
  }

  /**
   * Gets the y value of this vector.
   *
   * @return y value
   */
  public double getY()
  {
    return vec4.getY();
  }

  /**
   * Normalizes this vector.
   */
  public void normalize()
  {
    vec4.normalize();
  }

  /**
   * Gets the magnitude of this vector.
   *
   * @return magnitude
   */
  public double magnitude()
  {
    return vec4.magnitude();
  }
}
