package cs351.project2;

import cs351.core.Engine.*;
import cs351.core.Genome;
import cs351.core.Tribe;
import cs351.project2.crossover.CrossMutateSelector;
import cs351.project2.crossover.SinglePointCrossMutate;
import cs351.project2.crossover.TwoPointCrossMutate;
import cs351.utility.Job;
import cs351.utility.JobList;
import cs351.utility.ParallelJobSystem;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This implementation of EvolutionEngine currently manages the entire program. The
 * upside to using this is that it is thread-safe and provides a very useful way
 * of performing an update sequence: while multi-threaded operations are occurring,
 * certain parts of the code are not told to update as it may be unsafe. Then, as soon
 * as these operations complete, it will move to update different parts of the program
 * and provide a guarantee that they will not be exposed to half-updated data.
 *
 * @author Justin, George
 */
public final class Engine implements EvolutionEngine
{
  // Engine version info
  private final int VERSION_MINOR = 0;
  private final int VERSION_MAJOR = 1;

  // General class members
  private Population population;
  private GUI gui;
  private Image target;
  private Log log;
  private int numTribes;
  private int numUpdates = 0;
  private ParallelJobSystem jobSystem;
  private Statistics statistics;
  private boolean isRunningConsoleMode = false;
  private JobList mutatorJobList;
  private JobList singlePointCrossList;
  private JobList twoPointCrossList;
  private String[] cmdArgs; // set during init()
  private double previousBest;
  private double totalSeconds;
  private double totalFitness = 0.0;
  private int tempGeneration = 0; // Used for generations per second over the last second

  // Atomic objects - these are the values that need to be thread safe
  private final AtomicInteger GENERATIONS;
  private final AtomicInteger GENERATION_SNAPSHOT;
  private final AtomicInteger CROSSOVER_GENS;
  private final AtomicInteger HILLCLIMB_GENS;
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
  private int currentNumMutatorPhasesRun = 0;
  private int currentNumCrossPhasesRun = 0;

  // Additional Engine Stats
  private int middleMan = 0; // milliseconds that have elapsed
  private int seconds = 0;
  private int minutes = 0;
  private int hours = 0;
  private int populationCount = 0; // sum of genomes among all tribes

  /**
   * MutatorJob that performs mutations on the given tribe.
   * @author Justin
   */
  private final class MutatorJob implements Job
  {
    private final Population POPULATION;
    private final Tribe TRIBE;
    private final Engine ENGINE;

    /**
     * Creates a new mutator job with the given references.
     * @param population population to work with
     * @param tribe tribe to pull the best genome from
     * @param engine engine reference for callbacks
     */
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
   *
   * @author Justin Hall
   */
  private final class GenerationsPerSecond extends DataField<Double>
  {
    private final String LOG_TAG;

    /**
     * Creates a new DataField to update generations per second.
     * @param dataTag data tag for this field
     * @param logTag log tag (used to interface with a logging system)
     */
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

  /**
   * DataField representing the total generations that have passed.
   * @author Justin
   */
  private final class TotalGenerations extends DataField<Integer>
  {
    private final String LOG_TAG;

    /**
     * Creates a new data field that updates the total generations since
     * the last population initialization.
     * @param dataTag data tag to associate with this data field
     * @param logTag log tag (used when interfacing with a logging system)
     */
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
    GENERATIONS = new AtomicInteger(0);
    GENERATION_SNAPSHOT = new AtomicInteger(0);
    CROSSOVER_GENS = new AtomicInteger(0);
    HILLCLIMB_GENS = new AtomicInteger(0);
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
  }

  /**
   * NOTE :: For cmdArgs, the Engine (if cmdArgs is not length 0) expects exactly 2 arguments -
   *         the first in the form of "images/*imgName.ext*" and the second as a number to use
   *         to initialize the number of tribes
   * @param cmdArgs (OPTIONAL) list of command line arguments for the engine to use, but this
   *                should be completely optional - just don't pass it a null value (pass in
   *                a length 0 String array)
   * @param stage stage object so the engine can initialize the main GUI
   * @param population population to use to run the simulation
   * @param mainGUI gui to use
   */
  @Override
  public void init(String[] cmdArgs, Stage stage, Population population, GUI mainGUI)
  {
    // Error handling
    if (IS_PENDING_SHUTDOWN.get()) throw new IllegalStateException("Engine is shutting down - can't initialize");
    else if (IS_INITIALIZED.get()) throw new IllegalStateException("Engine already initialized");

    this.population = population;
    gui = mainGUI;

    // Create the log
    log = new Log("GeneticLog" + "-RuntimeCode_" + System.currentTimeMillis() + ".txt");

    this.cmdArgs = cmdArgs;
    isRunningConsoleMode = false;
    // Initialize the engine
    generateStartingState(cmdArgs, stage, true);
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

  /**
   * Gets the total mutations (in generations) done by hillclimbing since the last reset.
   * @return total mutations (in generations)
   */
  public int getMutationCount()
  {
    return HILLCLIMB_GENS.get();
  }

  /**
   * Gets the total number of crossover generations since the last reset.
   * @return crossover generations
   */
  public int getCrossCount()
  {
    return CROSSOVER_GENS.get();
  }

  /**
   * Gets the fitness per second.
   * @return fitness per second
   */
  public double getFitnessPerSecond()
  {
    return totalFitness / totalSeconds;
  }

  /**
   * Increments the number of crossover generations that have passed.
   */
  public void incrementCrossCount()
  {
    CROSSOVER_GENS.getAndIncrement();
  }

  /**
   * Increments the number of mutation generations (hillclimbing) that have passed.
   */
  public void incrementMutationCount()
  {
    HILLCLIMB_GENS.getAndIncrement();
  }

  @Override
  public void generation()
  {
    if (!IS_INITIALIZED.get()) throw new RuntimeException("Engine was not initialized before generation() call");
    if (IS_PENDING_SHUTDOWN.get())
    {
      twoPointCrossList.waitForCompletion();
      IS_PENDING_SHUTDOWN.set(false);
      IS_INITIALIZED.set(false);
      IS_SHUTDOWN.set(true);
      log.destroy(); // Let the log free its resource(s)
      jobSystem.destroy(); // destroy the job system
      System.out.println("--- Engine Shutdown Successfully ---");
      return; // finish here
    }
    // Check the status of the last queued frame
    if (mutatorJobList.containsActiveJobs() || twoPointCrossList.containsActiveJobs()) return;//twoPointCrossList.waitForCompletion();
    // Tell the GUI it's a good time to do a rendering update since the previous
    // frame is done
    if (gui.getTargetImage() != target && population != null) generateStartingState(cmdArgs, null, false);

    gui.update(this);
    if (numTribes != gui.getTribes()) generateStartingState(cmdArgs, null, false);

    if (!IS_PAUSED.get())
    {
      millisecondsSinceLastFrame = System.currentTimeMillis() - millisecondTimeStamp;
      millisecondTimeStamp = System.currentTimeMillis(); // mark the time when this frame started
      addToRunningTime(millisecondsSinceLastFrame);
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

      if (isRunningConsoleMode && numUpdates % 1000 == 0)
      {
        enginePrint(GENERATIONS.get() + " generations have passed");
        // getOverallBest returns null in very few situations, but check anyway
        if (population.getOverallBest() != null) enginePrint("Best fitness: " + population.getOverallBest().getFitness());
      }

      // If null then pr = 0
      if (getMinutes()*60 + getSeconds() > 0)
      {
        totalSeconds = getMinutes()*60 + getSeconds();
        totalFitness = totalFitness + (population.getOverallBest().getFitness() - previousBest);
        previousBest = population.getOverallBest().getFitness();
      }

      double percentToCross = .95;
      if (currentNumMutatorPhasesRun < 500 || population.getOverallBest().getFitness() < percentToCross)
      {
        mutatorJobList.submitJobs(false);
        ++currentNumMutatorPhasesRun;
      }
      // Don't start crossover until 90% fitness which in testing is where
      // our hill climber started to slow down
      else if (population.getOverallBest().getFitness() > percentToCross)
      {
        if (currentNumCrossPhasesRun < 3) twoPointCrossList.submitJobs(false);
        else singlePointCrossList.submitJobs(false);
        //++currentNumMutatorPhasesRun;
        ++currentNumCrossPhasesRun;
        if (currentNumCrossPhasesRun >= 5)
        {
          currentNumMutatorPhasesRun = 0;
          currentNumCrossPhasesRun = 0;
        }
      }

      ++numUpdates; // number of full generation updates
    }
  }

  @Override
  public double getAverageGenerationsPerSecond()
  {
    long totalMilliseconds = 0;
    for (int i = 0; i < LAST_100_FRAME_TIMESTAMPS.length; i++) totalMilliseconds += LAST_100_FRAME_TIMESTAMPS[i];
    double averageTime = totalMilliseconds / (double) LAST_100_FRAME_TIMESTAMPS.length;
    return averageTime / 1000.0; // convert to seconds
  }

  /**
   * Total generations averaged with all non-paused time.
   * @return average generations per second since the last reset
   */
  public double getAverageGenerationsPerSecondSinceLastInit()
  {
    return GENERATIONS.get() / (double)(getHours() * 60 * 60 + getMinutes() * 60 + getSeconds());
  }

  /**
   *
   * @return double of generations per second
   */
  public int getGenerationsLastSecond()
  {
    return tempGeneration;
  }

  /**
   * Hours that have elapsed when the game is running
   *
   * @return number of elapsed hours
   */
  @Override
  public int getHours()
  {
    return hours;
  }

  /**
   * Minutes that have elapsed when the game is running. When minutes reaches 60, variable resets to 0
   * and increments hours by 1.
   *
   * @return number of minutes
   */
  @Override
  public int getMinutes()
  {
    return minutes;
  }

  /**
   * Seconds that have elapsed when the game is running. When seconds reaches 60, variable resets to 0
   * and increments minutes by 1.
   *
   * @return number of seconds
   */
  @Override
  public int getSeconds()
  {
    return seconds;
  }

  /**
   * The sum of genomes across all tribes
   *
   * @return amount of genomes in the population
   */
  @Override
  public int getPopulationCount()
  {
    return populationCount;
  }

  /**
   * Increments the population count by one
   */
  @Override
  public void incrementPopulationCount()
  {
    populationCount ++;
  }

  /**
   * Decrements the population count by one
   */
  @Override
  public void decrementPopulationCount()
  {
    if(populationCount > 0) populationCount --;
  }

  /**
   * Gets the minor version of this engine (ex: 0.X where X is the minor version).
   * @return minor version of this engine
   */
  public int getVersionMinor()
  {
    return VERSION_MINOR;
  }

  /**
   * Gets the major version of this engine (ex: X.0 where X is the major version).
   * @return major version of this engine
   */
  public int getVersionMajor()
  {
    return VERSION_MAJOR;
  }

  /**
   * Helper method that combined getVersionMajor() and getVersionMinor(), returning a formatted
   * string containing both.
   * @return fotmatted version string for this engine
   */
  public String getFullVersion()
  {
    return Integer.toString(getVersionMajor()) + "." + Integer.toString(getVersionMinor());
  }

  /**
   * Increments the total number of generations since the last reset.
   */
  public void incrementGenerationCount()
  {
    GENERATION_SNAPSHOT.getAndIncrement();
    GENERATIONS.getAndIncrement();
  }

  private void addToRunningTime(long time)
  {
    if(time > 1000) return;
    middleMan += time;

    // Check if 1 second has elapsed
    if(middleMan >= 1000)
    {
      middleMan = middleMan - 1000;
      tempGeneration = GENERATION_SNAPSHOT.get();
      GENERATION_SNAPSHOT.set(0);
      seconds ++;
    }

    // Check if 1 minute has elapsed
    if(this.seconds >= 60)
    {
      this.seconds = this.seconds - 60;
      this.minutes += 1;
    }

    // Check if 1 hour has elapsed
    if(this.minutes >= 60)
    {
      this.minutes = this.minutes - 60;
      this.hours += 1;
    }
  }

  /**
   * Resets running time statistics
   */
  private void resetRunningTime()
  {
    seconds = 0;
    minutes = 0;
    hours = 0;
  }

  /**
   * Resets the population count statistic to zero
   */
  private void resetPopulationCount()
  {
    populationCount = 0;
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
    // Do nothing
  }

  private void generateStartingState(String[] cmdArgs, Stage stage, boolean initializeGUI)
  {
    System.out.println("--- Initializing Engine ---");
    System.out.println("Engine Version: " + getFullVersion());
    System.out.println("Available Memory (JVM): " + Runtime.getRuntime().totalMemory() + " bytes");
    System.out.println("Valid Population: " + (population != null));
    System.out.println("Console Mode: " + (gui == null));

    currentNumMutatorPhasesRun = 0;
    currentNumCrossPhasesRun = 0;

    printLogHeader();

    // Initialize the statistics system
    initStats();
    resetRunningTime();
    resetPopulationCount();

    // If there are command line arguments, create a console gui wrapper object
    // to stand in place of a full-fledged gui
    //
    // This needs to be done *after* printLogHeader() or the log will say that we
    // were never running in console mode
    if (gui == null)
    {
      isRunningConsoleMode = true;
      if (cmdArgs.length != 2)
      {
        throw new IllegalArgumentException("Command line arguments must be of length 2 and be of the form: " +
                                           "<images/imageFile.extension> <numTribes> (minus the < >)");
      }
      gui = new ConsoleGUIWrapper(cmdArgs[0], Integer.parseInt(cmdArgs[1]));
    }
    if (initializeGUI) gui.init(stage, this);
    target = gui.getTargetImage();

    numTribes = gui == null ? 1 : gui.getTribes();
    if (jobSystem != null) jobSystem.destroy(); // Make sure this gets cleaned up
    jobSystem = new ParallelJobSystem(numTribes);
    jobSystem.init();
    mutatorJobList = new JobList(jobSystem);
    singlePointCrossList = new JobList(jobSystem);
    twoPointCrossList = new JobList(jobSystem);
    GENERATIONS.set(0);
    if (population != null)
    {
      population.generateStartingState(this, numTribes);
      for (Tribe tribe : population.getTribes())
      {
        mutatorJobList.add(new MutatorJob(population, tribe, this), 1);
        singlePointCrossList.add(new CrossMutateSelector(this, tribe, new SinglePointCrossMutate()), 1);
        //twoPointCrossList.add(new CrossPhase(this, tribe), 1);
        twoPointCrossList.add(new CrossMutateSelector(this, tribe, new TwoPointCrossMutate()), 1);
      }
      //for (Tribe tribe : population.getTribes()) twoPointCrossList.add(new MutatorJob(population, tribe, this), 1);
    }

    // Clear the global genome list
    try
    {
      Globals.LOCK.lock();
      Globals.CONCURRENT_GENOME_LIST.clear();
    }
    finally
    {
      Globals.LOCK.unlock();
    }

    numUpdates = 0;
    totalFitness = 0;
    previousBest = 0;
    totalSeconds = 0;
    IS_INITIALIZED.set(true);
    IS_SHUTDOWN.set(false);
    GENERATIONS.set(0);
    GENERATION_SNAPSHOT.set(0);
    CROSSOVER_GENS.set(0);
    HILLCLIMB_GENS.set(0);
    NUM_WORKING_JOBS.set(0);
  }
}
