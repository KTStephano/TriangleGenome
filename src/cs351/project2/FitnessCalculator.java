package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import cs351.core.FitnessFunction;
import cs351.core.Genome;
import cs351.core.TriangleManager;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Given a target image and a genome, this will calculate the fitness (but not set it)
 * for the genome in question.
 *
 * @author Justin
 */
public class FitnessCalculator implements FitnessFunction
{
  private final ReentrantLock LOCK = new ReentrantLock();
  private Image target;
  private LinkedList<TriangleRenderer> renderList = new LinkedList<>();

  @Override
  public void setTargetImage(Image image)
  {
    target = image;
  }

  /**
   * Takes two Genomes and compares them based on their fitness.
   *
   * @param first  first genome
   * @param second second genome
   * @return the better genome
   */
  @Override
  public Genome compare(Genome first, Genome second)
  {
    return first.getFitness() > second.getFitness() ? first : second;
  }

  /**
   * Takes the given genome, compares it to the target genome and outputs
   * a normalized fitness value.
   *
   * @param genome genome to generate a fitness for
   * @return normalized fitness for the given genome
   */
  @Override
  public double generateFitness(EvolutionEngine engine, Genome genome)
  {
    TriangleRenderer renderer;
    try
    {
      LOCK.lock();
      if (renderList.size() == 0) renderList.add(new TriangleRenderer(engine.getGUI().getImageWidth(),
                                                                    engine.getGUI().getImageHeight()));
      renderer = renderList.pop();
    }
    finally
    {
      LOCK.unlock();
    }
    TriangleManager manager = new TriangleManager();

    renderer.clear();

    Collection<float[]> triangles = genome.getTriangles();
    GUI gui = engine.getGUI();
    for (float[] triangle : triangles)
    {
      manager.setTriangleData(gui, triangle);
      renderer.renderTriangle(manager.getXCoordinates(), manager.getYCoordinates(), manager.getColor());
    }
    renderer.markComplete();

    PixelReader reader = target.getPixelReader();
    int width = engine.getGUI().getImageWidth();
    int height = engine.getGUI().getImageHeight();
    double fitness = 0.0;
    int offset = 1;
    for (int x = 0; x < width; x+=offset)
    {
      for (int y = 0;y < height; y+=offset)
      {
        float[] targetImg = TriangleRenderer.unpackData(reader.getArgb(x, y));
        float[] genomeImg = TriangleRenderer.unpackData(renderer.getPackedARGB(x, y));
        float redDiff = targetImg[0] - genomeImg[0];
        float greenDiff = targetImg[1] - genomeImg[1];
        float blueDiff = targetImg[2] - genomeImg[2];
        if (redDiff > 255 || blueDiff > 255 || greenDiff > 255) System.out.println("true");
        fitness += (redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);// + alphaDiff * alphaDiff;
      }
    }
    //fitness = 1.0 - fitness / ((width / offset) * (height / offset) * 3 * 256);
    fitness = 1.0 - fitness / (width * height * 3 * 256.0 * 256.0);
    //fitness = width * height * 3 * 256.0 * 256.0 - fitness;
    //System.out.println(fitness);
    try
    {
      LOCK.lock();
      renderList.add(renderer);
    }
    finally
    {
      LOCK.unlock();
    }
    return fitness;
  }

  /**
   * Returns the maximum fitness for a Genome as a non-normalized integer. This
   * can be used to undo a normalized fitness (generateFitness(genome) * getMaxFitness() =
   * non-normalzed fitness of a genome).
   *
   * @return maximum fitness for any genome as used by this fitness function
   */
  @Override
  public int getMaxFitness()
  {
    return 1;
  }
}
