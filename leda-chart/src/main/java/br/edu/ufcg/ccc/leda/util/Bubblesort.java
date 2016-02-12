package br.edu.ufcg.ccc.leda.util;

import br.edu.ufcg.ccc.leda.sorting.SortingImpl;
import br.edu.ufcg.ccc.leda.sorting.Util;


/**
 * The bubble sort algorithm pushes big elements to the right or small elements to 
 * the left by exchanging adjacent elements. The algorithm must sort the elements from 
 * leftIndex to rightIndex (inclusive). 
 */
public class Bubblesort<T extends Comparable<T>> extends SortingImpl<T> {

	@Override
	protected void sort(T[] array, int leftIndex, int rightIndex) {
		for(int i = leftIndex; i <= rightIndex; i++) {
			for(int j = leftIndex; j <= rightIndex; j++) {
				if(array[i].compareTo(array[j]) < 0) {
					Util.swap(array, j, i);
				}
			}
			
		}
	}

}
