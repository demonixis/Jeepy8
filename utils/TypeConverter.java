package jeepy8.utils;

public final class TypeConverter {
	public static int getUint8(byte iByte) {
		int value = iByte;
		if (iByte < 0)
			value = iByte + 256;
		return value;
	}
	
	public static int getUint16(short iShort) {
		int value = iShort;
		if (iShort < 0)
			value = iShort + 65536;
		
		return value;
	}
}
