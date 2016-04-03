package cs351.project2;

import cs351.core.Cross;
import cs351.core.Tribe;

/**
 * TODO this class is pretty bad
 */
public class CrossPhase
{
  private final Engine ENGINE;
  private final Tribe TRIBE;
  private final Cross CROSS;

  public CrossPhase(Engine engine, Tribe tribe)
  {
    ENGINE = engine;
    TRIBE = tribe;
    CROSS = new GameCross();
  }
}
