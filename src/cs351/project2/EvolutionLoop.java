package cs351.project2;

import cs351.core.Engine.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This is where the whole application starts and contains the main loop.
 */
public class EvolutionLoop extends Application
{
  protected EvolutionEngine engine;
  protected GUI game;
  protected boolean jobSystemShutdown = false;
  protected Stage stage;

  @Override
  public void start(Stage stage)
  {
    this.stage = stage;
    stage.setTitle("The Triangle Genome");
    stage.setWidth(350);
    stage.setHeight(350);
    stage.setOnCloseRequest(this::windowClosed); // Just in case the GUI doesn't overwrite this
    engine = new Engine();
    Globals.CONCURRENT_GENOME_LIST.init(engine);
    // null for population and gui for now until those classes are up and running
    engine.init(new String[] {"images/MonaLisa.jpg", "1"}, stage, new GamePopulation(), new GameWindow());
    if (!stage.isShowing()) stage.show();
    loop();
  }

  protected void loop()
  {
    // Main loop
    new AnimationTimer()
    {
      @Override
      public void handle(long now)
      {
        // Check to see if we need to quit
        checkForShutdown();

        if (engine.isEngineShutdown())
        {
          this.stop();
          return;
        }

        // Start the next generation
        engine.generation();
      }
    }.start();
  }

  protected void checkForShutdown()
  {
    if (engine.getGUI() != null)
    {
      if (engine.getGUI().hasUserSignaledQuit() && !engine.isEnginePendingShutdown())
      {
        closeApplication();
        return;
      }
      engine.togglePause(engine.getGUI().isGenomePaused());
    }
  }

  protected void windowClosed(WindowEvent event)
  {
    closeApplication();
  }

  protected void closeApplication()
  {
    // Let the engine know it's time to quit
    engine.beginShutdown();
    // Engine needs to keep updating to eventually close
    while (!engine.isEngineShutdown()) engine.generation();
    // Make sure the stage is closed
    if (stage.isShowing()) stage.close();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}