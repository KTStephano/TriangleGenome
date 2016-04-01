package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.FitnessFunction;
import cs351.core.Genome;
import javafx.scene.image.Image;

import java.util.Random;

/**
 * Gives a fitness based on the time of day, how it's feeling or
 * what it had for breakfast.
 */
public class NotTheRealFitnessFunction implements FitnessFunction
{
  private final Random RAND = new Random();

  @Override
  public void setTargetImage(Image image)
  {

  }

  /**
   * This one actually works.
   */
  @Override
  public Genome compare(Genome first, Genome second)
  {
    return first.getFitness() > second.getFitness() ? first : second;
  }

  @Override
  public double generateFitness(EvolutionEngine engine, Genome genome)
  {
    return RAND.nextDouble();
  }

  @Override
  public int getMaxFitness()
  {
    return RAND.nextInt();
  }
}