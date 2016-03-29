package cs351.core.Engine;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * A Log provides a way to write structured debug output to the given file.
 *
 * Files that do not exist will be created and files that do exist will
 * be appended to.
 *
 * By default, even when a filename is provided, the Log class will create
 * a new directory within the project called debug, and within that it
 * will create sub-folders labeled with the current data. Ex:
 *
 *    debug/03_25_16/
 *
 * and within the date folder it will put the debug logs.
 */
public class Log
{
  private final Path FILE_PATH;
  private BufferedWriter fileWriter;
  private boolean readyForFileWriting = true;

  /**
   * Default constructor. All requests will be pushed to the created
   * debug folder to debug.txt.
   */
  public Log()
  {
    this("debug.txt");
  }

  public Log(String filename)
  {
    DateFormat dateFormat = new SimpleDateFormat("MM_dd_yy");
    //System.out.println(dateFormat.format(new Date()));
    String path = "debug/" + dateFormat.format(new Date()) + "/" + filename;
    //System.out.println(path);
    FILE_PATH = Paths.get(path);
    createDirectoryFromPath(path);
    try
    {
      if (!Files.exists(FILE_PATH)) Files.createFile(FILE_PATH);
      FileOutputStream stream = new FileOutputStream(new File(formatForFileOpen(FILE_PATH.toAbsolutePath().toString())));
      fileWriter = new BufferedWriter(new OutputStreamWriter(stream));
    }
    catch (Exception e)
    {
      readyForFileWriting = false;
    }
  }

  /**
   * This writes a String message to the current working debug file. To do this,
   * specify a log type (ex: "debug"), a message, and any extra arguments to
   * the message. This works exactly like printf:
   *
   *    log("debug", "test %d", 3);
   *
   * will write "(DEBUG) test 3" to the current log file.
   *
   * @param logType Keyword representing the log type. Ex: "debug" - the log will
   *                automatically convert it to uppercase and wrap it inside parenthesis.
   * @param format format for the message
   * @param args any arguments to use if the message needs them
   */
  public void log(String logType, String format, Object ... args)
  {
    if (!readyForFileWriting) return;
    StringBuilder str = new StringBuilder(format.length());
    Formatter formatter = new Formatter(str, Locale.US);
    String key = generateLogKey(logType);
    String result = formatter.format(format, args).toString();
    //System.out.println(result);
    try
    {
      //LinkedList<String> list = new LinkedList<>();
      String logData = key + " " + result + "\n";
      fileWriter.write(logData);
      fileWriter.flush();
      //Files.write(FILE_PATH, list, Charset.defaultCharset());
    }
    catch (Exception e)
    {
      System.out.println("Error writing to log file " + FILE_PATH.toString());
    }
  }

  /**
   * This formats an exception and logs it to the current working log file.
   *
   * @param logType Keyword representing the log type. Ex: "debug" - the log will
   *                automatically convert it to uppercase and wrap it inside parenthesis.
   * @param exception Exception to log
   */
  public void logException(String logType, Exception exception)
  {
    StringBuilder str = new StringBuilder(25);
    str.append(exception.toString()); // Add the message data to the String
    StackTraceElement[] elements = exception.getStackTrace();
    if (elements.length > 0) str.append("\n"); // only add when there are more messages to display
    for (StackTraceElement element : elements)
    {
      str.append("\t\t\t\t--> ");
      str.append(element.toString());
      str.append("\n");
    }
    log(logType, str.toString());
  }

  /**
   * Ensures that the resource(s) used by the Log object are freed.
   */
  public void destroy()
  {
    if (readyForFileWriting)
    {
      try
      {
        fileWriter.close();
      }
      catch (Exception e)
      {
        // Do nothing
      }
    }
  }

  private void createDirectoryFromPath(String path)
  {
    String pathWithoutFile = stripFile(path);
    // If directory path already exists, stop here
    if (Files.exists(Paths.get(pathWithoutFile))) return;
    File directory = new File(pathWithoutFile);
    if (!directory.mkdirs()) throw new RuntimeException("Could not create directory " + pathWithoutFile);
  }

  private String stripFile(String path)
  {
    StringBuilder str = new StringBuilder(path.length());
    boolean foundFirstSlash = false;
    for (int i = path.length() - 1; i >= 0; i--)
    {
      if (foundFirstSlash) str.append(path.charAt(i));
      else if (path.charAt(i) == '/') foundFirstSlash = true;
    }
    return str.reverse().toString();
  }

  private String generateLogKey(String logKey)
  {
    return "(" + logKey.toUpperCase() + ")";
  }

  private String formatForFileOpen(String filename)
  {
    StringBuilder str = new StringBuilder(filename.length());
    for (int i = 0; i < filename.length(); i++)
    {
      if (filename.charAt(i) == '\\') str.append('/');
      else str.append(filename.charAt(i));
    }
    return str.toString();
  }
}
