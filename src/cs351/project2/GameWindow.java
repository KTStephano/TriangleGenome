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
  private int sceneWidth = 900;
  private int sceneHeight = 600;

  private double canvasWidth = 400;
  private double canvasHeight = 300;

  private double canvasMargin = 40;
  private double canvasStartX = sceneWidth / 2 - canvasWidth - canvasMargin / 2;
  private double canvasStartY = canvasMargin;
  //private double canvasStartY = sceneHeight / 2 - canvasHeight / 2 - canvasHeight / 4;

  private int tribeSize = 8;
  private int genomeSize = 200;
  private double[] xVals = new double [3];
  private double[] yVals = new double [3];
  Vector2f[] vector1List = new Vector2f[genomeSize];
  Vector2f[] vector2List = new Vector2f[genomeSize];
  Vector2f[] vector3List = new Vector2f[genomeSize];
  Vector4f[] vectorColorList = new Vector4f[genomeSize];

  private Random randNum = new Random();

  private Canvas canvasOriginal;
  private Canvas canvasGenetic;

  private GraphicsContext gcOriginal;
  private GraphicsContext gcGenetic;

  private boolean canvasDebugging = false;

  private Slider triangleListSlider; // Slider for triangle selection
  private Slider genomeListSlider; // Slider for genome selection
  private Slider tribeListSlider;  // Slider for tribe selection

  private int selectedTriangle;
  private int selectedGenome;
  private int selectedTribe;

  private HBox canvasSubMenus;        // holds dialog and slider containers
  private VBox canvasDialogContainer; // container underneath canvas level
  private VBox canvasSliderContainer; // container underneath canvas level
  private HBox topRowContainer;       // below canvas dialog and slider containers
  private HBox middleRowContainer;    // below top row container
  private HBox bottomRowContainer;    // most bottom row container
  private HBox triangleSliderContainer; // inside canvas slider container
  private HBox genomeSliderContainer;   // inside canvas slider container
  private HBox tribeSliderContainer;    // inside canvas slider container


  private Button pauseButton;
  private Button testButton;
  private boolean genomePaused = false;
  private Boolean userWantsToClose = false;

  private Genome currentGenome;
  private Triangle currentTriangle;
  private int triangleCounter;

  /**
   * Called to enable buttons when the game is paused
   */
  private void enableButtons()
  {
    testButton.setDisable(false);
  }

  /**
   * Called to disable buttons when the game is paused
   */
  private void disableButtons()
  {
    testButton.setDisable(true);
  }

  /**
   * This is called by the GUI when deciding how many triangles to draw on the screen
   * @return which genome to show on the screen
   */
  private int getSelectedGenome()
  {
    return selectedGenome;
  }

  /**
   *
   * @param num Index of the genome selected
   */
  private void setSelectedGenome(int num)
  {
    selectedGenome= num;
  }

  /**
   * This is called by the GUI when deciding how many triangles to draw on the screen
   * @return amount of triangles to show on the screen
   */
  private int getSelectedTriangle()
  {
    return selectedTriangle;
  }

  /**
   *
   * @param num Amount of triangles to show
   */
  private void setSelectedTriangle(int num)
  {
    selectedTriangle = num;
  }

  /**
   * This is called by the GUI when deciding which tribe it should draw onto the screen.
   * @return tribe number that the slider has selected
   */
  private int getSelectedTribe()
  {
    return selectedTribe;
  }

  /**
   *
   * @param num Label of the selected tribe
   */
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
        tribeSize = 4;
    } catch (Exception e1)
    {
      System.exit(0);
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

      // Create slider - triangle list
      int triangleLabelWidth = 55;
      Label triangleLabel = new Label("Triangle:");
      triangleLabel.setMinWidth(triangleLabelWidth);
      triangleListSlider = new Slider(1, 200, 200);
      triangleListSlider.setMinWidth(canvasWidth - triangleLabelWidth);
      triangleListSlider.setMajorTickUnit(1);
      triangleListSlider.setMinorTickCount(0);
      triangleListSlider.setBlockIncrement(1);
      triangleListSlider.snapToTicksProperty().set(true);
      triangleListSlider.setShowTickMarks(false);
      triangleListSlider.setShowTickLabels(false);
      triangleListSlider.valueProperty().addListener(new ChangeListener<Number>()
      {
        public void changed(ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val)
        {
          setSelectedTriangle(new_val.intValue()-1);
        }
      });


      // Create slider - genome list
      int genomeLabelWidth = 55;
      Label genomeLabel = new Label("Genome:");
      genomeLabel.setMinWidth(genomeLabelWidth );
      genomeListSlider = new Slider(1, 2000, 0);
      genomeListSlider.setMinWidth(canvasWidth - genomeLabelWidth);
      genomeListSlider.setMajorTickUnit(1);
      genomeListSlider.setMinorTickCount(0);
      genomeListSlider.setBlockIncrement(1);
      genomeListSlider.snapToTicksProperty().set(true);
      genomeListSlider.setShowTickMarks(false);
      genomeListSlider.setShowTickLabels(false);
      genomeListSlider.valueProperty().addListener(new ChangeListener<Number>()
      {
        public void changed(ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val)
        {
          setSelectedGenome(new_val.intValue() - 1);
        }
      });

      // Create slider - tribe list
      int tribeLabelWidth = 55;
      double sliderNumBuffer = 0.1;  // how close the track has to be before switching
      Label tribeLabel = new Label("Tribe:");
      tribeLabel.setMinWidth(tribeLabelWidth);
      setSelectedTribe(0);
      tribeListSlider = new Slider(1, getTribes(), 1);
      tribeListSlider.setMajorTickUnit(1d);
      tribeListSlider.setMinorTickCount(0);
      tribeListSlider.setBlockIncrement(1d);
      tribeListSlider.setMinWidth(canvasWidth - tribeLabelWidth);
      tribeListSlider.snapToTicksProperty().set(true);
      tribeListSlider.setShowTickMarks(true);
      tribeListSlider.setShowTickLabels(true);
      tribeListSlider.valueProperty().addListener(new ChangeListener<Number>()
      {
        public void changed(ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val)
        {
          if (new_val.doubleValue() % new_val.intValue() <= sliderNumBuffer) setSelectedTribe(new_val.intValue() - 1);
        }
      });
      // If only 1 tribe is selected, do not show the tick marks
      if(getTribes() == 1)
      {
        tribeListSlider.setShowTickMarks(false);
        tribeListSlider.setShowTickLabels(false);
      }

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
          if (!genomePaused)
          {
            pauseButton.setText("Resume");
            enableButtons();
          }
          else
          {
            pauseButton.setText("Pause");
            disableButtons();
          }
          genomePaused = !genomePaused;
        }
      });

      // Create test button
      testButton = new Button("TEST");
      testButton.setMinWidth(70);
      testButton.setDisable(true);


      // Create containers to hold components
      canvasSubMenus = new HBox(canvasMargin);              // holds dialog and slider containers

      canvasSliderContainer = new VBox(0);     // holds triangle and genome containers
      triangleSliderContainer = new HBox();     // holds triangle label and triangle slider
      genomeSliderContainer = new HBox();       // holds genome label and genome slider
      tribeSliderContainer = new HBox();        // holds tribe label and tribe slider

      canvasDialogContainer = new VBox(10);     // holds drop down menu and file chooser

      topRowContainer = new HBox(15);             // holds tribe label and tribe slider
      middleRowContainer = new HBox(10);        // holds pause button
      bottomRowContainer = new HBox(10);        // holds ________

      // set up sub menu container
      canvasSubMenus.setMaxWidth(2 * canvasWidth + canvasMargin);
      canvasSubMenus.setMaxHeight(canvasHeight);
      canvasSubMenus.setLayoutX(canvasStartX);
      canvasSubMenus.setLayoutY(canvasStartY + canvasHeight + canvasMargin / 3);
      canvasSubMenus.getChildren().addAll(canvasDialogContainer, canvasSliderContainer);

      // set up dialog container (VBox)
      canvasDialogContainer.setMinWidth(canvasWidth);
      canvasDialogContainer.setMaxWidth(canvasWidth);
      canvasDialogContainer.setMaxHeight(canvasHeight);

      // set up slider container (VBox)
      canvasSliderContainer.setMinWidth(canvasWidth);
      canvasSliderContainer.setMaxHeight(canvasHeight);
      canvasSliderContainer.getChildren().addAll(triangleSliderContainer, genomeSliderContainer, tribeSliderContainer);

      // set up triangle container (HBox)
      triangleSliderContainer.setMaxWidth(canvasWidth);
      triangleSliderContainer.getChildren().addAll(triangleLabel, triangleListSlider);

      // set up genome container (HBox)
      genomeSliderContainer.setMaxWidth(canvasWidth);
      genomeSliderContainer.getChildren().addAll(genomeLabel, genomeListSlider);

      // set up tribe container (HBox)
      tribeSliderContainer.setMaxWidth(canvasWidth);
      tribeSliderContainer.getChildren().addAll(tribeLabel, tribeListSlider);

      // Add items to top container - Slider
      //topRowContainer.setMaxSize(2 * canvasWidth + canvasMargin, canvasHeight);
      topRowContainer.setLayoutX(canvasStartX);
      topRowContainer.setLayoutY(canvasSubMenus.getLayoutY() + 2*canvasMargin );
      topRowContainer.setMinWidth(canvasWidth*2 + canvasMargin);
      topRowContainer.getChildren().addAll(pauseButton, testButton);

      // Add items to middle container - Pause Button
      middleRowContainer.setLayoutX(topRowContainer.getLayoutX());
      middleRowContainer.setLayoutY(topRowContainer.getLayoutY() + canvasMargin);
      middleRowContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      middleRowContainer.getChildren().addAll();

      // Add items to bottom container - N/A
      bottomRowContainer.setLayoutX(topRowContainer.getLayoutX());
      bottomRowContainer.setLayoutY(middleRowContainer.getLayoutY() + canvasMargin / 2);
      bottomRowContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      bottomRowContainer.getChildren().addAll();

      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      root.getChildren().addAll(canvasOriginal, canvasGenetic, canvasSubMenus,
        topRowContainer, middleRowContainer, bottomRowContainer);

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

    ArrayList<Genome> genomes = new ArrayList<>();
    genomes.addAll(tribes.get(selectedTribe).getGenomes());
    Genome selectedGenome = genomes.get(getSelectedGenome());

    // Loop through each triangle of genome
    TriangleManager manager = new TriangleManager(); // need this to interpret the triangle data

    triangleCounter = 0;
    for(float[] currentTriangle: selectedGenome.getTriangles())
    {
      vertexCounter = 0;

      // Set the manager's triangle data
      manager.setTriangleData(currentTriangle);

      // Get Color Values
      float[] color = manager.getColor();
      rColor = (int)color[0];
      gColor = (int)color[1];
      bColor = (int)color[2];
      alpha = color[3];
      gcGenetic.setFill(Color.rgb(rColor, gColor, bColor, alpha));

      float[] xVertices = manager.getXCoordinates();
      float[] yVertices = manager.getYCoordinates();
      for(int i = 0; i < xVertices.length; i++)
      {
        xVals[i] = xVertices[i];
        yVals[i] = yVertices[i];
      }
      //engine.getLog().log("window", "\n");
      gcGenetic.fillPolygon(xVals, yVals, 3);

      // Draw only specified amount of triangles
      triangleCounter ++;
      if(triangleCounter == getSelectedTriangle()) break;
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
