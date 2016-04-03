package cs351.project2;

import cs351.core.Cross;

/**
 * TODO this class is pretty bad
 */
public class CrossPhase
{
  private final Engine ENGINE;
  private final Cross CROSS;

  public CrossPhase(Engine engine)
  {
    ENGINE = engine;
    CROSS = new GameCross();
  }
}
