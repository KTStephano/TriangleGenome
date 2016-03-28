package cs351.project2;

import cs351.core.*;
import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import cs351.utility.Vector2f;
import cs351.utility.Vector4f;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

  private int tribeSize = 8;
  private int genomeSize = 200;
  private double[] xVals = new double [3];
  private double[] yVals = new double [3];
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
  private int selectedTribe;

  private HBox topRowContainer;
  private HBox middleRowContainer;
  private HBox bottomRowContainer;
  private VBox allContainer;

  private Button pauseButton;
  private boolean genomePaused = false;
  private Boolean userWantsToClose = false;

  private Genome currentGenome;
  private Triangle currentTriangle;

  /**
   * This is called by the GUI when deciding which tribe it should draw onto the screen.
   * @return tribe number that the slider has selected
   */
  private int getSelectedTribe()
  {
    return selectedTribe;
  }

  private void setSelectedTribe(int num)
  {
    selectedTribe = num;
  }

  /**
   * This method is called from the popup dialog box when the user has clicked on the "close" button
   */
  private void dialogWindowClose()
  {
    userWantsToClose = true;
  }


  /**
   * Creates a popup dialog box prompting the user for the amount
   * of tribes that they would like to use
   *
   * @param stage Stage of the game
   */
  private void askForTribes(Stage stage)
  {
    try
    {
      // Give choice how many threads user would like to have running
      List<String> choices = new ArrayList<String>();
      choices.add("1");
      choices.add("2");
      choices.add("3");
      choices.add("4");
      choices.add("5");
      choices.add("6");
      choices.add("7");
      choices.add("8");
      choices.add("9");
      choices.add("10");
      choices.add("11");
      choices.add("12");
      choices.add("13");
      choices.add("14");
      choices.add("15");
      choices.add("16");

      ChoiceDialog<String> prompt = new ChoiceDialog<>("--", choices);
      prompt.setContentText("How many tribes would you like to use?");
      prompt.setHeaderText("Triangle Genome Project");
//      prompt.setOnCloseRequest(new EventHandler<DialogEvent>()
//      {
//        /**
//         * Called when the user wants to quit from the choice dialog
//         *
//         * @param event the event which occurred
//         */
//        @Override
//        public void handle(DialogEvent event)
//        {
//          if (event.getEventType().equals(DialogEvent.DIALOG_CLOSE_REQUEST))
//          {
//            System.out.println(">>>> HERE");
//            dialogWindowClose();
//          }
//        }
//      });

      // Makes everything wait until prompt is finished being used
      Optional<String> result = prompt.showAndWait();

      // Assign their answer to variables list
      if (result.get().equals("1"))
        tribeSize = 1;
      else if (result.get().equals("2"))
        tribeSize = 2;
      else if (result.get().equals("3"))
        tribeSize = 3;
      else if (result.get().equals("4"))
        tribeSize = 4;
      else if (result.get().equals("5"))
        tribeSize = 5;
      else if (result.get().equals("6"))
        tribeSize = 6;
      else if (result.get().equals("7"))
        tribeSize = 7;
      else if (result.get().equals("8"))
        tribeSize = 8;
      else if (result.get().equals("9"))
        tribeSize = 9;
      else if (result.get().equals("10"))
        tribeSize = 10;
      else if (result.get().equals("11"))
        tribeSize = 11;
      else if (result.get().equals("12"))
        tribeSize = 12;
      else if (result.get().equals("13"))
        tribeSize = 13;
      else if (result.get().equals("14"))
        tribeSize = 14;
      else if (result.get().equals("15"))
        tribeSize = 15;
      else if (result.get().equals("16"))
        tribeSize = 16;
      else
        tribeSize = 16;
    } catch (Exception e1)
    {
      System.out.println("Exception occured in ChoiceDialog box");
      tribeSize = 16;
    }

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
    // Ask user for amount of tribes that they would like to use
    askForTribes(stage);

    // Error check to see if clicked the close button in the dialog box
    if(hasUserSignaledQuit()) return;

    try
    {
      System.out.println("Amount of Tribes: " + tribeSize);

      BorderPane root = new BorderPane();
      Scene scene = new Scene(root, sceneWidth, sceneHeight);
      stage.setWidth(sceneWidth);
      stage.setHeight(sceneHeight);

      // Two canvas' to hold the Mona Lisa and the triangles
      canvasOriginal = new Canvas(canvasWidth, canvasHeight);
      canvasGenetic = new Canvas(canvasWidth, canvasHeight);

      // Location of canvasOriginal
      canvasOriginal.setTranslateX(canvasStartX);
      canvasOriginal.setTranslateY(canvasStartY);

      // Location of canvasGenetic
      canvasGenetic.setTranslateX(canvasStartX + canvasWidth + canvasMargin);
      canvasGenetic.setTranslateY(canvasStartY);

      gcOriginal = canvasOriginal.getGraphicsContext2D();
      gcGenetic = canvasGenetic.getGraphicsContext2D();

      // Draw Mona Lisa
      gcOriginal.setFill(Color.BLACK);
      gcOriginal.setStroke(Color.BLACK);
      gcOriginal.strokeRect(0, 0, canvasWidth, canvasHeight);
      gcOriginal.fillText("Placeholder for MonaLisa", canvasWidth / 4, canvasHeight / 2);
      InputStream stream = GameWindow.class.getResourceAsStream("images/mona-lisa-cropted-512x413.png");
      Image monaLisa = new Image(stream);
      gcOriginal.drawImage(monaLisa, 0, 0, canvasWidth, canvasHeight);

      // Draw Triangles
      gcGenetic.setFill(Color.BLACK);
      gcGenetic.setStroke(Color.BLACK);
      gcGenetic.strokeRect(0, 0, canvasWidth, canvasHeight);
      gcGenetic.fillText("Placeholder for Triangle Genomes", canvasWidth / 4, canvasHeight / 2);

      // Create the slider(s)
      int tribeLabelWidth = 90;
      Label tribeLabel = new Label("Showing Tribe:");
      tribeLabel.setMinWidth(tribeLabelWidth);
      setSelectedTribe(0);
      genomeListSlider = new Slider(1, getTribes(), 1);
      genomeListSlider.setMajorTickUnit(1.0f);
      genomeListSlider.setMinorTickCount(0);
      genomeListSlider.setBlockIncrement(1.0f);
      genomeListSlider.setMinWidth(canvasWidth * 2 + canvasMargin - tribeLabelWidth);
      genomeListSlider.snapToTicksProperty().set(true);
      genomeListSlider.setShowTickMarks(true);
      genomeListSlider.setShowTickLabels(true);
      genomeListSlider.valueProperty().addListener(new ChangeListener<Number>() {
        public void changed(ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
          setSelectedTribe(new_val.intValue()-1);
        }
      });
      // Create the pause button
      pauseButton = new Button("Pause");
      pauseButton.setMinWidth(70);
      pauseButton.setOnAction(new EventHandler<ActionEvent>()
      {
        /**
         * Toggles the pause/play button
         *
         * @param e Event from the pause button
         */
        @Override
        public void handle(ActionEvent e)
        {
          if (!genomePaused) pauseButton.setText("Resume");
          else pauseButton.setText("Pause");
          genomePaused = !genomePaused;
        }
      });


      // Create containers to hold components
      topRowContainer = new HBox();
      middleRowContainer = new HBox(10);
      bottomRowContainer = new HBox(10);

//      allContainer = new VBox();
//      allContainer.getChildren().addAll(canvasOriginal, canvasGenetic);

      // Add items to top container - Slider
      topRowContainer.setMaxSize(2 * canvasWidth + canvasMargin, canvasHeight);
      topRowContainer.setLayoutX(canvasStartX);
      topRowContainer.setLayoutY(canvasStartY + canvasHeight + canvasMargin);
      topRowContainer.getChildren().addAll(tribeLabel, genomeListSlider);

      // Add items to middle container - Pause Button
      middleRowContainer.setLayoutX(topRowContainer.getLayoutX());
      middleRowContainer.setLayoutY(topRowContainer.getLayoutY() + canvasMargin);
      middleRowContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      middleRowContainer.getChildren().addAll(pauseButton);

      // Add items to bottom container - N/A
      bottomRowContainer.setLayoutX(topRowContainer.getLayoutX());
      bottomRowContainer.setLayoutY(middleRowContainer.getLayoutY() + canvasMargin / 2);
      bottomRowContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      bottomRowContainer.getChildren().addAll();

      // Initialize random triangles and call an update
      //initTriangles();
      //update(engine);

      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      root.getChildren().addAll(canvasOriginal, canvasGenetic, topRowContainer, middleRowContainer, bottomRowContainer);

      stage.setOnCloseRequest(this::windowClosed);
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
    int selectedTribe = getSelectedTribe();
    int rColor, gColor, bColor = 0;
    double alpha = 0;
    int vertexCounter = 0;

    // If we are not debugging, then refresh the canvas at each update
    if (!canvasDebugging) clearGeneticCanvas();

    // For time being, select very first genome
    ArrayList<Tribe> tribes = new ArrayList<>();
    tribes.addAll(engine.getPopulation().getTribes());
    currentGenome = tribes.get(selectedTribe).getGenomes().iterator().next();

    // Loop through each triangle of genome
    for(Triangle currentTriangle: currentGenome.getTriangles())
    {
      vertexCounter = 0;

      // Get Color Values
      float[] color = currentTriangle.getColor();
      rColor = (int)color[0];
      gColor = (int)color[1];
      bColor = (int)color[2];
      alpha = color[3];
      gcGenetic.setFill(Color.rgb(rColor, gColor, bColor, alpha));

      float[] xVertices = currentTriangle.getXVertices();
      float[] yVertices = currentTriangle.getYVertices();
      for(int i = 0; i < currentTriangle.getXVertices().length; i++)
      {
        xVals[i] = xVertices[i];
        yVals[i] = yVertices[i];
      }
      //engine.getLog().log("window", "\n");
      gcGenetic.fillPolygon(xVals, yVals, 3);
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

  /**
   * This allows the Engine to know how many tribes are being used
   * @return tribeSize The amount of tribes
   */
  @Override
  public int getTribes()
  {
    return tribeSize;
  }

  /**
   * TODO: Change return value to match actual selected image width
   * @return Width of the image being drawn
   */
  public int getImageWidth()
  {
    return (int)canvasWidth;
  }

  /**
   * TODO: Change return value to match actual selected image height
   * @return Height of the image being drawn
   */
  public int getImageHeight()
  {
    return (int)canvasHeight;
  }
}
