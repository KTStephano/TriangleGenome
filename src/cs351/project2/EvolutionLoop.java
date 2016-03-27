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
  private EvolutionEngine engine;
  private GUI game;
  private boolean jobSystemShutdown = false;
  private Stage stage;

  @Override
  public void start(Stage stage)
  {
    new Log();
    this.stage = stage;
    stage.setTitle("The Triangle Genome");
    stage.setWidth(350);
    stage.setHeight(350);
    stage.setOnCloseRequest(this::windowClosed); // Just in case the GUI doesn't overwrite this
    engine = new Engine();

    // null for population and gui for now until those classes are up and running
    engine.init(stage, "images/MonaLisa.jpg", new GamePopulation(), new GameWindow());
    if (!stage.isShowing()) stage.show();
    loop();
  }

  private void loop()
  {
    // Main loop
    new AnimationTimer()
    {
      @Override
      public void handle(long now)
      {
        // Check to see if we need to quit
        if (engine.getGUI() != null)
        {
          if (engine.getGUI().hasUserSignaledQuit() && !engine.isEnginePendingShutdown())
          {
            closeApplication();
            return;
          }
          engine.togglePause(engine.getGUI().isGenomePaused());
        }

        if (engine.isEngineShutdown())
        {
          shutdownJobSystem();
          this.stop();
          return;
        }

        // Start the next generation
        engine.generation();
      }
    }.start();
  }

  private void windowClosed(WindowEvent event)
  {
    closeApplication();
  }

  private void closeApplication()
  {
    // Let the engine know it's time to quit
    engine.beginShutdown();
    // Engine needs to keep updating to eventually close
    while (!engine.isEngineShutdown()) engine.generation();
    shutdownJobSystem();
    // Make sure the stage is closed
    if (stage.isShowing()) stage.close();
  }

  private void shutdownJobSystem()
  {
    if (!jobSystemShutdown) Globals.JOB_SYSTEM.destroy();
    jobSystemShutdown = true;
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}