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
	private static FileInputStream[] is;
	private static InputStreamReader isr;
	
	private DataStream() throws IOException{
		setIs(new File(""));
		setIsr(null);
	}
	
	
	
	public static List<BufferedReader> getData() throws IOException {
		List<BufferedReader> dataStreamed = new ArrayList<BufferedReader>();
		
		for (File fi : file.listFiles()) {
			setIs(fi); 
			//setIsr(is);
			
			//BufferedReader br = new BufferedReader(isr);
			
			//dataStreamed.add(br);
		}
		
		
		return dataStreamed;	
	}
	
	public static void closeStream() throws IOException{
				isr.close();
	}

	public static void setIs(File fi) throws IOException {
		is = new FileInputStream[fi.listFiles().length];
		
		for(int i = 0; i < fi.listFiles().length; i ++){
			System.out.println(fi.listFiles()[i].getCanonicalPath());
			is[i] = new FileInputStream(fi.listFiles()[i].getCanonicalPath());
		}
	}
	
	public static void setIsr(FileInputStream is) throws FileNotFoundException {
		isr = new InputStreamReader(is);
	}
}
