package br.edu.ufcg.ccc.leda.graph;

public class Point<Tx, Ty> {
	private Tx x;
	private Ty y;

	public Point(Tx x, Ty y) {
		super();
		this.x = x;
		this.y = y;
	}

	public void setxValue(Tx xValue) {
		this.x = xValue;
	}

	public Tx getX() {
		return x;
	}

	public void setX(Tx x) {
		this.x = x;
	}

	public Ty getY() {
		return y;
	}

	public void setY(Ty y) {
		this.y = y;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("x:" + this.getX());
		sb.append(",");
		sb.append("y:" + this.getY());
		sb.append("}");

		return sb.toString();
	}

}
