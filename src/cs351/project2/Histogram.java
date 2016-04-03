package cs351.project2;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Extracts data from an image so it can be used for comparisons.
 */
public class Histogram
{
  private final Image IMAGE;
  private final HashMap<Integer, Integer> RED_SHADE_FREQUENCY;
  private final HashMap<Integer, Integer> GREEN_SHADE_FREQUENCY;
  private final HashMap<Integer, Integer> BLUE_SHADE_FREQUENCY;

  public class ColorData
  {
    private final HashMap<Integer, Integer> DATA;

    public ColorData(HashMap<Integer, Integer> data)
    {
      DATA = data;
    }

    /**
     * Returns the number of color values. For example, if the range is [0, 255]
     * for each channel, it will return 256.
     *
     * @return number of color values for this channel
     */
    public int getNumColorValues()
    {
      return DATA.size();
    }

    /**
     * Gets the frequency of the specified color shade.
     *
     * @param shade color shade to get the frequency for
     * @return the frequency of the given color shade
     */
    public int getFrequencyOfColorShade(int shade)
    {
      return DATA.get(shade);
    }
  }

  {
    RED_SHADE_FREQUENCY = new HashMap<>();
    GREEN_SHADE_FREQUENCY = new HashMap<>();
    BLUE_SHADE_FREQUENCY = new HashMap<>();
    // For each possible red/green/blue value ([0, 255]), add it to the hash map
    for (int i = 0; i <= 255; i++)
    {
      RED_SHADE_FREQUENCY.put(i, 0);
      GREEN_SHADE_FREQUENCY.put(i, 0);
      BLUE_SHADE_FREQUENCY.put(i, 0);
    }
  }

  public Histogram(Image image)
  {
    IMAGE = image;
    extractImageData();
  }

  public Histogram(String relativePath)
  {
    IMAGE = loadImage(relativePath);
    extractImageData();
  }

  public ColorData getRedColorData()
  {
    return new ColorData(RED_SHADE_FREQUENCY);
  }

  public ColorData getGreenColorData()
  {
    return new ColorData(GREEN_SHADE_FREQUENCY);
  }

  public ColorData getBlueColorData()
  {
    return new ColorData(BLUE_SHADE_FREQUENCY);
  }

  private Image loadImage(String relativePath)
  {
    InputStream stream = Histogram.class.getResourceAsStream(relativePath);
    if (stream == null) throw new RuntimeException("Unable to load " + relativePath + " as a resource stream");
    return new Image(stream);
  }

  private void extractImageData()
  {
    try
    {
      if (IMAGE.isBackgroundLoading())
      {
        Thread.sleep(1);
        extractImageData();
      }
    }
    catch (InterruptedException e)
    {
      // Do nothing
    }
    PixelReader reader = IMAGE.getPixelReader();
    int width = (int)IMAGE.getWidth();
    int height = (int)IMAGE.getHeight();
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        Color color = reader.getColor(x, y);
        int r = (int)(color.getRed() * 255);
        int g = (int)(color.getGreen() * 255);
        int b = (int)(color.getBlue() * 255);
        int rFreq = RED_SHADE_FREQUENCY.get(r) + 1;
        int gFreq = GREEN_SHADE_FREQUENCY.get(g) + 1;
        int bFreq = BLUE_SHADE_FREQUENCY.get(b) + 1;
        RED_SHADE_FREQUENCY.put(r, rFreq);
        GREEN_SHADE_FREQUENCY.put(g, gFreq);
        BLUE_SHADE_FREQUENCY.put(b, bFreq);

      }
    }
  }
}
