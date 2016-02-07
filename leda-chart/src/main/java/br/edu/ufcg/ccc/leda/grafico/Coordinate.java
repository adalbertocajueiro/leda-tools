package br.edu.ufcg.ccc.leda.grafico;

import java.util.Map.Entry;

/**
 * Class that represents a specific point in a graph
 * 
 * @param <K>
 *            K represents the X-coordinate
 * @param <V>
 *            V Represents the Y-coordinate
 * 
 * @author Gustavo
 */
public class Coordinate<K extends Comparable<K>, V> implements Entry<K, V>,
		Comparable<Coordinate<K, V>> {
	private final K key;
	private V value;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            A value that represents X axis at an specific point
	 * @param value
	 *            A value that represents Y axis at an specific point
	 */
	protected Coordinate(K key, V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return Key value representing a X axis value at the specific point
	 */
	public K getKey() {
		return key;
	}

	/**
	 * @return Value representing a Y axis value at the specific point
	 */
	public V getValue() {
		return value;
	}

	public V setValue(V value) {
		V old = this.value;
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

	public int compareTo(Coordinate<K, V> o) {
		return this.getKey().compareTo(o.getKey());
	}

	@Override
	public String toString() {
		return "Xaxis: " + key.toString() + ", Yaxis: " + value.toString();
	}

}
