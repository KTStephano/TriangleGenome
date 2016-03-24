package cs351.core.Engine;

import javafx.stage.Stage;

/**
 * The GUI is responsible for managing the user interface and handling
 * all triangle rendering. During an update, the gui should decide
 * which genomes are highlighted for the user to see (Ex: selects the
 * most fit genomes and renders them to the screen).
 */
public interface GUI
{
  /**
   * Initializes the GUI with the given JavaFX stage and the given engine.
   *
   * @param stage JavaFX stage to use to initialize the main GUI
   * @param engine evolution engine to use for callbacks during init
   */
  void init(Stage stage, EvolutionEngine engine);

  /**
   * Decides which genomes should be highlighted and renders them. Performs any
   * update work to keep the GUI current.
   *
   * @param engine reference to an evolution engine for callbacks
   */
  void update(EvolutionEngine engine);

  /**
   * This is an easy way for outside classes to check to see if the user
   * has requested that the application close itself. Other systems can shut
   * themselves down when this returns true.
   *
   * @return true if the user wants to quit and false if not
   */
  boolean hasUserSignaledQuit();

  /**
   * This method is used to check if the user has pressed the pause button in the GUI class
   * @return true if user has pressed pause button
   */
  boolean isGenomePaused();
}
