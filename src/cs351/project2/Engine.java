package cs351.project2;

import cs351.core.Engine.*;
import cs351.core.Genome;
import cs351.core.Tribe;
import cs351.utility.Job;
import cs351.utility.JobList;
import cs351.utility.ParallelJobSystem;
import javafx.scene.image.Image;
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
  private int numTribes;
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
    private final Tribe TRIBE;
    private final Engine ENGINE;

    public MutatorJob(Population population, Tribe tribe, Engine engine)
    {
      POPULATION = population;
      TRIBE = tribe;
      ENGINE = engine;
    }

    @Override
    public void start(int threadID)
    {
      Genome best = TRIBE.getBest();
      TRIBE.getMutatorForGenome(best).mutate(POPULATION.getFitnessFunction(), ENGINE);
      TRIBE.sort();
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
      if (log != null) log.log(LOG_TAG, getDataTag() + ": %f seconds", getData());
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
      if (log != null) log.log(LOG_TAG, getDataTag() + ": %d generations", getData());
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
  public Image getTarget()
  {
    throw new RuntimeException("getTarget() not finished");
    //return null;
  }

  @Override
  public void init(Stage stage, Population population, GUI mainGUI)
  {
    // Error handling
    if (IS_PENDING_SHUTDOWN.get()) throw new IllegalStateException("Engine is shutting down - can't initialize");
    else if (IS_INITIALIZED.get()) throw new IllegalStateException("Engine already initialized");

    this.population = population;
    gui = mainGUI;

    // Create the log
    log = new Log("GeneticLog" + "-RuntimeCode_" + System.currentTimeMillis() + ".txt");

    // Initialize the engine
    generateStartingState(stage, true);
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
    if (!isRunningConsoleMode)
    {
      if (gui.getHasSelectedNewImage() && population != null)
      {
        population.generateStartingState(this, numTribes);
        mainJobList.clear();
        for (Tribe tribe : population.getTribes()) mainJobList.add(new MutatorJob(population, tribe, this), 1);
      }
      gui.update(this);
      if (numTribes != gui.getTribes()) generateStartingState(null, false);
    }

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
      //GENERATIONS.getAndIncrement();
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

  public void incrementGenerationCount()
  {
    GENERATIONS.getAndIncrement();
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

  private void printLogHeader()
  {
    String engineTag = "engine";
    log.log(engineTag, "Engine Version: %s", getFullVersion());
    log.log(engineTag, "Available Memory (JVM): %s bytes", Runtime.getRuntime().totalMemory());
    log.log(engineTag, "Valid Population: %b", (population != null));
    log.log(engineTag, "Console Mode: %b", (gui == null));
  }

  /**
   * Makes sure the engine's data is current.
   */
  private void validateEngineState()
  {

  }

  private void generateStartingState(Stage stage, boolean initializeGUI)
  {
    System.out.println("--- Initializing Engine ---");
    System.out.println("Engine Version: " + getFullVersion());
    System.out.println("Available Memory (JVM): " + Runtime.getRuntime().totalMemory() + " bytes");
    System.out.println("Valid Population: " + (population != null));
    System.out.println("Console Mode: " + (gui == null));

    printLogHeader();

    // Initialize the statistics system
    initStats();

    // Init the population and main GUI if they are not null
    if (gui == null) isRunningConsoleMode = true;
    else if (initializeGUI)
    {
      isRunningConsoleMode = false;
      gui.init(stage, this);
    }

    numTribes = gui == null ? 1 : gui.getTribes();
    if (jobSystem != null) jobSystem.destroy(); // Make sure this gets cleaned up
    jobSystem = new ParallelJobSystem(numTribes);
    jobSystem.init();
    mainJobList = new JobList(jobSystem);
    GENERATIONS.set(0);
    if (population != null)
    {
      population.generateStartingState(this, numTribes);
      for (Tribe tribe : population.getTribes()) mainJobList.add(new CrossPhase(this, tribe), 1);
      //for (Tribe tribe : population.getTribes()) mainJobList.add(new MutatorJob(population, tribe, this), 1);
    }

    IS_INITIALIZED.set(true);
    IS_SHUTDOWN.set(false);
    GENERATIONS.set(1);
    NUM_WORKING_JOBS.set(0);
  }
}
