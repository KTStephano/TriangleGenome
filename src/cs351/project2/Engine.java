package cs351.project2;

import cs351.core.Engine.*;
import cs351.core.Genome;
import cs351.core.Tribe;
import cs351.utility.Job;
import cs351.utility.JobList;
import cs351.utility.ParallelJobSystem;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Engine implements EvolutionEngine
{
  // Engine version info
  private final int VERSION_MINOR = 1;
  private final int VERSION_MAJOR = 0;

  // General class members
  private Population population;
  private GUI gui;
  private Genome target;
  private Log log;
  private ParallelJobSystem jobSystem;
  private Statistics statistics;
  private boolean isRunningConsoleMode;
  private JobList mainJobList;

  // Atomic objects - these are the values that need to be thread safe
  private final AtomicInteger GENERATIONS;
  private final AtomicInteger NUM_WORKING_JOBS;
  private final AtomicBoolean IS_INITIALIZED;
  private final AtomicBoolean IS_PENDING_SHUTDOWN;
  private final AtomicBoolean IS_SHUTDOWN;
  private final AtomicBoolean IS_PAUSED;

  // Engine benchmarking
  private long millisecondsSinceLastFrame;
  private long millisecondTimeStamp;
  private final long[] LAST_100_FRAME_TIMESTAMPS = new long[100];
  private int currentTimestamp = 0;

  private final class MutatorJob implements Job
  {
    private final Population POPULATION;
    private final Genome GENOME;

    public MutatorJob(Population population, Genome genome)
    {
      POPULATION = population;
      GENOME = genome;
    }

    @Override
    public void start(int threadID)
    {
      //Mutator mutator = POPULATION.getMutatorForGenome(GENOME);
      //mutator.mutate();
    }
  }

  /**
   * Generations per second data field.
   */
  private final class GenerationsPerSecond extends DataField<Double>
  {
    private final String LOG_TAG;

    public GenerationsPerSecond(String dataTag, String logTag)
    {
      super(dataTag);
      LOG_TAG = logTag;
      data = 0.0;
    }

    @Override
    public void update(Log log)
    {
      data = getAverageGenerationsPerSecond();
      if (log != null) log.log(LOG_TAG, getDataTag() + ": %f seconds\n", getData());
    }
  }

  private final class TotalGenerations extends DataField<Integer>
  {
    private final String LOG_TAG;

    public TotalGenerations(String dataTag, String logTag)
    {
      super(dataTag);
      LOG_TAG = logTag;
      data = 0;
    }

    @Override
    public void update(Log log)
    {
      data = getGenerationCount();
      if (log != null) log.log(LOG_TAG, getDataTag() + ": %d generations\n", getData());
    }
  }

  // Initialize atomic objects
  {
    //mainJobList = new JobList(Globals.JOB_SYSTEM);
    GENERATIONS = new AtomicInteger(1);
    NUM_WORKING_JOBS = new AtomicInteger(0);
    IS_INITIALIZED = new AtomicBoolean(false);
    IS_PENDING_SHUTDOWN = new AtomicBoolean(false);
    IS_SHUTDOWN = new AtomicBoolean(false);
    IS_PAUSED = new AtomicBoolean(false);
  }

  @Override
  public Statistics getStatistics()
  {
    return statistics;
  }

  @Override
  public Log getLog()
  {
    return log;
  }

  @Override
  public ParallelJobSystem getParallelJobSystem()
  {
    return jobSystem;
  }

  @Override
  public Population getPopulation()
  {
    return population;
  }

  @Override
  public GUI getGUI()
  {
    return gui;
  }

  @Override
  public Genome getTarget()
  {
    throw new RuntimeException("getTarget() not finished");
    //return null;
  }

  @Override
  public void init(Stage stage, String imageFile, Population population, GUI mainGUI)
  {
    // Error handling
    if (IS_PENDING_SHUTDOWN.get()) throw new IllegalStateException("Engine is shutting down - can't initialize");
    else if (IS_INITIALIZED.get()) throw new IllegalStateException("Engine already initialized");

    System.out.println("--- Initializing Engine ---");
    System.out.println("Engine Version: " + getFullVersion());
    System.out.println("Available Memory (JVM): " + Runtime.getRuntime().totalMemory() + " bytes");
    System.out.println("Target Image: " + imageFile);
    System.out.println("Valid Population: " + (population != null));
    System.out.println("Console Mode: " + (mainGUI == null));

    this.population = population;
    gui = mainGUI;

    // Create the log
    log = new Log("GeneticLog" + "-RuntimeCode_" + System.currentTimeMillis() + ".txt");
    printLogHeader(imageFile);

    // Initialize the statistics system
    initStats();

    // Init the population and main GUI if they are not null
    if (mainGUI == null) isRunningConsoleMode = true;
    else
    {
      isRunningConsoleMode = false;
      gui.init(stage, this);
    }

    if (population != null)
    {
      int numTribes = mainGUI == null ? 1 : mainGUI.getTribes();
      jobSystem = new ParallelJobSystem(numTribes);
      jobSystem.init();
      population.generateStartingState(this, numTribes);
      //Tribe tribe = population.getTribe();
      // Initialize the mutator jobs
      //for (int i = 0; i < tribe.size(); i++) mainJobList.add(new MutatorJob(population, tribe.get(i)), 1);
      // TODO destroy this
      for (Tribe tribe : population.getTribes())
      {
        for(Genome genome : tribe.getGenomes()) mainJobList.add(new MutatorJob(population, genome), 1);
      }
    }

    IS_INITIALIZED.set(true);
    IS_SHUTDOWN.set(false);
  }

  @Override
  public void beginShutdown()
  {
    IS_PENDING_SHUTDOWN.set(true);
  }

  @Override
  public boolean isEnginePendingShutdown()
  {
    return IS_PENDING_SHUTDOWN.get();
  }

  @Override
  public boolean isEngineShutdown()
  {
    return IS_SHUTDOWN.get();
  }

  /**
   * Checks to see if the engine is paused.
   *
   * @return true if paused, false if not
   */
  @Override
  public boolean isEnginePaused()
  {
    return IS_PAUSED.get();
  }

  /**
   * Toggles the pause state of the engine.
   *
   * @param value true if paused, false if not
   */
  @Override
  public void togglePause(boolean value)
  {
    IS_PAUSED.set(value);
  }

  @Override
  public int getGenerationCount()
  {
    return GENERATIONS.get();
  }

  @Override
  public void generation()
  {
    if (!IS_INITIALIZED.get()) throw new RuntimeException("Engine was not initialized before generation() call");
    if (IS_PENDING_SHUTDOWN.get())
    {
      mainJobList.waitForCompletion();
      IS_PENDING_SHUTDOWN.set(false);
      IS_INITIALIZED.set(false);
      IS_SHUTDOWN.set(true);
      log.destroy(); // Let the log free its resource(s)
      jobSystem.destroy(); // destroy the job system
      System.out.println("--- Engine Shutdown Successfully ---");
      return; // finish here
    }
    // Check the status of the last queued frame
    if (mainJobList.containsActiveJobs()) mainJobList.waitForCompletion();
    // Tell the GUI it's a good time to do a rendering update since the previous
    // frame is done
    if (!isRunningConsoleMode) gui.update(this);

    if (!IS_PAUSED.get())
    {
      millisecondsSinceLastFrame = System.currentTimeMillis() - millisecondTimeStamp;
      millisecondTimeStamp = System.currentTimeMillis(); // mark the time when this frame started
      // Log the milliseconds since last frame for frame rate averaging
      LAST_100_FRAME_TIMESTAMPS[currentTimestamp] = millisecondsSinceLastFrame;
      currentTimestamp++;
      // Write the average frame rate to the console
      if (currentTimestamp >= LAST_100_FRAME_TIMESTAMPS.length)
      {
        currentTimestamp = 0; // reset
        if (isRunningConsoleMode)
        {
          double avgGPS = getAverageGenerationsPerSecond();
          enginePrint("Average time per generation: " + avgGPS + " seconds");
          //System.out.println("Average time per generation: " + avgFPS);
        }
        // Tell the statistics system to update
        statistics.update(null);
      }

      if (isRunningConsoleMode && GENERATIONS.get() % 100 == 0)
      {
        enginePrint(GENERATIONS.get() + " generations have passed");
      }

      // TODO add rest of loop here
      GENERATIONS.getAndIncrement();
      mainJobList.submitJobs(false);
    }
  }

  public double getAverageGenerationsPerSecond()
  {
    long totalMilliseconds = 0;
    for (int i = 0; i < LAST_100_FRAME_TIMESTAMPS.length; i++) totalMilliseconds += LAST_100_FRAME_TIMESTAMPS[i];
    double averageTime = totalMilliseconds / (double) LAST_100_FRAME_TIMESTAMPS.length;
    return averageTime / 1000.0; // convert to seconds
  }

  public int getVersionMinor()
  {
    return VERSION_MINOR;
  }

  public int getVersionMajor()
  {
    return VERSION_MAJOR;
  }

  public String getFullVersion()
  {
    return Integer.toString(getVersionMajor()) + "." + Integer.toString(getVersionMinor());
  }

  private void enginePrint(String message)
  {
    System.out.println("(ENGINE) " + message);
  }

  private void initStats()
  {
    statistics = new Statistics(log);
    statistics.add(new GenerationsPerSecond("Average Generations Per Second", "engine"));
    statistics.add(new TotalGenerations("Total Generations", "engine"));
  }

  private void printLogHeader(String imageFile)
  {
    String engineTag = "engine";
    log.log(engineTag, "Engine Version: %s\n", getFullVersion());
    log.log(engineTag, "Available Memory (JVM): %s bytes\n", Runtime.getRuntime().totalMemory());
    log.log(engineTag, "Target Image: %s\n", imageFile);
    log.log(engineTag, "Valid Population: %b\n", (population != null));
    log.log(engineTag, "Console Mode: %b\n", (gui == null));
  }
}
