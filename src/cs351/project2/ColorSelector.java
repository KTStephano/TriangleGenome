package cs351.project2;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * ColorSelector is tasked with providing methods to get different color values from the target image.
 * Currently color selector will return the average used color in the target image
 */
public class ColorSelector
{
  /**
   *
   * @param img Target image to acquire the average color
   * @return color, average color found in image
   */
  public Color getAverageColor(Image img)
  {
    Color backgroundColor = null;

    Histogram histogram = new Histogram(img);
    Histogram.ColorData rData = histogram.getRedColorData();
    Histogram.ColorData gData = histogram.getGreenColorData();
    Histogram.ColorData bData = histogram.getBlueColorData();

    // Used to find the most used color
    int counter = 0;
    int red = 0;
    int green = 0;
    int blue = 0;

    for (int i = 0; i < rData.getNumColorValues(); i++)
    {
      counter += rData.getFrequencyOfColorShade(i);
      red += rData.getFrequencyOfColorShade(i) * i;
      green += gData.getFrequencyOfColorShade(i) * i;
      blue += bData.getFrequencyOfColorShade(i) * i;

    }

    // Find the average color, add 1 to fix the offset value
    red = red/counter;
    green = green/counter;
    blue = blue/counter;
    System.out.println("\n---- Average Used Color ----");
    System.out.printf("Red: %d Green: %d Blue %d", red, green, blue);

    backgroundColor = Color.rgb((int)red, (int)green, (int)blue);
    return backgroundColor;
  }
}
