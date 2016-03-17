package cs351.utility;


/**
 * A Vector3f can maintain double values for x/y/z components and perform
 * vector operations on them.
 */
final public class Vector3f
{
  private double x, y, z;
  private float magnitude;
  private boolean calcMagnitude = true;

  /**
   * Main constructor - all values initialized to value.
   *
   * @param value value to use to initialize
   */
  public Vector3f(double value)
  {
    set(value, value, value);
  }

  /**
   * Specifies each value individually.
   * @param x x value
   * @param y y value
   * @param z z value
   */
  public Vector3f(double x, double y, double z)
  {
    set(x, y, z);
  }

  /**
   * Creates a Vector3f from the values of another Vector3f.
   *
   * @param vec3 Vector3f to use to initialize
   */
  public Vector3f(Vector3f vec3)
  {
    set(vec3.x, vec3.y, vec3.z);
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
    return (int)((10 * ((int)x ^ 10) / z) * (20 * ((int)y ^ 20) / x) * (30 * ((int)z ^ 30) / y));
  }

  /**
   * Custom equals.
   *
   * @param other other Vector3f
   * @return true if equal
   */
  @Override
  public boolean equals(Object other)
  {
    if (this == other) return true;
    else if (!(other instanceof Vector3f)) return false;
    Vector3f vec3 = (Vector3f)other;
    return this.x == vec3.x && this.y == vec3.y && this.z == vec3.z;
  }

  /**
   * Sets the values to be x, y, z
   * @param x x value
   * @param y y value
   * @param z z value
   */
  public void set(double x, double y, double z)
  {
    calcMagnitude = true;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Sets the values to be the values of the Vector3f
   * @param vec3 other Vector3f object
   */
  public void set(Vector3f vec3)
  {
    set(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Sets the x value
   * @param x x value
   */
  public void setX(double x)
  {
    set(x, this.y, this.z);
  }

  /**
   * Sets the y value
   * @param y y value
   */
  public void setY(double y)
  {
    set(this.x, y, this.z);
  }

  /**
   * Sets the z value
   * @param z z value
   */
  public void setZ(double z)
  {
    set(this.x, this.y, z);
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

  /**
   * Normalizes the current Vector3f.
   */
  public void normalize()
  {
    magnitude();
    x /= magnitude;
    y /= magnitude;
    z /= magnitude;
  }

  /**
   * Gets the magnitude
   * @return magnitude
   */
  public double magnitude()
  {
    if (calcMagnitude) magnitude = (float)Math.sqrt(x * x + y * y + z * z);
    calcMagnitude = false;
    return magnitude;
  }

  /**
   * Subtracts this vector from the other and creates a new Vector3f.
   * @param other other vector
   * @return new vector representing the subtraction
   */
  public Vector3f subtract(Vector3f other)
  {
    return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.y);
  }

  /**
   * Adds this vector from the other and creates a new Vector3f.
   * @param other other vector
   * @return new vector representing the addition
   */
  public Vector3f add(Vector3f other)
  {
    return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
  }

  /**
   * Performs dot product
   * @param other other vector
   * @return dot product
   */
  public double dot(Vector3f other)
  {
    return this.x * other.x + this.y * other.y + this.z * other.z;
  }

  /**
   * Performs the cross product
   * @param other other vector
   * @return cross product
   */
  public Vector3f cross(Vector3f other)
  {
    return new Vector3f(this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x);
  }
}
