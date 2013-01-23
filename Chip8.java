package jeepy8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import jeepy8.core.Cpu;
import jeepy8.core.Memory;
import jeepy8.core.Screen;

public final class Chip8 {

	private Cpu cpu;
	private Screen screen;
	
	public Cpu getCpu() {
		return this.cpu;
	}
	
	public Screen getScreen() {
		return this.screen;
	}
	
	public Chip8 () {
		this.cpu = new Cpu(this);
		this.cpu.resetComplete();
		this.cpu.stop();
		this.screen = new Screen(this);
		this.screen.reset();
	}
	
	public void startEmulator() {
		this.cpu.start();
	}
	
	public void stopEmulator() {
		this.cpu.stop();
	}
	
	public void resetEmulator(boolean complete) {
		if (complete) {
			this.cpu.resetComplete();
		}
		else {
			this.cpu.reset();
		}
		this.screen.reset();
	}
	
	/**
	 * Charge un fichier binaire dans la mémoire
	 * @param romFile
	 */
	public void loadRom(File romFile) {
		FileInputStream inputRom = null;
		byte [] romBinary = new byte[Memory.RAM_SIZE];
		try {
			inputRom = new FileInputStream(romFile);
			inputRom.read(romBinary);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (inputRom != null) {
				try {
					inputRom.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		cpu.getMemory().loadRomIntoMemory(romBinary);
	}
}
