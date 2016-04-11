package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;

/**
 * This is a very simple "GUI" that serves as an wrapper so that the engine can keep
 * working even when it is in console mode. It provides all of the base GUI functionality
 * but instead does not require a valid JavaFX stage.
 */
public class ConsoleGUIWrapper extends GameWindow implements GUI
{
  private final Image IMAGE;

  public ConsoleGUIWrapper(String imageFile, int numTribes)
  {
    // Initialize some stuff from GameWindow
    graphBuilding = true;
    tribeSize = numTribes;
    oldTribeSize = 0;

    InputStream stream = Histogram.class.getResourceAsStream(imageFile);
    if (stream == null) throw new RuntimeException("Unable to load " + imageFile + " as a resource stream");
    IMAGE = new Image(stream);
  }

  /**
   * Initializes the GUI with the given JavaFX stage and the given engine.
   *
   * @param stage  JavaFX stage to use to initialize the main GUI
   * @param engine evolution engine to use for callbacks during init
   */
  @Override
  public void init(Stage stage, EvolutionEngine engine)
  {
    this.engine = engine;
  }

  /**
   * Decides which genomes should be highlighted and renders them. Performs any
   * update work to keep the GUI current.
   *
   * @param engine reference to an evolution engine for callbacks
   */
  @Override
  public void update(EvolutionEngine engine)
  {
    // If we are only running the TG project. We should save our data every
    if(graphBuilding)
    {
      if (mustChangeTribes)
      {
        mustChangeTribes = false;
        tribeSize = oldTribeSize;
        return;
      }
      graphSaveData();
    }
  }

  /**
   * This is an easy way for outside classes to check to see if the user
   * has requested that the application close itself. Other systems can shut
   * themselves down when this returns true.
   *
   * @return true if the user wants to quit and false if not
   */
  @Override
  public boolean hasUserSignaledQuit()
  {
    return false;
  }

  /**
   * Allows the engine to ask if the user has changed the tribe count.
   *
   * @return True if user changed the tribe count since the last iteration
   */
  @Override
  public boolean getHasChangedTribeCount()
  {
    return false;
  }

  /**
   * Allows the evolution engine to ask if there has been a new image selected. This will allow the engine to
   * reinitialize
   * the population. This boolean is turned on by the action listener within the choice box. This is turned to false
   * when the update method occurs from inside the update method of GUI.
   *
   * @return True if user has selected a new image since the last update
   */
  @Override
  public boolean getHasSelectedNewImage()
  {
    return false;
  }

  /**
   * This method is used to check if the user has pressed the pause button in the GUI class
   *
   * @return true if user has pressed pause button
   */
  @Override
  public boolean isGenomePaused()
  {
    return false;
  }

  /**
   * This allows the Engine to know how many tribes are being used
   *
   * @return tribeSize The amount of tribes
   */
  @Override
  public int getTribes()
  {
    return tribeSize;
  }

  /**
   * @return Width of the image being drawn
   */
  @Override
  public int getImageWidth()
  {
    return (int)IMAGE.getWidth();
  }

  /**
   * @return Height of the image being drawn
   */
  @Override
  public int getImageHeight()
  {
    return (int)IMAGE.getHeight();
  }

  /**
   * @return The target image that is supposed to be replicated
   */
  @Override
  public Image getTargetImage()
  {
    return IMAGE;
  }
}
