package jeepy8.core;

import java.util.Random;

import jeepy8.Chip8;

public class Interpreter {
	private Chip8 chip8;
	private Screen screen;
	private Cpu cpu;
	
	public Interpreter(Chip8 chip8) {
		this.chip8 = chip8;
		this.screen = chip8.getScreen();
		this.cpu = chip8.getCpu();
	}
}
