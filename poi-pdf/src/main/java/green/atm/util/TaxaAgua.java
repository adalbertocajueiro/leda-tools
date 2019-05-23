package green.atm.util;

import java.text.DecimalFormat;

public class TaxaAgua {
	public static double calculaPreco(double consumo) {
	double result = 0.0;
	
	if(consumo < 10.0) {
		result = 37.91;
	} else if (consumo >= 10.0 && consumo < 20.0) {
		result = 37.91 + (consumo - 10)*4.89;
	} else if (consumo >= 20.0 && consumo < 30.0) {
		result = 37.91 + 10*4.89 + (consumo - 20)*6.45;
	} else if (consumo >= 30.0) {
		result = 37.91 + 10*4.89 + 10*6.45 + (consumo - 30)*8.76;
	}
	
	return result;
		
		
	}
	
	public static void main(String[] args) {
		Double[] consumos = {54.293, 17.912, 6.172, 24.652, 107.381, 0.637, 27.942, 39.702, 23.849, 0.699, 13.172, 34.328, 24.277, 163.916, 38.716, 3.531,
				32.676, 19.946, 17.1, 32.171, 15.904, 17.142, 23.113, 7.794, 20.495, 21.05, 0.123, 10.064, 10.234, 43.294, 30.677, 5.863, 42.397,
				42.112, 14.469, 4.79, 25.155, 156.52, 38.704, 103.193};
		
		for (Double consumo : consumos) {
			System.out.println(consumo);
		}
		
		for (Double consumo : consumos) {
			System.out.println(calculaPreco(consumo));
		}
	}
}
