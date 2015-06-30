package br.edu.ufcg.ccc.leda.chart.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class LineChart {
	JFreeChart graph;
	
	private String title, 
					x_axis,
					y_axis;
	
	private XYSeriesCollection dataSet = new XYSeriesCollection();

	/**
	 * 
	 */
	public LineChart(){
		this("Default Title","X-axis", "Y-axis ");
	}
	
	/**
	 * @param title String that represents the Chart
	 * @param x_axis  The name of the domain axis
	 * @param y_axis The name of the image axis
	 */
	public LineChart(String title, String x_axis, String y_axis ){
		setTitle(title);
		setXAxis(x_axis);
		setYAxis(y_axis);
		
		
	}
	
	/**
	 * It set a series on the chart. By receiving a group of coordinates it will draw the line chart for the
	 * Series that has been set up. For more than one series it must be called multiple times.
	 * @param coordinates A TreeSet of Coordinate. A coordinate is one pair of Doubles represented by Coordinate Entity. 
	 * @param seriesName A string that represent the series that will be set up, it will be showed on the legend of the chart. 
	 */
	public void setCoordinates(TreeSet<Coordinate<Double, Double>> coordinates, String seriesName){
			XYSeries serie = new XYSeries(seriesName);
			Iterator<Coordinate<Double, Double>> coordIter = coordinates.iterator();
			while(coordIter.hasNext()) {
				Coordinate<Double, Double> next = coordIter.next();
				serie.add(next.getKey(), next.getValue());
			}
			
			dataSet.addSeries(serie);
	}
	
	/**
	 * 	Generates an Image(.png) on the root. The image contains the line chart drawn.
	 * @throws IOException
	 */
	public void generateChart() throws IOException{
		generateChart("DefaultImage.png");
	}
	
	
	/**
	 * Generates an Image on the root. The image contains the line chart drawn.
	 * @param filename String that represent the name of the file and the extension(.jpg, .png). 'myLineChart.png'
	 * @throws IOException
	 */
	public void generateChart(String filename) throws IOException{
		graph = ChartFactory.createXYLineChart(title,x_axis,y_axis,(XYDataset) dataSet, PlotOrientation.VERTICAL, 
                true, true, false);
		chartStyle();
		
		saveImage(graph, filename);
	}
	
	
	
	/**
	 * Generates a Panel just after run the code.
	 */
	public void generateChartPanel(){
		JPanel panel = new ChartPanel(graph);
		
		JFrame frame = new JFrame(title);
		
		frame.add(panel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void  saveImage(JFreeChart graph, String filename) throws IOException {
		OutputStream files = new FileOutputStream(filename);
		ChartUtilities.writeChartAsPNG(files, graph, 500, 400);
		files.close();
	}
	
	private void chartStyle() {
		XYPlot plot = graph.getXYPlot();
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.CYAN);
		renderer.setSeriesPaint(2, Color.GREEN);
		renderer.setSeriesPaint(3, Color.MAGENTA);
		renderer.setSeriesPaint(4, Color.ORANGE);
		renderer.setSeriesPaint(5, Color.YELLOW);
		renderer.setSeriesPaint(6, Color.PINK);
		renderer.setSeriesPaint(7, Color.RED);
		
		
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(4.0f));
		renderer.setSeriesStroke(2, new BasicStroke(4.0f));
		renderer.setSeriesStroke(3, new BasicStroke(4.0f));
		renderer.setSeriesStroke(4, new BasicStroke(4.0f));
		renderer.setSeriesStroke(5, new BasicStroke(4.0f));
		renderer.setSeriesStroke(6, new BasicStroke(4.0f));
		renderer.setSeriesStroke(7, new BasicStroke(4.0f));

		plot.setRenderer(renderer);
		//plot.setOutlinePaint(Color.BLUE);
		//plot.setOutlineStroke(new BasicStroke(2.0f));
		plot.setBackgroundPaint(Color.DARK_GRAY);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		 
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public String getYAxis() {
		return x_axis;
	}

	/**
	 * @param eixoX
	 */
	public void setXAxis(String eixoX) {
		this.x_axis = eixoX;
	}

	/**
	 * @return
	 */
	public String getXAxis() {
		return y_axis;
	}

	/**
	 * @param eixoY
	 */
	public void setYAxis(String eixoY) {
		this.y_axis = eixoY;
	}
	
	
}