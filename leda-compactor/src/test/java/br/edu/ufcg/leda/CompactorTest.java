package br.edu.ufcg.leda;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import br.edu.ufcg.leda.util.Compactor;

public class CompactorTest {

	@Test
	public void testCompact01() throws IOException{
		Compactor fu = new Compactor();
		File folder = new File("D:\\trash2\\leda-upload\\2016.1\\PP1-01");
		File destZipFile = new File("D:\\trash2\\leda-upload\\2016.1\\PP1-01.zip");
		if(folder.exists()){
			fu.zipFolder(folder, destZipFile);
		}
		System.out.println("Compaction finished!!!");
	}
}
