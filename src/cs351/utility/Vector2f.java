package cs351.utility;

/**
 * Vector2f can maintain double values for x/y components and perform some vector
 * operations with them.
 */
public class Vector2f
{
  Vector3f vec3;

  public Vector2f(double value)
  {
    vec3 = new Vector3f(value);
  }

  public Vector2f(double x, double y)
  {
    vec3 = new Vector3f(x, y, 0.0);
  }

  public Vector2f(Vector2f other)
  {
    vec3 = new Vector3f(other.vec3);
  }

  /**
   * Custom hash code.
   *
   * @return hash code
   */
  @Override
  public int hashCode()
  {
    return vec3.hashCode();
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
    return vec3.equals(vec2i.vec3);
  }

  /**
   * Sets the x and y values for this vector.
   *
   * @param x x value
   * @param y y value
   */
  public void set(double x, double y)
  {
    vec3.set(x, y, 0.0);
  }

  public void set(Vector2f other)
  {
    vec3.set(other.vec3);
  }

  public void setX(double x)
  {
    vec3.setX(x);
  }

  public void setY(double y)
  {
    vec3.setY(y);
  }

  public double getX()
  {
    return vec3.getX();
  }

  public double getY()
  {
    return vec3.getY();
  }

  public double magnitude()
  {
    return vec3.magnitude();
  }
}
