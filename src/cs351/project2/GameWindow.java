package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;

import java.io.InputStream;
import java.util.Random;

import cs351.utility.Vector2f;
import cs351.utility.Vector4f;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;

/**
 * GameWindow creates the GUI. The GUI is responsible for showing two images: the original image and the
 * image created by the Genetic Algorithm.
 *
 */
public class GameWindow implements GUI
{
  private int sceneWidth = 800;
  private int sceneHeight = 500;

  private double canvasWidth = 300;
  private double canvasHeight = 300;

  private double canvasMargin = 40;

  private double canvasStartX = sceneWidth / 2 - canvasWidth - canvasMargin / 2;
  private double canvasStartY = sceneHeight / 2 - canvasHeight / 2 - canvasHeight / 4;

  private int genomeSize = 200;
  Vector2f[] vector1List = new Vector2f[genomeSize];
  Vector2f[] vector2List = new Vector2f[genomeSize];
  Vector2f[] vector3List = new Vector2f[genomeSize];
  Vector4f[] vectorColorList = new Vector4f[genomeSize];
  //   VectorPoints[] vectorTestList = new VectorPoints[genomeSize];

  private Random randNum = new Random();

  private Canvas canvasOriginal;
  private Canvas canvasGenetic;

  private GraphicsContext gcOriginal;
  private GraphicsContext gcGenetic;

  private boolean canvasDebugging = false;

  private Slider genomeListSlider;

  private HBox sliderContainer;
  private HBox middleRowContainer;
  private HBox bottomRowContainer;
  private HBox optionsContainer;
  private VBox allContainer;

  private Button pauseButton;
  private boolean genomePaused = false;
  private Boolean userWantsToClose = false;


  /**
   * Initializes the GUI with the given JavaFX stage and the given engine.
   *
   * @param stage  JavaFX stage to use to initialize the main GUI
   * @param engine evolution engine to use for callbacks during init
   */
  @Override
  public void init(Stage stage, EvolutionEngine engine)
  {

    try
    {
      BorderPane root = new BorderPane();
      Scene scene = new Scene(root, sceneWidth, sceneHeight);
      stage.setWidth(sceneWidth);
      stage.setHeight(sceneHeight);

      // Two canvas' to hold the Mona Lisa and the triangles
      canvasOriginal = new Canvas(canvasWidth, canvasHeight);
      canvasGenetic = new Canvas(canvasWidth, canvasHeight);

      canvasOriginal.setTranslateX(canvasStartX);
      canvasOriginal.setTranslateY(canvasStartY);

      canvasGenetic.setTranslateX(canvasStartX + canvasWidth + canvasMargin);
      canvasGenetic.setTranslateY(canvasStartY);

      gcOriginal = canvasOriginal.getGraphicsContext2D();
      gcGenetic = canvasGenetic.getGraphicsContext2D();

      // Draw Mona Lisa
      gcOriginal.setFill(Color.BLACK);
      gcOriginal.setStroke(Color.BLACK);
      gcOriginal.strokeRect(0, 0, canvasWidth, canvasHeight);
      gcOriginal.fillText("Placeholder for MonaLisa", canvasWidth / 4, canvasHeight / 2);
      InputStream stream = GameWindow.class.getResourceAsStream("images/MonaLisa.jpg");
      Image monaLisa = new Image(stream);
      gcOriginal.drawImage(monaLisa, 0, 0, canvasWidth, canvasHeight);

      // Draw Triangles
      gcGenetic.setFill(Color.BLACK);
      gcGenetic.setStroke(Color.BLACK);
      gcGenetic.strokeRect(0, 0, canvasWidth, canvasHeight);
      gcGenetic.fillText("Placeholder for Triangle Genomes", canvasWidth / 4, canvasHeight / 2);

      // Create the slider(s)
      genomeListSlider = new Slider(1, 8, 1);
      genomeListSlider.setMajorTickUnit(1.0f);
      genomeListSlider.setMinorTickCount(0);
      genomeListSlider.setBlockIncrement(1.0f);
      genomeListSlider.setMinWidth(200);
      genomeListSlider.snapToTicksProperty().set(true);

      genomeListSlider.setShowTickMarks(true);
      genomeListSlider.setShowTickLabels(true);

      // Create the pause button
      pauseButton = new Button("Pause");
      pauseButton.setMinWidth(70);
      pauseButton.setOnAction(new EventHandler<ActionEvent>()
      {
        @Override
        public void handle(ActionEvent e)
        {
          if (!genomePaused) pauseButton.setText("Resume");
          else pauseButton.setText("Pause");
          genomePaused = !genomePaused;
        }
      });


      // Create containers to hold components
      optionsContainer = new HBox(50);

      allContainer = new VBox();
      allContainer.getChildren().addAll(canvasOriginal, canvasGenetic);

      // Add items to containers
      optionsContainer.setMaxSize(2 * canvasWidth + canvasMargin, canvasHeight);
      optionsContainer.setLayoutX(canvasStartX);
      optionsContainer.setLayoutY(canvasStartY + canvasHeight + canvasMargin);
      optionsContainer.getChildren().addAll(genomeListSlider, pauseButton);

      initTriangles();
      update(engine);

      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      root.getChildren().addAll(canvasOriginal, canvasGenetic, optionsContainer);

      stage.setOnCloseRequest(this::windowClosed); // Just in case the GUI doesn't overwrite this
      stage.setTitle("Triangle Genome Project");
      stage.setScene(scene);
      stage.show();
    } catch (Exception e)
    {
      e.printStackTrace();
    }

  }

  /*
  * PLACEHOLDER METHOD
  *
  * This method is to create a list of triangles that the gui would normally
  * use when updating itself
  */
  private void initTriangles()
  {

    int triangleSize = 80;
    int red, green, blue = 0;
    double x1, x2, x3, y1, y2, y3, alpha = 0;

    for (int i = 0; i < vector1List.length; i++)
    {
      // Generate random points
      x1 = (double) randNum.nextInt((int) canvasHeight);
      y1 = (double) randNum.nextInt((int) canvasHeight);

      x2 = x1 + (double) randNum.nextInt(triangleSize);
      y2 = y1 + (double) randNum.nextInt(triangleSize);

      x3 = x1 + (double) randNum.nextInt(triangleSize);
      y3 = y1 + (double) randNum.nextInt(triangleSize);

      // Generate RGB values
      red = randNum.nextInt(255);
      green = randNum.nextInt(255);
      blue = randNum.nextInt(255);

      System.out.printf("red: %d \t green: %d \t blue: %d\n", red, green, blue);

      // Generate Transparency Value
      alpha = randNum.nextDouble();

      vector1List[i] = new Vector2f(x1, y1);
      vector2List[i] = new Vector2f(x2, y2);
      vector3List[i] = new Vector2f(x3, y3);

      vectorColorList[i] = new Vector4f(red, green, blue, alpha);

      //vectorTestList[i] = new VectorPoints(x1, y1, x2, y2, x3, y3, red, green, blue, alpha);
    }
  }

  /*
   * Clears the canvas holding genomes. This method is to be used at
   * each update of the canvas
   */
  private void clearGeneticCanvas()
  {
    gcGenetic.clearRect(0, 0, canvasWidth, canvasHeight);
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
    // If we are not debugging, then refresh the canvas at each update
    if (!canvasDebugging) clearGeneticCanvas();

    int rColor, gColor, bColor = 0;
    double alpha = 0;
    double x1, x2, x3, y1, y2, y3 = 0;

    // Loop through selected array and draw the triangles
    for (int i = 0; i < genomeSize; i++)
    {
      // Select and set color
      rColor = (int) vectorColorList[i].getX();
      gColor = (int) vectorColorList[i].getY();
      bColor = (int) vectorColorList[i].getZ();
      alpha = vectorColorList[i].getW();

      gcGenetic.setFill(Color.rgb(rColor, gColor, bColor, alpha));
//      gcGenetic.setFill(Color.BLACK);

      // Get triangle points
      x1 = vector1List[i].getX();
      x2 = vector2List[i].getX();
      x3 = vector3List[i].getX();

      //System.out.printf("x1: %.1f  x2: %.1f  x3: %.1f\n", x1, x2, x3);

      y1 = vector1List[i].getY();
      y2 = vector2List[i].getY();
      y3 = vector3List[i].getY();

      gcGenetic.fillPolygon(new double[]{x1, x2, x3}, new double[]{y1, y2, y3}, 3);
    }
  }

  private void windowClosed(WindowEvent event)
  {
    userWantsToClose = true;
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
    return userWantsToClose;
  }

  /**
   * This method is used to check if the user has pressed the pause button in the GUI class
   *
   * @return true if user has pressed pause button
   */
  @Override
  public boolean isGenomePaused()
  {
    return genomePaused;
  }
}