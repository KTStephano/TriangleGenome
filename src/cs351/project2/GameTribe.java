package cs351.project2;


import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.Tribe;

import java.util.ArrayList;
import java.util.Collection;

/**
 * GameTribe is initially called by GamePopulation when a new tribe is created. Upon initialization, GameTribe will
 * by default create 2,000 genomes (as of @version 03-26-16).
 */
public class GameTribe implements Tribe
{
  private int numGenomes = 0; // counter of genomes in this specific tribe
  private Collection<Genome> genomeCollection = new ArrayList<>();

  /**
   * Gets the mutator object that was assigned to the genome when
   * it was added to the tribe.
   *
   * @param genome genome object that is apart of the population
   * @return Mutator object for the given genome
   * @throws RuntimeException thrown if the given genome is not apart of the population
   */
  @Override
  public Mutator getMutatorForGenome(Genome genome) throws RuntimeException
  {
    return null;
  }

  /**
   * Adds a genome to the tribe.
   *
   * @param genome genome to add
   */
  @Override
  public void add(Genome genome)
  {
    genomeCollection.add(genome);
    numGenomes ++;
  }

  /**
   * Removes a genome from the tribe.
   *
   * @param genome genome to remove
   */
  @Override
  public void remove(Genome genome)
  {
  }

  /**
   * Clears the tribe of all genomes.
   */
  @Override
  public void clear()
  {
    while(genomeCollection != null)
    {
      genomeCollection.remove(0);
      numGenomes --;
    }
  }

  /**
   * Checks to see if the genome is part of the tribe.
   *
   * @param genome genome to check for
   * @return true if it exists and false if not
   */
  @Override
  public boolean contains(Genome genome)
  {
    return false;
  }

  /**
   * Gets the number of genomes that are part of the tribe.
   *
   * @return number of genomes
   */
  @Override
  public int size()
  {
    return 0;
  }

  /**
   * Gets the best genome in the tribe.
   *
   * @return best genome
   */
  @Override
  public Genome getBest()
  {
    return null;
  }

  /**
   * Returns an ordered list of genomes. The first is the most fit and the last
   * is the least fit.
   *
   * @return ordered list of genomes
   */
  @Override
  public Collection<Genome> getGenomes()
  {
    return null;
  }

  /**
   * Runs through all genomes in the tribe and reorders them based on their fitness.
   * <p>
   * This is needed since the genomes in a population might mutate at some point, but
   * the tribe won't know about these mutations.
   */
  @Override
  public void recalculate()
  {

  }

  /**
   * Initialize function for the Tribe class. This method will generate a specified number of genomes
   * for this instance of the tribe.
   *
   * This function will call recalculate in order to exit the method with an already organized list of genomes.
   */
  @Override
  public void init()
  {
    System.out.println(">>> Inside GameTribe init");
    genomeCollection = new ArrayList<>();
    for(int i = 0; i < numGenomes; i++)
    {
      add(new Genome());
    }

    // TODO: Write method "recalculate"
    recalculate();
  }
}
