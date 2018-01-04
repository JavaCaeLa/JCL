package appl.simpleAppl;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarExample2
{
  public Boolean execute()
  {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    dataset.setValue(6.0D, "Science", "Rahul");
    dataset.setValue(8.0D, "Maths", "Rahul");
    dataset.setValue(5.0D, "Science", "Deepak");
    dataset.setValue(3.0D, "Maths", "Deepak");
    dataset.setValue(6.0D, "Science", "Vinod");
    dataset.setValue(9.0D, "Maths", "Vinod");
    dataset.setValue(2.0D, "Science", "Chandan");
    dataset.setValue(4.0D, "Maths", "Chandan");
    JFreeChart chart = ChartFactory.createBarChart3D("Comparison between Students", "Students", "Marks", dataset, PlotOrientation.VERTICAL, true, true, false);
    chart.setBackgroundPaint(Color.white);
    chart.getTitle().setPaint(Color.blue);
    CategoryPlot p = chart.getCategoryPlot();
    p.setRangeGridlinePaint(Color.red);
    ChartFrame frame1 = new ChartFrame("3D Bar Chart", chart);
    frame1.setVisible(true);
    frame1.setSize(300, 300);
    
    DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
    dataset1.setValue(6.0D, "Rahul", "Science");
    dataset1.setValue(8.0D, "Rahul", "Maths");
    dataset1.setValue(5.0D, "Deepak", "Science");
    dataset1.setValue(3.0D, "Deepak", "Maths");
    dataset1.setValue(6.0D, "Vinod", "Science");
    dataset1.setValue(9.0D, "Vinod", "Maths");
    dataset1.setValue(2.0D, "Chandan", "Science");
    dataset1.setValue(4.0D, "Chandan", "Maths");
    JFreeChart chart1 = ChartFactory.createBarChart3D("Comparison between Students - pivoted", "Discipline", "Marks", dataset1, PlotOrientation.VERTICAL, true, true, false);
    chart1.setBackgroundPaint(Color.white);
    chart1.getTitle().setPaint(Color.blue);
    CategoryPlot p1 = chart1.getCategoryPlot();
    p1.setRangeGridlinePaint(Color.red);
    ChartFrame frame2 = new ChartFrame("3D Bar Chart", chart1);
    frame2.setVisible(true);
    frame2.setSize(300, 300);
    
    return new Boolean(true);
  }
}
