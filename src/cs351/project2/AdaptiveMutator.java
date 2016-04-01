package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.FitnessFunction;
import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.TriangleManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Tries to make a change to a gene(s) of the best triangle in the
 * given genome.
 */
public final class AdaptiveMutator implements Mutator
{
  private static final Random RAND = new Random();
  private static final Object[] GENES =
                           {
                             TriangleManager.Coordinate.X1, TriangleManager.Coordinate.X2, TriangleManager.Coordinate.X3,
                             TriangleManager.Coordinate.Y1, TriangleManager.Coordinate.Y2, TriangleManager.Coordinate.Y3,
                             TriangleManager.ColorValue.RED, TriangleManager.ColorValue.GREEN,
                             TriangleManager.ColorValue.BLUE, TriangleManager.ColorValue.ALPHA
                           };
  private static final int MULTIPLIER = 5;
  private static final float WEIGHT_OFFSET = MULTIPLIER / 100.0f; // increase/decrease amount on success
  private static final float MAX_WEIGHT = 1.0f;
  private static final float MIN_WEIGHT = MAX_WEIGHT * (MULTIPLIER / 100.0f);
  private static final int MIN_STEP = 10;
  private static final int MAX_STEP = 50;
  private static final float STEP_OFFSET = 0.05f;
  private static final float START_WEIGHT = MAX_WEIGHT * (MULTIPLIER / 100.0f);
  private final TriangleManager MANAGER = new TriangleManager();
  private Genome genome = null;
  private float[] weights = {
                               START_WEIGHT, START_WEIGHT, START_WEIGHT, START_WEIGHT, START_WEIGHT,
                               START_WEIGHT, START_WEIGHT, START_WEIGHT, START_WEIGHT, START_WEIGHT
                            };
  private float[] stepSizes = {
                                MIN_STEP, MIN_STEP, MIN_STEP, MIN_STEP, MIN_STEP,
                                MIN_STEP, MIN_STEP, MIN_STEP, MIN_STEP, MIN_STEP
                              };
  private int[] lastSuccessfulDirection = new int[10];

  {
    for (int i = 0; i < lastSuccessfulDirection.length; i++) lastSuccessfulDirection[i] = 0;
  }

  @Override
  public void setGenome(Genome genome)
  {
    this.genome = genome;
  }

  @Override
  public void mutate(FitnessFunction function, EvolutionEngine engine)
  {
    if (genome == null) throw new RuntimeException("Must call setGenome");
    ArrayList<float[]> triangles = new ArrayList<>(genome.getTriangles());
    // Select one based on their weights
    int selection = -1;
    // Run through the genes and pick random numbers to check against their
    // weights to choose one of the genes
    while (selection == -1)
    {
      for (int i = 0; i < weights.length; i++)
      {
        if (RAND.nextDouble() <= weights[i]) selection = i;
      }
    }
    int direction = lastSuccessfulDirection[selection] != 0 ? lastSuccessfulDirection[selection] :
                    RAND.nextInt(3) - 1; // [-1, 1]
    int index = RAND.nextInt(triangles.size());
    float step = stepSizes[selection];
    MANAGER.setTriangleData(engine.getGUI(), triangles.get(index));
    double previousFitness = genome.getFitness();
    if (selection < 6)
    {
      MANAGER.mutateCoordinate((TriangleManager.Coordinate) GENES[selection], step * direction);
    }
    else
    {
      if (selection == 9) step /= MAX_STEP; // normalize the step for alpha value
      MANAGER.mutateColorValue((TriangleManager.ColorValue) GENES[selection], step * direction);
    }
    // Get the new fitness
    genome.setFitness(function.generateFitness(engine, genome));
    ((Engine)engine).incrementGenerationCount();
    double newFitness = genome.getFitness();
    int improved = newFitness > previousFitness ? 1 : -1;
    if (improved == 1) lastSuccessfulDirection[selection] = direction;
    else lastSuccessfulDirection[selection] = 0;
    // Adapt the step sizes/weights depending on how things went
    stepSizes[selection] = constrain(stepSizes[selection] + STEP_OFFSET * improved, MIN_STEP, MAX_STEP);
    weights[selection] = constrain(weights[selection] + WEIGHT_OFFSET * improved, MIN_WEIGHT, MAX_WEIGHT);
  }

  private float constrain(float num, float min, float max)
  {
    if (num < min) num = min;
    else if (num > max) num = max;
    return num;
  }
}
