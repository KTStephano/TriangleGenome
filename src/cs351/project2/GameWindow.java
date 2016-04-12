package cs351.project2;

import cs351.core.*;
import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import java.awt.*;
import java.io.*;
import java.security.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import cs351.utility.Vector2f;
import cs351.utility.Vector4f;
import jxl.*;                                      // Used to create Excel spreadsheet
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * GameWindow creates the GUI. The GUI is responsible for showing two images: the original image and the
 * image created by the Genetic Algorithm.
 *
 * @author George
 */
public class GameWindow implements GUI
{
  protected boolean graphBuilding = false; // <----- MAKE TRUE for graph building!!!
  protected EvolutionEngine engine;
  protected boolean mustChangeTribes = false;
  protected int oldTribeSize;
  protected int tribeSize = 1; // <---- CHANGE HERE

  private int updateCount = 0;
  private int run = 1; // for graph building re-write issues
  private boolean updateOkay = true;
  private int imageNum = 1; // for graph building re-write issues  <---- CHANGE HERE
  private int timeLimit = 10; // written in minutes, is used for graph building  <----LEAVE AT 10 MINUTES
  private int imageSelect = 11; // 2 (MonaLisa), 5 (PoppyFields), 8 (Oceans), 11 (Piet Mondrian), 17 (Petronas Towers) <---- CHANGE FOR IMAGES
  private ArrayList<String> graphInformation = new ArrayList<>();
  private ArrayList<String> graphInformationLabels = new ArrayList<>();
  private ArrayList<Double> graphInformationNumbers = new ArrayList<>();
  private ArrayList<Double> graphInformationAverages = new ArrayList<>();

  private int startingTribes = 1;
  private int sceneWidth = 1100;
  private int sceneHeight = 720;

  private double canvasWidth = 512;
  private double canvasHeight = 413;

  private double canvasMargin = 40;
  private double canvasStartX = sceneWidth / 2 - canvasWidth - canvasMargin / 2;
  private double canvasStartY = 10;

  private int genomeSize = 200;
  private double[] xVals = new double [3];
  private double[] yVals = new double [3];

  private Color backgroundColor = Color.BLACK;

  private Random randNum = new Random();

  private Canvas canvasOriginal;      // Holds the target image
  private Canvas canvasGenetic;       // Holds the triangles painting

  private GraphicsContext gcOriginal; // Draws the target image
  private GraphicsContext gcGenetic;  // Draws the triangles

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
  private Button nextButton;              // Runs the next generation
  private Button fileChooserButton;       // chooses custom image to draw
  private Button tribeButton;             // applies number of tribes
  private Button saveGenome;              // saves the current genome to a file
  private Button loadGenome;              // writes the uploaded genome to the game
  private FileChooser fileChooser;
  private FileChooser loadGenomeChooser;
  private boolean genomePaused = false;
  private boolean nextGen = false;
  private boolean tribeCountChanged = false;
  private boolean hasSecondElapsed = false; // used for statistics dependent on time
  private Boolean userWantsToClose = false;
  private ChoiceBox pictureSelect;
  private TextField tribeField;
  private double amtButtons = 5; // how many buttons per row
  private double buttonSize = (canvasWidth*2 + canvasMargin) / amtButtons - 20;

  private Genome currentGenome = new Genome();
  private Triangle currentTriangle;
  private int triangleCounter;
  private NumberFormat formatter = new DecimalFormat("#0.0000");
  private NumberFormat formatterFit = new DecimalFormat("#0.00000000");
  private NumberFormat formatterTime = new DecimalFormat("#00");
  private NumberFormat formatterGraph = new DecimalFormat("#0.0000");
  private NumberFormat tinyFormat = new DecimalFormat("#0.0");
  private Stage stage;

  // Image and Image View Stuff
  private Image targetImage = null;
  private double targetImageWidth = 0;
  private double targetImageHeight = 0;
  private double targetImageWidthCropped = 0;
  private double targetImageHeightCropped = 0;
  private ColorSelector colorSelector;

  // TableView and Stuff
  private Button tableButton;               // shows table of the selected genome
  private TableView <Genes> tableView;

  // Create array of default pictures
  final private String[] pictureUrls = new String[]{"images/mona-lisa-cropted-100x81.png", "images/mona-lisa-cropted-250x202.png",
    "images/mona-lisa-cropted-512x413.png", "images/poppyfields-100x75.png", "images/poppyfields-250x188.png", "images/poppyfields-512x384.png",
    "images/the_great_wave_off_kanagawa-100x69.png", "images/the_great_wave_off_kanagawa-250x172.png",
    "images/the_great_wave_off_kanagawa-512x352.png", "images/Piet_Mondrian-100x75.png", "images/Piet_Mondrian-250x188.png",
    "images/Piet_Mondrian-512x385.png", "images/trianglePic-100x80.png", "images/trianglePic-250x201.png", "images/trianglePic-512x412.png",
    "images/MonaClose - 200x161.png", "images/TriangleOcean.png", "images/petronas_towers-512x352.png", "images/petronas_towers-250x172.png" };


  /**
   * GeneTablePopup is used to show the genes of the selected Genome. It opens a second window, which contains
   * and displays the genes.
   */
  class GeneTablePopup extends Stage
  {
    VBox vBox = new VBox(10);
    private final ObservableList<Genes> data = FXCollections.observableArrayList(); // Holds genome values

    /**
     * Fills data array with information from the selected genome
     */
    private void populateData()
    {
      Collection<float[]> triangles = new ArrayList<>();
      triangles = currentGenome.getTriangles();

      // Loop through triangle list
      for(float[] triangle: triangles)
      {
        data.add(new Genes(triangle[0], triangle[1], triangle[2], triangle[3], triangle[4], triangle[5], triangle[6],
          triangle[7], triangle[8], triangle[9]));
      }
    }

    /**
     * CONSTRUCTOR
     * Constructs the window followed by placing a table within the window to display a gene table. It will create
     * and define the different columns. It will then save the data from the current genome and set it to the table.
     */
    GeneTablePopup()
    {
      // Create Table
      tableView = new TableView<Genes>();
      tableView.setEditable(true);

      // Create table Columns
      TableColumn x1 = new TableColumn("X1");
      TableColumn y1 = new TableColumn("Y1");
      TableColumn x2 = new TableColumn("X2");
      TableColumn y2 = new TableColumn("Y2");
      TableColumn x3 = new TableColumn("X3");
      TableColumn y3 = new TableColumn("Y3");
      TableColumn red = new TableColumn("Red");
      TableColumn green = new TableColumn("Green");
      TableColumn blue = new TableColumn("Blue");
      TableColumn alpha = new TableColumn("Alpha");

      // Set up columns
      x1.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueX1"));
      y1.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueY1"));
      x2.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueX2"));
      y2.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueY2"));
      x3.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueX3"));
      y3.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueY3"));
      red.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueRed"));
      green.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueGreen"));
      blue.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueBlue"));
      alpha.setCellValueFactory(new PropertyValueFactory<Genes, Float>("geneValueAlpha"));

      // Populate data with information from genome
      populateData();

      // Add values to their containers
      tableView.getColumns().addAll(x1, y1, x2, y2, x3, y3, red, green, blue, alpha);
      tableView.setItems(data);
      vBox.getChildren().addAll(tableView);

      this.setTitle("Selected Genome - Gene Table");
      this.setScene(new Scene(vBox, 400, 400));
      this.show();
    }
  }

  /**
   * Genes is an internal class that is used and regulated by the "Show Genome Table" button. This allows properties to
   * be shown in the table as well as having the capability of being changed
   */
  public static class Genes {

    private final SimpleFloatProperty geneValueX1;
    private final SimpleFloatProperty geneValueY1;
    private final SimpleFloatProperty geneValueX2;
    private final SimpleFloatProperty geneValueY2;
    private final SimpleFloatProperty geneValueX3;
    private final SimpleFloatProperty geneValueY3;
    private final SimpleFloatProperty geneValueRed;
    private final SimpleFloatProperty geneValueGreen;
    private final SimpleFloatProperty geneValueBlue;
    private final SimpleFloatProperty geneValueAlpha;


    private Genes(Float x1,Float y1,Float x2,Float y2,Float x3,Float y3,Float red,Float green,Float blue,Float alpha) {
      this.geneValueX1 = new SimpleFloatProperty(x1);
      this.geneValueY1 = new SimpleFloatProperty(y1);
      this.geneValueX2 = new SimpleFloatProperty(x2);
      this.geneValueY2 = new SimpleFloatProperty(y2);
      this.geneValueX3 = new SimpleFloatProperty(x3);
      this.geneValueY3 = new SimpleFloatProperty(y3);
      this.geneValueRed = new SimpleFloatProperty(red);
      this.geneValueGreen = new SimpleFloatProperty(green);
      this.geneValueBlue = new SimpleFloatProperty(blue);
      this.geneValueAlpha = new SimpleFloatProperty(alpha);
    }

    /**
     * Gets and sets per method name
     * @return the triangle's first x value
     */
    public Float getGeneValueX1() {
      return geneValueX1.get();
    }
    public void setGeneValueX1(Float value) {
      geneValueX1.set(value);
    }

    public Float getGeneValueY1() {
      return geneValueY1.get();
    }
    public void setGeneValueY1(Float value) {
      geneValueY1.set(value);
    }

    public Float getGeneValueX2() {
      return geneValueX2.get();
    }
    public void setGeneValueX2(Float value) {
      geneValueX2.set(value);
    }

    public Float getGeneValueY2() {
      return geneValueY2.get();
    }
    public void setGeneValueY2(Float value) {
      geneValueY2.set(value);
    }

    public Float getGeneValueX3() {
      return geneValueX3.get();
    }
    public void setGeneValueX3(Float value) {
      geneValueX3.set(value);
    }

    public Float getGeneValueY3() {
      return geneValueY3.get();
    }
    public void setGeneValueY3(Float value) {
      geneValueY3.set(value);
    }

    public Float getGeneValueRed() {
      return geneValueRed.get();
    }
    public void setGeneValueRed(Float value) {
      geneValueRed.set(value);
    }

    public Float getGeneValueGreen() {
      return geneValueGreen.get();
    }
    public void setGeneValueGreen(Float value) {
      geneValueGreen.set(value);
    }

    public Float getGeneValueBlue() {
      return geneValueBlue.get();
    }
    public void setGeneValueBlue(Float value) {
      geneValueBlue.set(value);
    }

    public Float getGeneValueAlpha() {
      return geneValueAlpha.get();
    }
    public void setGeneValueAlpha(Float value) {
      geneValueAlpha.set(value);
    }
  }


  protected void graphWriteData(File file)
  {
    // Error check for null pointer exceptions, if true then return from the method
    if (file == null) return;

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
    {
      for(String str: graphInformation)
      {
        bw.write(str);
        bw.write("\n");
      }
      bw.close();
    } catch (IOException e)
    {
      System.out.println("Well ... I guess something broke");
    }
    try
    {
      WritableWorkbook workbook = Workbook.createWorkbook(new File("Thread" + oldTribeSize + "-Image" + imageNum +"-" + "Run" + run +".xls"));
      WritableSheet sheet = workbook.createSheet("First Sheet", 0);
      int labelCounter = 0;
        for(String str: graphInformationLabels)
        {
          jxl.write.Label label = new jxl.write.Label(0, labelCounter,str);
          jxl.write.Number number = new jxl.write.Number(2, labelCounter, graphInformationNumbers.get(labelCounter));
          sheet.addCell(label);
          sheet.addCell(number);
          labelCounter ++;
        }
      labelCounter ++;
      int labelUpdateCounter = 1;
        for(Double dbl: graphInformationAverages)
        {
          jxl.write.Label label = new jxl.write.Label(0, labelCounter,"update " + labelUpdateCounter);
          jxl.write.Number number = new jxl.write.Number(2, labelCounter, graphInformationAverages.get(labelUpdateCounter-1));
          sheet.addCell(label);
          sheet.addCell(number);
          labelCounter ++;
          labelUpdateCounter ++;
        }
      workbook.write();
      workbook.close();
    } catch (WriteException e )
    {

    } catch(IOException e)
    {

    }
  }

  /**
   * This will save the genome file for every tribe
   */
  protected void graphSaveGenomeFile(File file, Genome genome)
  {
    // Error check for null pointer exceptions, if true then return from the method
    if (file == null) return;

    // Otherwise write the file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
    {
      // Get list of triangles from current Genome
      Collection<float[]> triangles = new ArrayList<>();
      triangles = genome.getTriangles();

      // Loop through triangle list
      for(float[] triangle: triangles)
      {
        // Loop through each index of the array
        for(int i = 0; i < 10; i++)
        {
          bw.write(Float.toString(triangle[i]));
          bw.write(" ");
        }
        bw.newLine();
      }
      bw.close();
    } catch (IOException e)
    {
      e.printStackTrace();

    }
  }

  /**
   * This saves all written data to an array list that will eventually be written to a text document
   */
  protected void graphSaveWrittenData(int seconds)
  {
    ArrayList<Tribe> tribes = new ArrayList<>();
    tribes.addAll(engine.getPopulation().getTribes());
    Genome bestGenomeInTribe = tribes.get(0).getBest();

    double temp = 0;                    // The sum of all fitness
    double currentFitness = 0;          // Fitness of the current genome
    double tribeAmt = tribes.size();    // Tribe number, to be used when computing the average
    double average = 0;                 // Average fitness among all threads

    int tribeNum = 0;                   // Counter to know which tribe for info string

    String baseInfoString = "Thread" + tribeAmt + "-Image" + imageNum +"-Run" + run;
    String infoString = baseInfoString;

    // Get Fitness level for top Genomes
    for(Tribe tribe: tribes)
    {
      infoString = baseInfoString;
      tribeNum ++;
      bestGenomeInTribe = tribe.getBest();
      currentFitness = bestGenomeInTribe.getFitness();
      temp += currentFitness;

      infoString += "-" + "Tribe" + tribeNum + "-Update"+ updateCount + "-Fitness" + formatterGraph.format(currentFitness);
      graphInformation.add(infoString);
      graphInformationLabels.add("Tribe" + tribeNum);
      graphInformationNumbers.add(currentFitness);

      File genomeFile = new File( infoString + ".genome");
      if(seconds == 0 || seconds == 58)graphSaveGenomeFile(genomeFile, bestGenomeInTribe);
    }

    average = temp/tribeNum;

    infoString = baseInfoString;
    infoString += "-Average-"+"Update" + updateCount + "-Image" + imageNum +"-Run" + run+ "-Fitness" + average;
    graphInformation.add(infoString);
    graphInformationLabels.add("Average-Update " + updateCount);
    graphInformationNumbers.add(average);
    graphInformationAverages.add(average);
    graphInformation.add("\n");
  }

  /**
   * This method is used when the game is running in console mode. This allows the program to automatically
   * print out graphical information.
   */
  protected void graphSaveData()
  {
    int time1 = 9;
    int time2 = 19;
    int time3 = 29;
    int time4 = 39;
    int time5 = 49;
    int time6 = 58;
    int seconds = engine.getSeconds();
    int minutes = engine.getMinutes();
    int hours   = engine.getHours();
    // Every thirty seconds print out fitness data and save genome file
    if(((seconds == 0 && minutes == 0 && hours == 0) ||
      (seconds == time1 || seconds == time2 || seconds == time3
        || seconds == time4 || seconds == time5 || seconds == time6))&& updateOkay)
    {
      updateCount ++;
      graphSaveWrittenData(seconds);
      updateOkay = false;
      System.out.println("---- wrote data ---");
    }
    if(((seconds == 1 && minutes == 0 && hours == 0) ||
      (seconds == time1+1 || seconds == time2+1 || seconds == time3+1
        || seconds == time4+1 || seconds == time5+1 || seconds == time6+1)) && !updateOkay)
    {
      updateOkay = true;
    }

    // After alloted run time, reset to go for another run of data
    if(minutes >= timeLimit)
    {
      oldTribeSize = tribeSize;
      tribeSize = 0;
      mustChangeTribes = true;
      File writtenFile = new File( "Thread"+ oldTribeSize + "-Image"+ imageNum + "-Run"+ run+ "FinalOutput.txt");
      graphWriteData(writtenFile);
      graphInformation.clear();
      graphInformationLabels.clear();
      graphInformationNumbers.clear();
      graphInformationAverages.clear();
      updateCount = 0;
      run ++;
      // If we have done 5 runs then close the game
      if(run >= 6) {
        userWantsToClose = true;
        System.exit(0);
      }
    }
  }


  /**
   * Sets the background color
   * @param color Color to what the background should be
   */
  protected void setBackgroundColor(Color color)
  {
    backgroundColor = color;
  }

  /**
   *
   * @return color, returns the background color value
   */
  protected Color getBackgroundColor()
  {
    return backgroundColor;
  }

  /**
   * Used to write a genome file for the user. User will be able to save this file to their
   * own space to reuse at another time
   */
  protected void writeNewGenomeFile() {
    java.util.Date date2= new java.util.Date();
    //int tribeNumber = Integer.parseInt(str);

//    Date dNow = new Date( );
//    SimpleDateFormat ft =
//      new SimpleDateFormat("hh-mm-ss");
//    System.out.println("Current Date: " + ft.format(dNow));
//    String time = String.valueOf(Calendar.HOUR_OF_DAY) + ":" +String.valueOf(Calendar.MINUTE) + ":"  + String.valueOf(Calendar.SECOND);
//
//    System.out.println(">> DATE: " + time);
//    String fileName = "genome_" + ft.format(dNow) +  ".txt";

//    String fileName = "genome.txt";
//    String userHomeFolder = System.getProperty("user.home");

    // Create file chooser. User can only save .txt files
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("Genome Files (*.genome)", "*.genome"));
    File textFile = fileChooser.showSaveDialog(stage);

    // Error check for null pointer exceptions, if true then return from the method
    if (textFile == null) return;

    // Otherwise write the file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(textFile)))
    {
      // Get list of triangles from current Genome
      Collection<float[]> triangles = new ArrayList<>();
      triangles = currentGenome.getTriangles();

      // Loop through triangle list
      for(float[] triangle: triangles)
      {
        // Loop through each index of the array
        for(int i = 0; i < 10; i++)
        {
          bw.write(Float.toString(triangle[i]));
          bw.write(" ");
        }
        bw.newLine();
      }
      bw.close();
    } catch (IOException e)
    {
      e.printStackTrace();

    }
  }

  /**
   *
   * Resets the slider values when the tribe number is changed
   */
  private void resetSlider()
  {
    tribeListSlider.setMax(getTribes());
  }


  /**
   * Updates statistics values at each update
   */
  private void updateStatistics(EvolutionEngine engine)
  {
    fitnessLabel.setText("Selected Genome Fitness: " + formatter.format(currentGenome.getFitness()));
    fitnessPerSecondLabel.setText("Fitness/Sec: " + formatterFit.format(((Engine) engine).getFitnessPerSecond()));
    populationLabel.setText("Population (Genomes): " + getPopulationCount());
    generationLabel.setText("Amount of Generations: " + engine.getGenerationCount());
    if(hasSecondElapsed)generationPerSecondLabel.setText("Generations/Sec: " + tinyFormat.format(((Engine)engine).getAverageGenerationsPerSecondSinceLastInit()));
    generationAvgLabel.setText("Generations on Average: " + tinyFormat.format(((Engine)engine).getGenerationsLastSecond()));
    hillChildrenLabel.setText("Children from Hill Climbing: " + ((Engine)engine).getMutationCount());
    crossChildrenLabel.setText("Children from Crossover: " + ((Engine)engine).getCrossCount());
    nonPausedTime.setText("Running: " + formatterTime.format(engine.getHours()) + ":" +
      formatterTime.format(engine.getMinutes()) + ":" + formatterTime.format(engine.getSeconds()));
  }

  /**
   * @return number of genomes in the entire population
   */
  private int getPopulationCount()
  {
    return engine.getPopulationCount();
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

  private void addGenomeToTribe(File file)
  {
    int counter = 0;
    Genome userGenome = new Genome();

    try(FileReader fileReader = new FileReader(file.toString())){
      Scanner scanner = new Scanner(fileReader);
      scanner.useLocale(Locale.US);
      while(scanner.hasNextLine())
      {
        float[] triangle = new float[10];
        for(int i = 0; i<10 ; i++)
        {
          if(scanner.hasNextDouble())
          {
            triangle[i] = scanner.nextFloat();
          }
        }
        userGenome.add(triangle);
        counter ++;
        if(counter == 200) break;
      }
      System.out.println("-- Scanned Genome");
      userGenome.setFitness(1);
      //engine.getPopulation().getFitnessFunction().generateFitness(engine, userGenome);

      ArrayList<Tribe> tribes = new ArrayList<>();
      tribes.addAll(engine.getPopulation().getTribes());
      tribes.get(selectedTribe).add(userGenome);
      setSelectedGenome(0);

      scanner.close();
      System.out.println("-- Genome added to tribe");

    }catch (IOException e)
    {
      System.out.println("something broke in addGenomeToTribe");
    }
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
  private void configureGenomeChooser(final FileChooser fileChooser)
  {
    fileChooser.setTitle("Select genome file");
    fileChooser.setInitialDirectory(
      new File(System.getProperty("user.home"))
    );
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("GENOME", "*.genome")
    );
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
    setTargetImageWidthCropped(newX);
    setTargetImageHeightCropped(newY);
  }

  /**
   * @return cropped width of the image
   */
  public double getTargetImageWidth()
  {
    return targetImage.getWidth();
  }

  /**
   * @return cropped height of the image
   */
  public double getTargetImageHeight()
  {
    return targetImage.getHeight();
  }


  /**
   * @param width Width of the image
   */
  private void setTargetImageWidthCropped(double width)
  {
    targetImageWidthCropped = width;
  }

  /**
   * @param height Height of the image
   */
  private void setTargetImageHeightCropped(double height)
  {
    targetImageHeightCropped = height;
  }

  /**
   * @return cropped width of the image
   */
  public double getTargetImageWidthCropped()
  {
    return targetImageWidthCropped;
  }

  /**
   * @return cropped height of the image
   */
  public double getTargetImageHeightCropped()
  {
    return targetImageHeightCropped;
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
    nextButton.setDisable(false);
    pictureSelect.setDisable(false);
    fileChooserButton.setDisable(false);
    tribeField.setDisable(false);
    saveGenome.setDisable(false);
    loadGenome.setDisable(false);
    tableButton.setDisable(false);

    // Sliders
    triangleListSlider.setDisable(false);
    genomeListSlider.setDisable(false);
    tribeListSlider.setDisable(false);
  }

  /**
   * Called to disable buttons when the game is paused
   */
  private void disableButtons()
  {
    tribeButton.setDisable(true);
    nextButton.setDisable(true);
    pictureSelect.setDisable(true);
    fileChooserButton.setDisable(true);
    tribeField.setDisable(true);
    saveGenome.setDisable(true);
    loadGenome.setDisable(true);
    tableButton.setDisable(true);

    // Sliders
    triangleListSlider.setDisable(true);
    genomeListSlider.setDisable(true);
    tribeListSlider.setDisable(true);
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
    //setSelectedTribe(startingTribes);

    fitnessLabel = new Label();           // shows fitness level of current, selected genome
    fitnessPerSecondLabel = new Label();  // displays change of fitness per second of most fit genome in the population
    populationLabel = new Label();        // shows total current amount of generation
    generationLabel = new Label();        // amount of generations calculated by all tribes since the last population initialization
    generationPerSecondLabel = new Label(); // (avg of just last second) current generations per second averaged over the past second (not including paused time);
    generationAvgLabel = new Label();     // current generations averaged over all non-paused time since population initialization
    hillChildrenLabel = new Label();      // amount of hill-climb children
    crossChildrenLabel = new Label();     // amount of cross over children
    nonPausedTime = new Label();          // non paused time since the most recent population initialization hh:mm:ss

    this.engine = engine;
    this.stage = stage;

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

      // Draw the Mona Lisa
      gcOriginal.setFill(Color.BLACK);
      gcOriginal.setStroke(Color.BLACK);
      String defaultImage = pictureUrls[imageSelect];
      setTargetImage(defaultImage);
      resizeTargetImage();
      gcOriginal.drawImage(getTargetImage(), 0, 0, getTargetImage().getWidth(), getTargetImage().getHeight(), 0, 0, getTargetImageWidthCropped(), getTargetImageHeightCropped());
      colorSelector = new ColorSelector();
      setBackgroundColor(colorSelector.getAverageColor(getTargetImage()));

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
      fileChooser = new FileChooser();
      fileChooserButton = new Button("Choose an image ...");
      fileChooserButton.setMinWidth(100);
      fileChooserButton.setMaxWidth(150);
      fileChooserButton.setOnAction(
        new EventHandler<ActionEvent>()
        {
          @Override
          public void handle(final ActionEvent e)
          {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null)
            {
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
      if(graphBuilding) genomePaused = false;
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
          // if game is running, pause it and reset button
          if (!genomePaused)
          {
            pauseButton.setText("Resume");
            enableButtons();
          } else
          {
            pauseButton.setText("Pause");
            disableButtons();
          }
          genomePaused = !genomePaused;
        }
      });

      nextButton = new Button("Next");
      nextButton.setMinWidth(buttonSize);
      nextButton.setOnAction(e -> {
        nextGen = true;
        genomePaused = false;
        disableButtons();
        pauseButton.setDefaultButton(true);
      });


      saveGenome = new Button("Save Genome");
      saveGenome.setMinWidth(buttonSize);
      saveGenome.setOnAction(e ->
      {
        writeNewGenomeFile();
      });

      FileChooser genomeChooser = new FileChooser();
      loadGenome = new Button("Load Genome");
      loadGenome.setMinWidth(buttonSize);
      loadGenome.setOnAction(e ->
      {
        configureGenomeChooser(genomeChooser);
        File file = genomeChooser.showOpenDialog(stage);
        if (file != null)
        {
          System.out.println("HERE inside if");
          addGenomeToTribe(file);
        }
      });

      // Create show genome button
      tableButton = new Button("Show Genome");
      tableButton.setMinWidth(buttonSize);
      tableButton.setOnAction(e ->
      {
        new GeneTablePopup();
      });

      // Create file chooser button
      //FileChooser fileChooser = new FileChooser();
      fileChooserButton = new Button("Choose an image ...");
      fileChooserButton.setMinWidth(100);
      fileChooserButton.setMaxWidth(150);
      fileChooserButton.setOnAction(
        new EventHandler<ActionEvent>()
        {
          @Override
          public void handle(final ActionEvent e)
          {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null)
            {
              selectedNewImage = true;
              fileChooserButton.setText(file.toString());
              openFile(file);
            }
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
      tribeButtonLabel.setMinWidth(170);
      tribeButton = new Button("OK");
      tribeButton.setMinWidth(70);
      tribeButton.setOnAction(new EventHandler<ActionEvent>()
      {
        @Override
        public void handle(ActionEvent e)
        {
          String str = tribeField.getText();
          if (str == null) return;
          int tribeNumber = Integer.parseInt(str);
          System.out.println("tribeNumber: " + tribeNumber);
          if (tribeNumber <= 0) return;
          tribeSize = tribeNumber;
          resetSlider();
        }
      });

      // ************ ChoiceBox *********************
      pictureSelect = new ChoiceBox(FXCollections.observableArrayList(
        "MonaLisa - 100x81","MonaLisa - 250x202","MonaLisa - 512x413", "PoppyFields - 100x75", "PoppyFields - 250x188",
        "PoppyFields - 512x384", "The Great Wave - 100x69", "The Great Wave - 250x172", "The Great Wave - 512x352",
        "Piet Mondrian - 100x75", "Piet Mondrian - 250x188", "Piet Mondrian - 512385", "TriangePic - 100x80",
        "TriangePic - 250x201", "TriangePic - 512x412", "MonaClose", "TriangleOcean", "Petronas Towers - 512x352" , "Petronas Towers 250x172")
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

      topRowContainer = new HBox(15);           // holds pause, save, load, genome table buttons
      middleRowContainer = new HBox(10);        // holds tribe selector
      bottomRowContainer = new HBox(10);        //

      statsContainer = new HBox(30);        // Holds statistics
      stats1 = new VBox(10);
      stats2 = new VBox(10);
      stats3 = new VBox(10);

      stats1.setMinWidth(2*canvasWidth / 4);
      stats2.setMinWidth(2*canvasWidth / 4);
      stats3.setMinWidth(2*canvasWidth / 4);

      // Create statistics labels
      int labelWidth = 160;
      Label statsLabel = new Label ("\t Statistics:");
      fitnessLabel.setText("Selected Genome Fitness: ");
      fitnessPerSecondLabel.setText("Fitness/Sec: N/A");
      populationLabel.setText("Population: " + null + " genomes");
      generationLabel.setText("Amount of Generations: ");
      generationPerSecondLabel.setText("Generations/Sec: N/A");
      generationAvgLabel.setText("Generations on Average: ");
      hillChildrenLabel.setText("Children from Hill Climbing: ");
      crossChildrenLabel.setText("Children from Crossover: ");
      nonPausedTime.setText("Running: hh:mm:ss");

      statsLabel.setMinWidth(labelWidth/2);
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
      topRowContainer.getChildren().addAll(pauseButton, nextButton, saveGenome, loadGenome, tableButton);
      //topRowContainer.getChildren().addAll(pauseButton, saveGenome, loadGenome);


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

      currentGenome.setFitness(0);
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
      disableButtons();
      return;
    }

    int selectedTribe = getSelectedTribe();
    int rColor, gColor, bColor = 0;
    double alpha = 0;
    int vertexCounter = 0;

    if(engine.getSeconds() >= 1) hasSecondElapsed = true;

    if(nextGen == true && genomePaused == false)
    {
      nextGen = false;
      genomePaused = true;
      enableButtons();
      pauseButton.setDisable(false);
    }

    // If we are not debugging, then refresh the canvas at each update
    if (!canvasDebugging) clearGeneticCanvas();

    // If user selected a new image to draw, draw that image
    if (selectedNewImage)
    {
      Image img = getTargetImage();
      setBackgroundColor(colorSelector.getAverageColor(img));
      clearOriginalCanvas();
      //gcOriginal.drawImage(getTargetImage(), 0, 0);
      engine.getLog().log("window", "width: %f height: %f\n", getTargetImageWidth(), getTargetImageHeight());
      gcOriginal.drawImage(getTargetImage(), 0, 0, img.getWidth(), img.getHeight(), 0, 0, getTargetImageWidthCropped(), getTargetImageHeightCropped());
      selectedNewImage = false;
    }

    // Fill black background
    //gcGenetic.setFill(getBackgroundColor());
    gcGenetic.setFill(Color.BLACK);
    gcGenetic.fillRect(0, 0, getTargetImageWidth(), getTargetImageHeight());

    // For time being, select very first genome
    ArrayList<Tribe> tribes = new ArrayList<>();
    tribes.addAll(engine.getPopulation().getTribes());

    ArrayList<Genome> genomes = new ArrayList<>();
    genomes.addAll(tribes.get(selectedTribe).getGenomes());
    Genome selectedGenome = genomes.get(getSelectedGenome());
    selectedGenome.setFitness(engine.getPopulation().getFitnessFunction().generateFitness(engine, selectedGenome));
    currentGenome = selectedGenome;
    genomeListSlider.setMax(genomes.size()-1);

    // Loop through each triangle of genome
    TriangleManager manager = new TriangleManager(); // need this to interpret the triangle data
    //TriangleRenderer renderer = new TriangleRenderer(getImageWidth(), getImageHeight());

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
      //renderer.renderTriangle(manager.getXCoordinates(), manager.getYCoordinates(), manager.getColor());
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
    //gcGenetic.drawImage(renderer.convertToImage(), 0, 0);

    updateStatistics(engine);
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
    return (int)getTargetImageWidth();
  }

  /**
   * @return Height of the image being drawn
   */
  public int getImageHeight()
  {
    return (int)getTargetImageHeight();
  }
}
