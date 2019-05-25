package green.atm.util;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.ListIterator;

public class OrderedLinkedList<T extends Comparable<T>> extends LinkedList<T> {

	/**
	 * Acrescenta um novo elemento na lista de forma ordenada
	 */
	@Override
	public boolean add(T e) {
		// Vai varrendo a lista ate encontrar a posicao que uma transacao tem que ser
		// inserida
		// vai inserir pela data. Se a transacao ja existe entao ela nao é alterada
		// se ela nao existe entao a nova é inserida na ordem
		boolean result = false;

		ListIterator<T> iterator = this.listIterator();
		if (iterator.hasNext()) {
			while (iterator.hasNext()) {
				T t = (T) iterator.next();
				if (t.compareTo(e) > 0) {
					if (iterator.hasPrevious()) {
						iterator.previous();
						iterator.add(e);	
					} else {
						this.addFirst(e);
					}
					result = true;
					break;
				} else if (t.compareTo(e) == 0) { //aqui no caso de transacoes, compara apenas as datas
					if (e.equals(t)) { //aqui compara tambem o valor e numero do doc
						break;
					} else {
						if(!iterator.hasNext()) {
							iterator.add(e);
						}
					}
				} else if (t.compareTo(e) < 0) {
					if(!iterator.hasNext()) {
						iterator.add(e);
					}
				}
			}
		} else {
			this.addFirst(e);
			result = true;
		}
		return result;
	}

	public static void main(String[] args) {
		OrderedLinkedList<Integer> list = new OrderedLinkedList<Integer>();
		list.add(5);
		list.add(3);
		list.add(new Integer(3));
		list.add(7);
		list.add(2);
		int i = 0;
		GregorianCalendar d = new GregorianCalendar(2019,12,31);
		SimpleDateFormat df = new SimpleDateFormat("DD/mm/YYYY");
		System.out.println(df.format(d.getTime()));
	}

}
