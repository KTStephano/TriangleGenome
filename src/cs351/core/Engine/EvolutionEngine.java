package cs351.core.Engine;

import cs351.core.Genome;
import cs351.utility.Job;
import cs351.utility.ParallelJobSystem;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * An evolution engine sets the stage for the entire process
 * of the mutation/evolution of a population. As part of its initialization,
 * it should take the file path to a target image which it will load
 * and translate the image data into a target genome. It should also
 * drive the seeding of the initial population and initialization of any
 * other critical systems to be used during the process.
 *
 * An external source should be responsible for calling its nextGeneration()
 * function, which should perform all necessary steps to move from the current
 * generation to the next and then signal the GUI when it is a good and thread-safe
 * time to update.
 */
public interface EvolutionEngine
{
  /**
   * Returns a Statistics object reference which maintains a variety of data
   * about both the engine and the parts of the program that it maintains (especially
   * related to Genomes).
   *
   * @return engine statistics
   */
  Statistics getStatistics();

  /**
   * Returns a reference to the active Log that the engine is using. This can be used
   * to write data to the current log file.
   *
   * @return log system
   */
  Log getLog();

  /**
   * Returns the parallel job system that is being used by the engine. So long as engine.init()
   * has already been called, this should not be null/invalid.
   *
   * @return parallel job system
   */
  ParallelJobSystem getParallelJobSystem();

  /**
   * Gets the population being used by the engine. The population manages
   * the main tribe.
   *
   * @return population or null if none
   */
  Population getPopulation();

  /**
   * Returns the main GUI that was registered with the engine.
   *
   * @return main GUI or null if none
   */
  GUI getGUI();

  /**
   * Returns the target genome that was generated during initialization.
   *
   * @return target genome or null if none
   */
  Image getTarget();

  /**
   * NOTE :: If the population is set to null, the engine will run in a very
   *         limited state. If mainGUI is set to null, the engine will run
   *         and output its current status to the console each generation.
   *
   * Initializes the engine with the given image file (also calls the renderer's
   * init function at a good point during the engine's init sequence).
   *
   * @param cmdArgs (OPTIONAL) list of command line arguments for the engine to use, but this
   *                should be completely optional - just don't pass it a null value (pass in
   *                a length 0 String array)
   * @param stage stage object so the engine can initialize the main GUI
   * @param population population to use to run the simulation
   * @param mainGUI gui to use
   */
  void init(String[] cmdArgs, Stage stage, Population population, GUI mainGUI);

  /**
   * Tells the engine that it needs to begin shutting down. Things that might
   * prevent immediate shutdown include the engine waiting on existing jobs to
   * terminate.
   *
   * Once this is called and everything the engine is waiting on has finished,
   * it will shut down all subsystems it manages and terminate.
   */
  void beginShutdown();

  /**
   * Checks to see if the engine is currently working on shutting down.
   *
   * @return true if it is and false if not
   */
  boolean isEnginePendingShutdown();

  /**
   * Checks to see if the engine has successfully shutdown.
   *
   * @return true if it has shutdown and false if not
   */
  boolean isEngineShutdown();

  /**
   * Checks to see if the engine is paused.
   *
   * @return true if paused, false if not
   */
  boolean isEnginePaused();

  /**
   * Toggles the pause state of the engine.
   *
   * @param value true if paused, false if not
   */
  void togglePause(boolean value);

  /**
   * Gets the number of generations that have passed since the engine was initialized.
   *
   * @return number of generations
   */
  int getGenerationCount();

  /**
   * Gets the number of generations that have occurred on average over the past second
   * @return number of generations on average per second
   */
  double getAverageGenerationsPerSecond();

  /**
   * The engine may have any number of jobs running as part of a generation,
   * and this is an easy way for jobs to let the engine know when they are done
   * so it knows when the generation is complete.
   *
   * @param job job that just completed
   */
  //void notifyEngineOfJobCompletion(Job job);


  /**
   * Hours that have elapsed when the game is running
   * @return number of elapsed hours
   */
  int getHours();

  /**
   * Minutes that have elapsed when the game is running. When minutes reaches 60, variable resets to 0
   * and increments hours by 1.
   * @return number of minutes
   */
  int getMinutes();

  /**
   * Seconds that have elapsed when the game is running. When seconds reaches 60, variable resets to 0
   * and increments minutes by 1.
   * @return number of seconds
   */
  int getSeconds();

  /**
   * The sum of genomes across all tribes
   * @return amount of genomes in the population
   */
  int getPopulationCount();

  /**
   * Increments the population count by one
   */
  void incrementPopulationCount();

  /**
   * Decrements the population count by one
   */
  void decrementPopulationCount();

  /**
   * Creates the next generation of the given population. Each time this is called,
   * the engine will run through all steps to create a new generation and then
   * let the GUI know when it should update.
   */
  void generation();
}
