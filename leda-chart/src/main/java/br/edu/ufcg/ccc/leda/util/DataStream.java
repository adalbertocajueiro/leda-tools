package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataStream {
	
	private static File file = new File(PathsEnum.DATA_SOURCE.getPath());
	private static FileInputStream is;
	private static InputStreamReader isr;
	
	private DataStream() throws FileNotFoundException{
		setIs(null);
	}
	
	
	
	public static List<BufferedReader> getData() throws IOException {
		List<BufferedReader> dataStreamed = new ArrayList<BufferedReader>();
		
		for (File fi : file.listFiles()) {
			setIs(fi); 
			setIsr(is);
			
			BufferedReader br = new BufferedReader(isr);
			
			dataStreamed.add(br);
		}
		
		
		return dataStreamed;	
	}
	
	public static void closeStream() throws IOException{
		is.close();
		isr.close();
	}

	public static void setIs(File fi) throws FileNotFoundException {
		is = new FileInputStream(fi);
	}
	
	public static void setIsr(FileInputStream is) throws FileNotFoundException {
		isr = new InputStreamReader(is);
	}
}
