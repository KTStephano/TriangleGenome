package cs351.core.Engine;

/**
 * A data field represents some piece of data that is either being
 * actively computed or is static after being initially set.
 *
 * @author Justin
 */
public abstract class DataField<E>
{
  private final String TAG;
  protected E data;

  /**
   * Creates a DataField with a starting TAG but with no data mapped to it.
   * @param tag key for this data
   */
  public DataField(String tag)
  {
    TAG = tag;
  }

  /**
   * Creates a DataField with a starting TAG with associated data
   * @param tag key for this data
   * @param dataObj data to associate with key
   */
  public DataField(String tag, E dataObj)
  {
    this(tag);
    data = dataObj;
  }

  @Override
  public boolean equals(Object other)
  {
    if (!(other instanceof DataField)) return false;
    DataField o = (DataField)other;
    return this == o || this.TAG.equals(o.TAG);
  }

  @Override
  public int hashCode()
  {
    return TAG.hashCode();
  }

  /**
   * A data tag serves as an id for a particular piece of data. For example,
   * a data tag representing the change in seconds from one frame to the next
   * in a video game could be "Delta Seconds", and the actual data could be an int
   * or double.
   *
   * @return data tag
   */
  public String getDataTag()
  {
    return TAG;
  }

  /**
   * Sets the data.
   * @param data data to set
   */
  public void setData(E data)
  {
    this.data = data;
  }

  /**
   * Gets the data associated with this field.
   * @return reference to data
   */
  public E getData()
  {
    return data;
  }

  /**
   * This is meant to be called at regular intervals by the Statistics class so that
   * the data field can update its data if it needs to.
   *
   * If a Log object is supplied, the DataField should log its latest data.
   *
   * @param log log object reference - check for null since it might be if no logging
   *            is required
   */
  public abstract void update(Log log);
}
