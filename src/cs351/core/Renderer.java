package cs351.core;

import javafx.stage.Stage;

/**
 * The renderer is responsible for managing the main GUI and for rendering
 * all necessary triangles.
 *
 * When the render function is called, the renderer is responsible for deciding
 * which genomes are highlighted and rendered on the main GUI.
 */
public interface Renderer
{
  /**
   * Initializes the renderer with the given JavaFX stage and the given engine.
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
  void render(EvolutionEngine engine);
}
