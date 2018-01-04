package implementations.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicQueue<T> {
	private final AtomicInteger writeIndex = new AtomicInteger();
	private final AtomicInteger readIndex = new AtomicInteger();
	private final AtomicReferenceArray<T> queue;

	public AtomicQueue (int capacity) {
		queue = new AtomicReferenceArray(capacity);
	}

	private int next (int idx) {
		return (idx + 1) % queue.length();
	}

	public boolean put (T value) {
		int write = writeIndex.get();
		int read = readIndex.get();
		int next = next(write);
		if (next == read) return false;
		queue.set(write, value);
		writeIndex.set(next);
		return true;
	}

	public T poll () {
		int read = readIndex.get();
		int write = writeIndex.get();
		if (read == write) return null;
		T value = queue.get(read);
		readIndex.set(next(read));
		return value;
	}
}
