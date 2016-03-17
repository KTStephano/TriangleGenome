package cs351.core;

/**
 * A gene is a single integer value that can be combined with other
 * gene objects to represent something meaningful.
 */
public class Gene
{
  private int value;
  private GeneTypes type;

  /**
   * Constructs a new Gene with the given value/type.
   *
   * @param value value for the gene
   * @param type GeneTypes object
   */
  public Gene(int value, GeneTypes type)
  {
    this.value = value;
    this.type = type;
  }

  /**
   * Gets the current value of the gene.
   *
   * @return current value
   */
  public int getValue()
  {
    return value;
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
  public void mutate(int newVal)
  {
    value = newVal;
  }
}
