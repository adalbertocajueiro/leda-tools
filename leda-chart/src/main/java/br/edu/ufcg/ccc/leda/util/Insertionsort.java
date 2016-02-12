package br.edu.ufcg.ccc.leda.util;

import br.edu.ufcg.ccc.leda.sorting.SortingImpl;
import br.edu.ufcg.ccc.leda.sorting.Util;


/**
 * The insertion sort algorithm consumes the array (element by element) and inserts the elements 
 * into an ordered list. The algorithm must sort the elements from 
 * leftIndex to rightIndex (inclusive). 
 */
public class Insertionsort<T extends Comparable<T>> extends SortingImpl<T> {

	@Override
	protected void sort(T[] array, int leftIndex, int rightIndex) {
		
			for(int i = leftIndex + 1; i <= rightIndex; i++) {
				int auxiliar = i;
				int j = i - 1;
				while(j >= 0 && array[auxiliar].compareTo(array[j]) < 0 ) { //.compareTo(array[j]) < 0){
					Util.swap(array, auxiliar, j);
					auxiliar = j;
					j--;
				}
			}
	}

}
