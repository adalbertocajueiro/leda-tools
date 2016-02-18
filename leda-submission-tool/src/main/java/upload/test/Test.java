package upload.test;

import java.util.Iterator;
import java.util.Scanner;

import upload.util.Utilities;

/**
 * Classe que representa um teste a ser realizado.
 * @author Adalberto
 * @see upload.util.Utilities
 */
public class Test {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		System.out.println("User Home Path: "+ System.getProperty("user.home"));
		Utilities.load();
		Iterator<String> it = Utilities.getNames().iterator();
		while (it.hasNext()) {
			String e = (String) it.next();
			System.out.println(e);
		}
		
		//System.out.println(Utilities.containsName("ADALBERTO CAJUEIRO DE FARIAS"));
		//Utilities.removeName("ADALBERTO CAJUEIRO DE FARIAS");
		//Utilities.createFolder("lista1");
		System.out.println(Utilities.containsName("ADALBERTO CAJUEIRO DE FARIAS"));
		//InputStreamReader isr = new InputStreamReader(System.in);
		Scanner sc = new Scanner(System.in);
		System.out.print("Digite um numero: ");
		//int lido = System.in.read();
		
		int i = sc.nextInt();
		
		System.out.println("Numero lido: " + i);
		for (int j = 0; j < 10; j++) {
			
		}
		sc.close();


	}

}
