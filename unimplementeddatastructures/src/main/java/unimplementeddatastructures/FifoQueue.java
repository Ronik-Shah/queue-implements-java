package unimplementeddatastructures;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

// TODO: Implement copy, hashCode, equals, compareTo, serialize, deserialize
public class FifoQueue<E> extends AbstractQueue<E> {

	private static final int DEFUALT_CAPACITY = 5;

	private Object[] elements;
	private int front;
	private int rear;
	private int size;
	private int capacity;
	private int modCount;

	public FifoQueue() {
		this(DEFUALT_CAPACITY);
	}

	public FifoQueue(int capacity) {
		front = 0;
		rear = 0;
		size = 0;
		modCount = 0;
		elements = new Object[capacity + 1];
		this.capacity = capacity;
	}

	public FifoQueue(Collection<? extends E> collection) {
		this(collection.size() < DEFUALT_CAPACITY ? DEFUALT_CAPACITY : collection.size());

		for (E e : collection) {
			if (e == null) {
				throw new NullPointerException();
			}
			rear++;
			elements[rear] = e;
		}

		size = rear - front;
	}

	@Override
	public boolean offer(E e) {

		if (e == null) {
			throw new NullPointerException();
		}

		if (isFull()) {
			regrow(capacity + capacity >>> 1);
		}

		rear = nextInd(rear, elements.length);
		elements[rear] = e;
		size++;
		modCount++;
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E poll() {
		if (isEmpty())
			return null;

		front = nextInd(front, elements.length);
		E value = (E) elements[front];
		elements[front] = null;
		size--;
		modCount++;
		return value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E peek() {
		return (E) elements[nextInd(front, elements.length)];
	}

	@Override
	public Iterator<E> iterator() {
		return new QueueIterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		return new QueueSpliterator();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	// TODO: Optimize the logic by using System.array.
	// Optimization : Instead of creating new array, redefine front and rear, just
	// move front to enough units so that the portion before the front is just
	// copied directly.
	// Use Systems.arrayCopy to efficiently copy the array.
	@Override
	public boolean addAll(Collection<? extends E> c) {
		int collectionSize = c.size();
		if (capacity < (size + collectionSize)) {
			regrow(capacity + collectionSize);
		}

		for (E e : c) {
			if (e == null) {
				throw new NullPointerException();
			}

			rear++;
			elements[rear] = (E) e;
		}

		size = rear - front;
		capacity = collectionSize;
		return true;
	}

	public List<Integer> debugAll() {
		return Arrays.asList(capacity, size, front, rear);
	}

	private boolean isFull() {
		return size == capacity;
	}

	// TODO: Optimize the logic by using System.array.
	// Optimization : Instead of creating new array, redefine front and rear, just
	// move front to enough units so that the portion before the front is just
	// copied directly.
	// Use Systems.arrayCopy to efficiently copy the array.
	private void regrow(int newCapacity) {
		Object[] newElements = new Object[newCapacity + 1];

		int j = 1;

		for (int i = front + 1; i <= rear; nextInd(i++, elements.length)) {
			newElements[j] = elements[i];
			j++;
		}
		modCount++;
		elements = newElements;
	}

	@SuppressWarnings("unused")
	private static int prevInd(int i, int modulus) {
		if (i == 0)
			return modulus - 1;
		return i;
	}

	private static int nextInd(int i, int modulus) {
		if (i == modulus)
			return 0;
		return i + 1;
	}

	private class QueueIterator implements Iterator<E> {

		private int currModCount;
		private int cursor;

		public QueueIterator() {
			cursor = nextInd(front, elements.length);
			currModCount = modCount;
		}

		@Override
		public boolean hasNext() {
			return cursor != nextInd(rear, elements.length);
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			if (modCount != currModCount)
				throw new ConcurrentModificationException();
			if (!hasNext())
				throw new NoSuchElementException();
			E curr = (E) elements[cursor];
			cursor = nextInd(cursor, elements.length);
			return curr;
		}

	}

	// TODO: implement new Spliterator
	private class QueueSpliterator implements Spliterator<E> {

		@Override
		public boolean tryAdvance(Consumer<? super E> action) {
			return false;
		}

		@Override
		public Spliterator<E> trySplit() {
			return null;
		}

		@Override
		public long estimateSize() {
			return 0;
		}

		@Override
		public int characteristics() {
			return 0;
		}

	}

}
