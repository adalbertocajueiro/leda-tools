package br.edu.ufcg.ccc.leda.util;

import java.util.ArrayList;
import java.util.List;

public class InputGenerator {

	private Integer[] sizes = { 10, 100, 500, 1000, 2000, 4000, 6000, 10000,
			15000, 20000 };

	// {10,100,500,1000,5000,10000,50000,100000,500000}
	public List<List<Integer>> generateWorstCases() {
		List<List<Integer>> result = new ArrayList<List<Integer>>();

		for (Integer size : sizes) {
			ArrayList<Integer> newList = new ArrayList<Integer>();
			for (int i = size; i > 0; i--) {
				newList.add(i);
			}
			result.add(newList);
		}

		return result;
	}

	public static void main(String[] args) {
		InputGenerator ig = new InputGenerator();
		List list = ig.generateWorstCases();
		int i = 0;
	}
}
