package genericBuffer;

import java.util.LinkedList;
import java.util.Queue;

public class GenericBuffer<T> {


	private Queue<T> queue = new LinkedList<T>();
	private int capacity;

	
	public GenericBuffer(int capacity) {
		this.capacity = capacity;
	}


	public synchronized void put(T item) throws InterruptedException {

		while (1 + queue.size() > capacity) {
			wait();
		}
		queue.add(item);
		notifyAll();
	}



	public synchronized T get() throws InterruptedException {

		while (1 > queue.size()) {
			wait();
		}
		T item = queue.remove();
		notifyAll();
		return item;
	}
}
