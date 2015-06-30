package br.edu.ufcg.ccc.leda.chart.util;

import java.util.Map.Entry;


public class Coordinate<K extends Comparable<K>, V> implements Entry<K, V>, Comparable<Coordinate<K,V>>{
	private final K key;
    private V value;

    public Coordinate(K key, V value) {
        this.key = key;
        this.value = value;
    }
	
	
	@Override
	public K getKey() {
		return (K) key;
	}

	@Override
	public V getValue() {
		return (V) value;
	}

	@Override
	public V setValue(V value) {
		V old = (V) this.value;
        this.value = value;
        return old;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate<K, V> other = (Coordinate<K, V>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public int compareTo(Coordinate<K, V> o) {
		return this.getKey().compareTo(o.getKey());
	}
	
	
	
}
