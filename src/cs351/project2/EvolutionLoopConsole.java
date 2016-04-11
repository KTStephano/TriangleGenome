package cs351.project2;

import cs351.core.Engine.Globals;

public class EvolutionLoopConsole extends EvolutionLoop
{
  @Override
  protected void loop()
  {
    while (true)
    {
      // Check to see if we need to quit
      checkForShutdown();

      if (engine.isEngineShutdown()) break;

      // Start the next generation
      engine.generation();
    }
  }

  protected void init(String[] cmdArgs)
  {
    engine = new Engine();
    Globals.CONCURRENT_GENOME_LIST.init(engine);
    // null for population and gui for now until those classes are up and running
    engine.init(cmdArgs, null, new GamePopulation(), null);
  }

  public static void main(String[] args)
  {
    EvolutionLoopConsole loop = new EvolutionLoopConsole();
    loop.init(args);
    loop.loop();
  }
}
