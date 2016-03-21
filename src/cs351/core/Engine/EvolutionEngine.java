package cs351.core.Engine;

import cs351.core.Genome;
import cs351.utility.Job;
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
  GUI getGui();

  /**
   * Returns the target genome that was generated during initialization.
   *
   * @return target genome or null if none
   */
  Genome getTarget();

  /**
   * NOTE :: If the population is set to null, the engine will run in a very
   *         limited state. If mainGUI is set to null, the engine will run
   *         and output its current status to the console each generation.
   *
   * Initializes the engine with the given image file (also calls the renderer's
   * init function at a good point during the engine's init sequence).
   *
   * @param stage stage object so the engine can initialize the main GUI
   * @param imageFile image file
   * @param population population to use to run the simulation
   * @param mainGUI gui to use
   */
  void init(Stage stage, String imageFile, Population population, GUI mainGUI);

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
   * Gets the number of generations that have passed since the engine was initialized.
   *
   * @return number of generations
   */
  int getGenerationCount();

  /**
   * The engine may have any number of jobs running as part of a generation,
   * and this is an easy way for jobs to let the engine know when they are done
   * so it knows when the generation is complete.
   *
   * @param job job that just completed
   */
  void notifyEngineOfJobCompletion(Job job);

  /**
   * Creates the next generation of the given population. Each time this is called,
   * the engine will run through all steps to create a new generation and then
   * let the GUI know when it should update.
   */
  void generation();
}
