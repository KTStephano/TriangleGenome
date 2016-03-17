package cs351.core;

/**
 * Each Gene object also has a GeneType. This lets us know whether
 * it is a vertex component, color, etc.
 *
 * See slide 23.
 */
public enum GeneTypes
{
  TRIANGLE_VERTEX_X,
  TRIANGLE_VERTEX_Y,
  TRIANGLE_COLOR_RED,
  TRIANGLE_COLOR_GREEN,
  TRIANGLE_COLOR_BLUE,
  TRIANGLE_COLOR_TRANSPARENT
}
