package cs351.core;

/**
 * See slide 17.
 *
 * A FitnessFunction provides a way to quickly evaluate the fitness
 * of a given genome. No matter how a fitness function represents
 * fitness values internally, the outputted result should always
 * be on the range of [0.0, 1.0].
 *
 * The fitness function relies on an external source being able to
 * take a target image and break it down into a Genome that is similar
 * in structure to all other Genomes in the population. For example, if
 * all the Genomes in the population have 200 TRIANGLES which each have
 * 10 genes, then the target Genome should also be represented with this
 * same structure.
 */
public interface FitnessFunction
{
  void setTargetGenome(Genome genome);

  /**
   * Takes two Genomes and compares them based on their fitness.
   *
   * @param first first genome
   * @param second second genome
   * @return the better genome
   */
  Genome compare(Genome first, Genome second);

  /**
   * Takes the given genome, compares it to the target genome and outputs
   * a normalized fitness value.
   *
   * @param genome genome to generate a fitness for
   * @return normalized fitness for the given genome
   */
  double generateFitness(Genome genome);

  /**
   * Returns the maximum fitness for a Genome as a non-normalized integer. This
   * can be used to undo a normalized fitness (generateFitness(genome) * getMaxFitness() =
   * non-normalzed fitness of a genome).
   *
   * @return maximum fitness for any genome as used by this fitness function
   */
  int getMaxFitness();
}
