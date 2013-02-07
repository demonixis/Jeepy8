package jeepy8.core;

import jeepy8.utils.TypeConverter;

/**
 * Cette classe est charg�e de repr�senter la m�moire du Chip8
 * Ram et Rom
 * @author Yannic Comte
 *
 */
public class Memory {
	public static final int MEMORY_SIZE = 0x1000; 		// Mémoire total de 4096 octets
	public static final int RAM_SIZE = 0x1FF; 			// Mémoire morte 511 octets
	public static final int START_ADDRESS_RAM = 0x0;	// Adresse de début de la Ram
	public static final int STOP_ADDRESS_RAM = 0x1FF;	// Adresse de fin de la Ram
	public static final int ROM_SIZE = 0xE00;			// Mémoire vive de 3584 octets
	public static final int START_ADDRESS_ROM = 0x200; 	// Adresse de début de la Rom
	private final int START_ADDRESS_CHIP8_FONTS = 0x0;  // Les fonts chip8 sont stockés au débuts de la mémoire
	private final int END_ADDRESS_CHIP8_FONTS = 0x4F;   // Fin des fonts chip8
	
	/**
	 * Représentation BCD d'un caractére par exemple pour 0, 1, 2, A et F
	 *   [0]      [1]      [2]      [A]      [F]
	 * 1111000 00100000 11110000  11110000 11110000
	 * 1001000 01100000 00010000  10010000 10000000
	 * 1001000 00100000 11110000  11110000 11110000
	 * 1001000 00100000 10000000  10010000 10000000
	 * 1111000 01110000 11110000  10010000 10000000
	 */
	private static final int [] chip8FontSet = {
		0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
		0x20, 0x60, 0x20, 0x20, 0x70, // 1
		0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
		0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
		0x90, 0x90, 0xF0, 0x10, 0x10, // 4
		0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
		0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
		0xF0, 0x10, 0x20, 0x40, 0x40, // 7
		0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
		0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
		0xF0, 0x90, 0xF0, 0x90, 0x90, // A
		0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
		0xF0, 0x80, 0x80, 0x80, 0xF0, // C
		0xE0, 0x90, 0x90, 0x90, 0xE0, // D
		0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
		0xF0, 0x80, 0xF0, 0x80, 0x80  // F	
	};
	
	private short [] memory; // M�moire Rom + Ram
	
	/**
	 * Récupére un opcode à la position "position" dans la Rom
	 * @param position : Position d�finie par le Pointeur Compteur (PC)
	 * @return : L'opcode à exécuter
	 */
	public Opcode getOpcodeAt(int position) {
		if (position < Memory.MEMORY_SIZE && position >= 0) {
			int opcode16 = (memory[position] << 8) + memory[position + 1];
			return new Opcode(opcode16);
		}
		return new Opcode(0);
	}
	
	public short getByteAt(int position) {
		if (position < Memory.MEMORY_SIZE && position >= 0) {
			return memory[position];
		}
		return memory[Memory.MEMORY_SIZE];
	}
	
	/**
	 * Récupére le contenu de la mémoire Rom + Ram
	 * @return un tableau de short représentant la mémoire
	 */
	public short [] getEntireMemory() {
		return this.memory;
	}
	
	/**
	 * Récupére le contenu de la mémoire Rom (les donn�es)
	 * @return un tableau de short représentant la mémoire vive
	 */
	public short[] getRamMemory() {
		short [] ram = new short[Memory.RAM_SIZE];
		
		for (int i = Memory.START_ADDRESS_ROM; i < Memory.MEMORY_SIZE; i++) {
			ram[i] = (short) (memory[i] & 0x00FF);
		}
		
		return ram;
	}
	
	/**
	 * Récupére le contenu de la mémoire Ram (mémoire morte)
	 * @return
	 */
	public short[] getRomMemory() {
		short [] rom = new short[Memory.ROM_SIZE];
		
		for (int i = 0; i < Memory.STOP_ADDRESS_RAM; i++) {
			rom[i] = memory[i];
		}
		
		return rom;
	}
	
	public void setValueAt(int position, int value) {
		memory[position] = (byte)value;
	}
	
	/**
	 * Constructeur par defaut
	 * Initialise la mémoire et charge les fonts standards
	 */
	public Memory() {
		this.memory = new short[MEMORY_SIZE];
		reset();
	}
	
	/**
	 * Constructeur avec rom
	 * Initialize la mémoire, charge les fonts standards
	 * et charge le binaire passé en paramétre (la rom)
	 * @param romBinary : fichier binaire
	 */
	public Memory(byte [] romBinary) {
		this();
		loadRomIntoMemory(romBinary);
	}
	
	/**
	 * Chargement d'un fichier binaire dans la mémoire
	 * La taille du binaire � charg� ne doit pas dépasser 3584 octets
	 * @param romBinary
	 */
	public void loadRomIntoMemory(byte[] romBinary) {
		int binarySize = romBinary.length;
		int maxSize = Memory.MEMORY_SIZE - Memory.START_ADDRESS_ROM;
		
		if (binarySize > maxSize) {
			binarySize = maxSize;
		}
		int i = 0;
		for (int j = Memory.START_ADDRESS_ROM; j < (Memory.START_ADDRESS_ROM + binarySize); j++) {
			memory[j] = (short)TypeConverter.getUint8(romBinary[i]);
			i++;
		}
	}
	
	/**
	 * Remet à zéro la mémoire interne et charge les fonts chip8
	 */
	public void reset() {
		for (int i = 0; i < MEMORY_SIZE; i++) {
			memory[i] = 0x0;
		}
		
		loadChip8Fonts();
	}
	
	/**
	 * Charge les fonts chip8 dans la mémoire à partir de l'adresse 0x0 jusqu'à 0x49
	 */
	private void loadChip8Fonts() {
		for (int i = START_ADDRESS_CHIP8_FONTS; i < END_ADDRESS_CHIP8_FONTS; i++) {
			memory[i] = (byte) chip8FontSet[i];
		}
	}
}
