package cs351.core.Engine;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Manages DataField objects and updates/logs them to a logging system.
 */
public class Statistics implements Iterable<DataField>
{
  private final Log LOGGING_SYSTEM;
  private final HashSet<DataField> DATA_FIELDS = new HashSet<>();
  private final HashMap<String, DataField> TAG_DATA_MAP = new HashMap<>();

  public Statistics(Log log)
  {
    LOGGING_SYSTEM = log;
  }

  @Override
  public Iterator<DataField> iterator()
  {
    return DATA_FIELDS.iterator();
  }

  public Set<String> dataTagSet()
  {
    return TAG_DATA_MAP.keySet();
  }

  public void add(DataField field)
  {
    DATA_FIELDS.add(field);
    TAG_DATA_MAP.put(field.getDataTag(), field);
  }

  public void remove(DataField field)
  {
    if (!contains(field)) return;
    DATA_FIELDS.remove(field);
    TAG_DATA_MAP.remove(field.getDataTag());
  }

  public boolean contains(DataField field)
  {
    return DATA_FIELDS.contains(field);
  }

  public boolean contains(String dataTag)
  {
    return TAG_DATA_MAP.containsKey(dataTag);
  }
}
