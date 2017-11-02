package implementations.util;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Comparator;

/**
 * helper class that wraps a byte[] in order to properly get
 * Arrays.hashcode()/equals() for use in a HashSet; also implements Comparable
 */
public abstract class ByteBuffer implements Comparable<ByteBuffer> {
  public static final ByteArrayComparator BYTE_ARRAY_COMPARATOR = new ByteArrayComparator();

  abstract public byte[] getArray();

  abstract public int getLength();

  public abstract byte getAdjusted(int pos);

  public static ByteBuffer wrap(byte[] array) {
    return new PureByteArray(array);
  }

  public ByteBuffer() {
  }

  public static ByteBuffer wrap(byte[] array, int offset) {
    return new ByteArrayView(array, offset);
  }

  public static ByteBuffer wrap(byte[] array, int offset, int length) {
    Preconditions.checkArgument(offset + length <= array.length);
    return new ByteArrayView(array, offset, length);
  }

  public static boolean equals(ByteBuffer array1, ByteBuffer array2) {
    if (array1 == null) {
      if (array2 == null) {
        return true;
      } else {
        return false;
      }
    }
    if (array2 == null) {
      return false;
    }
    if (array1.getArray() == null) {
      if (array2.getArray() == null) {
        return true;
      } else {
        return false;
      }
    }

    if (array2.getArray() == null) {
      return false;
    }

    return Arrays.equals(array1.getArray(), array2.getArray());
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ByteBuffer)) {
      return false;
    }

    final ByteBuffer that = (ByteBuffer) o;

    return ByteBuffer.equals(this, that);
  }

  private static class PureByteArray extends ByteBuffer {
    private byte[] array;

    private PureByteArray() {
    }

    private PureByteArray(byte[] array) {
      this.array = array;
    }

    @Override
    public byte[] getArray() {
      return array;
    }

    @Override
    public int getLength() {
      return array.length;
    }

    @Override
    public byte getAdjusted(int pos) {
      return array[pos];
    }

    @Override
    public int compareTo(ByteBuffer o) {
      return BYTE_ARRAY_COMPARATOR.compare(this, o);
    }

    @Override
    public int hashCode() {
      return array != null ? Arrays.hashCode(array) : 0;
    }

    @Override
    public String toString() {
      return "PureByteArray{" +
        "array=" + Arrays.toString(array) +
        '}';
    }
  }

  private static class ByteArrayView extends ByteBuffer {
    private byte[] array;
    private int offset;
    private int length;

    public ByteArrayView() {
    }

    private ByteArrayView(byte[] array, int offset, int length) {
      this.array = array;
      this.offset = offset;
      this.length = length;
    }

    private ByteArrayView(byte[] array, int offset) {
      this(array, offset, array.length - offset);
    }

    private ByteArrayView(byte[] array) {
      this(array, 0, array.length);
    }

    @Override
    public byte[] getArray() {
      return array;
    }

    @Override
    public int getLength() {
      return length;
    }

    @Override
    public byte getAdjusted(int pos) {
      return array[offset + pos];
    }

    @Override
    public int hashCode() {
      int result = array != null ? Arrays.hashCode(array) : 0;
      result = 31 * result + offset;
      result = 31 * result + length;
      return result;
    }

    @Override
    public int compareTo(ByteBuffer o) {
      return BYTE_ARRAY_COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
      return "ByteArrayView{" +
        "array=" + Arrays.toString(array) +
        ", start=" + offset +
        ", length=" + length +
        "} " + toString();
    }
  }

  private static class ByteArrayComparator implements Comparator<ByteBuffer> {

    public ByteArrayComparator() {
    }

    @Override
    public int compare(ByteBuffer o1, ByteBuffer o2) {
      if (o1 == null) {
        if (o2 == null) {
          return 0;
        } else {
          return -1;
        }
      }

      if (o2 == null) {
        return 1;
      }

      if (o1.getArray() == null) {
        if (o2.getArray() == null) {
          return 0;
        } else {
          return -1;
        }
      }

      if (o2.getArray() == null) {
        return 1;
      }

      int array1Length = o1.getLength();
      int array2Length = o2.getLength();

      int length = Math.min(array1Length, array2Length);

      for (int i = 0; i < length; i++) {
        if (o1.getAdjusted(i) < o2.getAdjusted(i)) {
          return -1;
        } else if (o1.getAdjusted(i) > o2.getAdjusted(i)) {
          return 1;
        }
      }

      if (array1Length < array2Length) {
        return -1;
      } else if (array1Length > array2Length) {
        return 1;
      } else {
        return 0;
      }
    }
  }
}
