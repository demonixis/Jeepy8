package jeepy8.core;

import java.util.Random;
import java.util.Stack;

import jeepy8.Chip8;

/**
 * Cette classe gère le processeur du Chip8
 * On y retrouve les 16 registres V0 � V15
 * Les deux pointeurs de mémoire I et PC
 * La pile d'appel, la mémoire Rom + Ram
 * @author Yann
 *
 */
public final class Cpu {

	public static int operationPerSecond = 4;

	public final int STACK_SIZE = 16;
	public final int REGISTER_COUNT = 16;
	
	private Memory internalMemory;	// Rom + Ram
	private short [] registerV;		// Registres V0 � V15
	private int pointerI;			// Pointeur graphique
	private Stack<Integer> stack;		// Pile d'adresse mémoire, taille maximum 16
	private int stackSize;			// Taille de la pile mémoire
	private short delayTimer;		// Timer de jeu
	private short soundTimer;		// Timer de son
	private int pointerCounter;	// Pointeur compteur
	
	private Opcode currentOpcode;	// Opcode actuellement interpr�t�
	private boolean waitForInput;	// Utilisé par l'opcode FX05, attent-on une pression d'une touche
	private boolean isRunning;		// Indique si le processeur est à l'arrêt ou en fonctionnement

	private Input input;			// Clavier
	
	private Chip8 chip8;			// Objet de haut niveau repr�sentant le Chip8
	private Screen screen;
	
	/**
	 * Constructeur
	 * @param chip8
	 */
	public Cpu(Chip8 chip8) {
		this.internalMemory = new Memory();
		this.registerV = new short[REGISTER_COUNT];
		this.pointerI = 0x0;
		this.stack = new Stack<Integer>();
		this.stack.setSize(STACK_SIZE);
		this.stackSize = 0;
		this.delayTimer = 0x0;
		this.soundTimer = 0x0;
		this.pointerCounter = Memory.START_ADDRESS_ROM;
		this.currentOpcode = null;
		this.waitForInput = false;
		this.input = new Input();
		this.chip8 = chip8;
		this.screen = null;
		this.setRunning(false);
	}
	
	/**
	 * Met en fonctionnement le processeur
	 */
	public void start() {
		this.isRunning = true;
	}
	
	/**
	 * Arr�te le processeur
	 */
	public void stop() {
		this.isRunning = false;
	}
	
	/**
	 * Remise � z�ro de l'�tat du processeur
	 * La m�moire n'est pas remise � z�ro
	 */
	public void reset() {
		for (int i = 0; i < REGISTER_COUNT; i++) {
			registerV[i] = 0x0;
		}
		pointerI = 0x0;
		stack.clear();
		stackSize = 0;
		delayTimer = 0x0;
		soundTimer = 0x0;
		pointerCounter = Memory.START_ADDRESS_ROM;
		currentOpcode = null;
		input.reset();
	}

	/**
	 * Remet à zéro l'état du processeur et remet à zéro la mémoire
	 */
	public void resetComplete() {
		internalMemory.reset();
		reset();
	}
	
	/**
	 * Mise � jour des timers de jeu et de son
	 */
	public void updateTimers() {
		if (delayTimer > 0)
			delayTimer--;
		
		if (soundTimer > 0) 
			soundTimer--;
	}
	
	/**
	 * Interpr�tation des opcodes
	 * TODO : impl�menter un interpr�teur externe
	 * @return
	 */
	public int interpretOpcode() {
		currentOpcode = internalMemory.getOpcodeAt(pointerCounter);
		
		short vx = currentOpcode.getVX();
		short vy = currentOpcode.getVY();
		short nibble = currentOpcode.getByte();
		short nnn = currentOpcode.getNNN();
		short nn = currentOpcode.getNN();

		switch (currentOpcode.getIndex()) {
	        case 0:         // 00E0 : Effacer l'écran
	            getScreen().clear();
	            break;
	        case 1:         // 00EE : Retourne � partir d'un sous programme
	            if (stackSize > 0)
	            {
	                pointerCounter = stack.pop();
	                stackSize--;
	            }
	            break;
	        case 2:         // 0NNN
	            break;
	        case 3:         // 1NNN : Effectue un saut à l'adresse NNN
	            pointerCounter = nnn;
	            pointerCounter -= 2;   
	            break;
	        case 4:         // 2NNN : Execute un sous programme � l'adresse NNN
	            stack.push(pointerCounter);
	            stackSize++;
	            pointerCounter = nnn;
	            pointerCounter -= 2;  
	            break;
	        case 5:         // 3XNN : Saute l'instruction suivante si VX == NN
	        	if (registerV[vx] == nn)
	        		pointerCounter += 2;
	            break;  
	        case 6:         // 4XNN : Saute l'instruction suivante si VX != NN
	        	if (registerV[vx] != nn)
	        		pointerCounter += 2;
	            break;  
	        case 7:         // 5XY0 : Saute � l'instruction suivante si VX == VY
	        	if (registerV[vx] == vy)
	        		pointerCounter += 2;
	            break;  
	        case 8:         // 6XNN : Définie VX à NN
	        	registerV[vx] = nn;
	            break;
	        case 9:         // 7XNN : Ajoute NN à VX
	        	registerV[vx] += nn;
	            break;
	        case 10:        // 8XY0 : Définie VX à la valeur VY
	        	registerV[vx] = registerV[vy];
	            break;
	        case 11:        // 8XY1 :Définie VX à VX OR VY
	        	registerV[vx] = (short) (registerV[vx] | registerV[vy]);
	            break;
	        case 12:        // 8XY2 : Définie VX à VX AND VY
	        	registerV[vx] = (short) (registerV[vx] & registerV[vy]);
	            break;
	        case 13:        // 8XY3 : Définie VX à VX XOR VY
	        	registerV[vx] = (short) (registerV[vx] ^ registerV[vy]);
	            break;
	        case 14:        // 8XY4 : VY += VX, si le r�sultat est > 0xff VF = 0x1 sinon VF = 0x0
	            if (registerV[vx] + registerV[vy] > 0xFF)
	                registerV[0xF] = 1;
	            else
	                registerV[0xF] = 0;
	            registerV[vx] += registerV[vy];
	            break;
	        case 15:        // 8XY5 : VY = VY - VX, si le resultat est négatif VF = 0x1 sinon VF = 0x0
	            int subVY = (registerV[vx] - registerV[vy]);
	            if (subVY < 0)
	            {
	                registerV[0xF] = 1;
	                subVY *= -1;
	            }
	            else
	                registerV[0xF] = 0;
	            registerV[vx] = (short) subVY;
	            break;
	        case 16:        // 8XY6 : Décale VX à droite de 1 bit, VF = valeur du bit de poids faible de VX avant décalage
	            registerV[0xF] = (short)(registerV[vx] & 0x01);
	            registerV[vx] = (short)(registerV[vx] >> 1);
	            break;
	        case 17:        // 8XY7 : VX = VY - VX, si le résultat est < 0 VF = 0x1 sinon VF = 0x0
	            int subVX = registerV[vy] - registerV[vx];
	            if (subVX < 0)
	                registerV[0xF] = 1;
	            else
	                registerV[0xF] = 0;
	            registerV[vx] = (short) subVX;
	            break;
	        case 18:        // 8XYE : Décale VX à gauche de 1 bit, VF = valeur du bit de poids fort de VX avant décalage
	            registerV[0xF] = (short)(registerV[vx] >> 7);
	            registerV[vx] = (short)(registerV[vx] << 1);
	            break;
	        case 19:        // 9XY0 : Saute l'instruction suivante si VX et VY ne sont pas �gaux
	            if (registerV[vx] != registerV[vy])
	                pointerCounter += 2;
	            break;
	        case 20:        // ANNN : Affecte NNN à I
	            pointerI = nnn;
	            break;      
	        case 21:        // BNNN : Passe à l'adresse NNN + V0
	            pointerCounter = (short)(nnn + registerV[0x0]);
	            pointerCounter -= 2;
	            break;
	        case 22:        // CXNN : Définit VX à un nombre aléatoire < NN
	            Random rand = new Random();
	            registerV[vx] = (short)(rand.nextInt(255) % (nn + 1));
	            break;
	        case 23:        // DXYN : Dessine un sprite à l'écran
	            this.getScreen().draw(currentOpcode.getByte(), vy, vx);
	            break;
	        case 24:        // EX9E : Saute l'instruction suivante si la touche représentée par VX == 0x1
	            if (input.getKeyAt(vx) == 0x1)
	                pointerCounter += 2;
	            break;
	        case 25:        // EXA1 : Saute l'instruction suivante si la touche représentée par VX == 0x0
	            if (input.getKeyAt(vx) == 0x0)
	                pointerCounter += 2;
	            break;
	        case 26:        // FX07 : Définie VX = Tempo Jeu
	            registerV[vx] = delayTimer;
	            break;
	        case 27:        // FX0A : Attend l'appuie sur une touche et le retour est stocké dans VX
	            waitForInput = true;
	            break;
	        case 28:        // FX15 : Définie la tempo du jeu à VX
	            delayTimer = registerV[vx];
	            break;
	        case 29:        // FX18 : Définie la tempo du son à VX
	            soundTimer = registerV[vx];
	            break;
	        case 30:        // FX1E : I = VX + I, VF = 1 si il y a un depassement de mémoire sinon VF = 0
	            if (pointerI > 0xFFF) // 4095(10)
	                registerV[0xF] = 1;
	            else
	                registerV[0xF] = 0;
	            pointerI = (short)(registerV[vx] + pointerI);
	            break;
	        case 31:        // FX29 : Définit I à l'emplacement du caractére sotcké dans VX
	            pointerI = (short) (5 * vx);
	            break;
	        case 32:        // FX33 :  Stocke dans la mémoire le code décimal représentant VX
	        	internalMemory.setValueAt(pointerI, (registerV[vx] / 100)); // Chiffre des centaines
	        	internalMemory.setValueAt(pointerI + 1, ((registerV[vx] % 100) / 10));
	        	internalMemory.setValueAt(pointerI + 2, ((registerV[vx] % 100) % 10));
	            break;
	        case 33:        // FX55 : Stock le contenu des registres V0 jusqu'à VX en mémoire à partir de l'adresse I
	            for (byte i = 0; i <= vx; i++)
	            	internalMemory.setValueAt(pointerI + i, registerV[i]);
	            break;
	        case 34:        // FX65 : Rempli les registres V0 à VX avec le contenu de la mémoire à partir de I
	            for (byte i = 0; i <= vx; i++)
	            	registerV[i] = internalMemory.getByteAt(pointerI + i);
	            break;
	        default:
	            break;
		}
		
		pointerCounter += 2;
		return 0;
	}
	
	public String toString() {
		if (isRunning())
			return "Cpu is running Opcode " + currentOpcode.toString();
		return "Cpu is stopped";
	}
	
	// --------------------------------------
	// --- 		Accesseurs / mutateurs		-
	// --------------------------------------
	public static int getOperationPerSecond() {
		return operationPerSecond;
	}

	public static void setOperationPerSecond(int operationPerSecond) {
		Cpu.operationPerSecond = operationPerSecond;
	}

	public Memory getMemory() {
		return internalMemory;
	}

	public void setMemory(Memory rom) {
		this.internalMemory = rom;
	}
	
	public void setRegisterV(int register, int value) {
		if (register >= 0x0 && register <= 0x15) {
			registerV[register] = (short) value;
		}
	}
	
	public short getRegisterV(int register) {
		if (register >= 0x0 && register <= 0x15) {
			return registerV[register];
		}
		return 0;
	}
	
	public void setPointerI(int value) {
		pointerI = (short) value;
	}
	
	public int getpointerI() {
		return pointerI;
	}

	public short getSoundTimer() {
		return soundTimer;
	}

	public void setSoundTimer(short soundTimer) {
		this.soundTimer = soundTimer;
	}

	public Opcode getCurrentOpcode() {
		return currentOpcode;
	}

	public void setCurrentOpcode(Opcode currentOpcode) {
		this.currentOpcode = currentOpcode;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isWaitForInput() {
		return waitForInput;
	}

	public void setWaitForInput(boolean waitForInput) {
		this.waitForInput = waitForInput;
	}

	public int getPointerI() {
		return pointerI;
	}

	public Stack<Integer> getStack() {
		return stack;
	}
	
	public int getStackSize() {
		return stack.size();
	}

	public short getDelayTimer() {
		return delayTimer;
	}

	public int getPointerCounter() {
		return pointerCounter;
	}

	public Input getInput() {
		return input;
	}
	
	private Screen getScreen() {
		if (screen == null)
			screen = chip8.getScreen();
		return screen;
	}
}
