package cs351.project2;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Draws triangles to a buffered image. This is not thread safe.
 */
public class TriangleRenderer
{
  private int width, height;
  private int[] xVertBuffer, yVertBuffer;
  private final BufferedImage IMAGE;
  private final Graphics2D CONTEXT;

  public TriangleRenderer(int width, int height)
  {
    this.width = width;
    this.height = height;
    xVertBuffer = new int[3];
    yVertBuffer = new int[3];
    IMAGE = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    CONTEXT = (Graphics2D)IMAGE.getGraphics();
    CONTEXT.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    CONTEXT.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    clear();
  }

  /**
   * Expects 3 different vertices between the two vertex arrays, and a color of the form
   * {red, green, blue, alpha} where the alpha component is normalized.
   * @param xVertices array of in-order x-values
   * @param yVertices array of in-order y-values
   * @param color RGBA color (alpha normalized)
   */
  public void renderTriangle(float[] xVertices, float[] yVertices, float[] color)
  {
    xVertBuffer[0] = (int)xVertices[0];
    xVertBuffer[1] = (int)xVertices[1];
    xVertBuffer[2] = (int)xVertices[2];

    yVertBuffer[0] = (int)yVertices[0];
    yVertBuffer[1] = (int)yVertices[1];
    yVertBuffer[2] = (int)yVertices[2];

    CONTEXT.setColor(new Color(packData(color, false), true));
    CONTEXT.fillPolygon(xVertBuffer, yVertBuffer, 3);
  }

  public void clear()
  {
    CONTEXT.setBackground(Color.WHITE);
  }

  public int getWidth()
  {
    return width;
  }

  public int getHeight()
  {
    return height;
  }

  public int getPackedARGB(int x, int y)
  {
    return IMAGE.getRGB(x, y);
  }

  public float[] getRGBA(int x, int y)
  {
    return unpackData(IMAGE.getRGB(x, y));
  }

  public javafx.scene.image.Image convertToImage()
  {
    javafx.scene.image.WritableImage image = new WritableImage(width, height);
    PixelWriter writer = image.getPixelWriter();
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        writer.setArgb(x, y, getPackedARGB(x, y));
      }
    }
    return image;
  }

  /**
   * If packAsARGB is true, the value will be packed with the alpha value being
   * first (most significant bits) - otherwise it will be packed last (least significant
   * bits).
   * @param color color array (r, g, b, a) with a normalized alpha
   * @param packAsARGB true if ARGB, false if RGBA
   * @return packed data
   */
  private int packData(float[] color, boolean packAsARGB)
  {
    int alpha = (int)(color[3] * 255) << (32 - 8);
    int red = (int)(color[0]) << (32 - 2 * 8);
    int green = (int)(color[1]) << (32 - 3 * 8);
    int blue = (int)color[2];
    return packAsARGB ? alpha | red | green | blue :
                        red | green | blue | alpha;
  }

  /**
   * Takes an int representing all 4 color values and unpacks them into a float
   * array. The alpha value will be [0.0, 1.0] while the rest will be [0.0, 255.0].
   * @param argb int representing the 4 color values
   * @return an array with the unpacked data
   */
  private float[] unpackData(int argb)
  {
    float[] colorBuffer = new float[4];
    colorBuffer[3] = ((argb & 0xFF) >>> (32 - 8)) / 255;
    colorBuffer[0] = ((argb & 0x00FF) >>> (32 - 2 * 8));
    colorBuffer[1] = ((argb & 0x0000FF) >>> (32 - 3 * 8));
    colorBuffer[2] = (argb & 0x000000FF);
    return colorBuffer;
  }
}
