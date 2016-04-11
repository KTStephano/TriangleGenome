package cs351.project2;

import javafx.application.Application;
import javafx.stage.Stage;

public class HistogramTest
{
  public static void main(String[] args)
  {
    Histogram histogram = new Histogram("images/mona-lisa-cropted-512x413.png");
    Histogram.ColorData rData = histogram.getRedColorData();
    Histogram.ColorData gData = histogram.getGreenColorData();
    Histogram.ColorData bData = histogram.getBlueColorData();

    // frequency of shade used
    int amountRed = 0;
    int amountBlue = 0;
    int amountGreen = 0;

    // most used shade
    int bestRed = 0;
    int bestBlue = 0;
    int bestGreen = 0;

    // Used to find the most used color
    int counter = 0;
    int red = 0;
    int green = 0;
    int blue = 0;

    for (int i = 0; i < rData.getNumColorValues(); i++)
    {
      if(rData.getFrequencyOfColorShade(i) > amountRed)
      {
        amountRed = rData.getFrequencyOfColorShade(i);
        bestRed = i;
      }
      if(gData.getFrequencyOfColorShade(i) > amountGreen)
      {
        amountGreen = gData.getFrequencyOfColorShade(i);
        bestGreen = i;
      }
      if(bData.getFrequencyOfColorShade(i) > amountBlue)
      {
        amountBlue = bData.getFrequencyOfColorShade(i);
        bestBlue = i;
      }

      counter += rData.getFrequencyOfColorShade(i);
      red += rData.getFrequencyOfColorShade(i) * i;
      green += gData.getFrequencyOfColorShade(i) * i;
      blue += bData.getFrequencyOfColorShade(i) * i;


      System.out.print("(Color value " + i + ") ");
      System.out.print("R: " + rData.getFrequencyOfColorShade(i) + "; ");
      System.out.print("G: " + gData.getFrequencyOfColorShade(i) + "; ");
      System.out.println("B: " + bData.getFrequencyOfColorShade(i));
    }

    System.out.println("---- Most Frequent Individual Shades -----");
    System.out.println("[Red]\t shade: " + bestRed + " frequency: " + amountRed);
    System.out.println("[Green]\t shade: " + bestGreen + " frequency: " + amountGreen);
    System.out.println("[Blue]\t shade: " + bestBlue + " frequency: " + amountBlue);

    // Find the average color, add 1 to fix the offsetted value
    red = red/counter;
    green = green/counter;
    blue = blue/counter;
    System.out.println("\n---- Average Used Color ----");
    System.out.printf("Red: %d Green: %d Blue %d", red, green, blue);
  }
}
