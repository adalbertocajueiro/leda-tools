package br.edu.ufcg.ccc.leda.graph;

import java.util.ArrayList;
import java.util.Iterator;

public class Serie<Tx, Ty> extends ArrayList<Point<Tx, Ty>> {

	private String name;
	private int colorCode;

	public Serie(String name, int colorCode) {
		this.name = name;
		this.colorCode = colorCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getColorCode() {
		return colorCode;
	}

	public void setColorCode(int colorCode) {
		this.colorCode = colorCode;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("values:[" + this.toStringPoints() + "]");
		sb.append(",key:\"" + this.getName() + "\"");
		sb.append(",color:colors[" + this.getColorCode() + "]");
		sb.append("}");

		return sb.toString();
	}

	private String toStringPoints() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Point<Tx, Ty>> iterator = this.iterator(); iterator
				.hasNext();) {
			Point<Tx, Ty> point = (Point<Tx, Ty>) iterator.next();
			sb.append(point.toString());
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

}
