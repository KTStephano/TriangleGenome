package cs351.utility;

/**
 * Vector2i can maintain integer x/y values and perform some vector
 * operations with them.
 */
public class Vector2i
{
  Vector3f vec3;

  public Vector2i(int value)
  {
    vec3 = new Vector3f(value);
  }

  public Vector2i(int x, int y)
  {
    vec3 = new Vector3f(x, y, 0.0);
  }

  public Vector2i(Vector2i other)
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
    else if (!(other instanceof Vector2i)) return false;
    Vector2i vec2i = (Vector2i)other;
    return vec3.equals(vec2i.vec3);
  }

  /**
   * Sets the x and y values for this vector.
   *
   * @param x x value
   * @param y y value
   */
  public void set(int x, int y)
  {
    vec3.set(x, y, 0.0);
  }

  public void set(Vector2i other)
  {
    vec3.set(other.vec3);
  }

  public void setX(int x)
  {
    vec3.setX(x);
  }

  public void setY(int y)
  {
    vec3.setY(y);
  }

  public int getX()
  {
    return (int)vec3.getX();
  }

  public int getY()
  {
    return (int)vec3.getY();
  }

  public double magnitude()
  {
    return vec3.magnitude();
  }
}
