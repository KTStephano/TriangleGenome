package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.Globals;
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
  private boolean jobSystemShutdown = false;

  @Override
  public void start(Stage stage)
  {
    stage.setTitle("The Triangle Genome");
    stage.setWidth(350);
    stage.setHeight(350);
    stage.setOnCloseRequest(this::windowClosed); // Just in case the GUI doesn't overwrite this
    engine = new Engine();
    engine.init(stage, "images/MonaLisa.jpg", null, null);
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
          if (engine.getGUI().hasUserSignaledQuit() && !engine.isEnginePendingShutdown()) engine.beginShutdown();
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
    // Let the engine know it's time to quit
    engine.beginShutdown();
    // Engine needs to keep updating to eventually close
    while (!engine.isEngineShutdown()) engine.generation();
    shutdownJobSystem();
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