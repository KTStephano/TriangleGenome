package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import cs351.core.Engine.Globals;
import cs351.core.Engine.Population;
import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.Tribe;
import cs351.utility.Job;
import cs351.utility.JobList;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Engine implements EvolutionEngine
{
  private Population population;
  private GUI gui;
  private Genome target;
  private boolean isRunningConsoleMode;
  private final JobList MAIN_JOB_LIST;

  // Atomic objects - these are the values that need to be thread safe
  private final AtomicInteger GENERATIONS;
  private final AtomicInteger NUM_WORKING_JOBS;
  private final AtomicBoolean IS_INITIALIZED;
  private final AtomicBoolean IS_PENDING_SHUTDOWN;
  private final AtomicBoolean IS_SHUTDOWN;

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
      Mutator mutator = POPULATION.getMutatorForGenome(GENOME);
      mutator.mutate();
    }
  }

  // Initialize atomic objects
  {
    MAIN_JOB_LIST = new JobList(Globals.JOB_SYSTEM);
    GENERATIONS = new AtomicInteger(1);
    NUM_WORKING_JOBS = new AtomicInteger(0);
    IS_INITIALIZED = new AtomicBoolean(false);
    IS_PENDING_SHUTDOWN = new AtomicBoolean(false);
    IS_SHUTDOWN = new AtomicBoolean(false);
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
    System.out.println("Available Memory (JVM): " + Runtime.getRuntime().totalMemory() + " bytes");
    System.out.println("Target Image: " + imageFile);
    System.out.println("Valid Population: " + (population != null));
    System.out.println("Console Mode: " + (mainGUI == null));

    this.population = population;
    gui = mainGUI;

    // Init the population and main GUI if they are not null
    if (population != null)
    {
      population.generateStartingState(this);
      Tribe tribe = population.getTribe();
      // Initialize the mutator jobs
      for (int i = 0; i < tribe.size(); i++) MAIN_JOB_LIST.add(new MutatorJob(population, tribe.get(i)), 1);
    }
    if (mainGUI == null) isRunningConsoleMode = true;
    else
    {
      isRunningConsoleMode = false;
      gui.init(stage, this);
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
      MAIN_JOB_LIST.waitForCompletion();
      IS_PENDING_SHUTDOWN.set(false);
      IS_INITIALIZED.set(false);
      IS_SHUTDOWN.set(true);
      System.out.println("--- Engine Shutdown Successfully ---");
      return; // finish here
    }
    // Check the status of the last queued frame
    if (MAIN_JOB_LIST.containsActiveJobs()) MAIN_JOB_LIST.waitForCompletion();
    // Tell the GUI it's a good time to do a rendering update since the previous
    // frame is done
    if (!isRunningConsoleMode) gui.update(this);

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
        long totalMilliseconds = 0;
        for (int i = 0; i < LAST_100_FRAME_TIMESTAMPS.length; i++) totalMilliseconds += LAST_100_FRAME_TIMESTAMPS[i];
        double averageTime = totalMilliseconds / (double)LAST_100_FRAME_TIMESTAMPS.length;
        double avgFPS = averageTime / 1000.0; // convert to seconds
        enginePrint("Average time per generation: " + avgFPS);
        //System.out.println("Average time per generation: " + avgFPS);
      }
    }

    if (isRunningConsoleMode && GENERATIONS.get() % 100 == 0)
    {
      enginePrint(GENERATIONS.get() + " generations have passed");
    }

    // TODO add rest of loop here
    GENERATIONS.getAndIncrement();
    MAIN_JOB_LIST.submitJobs(false);
  }

  private void enginePrint(String message)
  {
    System.out.println("(ENGINE) " + message);
  }
}
