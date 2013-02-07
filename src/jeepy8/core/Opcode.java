package jeepy8.core;

public class Opcode {
	public final static int OPCODE_COUNT = 35;
	
	private static Opcode [] opcodes;
	private static boolean init = false;
	
	private int id;
	private int mask;
	private int value;
	private int index;
	private String name;
	
	public int getId() {
		return this.id;
	}
	
	public int getMask() {
		return this.mask;
	}

	public int getIndex() {
		return this.index;
	}
	
	@Deprecated
	public Opcode (int id, int mask) {
		this.id = id;
		this.mask = mask;
	}
	
	public Opcode(int opcode)
	{
		int b4 = ((opcode & 0xF000) >> 12);
		int b3 = ((opcode  & 0x0F00) >> 8);
		int b2 = ((opcode & 0x00F0) >> 4);
		int b1 = (opcode & 0x000F);
		
		this.value = opcode;
		
		switch (b4) {
		case 0x0:
			switch (b2) {
			case 0xE:
				if (b1 == 0x0) { 	
					this.id = 0x00E0;
					this.name = "00E0";
					this.index = 0;
				}
				else { 				// 00EE
					this.id = 0x00EE;
					this.name = "00EE";
					this.index = 1;
				}
				break;
			case 0xF:
				this.id = 0x0FFF;
				this.index = 2;
				break;
			}
			this.mask = 0xFFFF;
			break;
		case 0x1:
			this.id = 0x1000;
			this.name = "1NNN";
			this.mask = 0xF000;
			this.index = 3;
			break;
		case 0x2:
			this.id = 0x2000;
			this.name = "2NNN";
			this.mask = 0xF000;
			this.index = 4;
			break;
		case 0x3:
			this.id = 0x3000;
			this.name = "3XNN";
			this.mask = 0xF000;
			this.index = 5;
			break;
		case 0x4:
			this.id = 0x4000;
			this.name = "4XNN";
			this.mask = 0xF000;
			this.index = 6;
			break;
		case 0x5:
			this.id = 0x5000;
			this.name = "5XY0";
			this.mask = 0xF00F;
			this.index = 7;
			break;
		case 0x6:
			this.id = 0x6000;
			this.name = "6XNN";
			this.mask = 0xF000;
			this.index = 8;
			break;
		case 0x7:
			this.id = 0x7000;
			this.name = "7XNN";
			this.mask = 0xF000;
			this.index = 9;
			break;
		case 0x8:
			switch(b1) {
			case 0x0:
				this.id = 0x8000;
				this.name = "8XY0";
				this.index = 10;
			case 0x1:
				this.id = 0x8001;
				this.name = "8XY1";
				this.index = 11;
				break;
			case 0x2:
				this.id = 0x8002;
				this.name = "8XY2";
				this.index = 12;
				break;
			case 0x3:
				this.id = 0x8003;
				this.name = "8XY3";
				this.index = 13;
				break;
			case 0x4:
				this.id = 0x8004;
				this.name = "8XY4";
				this.index = 14;
				break;
			case 0x5:
				this.id = 0x8005;
				this.name = "8XY5";
				this.index = 15;
				break;
			case 0x6:
				this.id = 0x8006;
				this.name = "8XY6";
				this.index = 16;
				break;
			case 0x7:
				this.id = 0x8007;
				this.name = "8XY7";
				this.index = 17;
				break;
			case 0xE:
				this.id = 0x800E;
				this.name = "8XYE";
				this.index = 18;
				break;
			}
			this.mask = 0xF00F;
			break;
		case 0x9:
			this.id = 0x9000;
			this.name = "9XY0";
			this.mask = 0xF00F;
			this.index = 19;
			break;
		case 0xA:
			this.id = 0xA000;
			this.name = "ANNN";
			this.mask = 0xF000;
			this.index = 20;
			break;
		case 0xB:
			this.id = 0xB000;
			this.name = "BNNN";
			this.mask = 0xF000;
			this.index = 21;
			break;
		case 0xC:
			this.id = 0xC000;
			this.name = "CXNN";
			this.mask = 0xF000;
			this.index = 22;
			break;
		case 0xD:
			this.id = 0xD000;
			this.name = "DXYN";
			this.mask = 0xF000;
			this.index = 23;
			break;
		case 0xE:
			if (b1 == 0xE) {
				this.id = 0xE09E;
				this.name = "EX9E";
				this.index = 24;
			}
			else {
				this.id = 0xE0A1;
				this.name = "EXA1";
				this.index = 25;
			}
			this.mask = 0xF0FF;
			break;
		case 0xF:
			switch (b2) {
			case 0x0:
				if (b1 == 0x7) {
					this.id = 0xF007;
					this.name = "FX07";
					this.index = 26;
				}
				else {
					this.id = 0xF00A;
					this.name = "FX0A";
					this.index = 27;
				}
				break;
			case 0x1:
				switch (b1) {
				case 0x5:
					this.id = 0xF015;
					this.name = "FX15";
					this.index = 28;
					break;
				case 0x8:
					this.id = 0xF018;
					this.name = "FX18";
					this.index = 29;
					break;
				case 0xE:
					this.id = 0xF01E;
					this.name = "FX1E";
					this.index = 30;
					break;
				}
				break;
			case 0x2:
				this.id = 0xF029;
				this.name = "FX29";
				this.index = 31;
				break;
			case 0x3:
				this.id = 0xF033;
				this.name = "FX33";
				this.index = 32;
				break;
			case 0x5:
				this.id = 0xF055;
				this.name = "FX55";
				this.index = 33;
				break;
			case 0x6:
				this.id = 0xF065;
				this.name = "FX65";
				this.index = 34;
				break;
			}
			this.mask = 0xF0FF;
			break;
		}
	}
	
	public short getAction(short opcode) {
		return (short) ((opcode & 0xF000) << 12);
	}
	
	public short getNNN() {
		return (short) ((getVX() << 8) + ((getVY()) << 4) + getByte());
	}
	
	public short getNN() {
		return (short) (((getVY()) << 4) + getByte());
	}
	
	public short getVX() {
		return (short) ((value & 0x0F00) >> 8);
	}
	
	public short getVY() {
		return (short) ((value & 0x00F0) >> 4);
	}
	
	public short getByte() {
		return (short) (value & 0x000F);
	}
	
	public String toString() {
		return "Index: " + index + " - Name: " + name + " Value: " + Integer.toHexString(value).toString();
	}
}
