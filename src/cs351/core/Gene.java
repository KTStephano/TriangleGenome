package cs351.core;

/**
 * A gene is a single integer value that can be combined with other
 * gene objects to represent something meaningful.
 */
public class Gene
{
  private double value;
  private final double MIN_VALUE, MAX_VALUE;
  private GeneTypes type;

  /**
   * Constructs a new Gene with the given value/type.
   *
   * @param value value for the gene
   * @param minValue the minimum value that this gene can take on
   * @param maxValue the maximum value that this gene can take on
   * @param type GeneTypes object
   */
  public Gene(double value, double minValue, double maxValue, GeneTypes type)
  {
    this.value = value;
    MIN_VALUE = minValue;
    MAX_VALUE = maxValue;
    this.type = type;
  }

  /**
   * Gets the current value of the gene.
   *
   * @return current value
   */
  public double getValue()
  {
    return value;
  }

  /**
   * Gets the minimum value that this gene can take on.
   *
   * @return min value
   */
  public double getMinValue()
  {
    return MIN_VALUE;
  }

  /**
   * Gets the maximum value that this gene can take on.
   *
   * @return max value
   */
  public double getMaxValue()
  {
    return MAX_VALUE;
  }

  /**
   * Gets the type of the gene.
   *
   * @return type
   */
  public GeneTypes getType()
  {
    return type;
  }

  /**
   * Mutates the gene, destroying the old value and replacing it with the new
   * value.
   *
   * @param newVal new value for the gene
   */
  public void mutate(double newVal)
  {
    // Basic bounds checking
    if (newVal < MIN_VALUE) newVal = MIN_VALUE;
    else if (newVal > MAX_VALUE) newVal = MAX_VALUE;
    value = newVal;
  }
}
