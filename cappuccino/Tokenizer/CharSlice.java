package cappuccino.Tokenizer;

public final class CharSlice implements CharSequence {
	private final char[] data;
	private final int start;
	private final int length;
	private int hash = 0;

	public CharSlice(char[] data, int start, int length) {
		this.data = data;
		this.start = start;
		this.length = length;
	}

	public CharSlice(char[] data, int length) {
		this.data = data;
		this.start = 0;
		this.length = length;
	}

	public CharSlice(char[] data) {
		this.data = data;
		this.start = 0;
		this.length = this.data.length;
	}

	public CharSlice(char data, int start, int length) {
		this.data = new char[] { data };
		this.start = start;
		this.length = length;
	}

	public CharSlice(char data, int length) {
		this.data = new char[] { data };
		this.start = 0;
		this.length = length;
	}

	public CharSlice(char data) {
		this.data = new char[] { data };
		this.start = 0;
		this.length = this.data.length;
	}

	@Override public int length() { return length; }
	@Override public char charAt(int index) { return data[start + index]; }
	@Override public CharSequence subSequence(int start, int end) { return new CharSlice(data, this.start + start, end - start); }

	@Override
	public int hashCode() {
		if (hash == 0 && length > 0) {
			for (int i = start; i < start + length; i++) {
				hash = 31 * hash + data[i];
			}
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof CharSequence) {
			CharSequence other = (CharSequence) obj;
			if (other.length() != length)
				return false;

			for (int i = 0 ; i < length ; i++) {
				if (data[start + i] != other.charAt(i))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return new String(data, start, length); // solo cuando lo necesites como String real
	}
}
