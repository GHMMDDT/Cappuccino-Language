package cappuccino;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ArrayDeque<Value> implements Iterable<Value> {
	private int peek;
	private Value[] array;
	private int size;
	private int head;
	private int tail;

	@SuppressWarnings("unchecked")
	public ArrayDeque(int capacity) {
		array = (Value[]) new Object[Math.max(16, capacity)];
		head = tail = size = 0;
	}

	public ArrayDeque(ArrayDeque<Value> values) {
		this((int) (values.size() * 1.5));
		for (Value value : values) {
			addLast(value);
		}
	}

	@SafeVarargs
	public ArrayDeque(Value... values) {
		this((int) (values.length * 1.5));
		for (Value value : values) {
			addLast(value);
		}
	}

	public ArrayDeque() {
		this(16);
	}

	private int readIndex(int index) {
		return (head + index) % array.length;
	}

	private void ensureCapacity() {
		if (size == array.length) {
			int newCapacity = array.length * 2;
			@SuppressWarnings("unchecked")
			Value[] newArray = (Value[]) new Object[newCapacity];
			for (int i = 0; i < size; i++) {
				newArray[i] = array[readIndex(i)];
			}
			array = newArray;
			head = 0;
			tail = size;
		}
	}

	public Value get(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException(index + " >= " + size);
		}
		return array[readIndex(index)];
	}

	public Value getFirst() {
		if (size == 0) throw new NoSuchElementException("Deque is empty");
		return array[head];
	}

	public Value getLast() {
		if (size == 0) throw new NoSuchElementException("Deque is empty");
		return array[(tail - 1 + array.length) % array.length];
	}

	public void set(Value element, int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException(index + " ?= " + size);
		}
		array[readIndex(index)] = element;
	}

	public void setFirst(Value element) {
		if (size == 0) throw new NoSuchElementException("Deque is empty");
		array[head] = element;
	}

	public void setLast(Value element) {
		if (size == 0) throw new NoSuchElementException("Deque is empty");
		array[(tail - 1 + array.length) % array.length] = element;
	}

	public Value add(Value element, int index) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException(index + " ?= " + size);
		}
		ensureCapacity();
		if (index == 0) {
			addFirst(element);
		} else if (index == size) {
			addLast(element);
		} else if (index < size / 2) {
			head = (head - 1 + array.length) % array.length;
			for (int i = 0; i < index; i++) {
				array[readIndex(i)] = array[readIndex(i + 1)];
			}
		} else if (index > size / 2) {
			tail = (tail + 1) % array.length;
			for (int i = size; i > index; i--) {
				array[readIndex(i)] = array[readIndex(i - 1)];
			}
		}
		return element;
	}

	public Value addLast(Value element) {
		ensureCapacity();
		array[tail] = element;
		tail = (tail + 1) % array.length;
		size++;
		return element;
	}

	public Value addFirst(Value element) {
		ensureCapacity();
		head = (head - 1 + array.length) % array.length;
		array[head] = element;
		size++;
		return element;
	}

	public void addAll(ArrayDeque<Value> deque, int index) {
		for (int i = 0; i < deque.size(); i++) {
			add(deque.get(i), index);
		}
	}

	public void addAllFirst(ArrayDeque<Value> deque) {
		for (int i = 0; i < deque.size(); i++) {
			addFirst(deque.get(i));
		}
	}

	public void addAllLast(ArrayDeque<Value> deque) {
		for (int i = 0; i < deque.size(); i++) {
			addLast(deque.get(i));
		}
	}

	public void remove(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}
		if (index == 0) {
			removeFirst();
		} else if (index == (size - 1)) {
			removeLast();
		} else if (index < size / 2) {
			for (int i = index; i > 0; i--) {
				array[readIndex(i)] = array[readIndex(i - 1)];
			}
			array[head] = null;
			head = (head + 1) % array.length;
		} else if (index > size / 2) {
			for (int i = index; i < size - 1; i++) {
				array[readIndex(i)] = array[readIndex(i + 1)];
			}
			tail = (tail - 1 + array.length) % array.length;
			array[tail] = null;
		}
	}

	public void removeFirst() {
		if (size == 0) {
			throw new IndexOutOfBoundsException();
		}
		array[head] = null;
		head = (head + 1) % array.length;
		size--;
	}

	public void removeLast() {
		if (size == 0) {
			throw new IndexOutOfBoundsException();
		}
		tail = (tail - 1 + array.length) % array.length;
		array[tail] = null;
		size--;
	}

	public Value pop(int index) {
		Value element = get(index);
		remove(index);
		return element;
	}

	public Value popFirst() {
		Value element = getFirst();
		removeFirst();
		return element;
	}

	public Value popLast() {
		Value element = getLast();
		removeLast();
		return element;
	}

	public Value peek(int index) {
		peek = index;
		return get(peek++);
	}

	public Value peek() {
		return peek(peek);
	}

	public int size() {
		return size;
	}

	public int capacity() {
		return array.length;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int indexOf(Value element) {
		if (size == 0) {
			return -1;
		}
		for (int i = 0; i < size; i++) {
			if (get(i).equals(element)) {
				return i;
			}
		}
		return -1;
	}

	public boolean contains(Value element) {
		return indexOf(element) >= 0;
	}


	@SuppressWarnings("unchecked")
	public void clear() {
		array = (Value[]) new Object[16];
		head = tail = size = 0;
	}

	public Value[] getArray() {
		Value[] values = (Value[]) new Object[size()];
		for (int i = 0; i < size; i++) {
			values[i] = get(i);
		}
		return values;
	}

	public List<Value> getList() {
		List<Value> values = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			values.add(get(i));
		}
		return values;
	}

	@Override
	public Iterator<Value> iterator() {
		return new Iterator<Value>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < size;
			}

			@Override
			public Value next() {
				if (!hasNext()) throw new NoSuchElementException();
				return get(index++);
			}
		};
	}

	public Stream<Value> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	@Override
	public Spliterator<Value> spliterator() {
		return Spliterators.spliterator(this.getArray(), Spliterator.ORDERED);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		forEach(value -> {
			sb.append(value).append(", ");
		});
		if (sb.length() > 1) {
			sb.setLength(sb.length() - 2); // Remove the last comma and space
		}
		return sb.append(']').toString();
	}
}