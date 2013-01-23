package jeepy8.core;

public class Input {

	public final int KEY_COUNT = 16;
	
	private int [] keys;
	
	public int getKeyAt(int position) {
		if (position > KEY_COUNT || position < 0) {
			return 0;
		}
		return keys[position];
	}
	
	public void setKeyAt(int position, int value) {
		if (position < KEY_COUNT && position >= 0) {
			keys[position] = value;
		}
	}
	
	public Input() {
		this.keys = new int[KEY_COUNT];
		reset();
	}
	
	public void reset() {
		for (int i = 0; i < KEY_COUNT; i++) {
			keys[i] = 0x0;
		}
	}
}
