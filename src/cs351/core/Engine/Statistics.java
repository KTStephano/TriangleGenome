package cs351.core.Engine;


import java.util.*;

/**
 * Manages DataField objects and updates/logs them to a logging system.
 *
 * @author Justin
 */
public class Statistics implements Iterable<DataField>
{
  private final Log LOGGING_SYSTEM;
  // Keeps track of the exact order of the fields as they were entered
  private final LinkedList<DataField> ORDERED_FIELDS = new LinkedList<>();
  private final HashMap<String, DataField> TAG_DATA_MAP = new HashMap<>();

  /**
   * Creates a new statistics object with the associated log object.
   * @param log log to use during statistics logging process
   */
  public Statistics(Log log)
  {
    LOGGING_SYSTEM = log;
  }

  @Override
  public Iterator<DataField> iterator()
  {
    return ORDERED_FIELDS.iterator();
  }

  /**
   * Gets a set of data tags that map to different data objects that are being
   * managed by this statistics object.
   * @return set of data tags
   */
  public Set<String> dataTagSet()
  {
    return TAG_DATA_MAP.keySet();
  }

  /**
   * Adds a new data field to this statistics object. Its data tag will be extracted
   * and is guaranteed to be included in dataTagSet() unless the field is removed.
   * @param field data field to add to this statistics object
   */
  public void add(DataField field)
  {
    ORDERED_FIELDS.add(field);
    TAG_DATA_MAP.put(field.getDataTag(), field);
  }

  /**
   * Removes a data field from this statistics object.
   * @param field data field to remove
   */
  public void remove(DataField field)
  {
    if (!contains(field)) return;
    ORDERED_FIELDS.remove(field);
    TAG_DATA_MAP.remove(field.getDataTag());
  }

  /**
   * Checks to see if the field is contained within this object.
   * @param field field to check for
   * @return true if present and false if not
   */
  public boolean contains(DataField field)
  {
    return contains(field.getDataTag());
  }

  /**
   * This is the equivalent of contains(DataField), except it checks for the data's
   * tag rather than the field itself. The ouputs from the two contains() methods
   * should be in agreement.
   * @param dataTag data field's tag to check for
   * @return true if the associated data field is present and false if not
   */
  public boolean contains(String dataTag)
  {
    return TAG_DATA_MAP.containsKey(dataTag);
  }

  /**
   * Tells the object to update all data fields and write them to the log. The message
   * input variable is optional.
   *
   * @param message valid String to be logged first or null if none is needed
   */
  public void update(String message)
  {
    final String statTag = "stats";
    LOGGING_SYSTEM.log(statTag, "----> Statistics Begin");
    if (message != null) LOGGING_SYSTEM.log("external", message);
    for (DataField field : ORDERED_FIELDS) field.update(LOGGING_SYSTEM);
    LOGGING_SYSTEM.log(statTag, "----> Statistics End\n");
  }
}
