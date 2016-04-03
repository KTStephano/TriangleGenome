package cs351.project2;

public class HistogramTest
{
  public static void main(String[] args)
  {
    Histogram histogram = new Histogram("images/mona-lisa-cropted-100x81.png");
    Histogram.ColorData rData = histogram.getRedColorData();
    Histogram.ColorData gData = histogram.getGreenColorData();
    Histogram.ColorData bData = histogram.getBlueColorData();
    for (int i = 0; i < rData.getNumColorValues(); i++)
    {
      System.out.print("(Color value " + i + ") ");
      System.out.print("R: " + rData.getFrequencyOfColorShade(i) + "; ");
      System.out.print("G: " + gData.getFrequencyOfColorShade(i) + "; ");
      System.out.println("B: " + bData.getFrequencyOfColorShade(i));
    }
  }
}
