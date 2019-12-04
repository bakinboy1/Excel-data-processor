package emailer;

import java.util.ArrayList;

public class Arr {
	// create arraylist instance
	private static Arr a;
	private ArrayList<Person> arr = null;

	// create getter unnecessary
	public Arr() {
		arr = new ArrayList<Person>();
	}

	public static Arr getArr() {
		if (a == null) {
			a = new Arr();
		}
		return a;
	}

	public ArrayList<Person> getList() {
		return this.arr;
	}

}
