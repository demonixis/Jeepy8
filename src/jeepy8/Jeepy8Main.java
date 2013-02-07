package jeepy8;

import java.awt.EventQueue;

import jeepy8.gui.Jeepy8Window;

public class Jeepy8Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Launch the application.
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chip8 chip8 = new Chip8();
					Jeepy8Window jeepy8Window = new Jeepy8Window(chip8);
					jeepy8Window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
