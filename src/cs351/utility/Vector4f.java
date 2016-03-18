package cs351.utility;


/**
 * A Vector4f can maintain double values for x/y/z components and perform
 * vector operations on them.
 */
final public class Vector4f
{
  private double x, y, z, w;
  private float magnitude;
  private boolean calcMagnitude = true;

  /**
   * Main constructor - all values initialized to value.
   *
   * @param value value to use to initialize
   */
  public Vector4f(double value)
  {
    set(value, value, value, value);
  }

  /**
   * Specifies each value individually.
   * @param x x value
   * @param y y value
   * @param z z value
   */
  public Vector4f(double x, double y, double z, double w)
  {
    set(x, y, z, w);
  }

  /**
   * Creates a Vector4f from the values of another Vector4f.
   *
   * @param vec3 Vector4f to use to initialize
   */
  public Vector4f(Vector4f vec3)
  {
    set(vec3.x, vec3.y, vec3.z, vec3.w);
  }

  /**
   * Custom hash code.
   *
   * @return custom hash code
   */
  @Override
  public int hashCode()
  {
    // not very good but should be ok for now (I think)
    return (int)((10 * ((int)x ^ 10) / z) +
                 (20 * ((int)y ^ 20) / x) +
                 (30 * ((int)z ^ 30) / y) +
                 (40 * ((int)w)));
  }

  /**
   * Custom equals.
   *
   * @param other other Vector4f
   * @return true if equal
   */
  @Override
  public boolean equals(Object other)
  {
    if (this == other) return true;
    else if (!(other instanceof Vector4f)) return false;
    Vector4f vec3 = (Vector4f)other;
    return this.x == vec3.x && this.y == vec3.y && this.z == vec3.z && this.w == vec3.w;
  }

  /**
   * Sets the values to be x, y, z
   * @param x x value
   * @param y y value
   * @param z z value
   */
  public void set(double x, double y, double z, double w)
  {
    calcMagnitude = true;
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  /**
   * Sets the values to be the values of the Vector4f
   * @param vec3 other Vector4f object
   */
  public void set(Vector4f vec3)
  {
    set(vec3.x, vec3.y, vec3.z, vec3.w);
  }

  /**
   * Sets the x value
   * @param x x value
   */
  public void setX(double x)
  {
    set(x, this.y, this.z, this.w);
  }

  /**
   * Sets the y value
   * @param y y value
   */
  public void setY(double y)
  {
    set(this.x, y, this.z, this.w);
  }

  /**
   * Sets the z value
   * @param z z value
   */
  public void setZ(double z)
  {
    set(this.x, this.y, z, this.w);
  }

  /**
   * Sets the w value
   * @param w w value
   */
  public void setW(double w)
  {
    set(this.x, this.y, this.z, w);
  }

  /**
   * Gets the x value
   * @return x value
   */
  public double getX()
  {
    return x;
  }

  /**
   * Gets the y value
   * @return y value
   */
  public double getY()
  {
    return y;
  }

  /**
   * Gets the z value
   * @return z value
   */
  public double getZ()
  {
    return z;
  }

  public double getW()
  {
    return w;
  }

  /**
   * Normalizes the current Vector4f.
   */
  public void normalize()
  {
    magnitude();
    x /= magnitude;
    y /= magnitude;
    z /= magnitude;
    w /= magnitude;
  }

  /**
   * Gets the magnitude
   * @return magnitude
   */
  public double magnitude()
  {
    if (calcMagnitude) magnitude = (float)Math.sqrt(x * x + y * y + z * z + w * w);
    calcMagnitude = false;
    return magnitude;
  }

  /**
   * Subtracts this vector from the other and creates a new Vector4f.
   * @param other other vector
   * @return new vector representing the subtraction
   */
  public Vector4f subtract(Vector4f other)
  {
    return new Vector4f(this.x - other.x, this.y - other.y, this.z - other.y, this.w - other.w);
  }

  /**
   * Adds this vector from the other and creates a new Vector4f.
   * @param other other vector
   * @return new vector representing the addition
   */
  public Vector4f add(Vector4f other)
  {
    return new Vector4f(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w);
  }

  /**
   * Performs dot product
   * @param other other vector
   * @return dot product
   */
  public double dot(Vector4f other)
  {
    return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
  }

  /**
   * Performs the cross product - since this is only an orthogonal operation in 3D,
   * the result of this is a Vector4f with 0.0 for its w-component.
   * @param other other vector
   * @return cross product
   */
  public Vector4f cross(Vector4f other)
  {
    return new Vector4f(this.y * other.z - this.z * other.y,
                        this.z * other.x - this.x * other.z,
                        this.x * other.y - this.y * other.x,
                        0.0);
  }
}
