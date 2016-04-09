package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.FitnessFunction;
import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.TriangleManager;

import java.util.Iterator;
import java.util.Random;

/**
 * CITATION: http://katrinaeg.com/simulated-annealing.html
 *
 * Performs simple hill climbing by mutating different parts of a triangle's
 * genetic sequence.
 */
public class SimpleHillClimbing implements Mutator
{
  private Genome genome = null;
  private final float DEFAULT_MUTATION_CHANCE = 0.006f;
  private final Random RAND = new Random();
  private final TriangleManager MANAGER = new TriangleManager();
  private float vertexMutationChance = DEFAULT_MUTATION_CHANCE;
  private float colorMutationChance = DEFAULT_MUTATION_CHANCE;
  private int numTrianglesToMutate = 200;
  private double temperature = 1.0;
  private double friction = 0.99;

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
    // Error checking
    if (genome == null) return;

    int changeAmountMax = 10;
    int changeAmountMin = 3;
    int changeAmountBound = 100;
    int colorChangeAmount = 10;
    float addTriangleChance = 0.05f;
    Genome best = null;
    for (int trials = 0; trials < 10; trials++)
    {
      Genome spinoff = new Genome();
      if (RAND.nextFloat() <= addTriangleChance) numTrianglesToMutate++;
      Iterator<float[]> triangleItr = genome.getTriangles().iterator();
      int numMutations = 0;
      int index = 0;
      //System.out.println(numTrianglesToMutate);

      while (triangleItr.hasNext())
      {
        float[] triangle = triangleItr.next();
        float[] normalizedTriangle;
        float[] newGene = new float[10];
        MANAGER.setTriangleData(engine.getGUI(), triangle);
        normalizedTriangle = MANAGER.getNormalizedDNA();
        boolean shouldMutate = true;

        // Set up the vertices and give them a chance to mutate
        for (int i = 0; i < 6; i++)
        {
          newGene[i] = normalizedTriangle[i];
          // step 1 - possibly randomize the vertex completely
          if (shouldMutate)
          {
            //if (RAND.nextFloat() <= vertexMutationChance) newGene[i] = RAND.nextFloat();
            // step 2 - mutate randomly based on the max change amount
            if (RAND.nextFloat() <= vertexMutationChance)
            {
              numMutations++;
              newGene[i] += generateMutationAmount(changeAmountMax, changeAmountBound);
              newGene[i] = bound(newGene[i], 0.0f, 1.0f);
              //if (genome.getFitness() > .99) shouldMutate = false;
            }
            // step 3 - mutate randomly based on the min change amount
            if (RAND.nextFloat() <= vertexMutationChance)
            {
              numMutations++;
              newGene[i] += generateMutationAmount(changeAmountMin, changeAmountBound);
              newGene[i] = bound(newGene[i], 0.0f, 1.0f);
              //if (genome.getFitness() > .99) shouldMutate = false;
            }
          }
        }

        // Set up the colors and give them a chance to mutate
        for (int i = 6; i < newGene.length; i++)
        {
          newGene[i] = normalizedTriangle[i];
          if (RAND.nextFloat() <= colorMutationChance && shouldMutate)
          {
            numMutations++;
            // strict equals rather than additive mutation
            newGene[i] += generateMutationAmount(colorChangeAmount, 255.0f);
            newGene[i] = bound(newGene[i], 0.0f, 1.0f);
            //if (genome.getFitness() > .99) shouldMutate = false;
          }
        }

        spinoff.add(MANAGER.revertNormalization(newGene));
        index++;
        //if (index >= numTrianglesToMutate) shouldMutate = false;
      }

      //System.out.println(numMutations);
      ((Engine) engine).incrementGenerationCount();
      spinoff.setFitness(function.generateFitness(engine, spinoff));
      best = evaluate(best, spinoff);
    }
    completeStep(best);
    //if (temperature > 0.01) temperature = temperature * friction;
    //else temperature = 0.01;
  }

  private Genome evaluate(Genome currentBest, Genome spinoff)
  {
    if (currentBest == null) return spinoff;
    else if (currentBest.getFitness() < spinoff.getFitness()) return spinoff;
    return currentBest;
  }

  private void completeStep(Genome best)
  {
    double value = Math.pow(Math.E,
                            ((genome.getFitness() - best.getFitness()) / temperature));
    //System.out.println(value);
    double minval = 0.006;
    if ((temperature > minval && RAND.nextDouble() <= value) || best.getFitness() >= genome.getFitness())
    {
      temperature = temperature * friction;
      if (temperature < minval) temperature = minval;
      genome.clear();
      for (float[] triangle : best.getTriangles()) genome.add(triangle);
      genome.setFitness(best.getFitness());
    }
    else best.clear();

    /*
    if (best.getFitness() >= genome.getFitness())
    {
      genome.clear();
      for (float[] triangle : best.getTriangles()) genome.add(triangle);
      genome.setFitness(best.getFitness());
    }
    else best.clear();
    */
  }

  private float generateMutationAmount(int bound, float normalize)
  {
    float mutation = RAND.nextInt(bound) / normalize;
    if (RAND.nextFloat() < 0.5f) mutation *= -1;
    return mutation;
  }

  private float bound(float value, float min, float max)
  {
    if (value > max) return max;
    else if (value < min) return min;
    return value;
  }
}
