package cs351.project2;

import cs351.core.*;
import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import cs351.utility.Vector2f;
import cs351.utility.Vector4f;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
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
 */
public class GameWindow implements GUI
{
  private int sceneWidth = 900;
  private int sceneHeight = 600;

  private double canvasWidth = 400;
  private double canvasHeight = 300;

  private double canvasMargin = 40;
  private double canvasStartX = sceneWidth / 2 - canvasWidth - canvasMargin / 2;
  private double canvasStartY = 10;
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
  private boolean selectedNewImage = false;

  private Slider triangleListSlider; // Slider for triangle selection
  private Slider genomeListSlider; // Slider for genome selection
  private Slider tribeListSlider;  // Slider for tribe selection

  private int selectedTriangle;
  private int selectedGenome;
  private int selectedTribe;

  private HBox canvasSubMenus;        // holds dialog and slider containers
  private HBox canvasDialogContainer; // container underneath canvas level
  private VBox canvasSliderContainer; // container underneath canvas level
  private HBox topRowContainer;       // below canvas dialog and slider containers
  private HBox middleRowContainer;    // below top row container
  private HBox bottomRowContainer;    // most bottom row container
  private HBox triangleSliderContainer; // inside canvas slider container
  private HBox genomeSliderContainer;   // inside canvas slider container
  private HBox tribeSliderContainer;    // inside canvas slider container
  private HBox statsContainer;          // Holds all stats boxes
  private VBox stats1;
  private VBox stats2;
  private VBox stats3;

  private Label statsLabel;             // Just states, "Statistics:"
  private Label fitnessLabel;           // shows fitness level of current, selected genome
  private Label fitnessPerSecondLabel;  // displays change of fitness per second of most fit genome in the population
  private Label populationLabel;        // shows total current amount of generation
  private Label generationLabel;        // amount of generations calculated by all tribes since the last population initialization
  private Label generationPerSecondLabel; // (avg of just last second) current generations per second averaged over the past second (not including paused time);
  private Label generationAvgLabel;     // current generations averaged over all non-paused time since population initialization
  private Label hillChildrenLabel;      // amount of hill-climb children
  private Label crossChildrenLabel;     // amount of cross over children
  private Label nonPausedTime;          // non paused time since the most recent population initialization hh:mm:ss
  private AnimationTimer stopwatch;     // Keeps track of all nonPausedTime


  private Button pauseButton;             // pauses game
  private Button fileChooserButton;       // chooses custom image to draw
  private Button tribeButton;             // applies number of tribes
  private Button saveGenome;              // saves the current genome to a file
  private Button writeGenome;             // writes the uploaded genome to the game
  private boolean genomePaused = false;
  private boolean tribeCountChanged = false;
  private Boolean userWantsToClose = false;
  private ChoiceBox pictureSelect;
  private TextField tribeField;
  private double amtButtons = 5; // how many buttons per row
  private double buttonSize = (canvasWidth*2 + canvasMargin) / amtButtons;

  private Genome currentGenome;
  private Triangle currentTriangle;
  private int triangleCounter;

  // Image and Image View Stuff
  private Image targetImage = null;
  private double targetImageWidth = 0;
  private double targetImageHeight = 0;

  // Create array of default pictures
  final private String[] pictureUrls = new String[]{"images/mona-lisa-cropted-512x413.png", "images/poppyfields-512x384.png",
    "images/the_great_wave_off_kanagawa-512x352.png"};

  /**
   *
   * @return int value representing the current genome's fitness level
   */
  private int getGenomeFitness()
  {
    getSelectedGenome();
    return 0;
  }

  /**
   * Updates statistics values at each update
   */
  private void updateStatistics()
  {
    fitnessLabel.setText("Selected Genome Fitness: " + getGenomeFitness());
    fitnessPerSecondLabel.setText("Fitness/Sec: N/A");
    populationLabel.setText("Population: " + null + " genomes");
    generationLabel.setText("Amount of Generations: ");
    generationPerSecondLabel.setText("Generations/Sec: N/A");
    generationAvgLabel.setText("Generations on Average: ");
    hillChildrenLabel.setText("Children from Hill Climbing: ");
    crossChildrenLabel.setText("Children from Crossover: ");
    nonPausedTime.setText("Running: hh:mm:ss");
  }

  /**
   * Used when selecting an image not already preset in the GUI
   * @param imgURL String value of image location
   */
  public void setSelectedImage(String imgURL)
  {
    Image image = new Image("file:" + imgURL);
    targetImage = image;
    resizeTargetImage();
  }

  /**
   * Allows to access the file
   * @param file Location of the file
   */
  private void openFile(File file)
  {
    //System.out.println("file: " + file.toString());
    setSelectedImage(file.toString());
  }

  /**
   * Configures the file chooser. Applies filters to accepted file types.
   * @param fileChooser File chooser stage
   */
  private void configureFileChooser(final FileChooser fileChooser)
  {
    fileChooser.setTitle("Select Image");
    fileChooser.setInitialDirectory(
      new File(System.getProperty("user.home"))
    );
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("JPG", "*.jpg"),
      new FileChooser.ExtensionFilter("PNG", "*.png")
    );
  }


  @Override
  public boolean getHasChangedTribeCount()
  {
    return tribeCountChanged;
  }

  @Override
  /**
   * @return True if user has selected a new image since the last update
   */
  public boolean getHasSelectedNewImage()
  {
    return selectedNewImage;
  }

  /**
   * Resizes the image to fit inside of the canvas. The image file remains the same size however, it drawn smaller
   */
  private void resizeTargetImage()
  {
    Image img = getTargetImage();

    // Get the dimensions no matter what
    double newX = img.getWidth();
    double newY = img.getHeight();

    // When width is way too big
    if(img.getWidth() >= img.getHeight() && img.getWidth() > canvasWidth)
    {
      newX = canvasWidth;
      newY = img.getHeight() / img.getWidth() * newX;
      if(newY > canvasHeight)
      {
        newY = canvasHeight;
        newX = img.getWidth()/img.getHeight() * newY;
      }
    }

    else if(img.getHeight() > img.getWidth() && img.getHeight() > canvasHeight)
    {
      newY = canvasHeight;
      newX = img.getWidth()/img.getHeight() * newY;
      if(newX > canvasWidth)
      {
        newX = canvasWidth;
        newY = img.getHeight() / img.getWidth() * newX;
      }
    }
    setTargetImageWidth(newX);
    setTargetImageHeight(newY);
  }

  /**
   * @param width Width of the image
   */
  private void setTargetImageWidth(double width)
  {
    targetImageWidth = width;
  }

  /**
   * @param height Height of the image
   */
  private void setTargetImageHeight(double height)
  {
    targetImageHeight = height;
  }

  /**
   * @return width of the image
   */
  public double getTargetImageWidth()
  {
    return targetImageWidth;
  }

  /**
   * @return height of the image
   */
  public double getTargetImageHeight()
  {
    return targetImageHeight;
  }

  /**
   * Called when the user selects another image to draw
   *
   * @param imgURL Image to set the target image
   */
  public void setTargetImage(String imgURL)
  {
    InputStream stream = GameWindow.class.getResourceAsStream(imgURL);
    Image image = new Image(stream);
    targetImage = image;
    resizeTargetImage();
  }

  @Override
  /**
   *
   * @return The target image that is supposed to be replicated
   */
  public Image getTargetImage()
  {
    return targetImage;
  }


  /**
   * Called to enable buttons when the game is paused
   */
  private void enableButtons()
  {
    tribeButton.setDisable(false);
    pictureSelect.setDisable(false);
    fileChooserButton.setDisable(false);
    tribeField.setDisable(false);
  }

  /**
   * Called to disable buttons when the game is paused
   */
  private void disableButtons()
  {
    tribeButton.setDisable(true);
    pictureSelect.setDisable(true);
    fileChooserButton.setDisable(true);
    tribeField.setDisable(true);
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
    selectedGenome = num;
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
        tribeSize = 2;
    } catch (Exception e1)
    {
      System.exit(0);
    }

  }
  // Random comment because GitHub won't let me upload
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
      gcOriginal.fillText("Placeholder for MonaLisa", canvasWidth / 4, canvasHeight / 2);
      String defaultImage = pictureUrls[0];
      setTargetImage(defaultImage);
      resizeTargetImage();
      gcOriginal.drawImage(getTargetImage(), 0, 0, getTargetImage().getWidth(), getTargetImage().getHeight(), 0, 0, getTargetImageWidth(), getTargetImageHeight());

      // Draw Triangles
      gcGenetic.setFill(Color.BLACK);
      gcGenetic.setStroke(Color.BLACK);
      gcGenetic.strokeRect(0, 0, canvasWidth, canvasHeight);
      gcGenetic.fillText("Placeholder for Triangle Genomes", canvasWidth / 4, canvasHeight / 2);

      // ************* SLIDERS ************************
      // Create slider - triangle list
      int triangleLabelWidth = 55;
      Label triangleLabel = new Label("Triangle:");
      setSelectedTriangle(199);
      triangleLabel.setMinWidth(triangleLabelWidth);
      triangleListSlider = new Slider(0, 199, getSelectedTriangle());
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
          setSelectedTriangle(new_val.intValue());
        }
      });


      // Create slider - genome list
      int genomeLabelWidth = 55;
      Label genomeLabel = new Label("Genome:");
      genomeLabel.setMinWidth(genomeLabelWidth );
      setSelectedGenome(0);
      genomeListSlider = new Slider(0, 1999, getSelectedGenome());
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
          setSelectedGenome(new_val.intValue());
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

      // *********** BUTTONS ***********************
      // Create file chooser button
      FileChooser fileChooser = new FileChooser();
      fileChooserButton = new Button("Choose an image ...");
      fileChooserButton.setMinWidth(100);
      fileChooserButton.setMaxWidth(150);
      fileChooserButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(final ActionEvent e) {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
              selectedNewImage = true;
              fileChooserButton.setText(file.toString());
              openFile(file);
            }
          }
        });

      // Create the pause button
      pauseButton = new Button("Play");
      pauseButton.setMinWidth(buttonSize);
      genomePaused = true;
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

      // Create text field for tribe selection
      tribeField = new TextField();
      tribeField.setMinWidth(70);
      tribeField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
          if (!newValue.matches("\\d*")) {
            tribeField.setText(newValue.replaceAll("[^\\d]", ""));
          }
        }
      });

      // Create tribe button
      Label tribeButtonLabel = new Label("Input number of Tribes:");
      tribeButtonLabel.setMinWidth(150);
      tribeButton = new Button("OK");
      tribeButton.setMinWidth(70);
      tribeButton.setOnAction(new EventHandler<ActionEvent>()
      {
        @Override
        public void handle(ActionEvent e)
        {
//          String str = tribeField.getCharacters().toString();
//          int num = str.getNumericalValue();
//          setSelectedTribe(Character.getNumericValue(tribeField.getCharacters()));
//          System.out.println("Stuff: " + tribeField.getCharacters());
        }
      });

      // ************ ChoiceBox *********************
      pictureSelect = new ChoiceBox(FXCollections.observableArrayList(
        "MonaLisa - 512x413", "PoppyFields - 512x384", "The Great Wave - 512x352")
      );
      pictureSelect.setMinWidth(50);
      pictureSelect.setTooltip(new Tooltip("Select an image"));
      pictureSelect.getSelectionModel().selectedIndexProperty().addListener(new
        ChangeListener<Number>() {
         public void changed (ObservableValue ov,
          Number value, Number new_value){
           setTargetImage(pictureUrls[new_value.intValue()]);
           selectedNewImage = true;
           //engine.getLog().log("window", "Selected: %s" + pictureUrls[new_value.intValue()]);
      }
    });
      //
      // Create containers to hold components
      canvasSubMenus = new HBox(canvasMargin);              // holds dialog and slider containers

      canvasSliderContainer = new VBox(0);     // holds triangle and genome containers
      triangleSliderContainer = new HBox();     // holds triangle label and triangle slider
      genomeSliderContainer = new HBox();       // holds genome label and genome slider
      tribeSliderContainer = new HBox();        // holds tribe label and tribe slider

      canvasDialogContainer = new HBox(10);     // holds drop down menu and file chooser

      topRowContainer = new HBox(15);             // holds pause button
      middleRowContainer = new HBox(10);        // holds _______
      bottomRowContainer = new HBox(10);        // holds tribe selector

      statsContainer = new HBox(30);        // Holds statistics
      stats1 = new VBox(10);
      stats2 = new VBox(10);
      stats3 = new VBox(10);

      stats1.setMinWidth(2*canvasWidth / 4);
      stats2.setMinWidth(2*canvasWidth / 4);
      stats3.setMinWidth(2*canvasWidth / 4);

      // Create statistics labels
      int labelWidth = 160;
      Label statsLabel = new Label ("Statistics:");
      Label fitnessLabel = new Label("Selected Genome Fitness: ");
      Label fitnessPerSecondLabel = new Label("Fitness/Sec: N/A");
      Label populationLabel = new Label("Population: " + null + " genomes");
      Label generationLabel = new Label("Amount of Generations: ");
      Label generationPerSecondLabel = new Label("Generations/Sec: N/A");
      Label generationAvgLabel = new Label("Generations on Average: ");
      Label hillChildrenLabel = new Label("Children from Hill Climbing: ");
      Label crossChildrenLabel = new Label("Children from Crossover: ");
      Label nonPausedTime = new Label("Running: hh:mm:ss");

      statsLabel.setMinWidth(labelWidth);
      fitnessLabel.setMinWidth(labelWidth);
      fitnessPerSecondLabel.setMinWidth(labelWidth);
      populationLabel.setMinWidth(labelWidth);
      generationLabel.setMinWidth(labelWidth);
      generationPerSecondLabel.setMinWidth(labelWidth);
      generationAvgLabel.setMinWidth(labelWidth);
      hillChildrenLabel.setMinWidth(labelWidth);
      crossChildrenLabel.setMinWidth(labelWidth);
      nonPausedTime.setMinWidth(labelWidth);

      stats1.getChildren().addAll(fitnessLabel, fitnessPerSecondLabel, populationLabel);
      stats2.getChildren().addAll(generationLabel, generationPerSecondLabel, generationAvgLabel);
      stats3.getChildren().addAll(hillChildrenLabel, crossChildrenLabel, nonPausedTime);


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
      canvasDialogContainer.getChildren().addAll(pictureSelect, fileChooserButton);

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

      // Add items to top container
      //topRowContainer.setMaxSize(2 * canvasWidth + canvasMargin, canvasHeight);
      topRowContainer.setLayoutX(canvasStartX);
      topRowContainer.setLayoutY(canvasSubMenus.getLayoutY() + 2 * canvasMargin);
      topRowContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      topRowContainer.getChildren().addAll(pauseButton);

      // Add items to middle container
      middleRowContainer.setLayoutX(topRowContainer.getLayoutX());
      middleRowContainer.setLayoutY(topRowContainer.getLayoutY() + canvasMargin);
      middleRowContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      middleRowContainer.getChildren().addAll(tribeButtonLabel, tribeField, tribeButton);

      // Add items to bottom container
      bottomRowContainer.setLayoutX(topRowContainer.getLayoutX());
      bottomRowContainer.setLayoutY(middleRowContainer.getLayoutY() + canvasMargin / 2);
      bottomRowContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      bottomRowContainer.getChildren().addAll();

      // Add labels to statistics container
      statsContainer.setLayoutX(topRowContainer.getLayoutX());
      statsContainer.setLayoutY(bottomRowContainer.getLayoutY() + canvasMargin / 2);
      statsContainer.setMinWidth(canvasWidth * 2 + canvasMargin);
      statsContainer.getChildren().addAll(statsLabel, stats1, stats2, stats3);


      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      root.getChildren().addAll(canvasOriginal, canvasGenetic, canvasSubMenus,
        topRowContainer, middleRowContainer, bottomRowContainer, statsContainer);

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
  private void clearOriginalCanvas()
  {
    gcOriginal.clearRect(0, 0, canvasWidth, canvasHeight);
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

    // If user selected a new image to draw, draw that image
    if (selectedNewImage)
    {
      Image img = getTargetImage();
      double newWidth = canvasWidth;
      double newHeight = canvasHeight;
      clearOriginalCanvas();
      //gcOriginal.drawImage(getTargetImage(), 0, 0);
      engine.getLog().log("window", "width: %f height: %f\n", getTargetImageWidth(), getTargetImageHeight());
      gcOriginal.drawImage(getTargetImage(), 0, 0, img.getWidth(), img.getHeight(), 0, 0, getTargetImageWidth(), getTargetImageHeight());
      selectedNewImage = false;
    }

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
      manager.setTriangleData(this, currentTriangle);

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
      gcGenetic.fillPolygon(xVals, yVals, 3);

      // Draw only specified amount of triangles
      triangleCounter ++;
      if(triangleCounter > getSelectedTriangle()) break;
    }

//    updateStatistics();
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
   * @return Width of the image being drawn
   */
  public int getImageWidth()
  {
    return (int)targetImageWidth;
  }

  /**
   * @return Height of the image being drawn
   */
  public int getImageHeight()
  {
    return (int)targetImageHeight;
  }
}
