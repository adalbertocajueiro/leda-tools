package br.edu.ufcg.ccc.leda.chart.util;

import java.io.IOException;
import java.util.TreeSet;


public class Sample {

	public static void main(String[] args) throws IOException {
		// Set of Coordinate
		TreeSet<Coordinate<Double,Double>> serie1, serie2, serie3;
		
		//Create series 1 
		serie1 = createCoordinates();
		
		//Create Series 2
		serie2 = createCoordinates2();
		
		serie3 = createCoordinates3();
		
		//Create Graph
		LineChart graph = new LineChart();
		
		//Set Series 1
		graph.setCoordinates(serie1, "Serie 1");
		
		//Set Series 2
		graph.setCoordinates(serie2, "Serie 2");
		
		//Set Series 3
		graph.setCoordinates(serie3, "Polinomial Function Grade 2");
		
		//Set Additional information to the graph
		graph.setTitle("Exemplo 1");
		graph.setXAxis("Eixo x");
		graph.setYAxis("Eixo y");
		
		//Create just an Image of the chart on the root of the project
		graph.generateChart("MeuGrafico.png");
		
		//Opens a Panel showing the chart
		graph.generateChartPanel();
		
	}

	private static TreeSet<Coordinate<Double, Double>> createCoordinates() {
		//Create a TreeSet of Coordinates
		TreeSet<Coordinate<Double,Double>> coordinates = new TreeSet<Coordinate<Double,Double>>();
		
		//Create single coordinates
		Coordinate<Double,Double> point_a = new Coordinate<Double,Double>(0.,0.);
		Coordinate<Double,Double> point_b = new Coordinate<Double,Double>(1.,1.);
		Coordinate<Double,Double> point_c = new Coordinate<Double,Double>(2.,5.);
		Coordinate<Double,Double> point_d = new Coordinate<Double,Double>(3.,2.);
		
		//Insert the coordinate into the TreeSet
		coordinates.add(point_a);
		coordinates.add(point_b);
		coordinates.add(point_c);
		coordinates.add(point_d);
		
		return coordinates;
	}
	
	private static TreeSet<Coordinate<Double, Double>> createCoordinates2() {
		//Create a TreeSet of Coordinates
		TreeSet<Coordinate<Double,Double>> coordinates = new TreeSet<Coordinate<Double,Double>>();
		
		//Create single coordinates
		Coordinate<Double,Double> point_a = new Coordinate<Double,Double>(1.,0.);
		Coordinate<Double,Double> point_b = new Coordinate<Double,Double>(2.,1.);
		Coordinate<Double,Double> point_c = new Coordinate<Double,Double>(3.,5.);
		Coordinate<Double,Double> point_d = new Coordinate<Double,Double>(4.,2.);
		
		//Insert the coordinate into the TreeSet
		coordinates.add(point_a);
		coordinates.add(point_b);
		coordinates.add(point_c);
		coordinates.add(point_d);
		
		return coordinates;
	}
	
	private static TreeSet<Coordinate<Double, Double>> createCoordinates3() {
		//Create a TreeSet of Coordinates
		TreeSet<Coordinate<Double,Double>> coordinates = new TreeSet<Coordinate<Double,Double>>();
		
		
		for(double i = 0; i < 5; i ++) {
			double y = i * i + 2*i + 1;
			
			//Create single coordinates
			Coordinate<Double,Double> point = new Coordinate<Double,Double>(i,y);
			
			//Insert the coordinate into the TreeSet
			coordinates.add(point);
		}
		return coordinates;
	}
}
