package cs351.project2.hillclimbing;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.FitnessFunction;
import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.TriangleManager;
import cs351.project2.Engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Please be the last one.
 */
public class AdaptiveHillClimbing implements Mutator
{
  private final ArrayList<float[]> TRIANGLE_LIST = new ArrayList<>(200);
  private final LinkedList<TriangleGeneWrapper> TRIANGLE_GENE_PROBABILITY_MAP = new LinkedList<>();
  private final float START_STEP = 0.02f;
  private final float STEP_CHANGE = 0.02f;
  private final float PROBABILITY_CHANGE = 0.05f;
  private final float MIN_STEP = START_STEP;
  private final float MIN_PROBABILITY = 0.02f;
  private final Random RAND = new Random();
  private Genome genome = null;

  private final class TriangleGeneWrapper
  {
    private final int TRIANGLE_INDEX, GENE_INDEX;
    private float probability;
    private float step;
    private int direction;

    public TriangleGeneWrapper(float startStep, int direction, int triangleIndex, int geneIndex, float initialProbability)
    {
      step = startStep;
      this.direction = direction;
      TRIANGLE_INDEX = triangleIndex;
      GENE_INDEX = geneIndex;
      probability = initialProbability;
    }

    @Override
    public String toString()
    {
      return Float.toString(probability);
    }

    public int compareTo(TriangleGeneWrapper other)
    {
      return -1 * Float.compare(probability, other.probability);
    }

    public void setStep(float step)
    {
      this.step = step;
    }

    public float getStep()
    {
      return step;
    }

    public void setDirection(int direction)
    {
      this.direction = direction;
    }

    public int getDirection()
    {
      return direction;
    }

    public float getProbability()
    {
      return probability;
    }

    public void setProbability(float probability)
    {
      this.probability = probability;
    }

    public int getTriangleIndex()
    {
      return TRIANGLE_INDEX;
    }

    public int getGeneIndex()
    {
      return GENE_INDEX;
    }
  }

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
    TRIANGLE_GENE_PROBABILITY_MAP.clear();
    int i = 0;
    for (float[] triangle : genome.getTriangles())
    {
      TRIANGLE_LIST.add(triangle);
      for (int k = 0; k < triangle.length; k++)
      {
        TRIANGLE_GENE_PROBABILITY_MAP.add(new TriangleGeneWrapper(START_STEP,
                                                                  RAND.nextFloat() < 0.5f ? -1 : 1, // direction
                                                                  i, k,
                                                                  MIN_PROBABILITY));
      }
      i++;
    }
    sortProbabilityMap();
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
    TriangleManager manager = new TriangleManager();
    int size = TRIANGLE_GENE_PROBABILITY_MAP.size();
    int numTries = 0;
    while (numTries < 10)
    {
      numTries++;
      int choiceTriangleGene = Math.abs(RAND.nextInt(size) - RAND.nextInt(size));
      TriangleGeneWrapper wrapper = TRIANGLE_GENE_PROBABILITY_MAP.get(choiceTriangleGene);
      float[] triangle = TRIANGLE_LIST.get(wrapper.getTriangleIndex());
      manager.setTriangleData(engine.getGUI(), triangle);
      float[] normalizedTriangle = manager.getNormalizedDNA();
      float mutateAmount = wrapper.getStep() * wrapper.getDirection();
      float prevValue = triangle[wrapper.getGeneIndex()];
      double prevFitness = genome.getFitness();
      normalizedTriangle[wrapper.getGeneIndex()] += mutateAmount;
      normalizedTriangle[wrapper.getGeneIndex()] = bound(normalizedTriangle[wrapper.getGeneIndex()], 0.02f, 1.0f);
      triangle[wrapper.getGeneIndex()] = manager.revertNormalization(normalizedTriangle)[wrapper.getGeneIndex()];
      genome.setFitness(function.generateFitness(engine, genome));
      ((Engine) engine).incrementGenerationCount();
      if (genome.getFitness() < prevFitness)
      {
        triangle[wrapper.getGeneIndex()] = prevValue;
        genome.setFitness(prevFitness);
        wrapper.setDirection(wrapper.getDirection() * -1);
        wrapper.setStep(bound(wrapper.getStep() - STEP_CHANGE, MIN_STEP, 1.0f));
        wrapper.setProbability(bound(wrapper.getProbability() - PROBABILITY_CHANGE, MIN_PROBABILITY, 1.0f));
        sortProbabilityMap();
      }
      else
      {
        wrapper.setStep(bound(wrapper.getStep() + STEP_CHANGE, MIN_STEP, 1.0f));
        wrapper.setProbability(bound(wrapper.getProbability() + PROBABILITY_CHANGE, MIN_PROBABILITY, 1.0f));
        sortProbabilityMap();
        break;
      }
    }
  }

  private void sortProbabilityMap()
  {
    TRIANGLE_GENE_PROBABILITY_MAP.sort((gene1, gene2) -> gene1.compareTo(gene2));
  }

  private float bound(float value, float min, float max)
  {
    if (value > max) return max;
    else if (value < min) return min;
    return value;
  }
}
