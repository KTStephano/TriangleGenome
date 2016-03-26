package cs351.core.Engine;

/**
 * A data field represents some piece of data that is either being
 * actively computed or is static after being initially set.
 */
public abstract class DataField<E>
{
  private final String TAG;
  protected E data;

  public DataField(String tag)
  {
    TAG = tag;
  }

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
   * @return
   */
  public String getDataTag()
  {
    return TAG;
  }

  public void setData(E data)
  {
    this.data = data;
  }

  public E getData()
  {
    return data;
  }

  /**
   * This is meant to be called at regular intervals by the Statistics class so that
   * the data field can update its data if it needs to.
   */
  public abstract void update();
}
