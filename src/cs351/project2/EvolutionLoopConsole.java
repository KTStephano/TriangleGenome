package cs351.project2;

import cs351.core.Engine.Globals;

/**
 * This performs the same base functionality as EvolutionLoop but provides
 * a way of operating outside of the JavaFX thread without a real GUI.
 *
 * @author Justin
 */
public class EvolutionLoopConsole extends EvolutionLoop
{
  @Override
  protected void loop()
  {
    while (true)
    {
      // Check to see if we need to quit
      checkForShutdownOrPause();

      if (engine.isEngineShutdown()) break;

      // Start the next generation
      engine.generation();
    }
  }

  @Override
  protected void closeApplication()
  {
    // Let the engine know it's time to quit
    engine.beginShutdown();
    // Engine needs to keep updating to eventually close
    while (!engine.isEngineShutdown()) engine.generation();
  }

  /**
   * Initializes the program using the supplied command line arguments.
   * @param cmdArgs command line arguments
   */
  protected void init(String[] cmdArgs)
  {
    engine = new Engine();
    Globals.CONCURRENT_GENOME_LIST.init(engine);
    // null for population and gui for now until those classes are up and running
    engine.init(cmdArgs, null, new GamePopulation(), null);
  }

  /**
   * Entry point.
   * @param args command line arguments
   */
  public static void main(String[] args)
  {
    EvolutionLoopConsole loop = new EvolutionLoopConsole();
    loop.init(args);
    loop.loop();
  }
}
