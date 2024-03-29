package cs351.project2;

import cs351.core.Engine.EvolutionEngine;
import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.Tribe;
import cs351.project2.hillclimbing.AdaptiveHillClimbing;

import java.util.*;

/**
 * An OrderedGenomeList provides an easy way for storing Genomes and for
 * maintaining their order. If external sources modify the Genome fitness values,
 * sort() can be called manually. Otherwise, sort() will be called internally
 * whenever it is critical that the list be resorted (such as when returning an
 * iterator for the list after things had been added).
 *
 * @author Justin
 */
public final class OrderedGenomeList implements Tribe, Iterable<Genome>
{
  private static final int DEFAULT_CAPACITY = 25; // for initial array
  private int size = 0;
  private int internalCapacity;
  private Genome[] list;
  private boolean isDirty = false; // true if the list needs reordering
  private EvolutionEngine engine;

  /**
   * Genome Iterator that is returned by the iterator() function.
   * @author Justin
   */
  private final class GenomeIterator implements Iterator<Genome>
  {
    int currIndex = 0;

    @Override
    public boolean hasNext()
    {
      return currIndex < size();
    }

    @Override
    public Genome next()
    {
      if (!hasNext()) throw new NoSuchElementException("Iterator out of bounds");
      int index = currIndex;
      ++currIndex;
      return get(index);
    }
  }

  /**
   * Creates a new ordered genome list with the default starting capacity.
   */
  public OrderedGenomeList()
  {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Creates a new ordered genome list with the given initial capacity
   * @param initialCapacity starting capacity for the list
   */
  public OrderedGenomeList(int initialCapacity)
  {
    list = new Genome[initialCapacity];
    internalCapacity = initialCapacity;
  }

  /**
   * Initializes from collection.
   * @param collection collection - retains all values
   */
  public OrderedGenomeList(Collection<Genome> collection)
  {
    this(collection.size());
    for (Genome genome : collection) add(genome);
  }

  @Override
  public Iterator<Genome> iterator()
  {
    return new GenomeIterator();
  }

  @Override
  public Mutator getMutatorForGenome(Genome genome) throws RuntimeException
  {
    Mutator mutator = new AdaptiveHillClimbing();
    mutator.setGenome(genome);
    return mutator;
  }

  @Override
  public void add(Genome genome)
  {
    isDirty = true;
    if (needsToGrow()) grow(); // grow the array if needed
    list[size] = genome;
    genome.setTribe(this); // now a member of this tribe
    ++size;
    engine.incrementPopulationCount();
  }

  @Override
  public void remove(Genome genome)
  {
    // NOTE: this will remove one element if it exists and preserve the order
    // of everything else, so isDirty does not need to be set to true
    int index = indexOf(genome);
    if (index == -1) return; // not found
    genome.setTribe(null); // no longer has a tribe for the time being
    list[index] = null;
    for (int i = index; i < size - 1; i++) list[i] = list[i + 1];
    --size;
    engine.decrementPopulationCount();
  }

  /**
   * Removes the Genome stored at the specified index.
   * @param index valid index into this OrderedGenomeList
   */
  public void removeAt(int index)
  {
    if (!isValidIndex(index)) throw new IllegalArgumentException("Index out of bounds");
    list[index].setTribe(null);
    list[index] = null;
    for (int i = index; i < size - 1; i++) list[i] = list[i + 1];
    --size;
    engine.decrementPopulationCount();
  }

  @Override
  public void clear()
  {
    for (int i = 0; i < size; i++)
    {
      engine.decrementPopulationCount();
      list[i] = null;
    }
    size = 0;
  }

  @Override
  public boolean contains(Genome genome)
  {
    for (int i = 0; i < size; i++) if (list[i].equals(genome)) return true;
    return false;
  }

  @Override
  public int size()
  {
    return size;
  }

  @Override
  public Genome getBest()
  {
    if (isDirty) sort();
    return list[0];
  }

  @Override
  public Collection<Genome> getGenomes()
  {
    if (isDirty) sort();
    LinkedList<Genome> genomes = new LinkedList<>();
    for (int i = 0; i < size; i++) genomes.add(list[i]);
    return genomes;
  }

  @Override
  public void sort() {
    isDirty = false;
    Arrays.sort(list, 0, size, (first, second) -> -1 * Double.compare(first.getFitness(), second.getFitness()));
  }

  /**
   * Passes the address of the engine to this tribe
   */
  @Override
  public void init(EvolutionEngine engine)
  {
    this.engine = engine;
  }

  /**
   * Gets the index of the given genome. If it was not found, -1 is returned.
   *
   * @param genome genome to look for
   * @return the index of the genome if it exists and -1 if it was not found
   */
  public int indexOf(Genome genome)
  {
    for (int i = 0; i < size; i++) if (list[i].equals(genome)) return i;
    return -1;
  }

  /**
   * Gets the genome at the given index.
   *
   * @param index index into the list
   * @return Genome if it exists
   * @throws IllegalArgumentException thrown if the index was not valid
   */
  public Genome get(int index) throws IllegalArgumentException
  {
    if (!isValidIndex(index)) throw new IllegalArgumentException(index + " is not valid");
    return list[index];
  }

  private boolean needsToGrow()
  {
    return size == internalCapacity;
  }

  private void grow()
  {
    internalCapacity *= 2;
    list = Arrays.copyOf(list, internalCapacity);
  }

  private boolean isValidIndex(int index)
  {
    return index >= 0 && index < size;
  }
}