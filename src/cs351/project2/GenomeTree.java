package cs351.project2;

import cs351.core.Genome;
import cs351.core.Mutator;
import cs351.core.Tribe;

import java.util.Collection;

/**
 * Implements the Tribe interface using a tree.
 */
public class GenomeTree implements Tribe
{
  @Override
  public Mutator getMutatorForGenome(Genome genome) throws RuntimeException {
    return null;
  }

  @Override
  public void add(Genome genome) {

  }

  @Override
  public void remove(Genome genome) {

  }

  @Override
  public void clear() {

  }

  @Override
  public boolean contains(Genome genome) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Genome get(int index) {
    return null;
  }

  @Override
  public Collection<Genome> getGenomes() {
    return null;
  }

  @Override
  public void recalculate() {

  }
}
