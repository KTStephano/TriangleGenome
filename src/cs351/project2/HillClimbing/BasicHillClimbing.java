package cs351.project2.hillclimbing;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.FitnessFunction;
import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.TriangleManager;

import java.util.ArrayList;
import java.util.Random;

public class BasicHillClimbing implements Mutator
{
  private Genome genome = null;
  private int maxStep = 10;
  private float[] prevTriangle = null;
  private int prevGeneIndex = -1;
  private float prevStep = 0;
  private final ArrayList<float[]> TRIANGLE_LIST = new ArrayList<>(200);
  private final Random RAND = new Random();

  /**
   * Sets the genome. Should clear out all data maintained for the
   * previous genome if there is any.
   *
   * @param genome new genome
   */
  @Override
  public void setGenome(Genome genome)
  {
    this.genome = genome;
    TRIANGLE_LIST.clear();
    TRIANGLE_LIST.addAll(this.genome.getTriangles());
  }

  /**
   * At this point the mutator should decide on a mutation(s) and
   * perform them.
   *
   * @param function fitness function to use
   * @param engine   reference to the current engine
   */
  @Override
  public void mutate(FitnessFunction function, EvolutionEngine engine)
  {
    if (genome == null) return;
    float[] currTriangle;
    int currGeneIndex;
    float currStep;
    TriangleManager manager = new TriangleManager();
    if (prevTriangle != null)
    {
      currTriangle = prevTriangle;
      currGeneIndex = prevGeneIndex;
      currStep = prevStep;
    }
    else
    {
      currGeneIndex = RAND.nextInt(10);
      currTriangle = TRIANGLE_LIST.get(RAND.nextInt(genome.size()));
      currStep = RAND.nextInt(maxStep) / 100.0f;
    }
    manager.setTriangleData(engine.getGUI(), currTriangle);
    float[] normalizedTriangle = manager.getNormalizedDNA();
    normalizedTriangle[currGeneIndex] += currStep;
    if (normalizedTriangle[currGeneIndex] < 0.0f) normalizedTriangle[currGeneIndex] = 0.0f;
    else if (normalizedTriangle[currGeneIndex] > 1.0f) normalizedTriangle[currGeneIndex] = 1.0f;
    float prevValue = currTriangle[currGeneIndex];
    currTriangle[currGeneIndex] = manager.revertNormalization(normalizedTriangle)[currGeneIndex];
    double prevFitness = genome.getFitness();
    genome.setFitness(function.generateFitness(engine, genome));
    if (genome.getFitness() > prevFitness)
    {
      prevTriangle = currTriangle;
      prevGeneIndex = currGeneIndex;
      prevStep = currStep;
    }
    else
    {
      currTriangle[currGeneIndex] = prevValue;
      prevTriangle = null;
    }
  }
}
