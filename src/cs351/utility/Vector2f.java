package cs351.utility;

/**
 * Vector2f can maintain double values for x/y components and perform some vector
 * operations with them.
 */
public class Vector2f
{
  Vector4f vec4;

  public Vector2f(double value)
  {
    vec4 = new Vector4f(value);
  }

  public Vector2f(double x, double y)
  {
    vec4 = new Vector4f(x, y, 0.0, 0.0);
  }

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

  public void set(Vector2f other)
  {
    vec4.set(other.vec4);
  }

  public void setX(double x)
  {
    vec4.setX(x);
  }

  public void setY(double y)
  {
    vec4.setY(y);
  }

  public double getX()
  {
    return vec4.getX();
  }

  public double getY()
  {
    return vec4.getY();
  }

  public void normalize()
  {
    vec4.normalize();
  }

  public double magnitude()
  {
    return vec4.magnitude();
  }
}
