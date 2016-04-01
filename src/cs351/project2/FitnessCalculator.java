package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Engine.GUI;
import cs351.core.FitnessFunction;
import cs351.core.Genome;
import cs351.core.TriangleManager;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.util.Collection;

public class FitnessCalculator implements FitnessFunction
{
  private Image target;

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
    TriangleRenderer renderer = new TriangleRenderer(engine.getGUI().getImageWidth(),
                                                     engine.getGUI().getImageHeight());
    TriangleManager manager = new TriangleManager();

    Collection<float[]> triangles = genome.getTriangles();
    GUI gui = engine.getGUI();
    for (float[] triangle : triangles)
    {
      manager.setTriangleData(gui, triangle);
      renderer.renderTriangle(manager.getXCoordinates(), manager.getYCoordinates(), manager.getColor());
    }

    PixelReader reader = target.getPixelReader();
    int width = engine.getGUI().getImageWidth();
    int height = engine.getGUI().getImageHeight();
    double fitness = 0.0;

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
