package cs351.core.Engine;


import java.util.*;

/**
 * Manages DataField objects and updates/logs them to a logging system.
 */
public class Statistics implements Iterable<DataField>
{
  private final Log LOGGING_SYSTEM;
  // Keeps track of the exact order of the fields as they were entered
  private final LinkedList<DataField> ORDERED_FIELDS = new LinkedList<>();
  private final HashMap<String, DataField> TAG_DATA_MAP = new HashMap<>();

  public Statistics(Log log)
  {
    LOGGING_SYSTEM = log;
  }

  @Override
  public Iterator<DataField> iterator()
  {
    return ORDERED_FIELDS.iterator();
  }

  public Set<String> dataTagSet()
  {
    return TAG_DATA_MAP.keySet();
  }

  public void add(DataField field)
  {
    ORDERED_FIELDS.add(field);
    TAG_DATA_MAP.put(field.getDataTag(), field);
  }

  public void remove(DataField field)
  {
    if (!contains(field)) return;
    ORDERED_FIELDS.remove(field);
    TAG_DATA_MAP.remove(field.getDataTag());
  }

  public boolean contains(DataField field)
  {
    return contains(field.getDataTag());
  }

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
    LOGGING_SYSTEM.log(statTag, "----> Statistics Begin\n");
    if (message != null) LOGGING_SYSTEM.log("external", message);
    for (DataField field : ORDERED_FIELDS) field.update(LOGGING_SYSTEM);
    LOGGING_SYSTEM.log(statTag, "----> Statistics End\n\n");
  }
}
