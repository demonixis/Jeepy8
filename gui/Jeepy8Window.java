package jeepy8.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import jeepy8.Chip8;
import jeepy8.core.Cpu;
import jeepy8.core.Input;
import jeepy8.core.Memory;
import jeepy8.core.Pixel;
import jeepy8.tools.DebugWindow;

@SuppressWarnings("serial")
public class Jeepy8Window extends JFrame implements ActionListener, KeyListener, Runnable {
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu emulatorMenu;
	private JMenu configMenu;
	private JMenu toolsMenu;
	private JMenu helpMenu;
	
	private JMenuItem itemMenuOpen;
	private JMenuItem itemMenuClose;
	private JMenuItem itemMenuQuit;
	private JMenuItem itemMenuStart;
	private JMenuItem itemMenuStop;
	private JMenuItem itemMenuReset;
	private JMenuItem itemMenuGraphics;
	private JMenuItem itemMenuInput;
	private JMenuItem itemMenuSound;
	private JMenuItem itemMenuCpu;
	private JMenuItem itemMenuDebug;
	private JMenuItem itemMenuDisasembler;
	private JMenuItem itemMenuHelp;
	private JMenuItem itemMenuAbout;
	
	private Chip8RendererPanel rendererPanel;
	private StatusBar statusBar;
	
	private Chip8 chip8;
	private Input input;
	
	private Thread threadCpu;
	
	public Jeepy8Window(Chip8 chip8)
	{
		this.setTitle("Jeepy-8");
		this.setSize(530, 340);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		this.chip8 = chip8;
		this.input = chip8.getCpu().getInput();
		this.threadCpu = new Thread(this);
		
		initializeMenu();
		initializeComponents();
	}
	
	private void initializeMenu() {
		this.menuBar = new JMenuBar();
		this.fileMenu = new JMenu("Fichier");
		this.emulatorMenu = new JMenu("Emulation");
		this.configMenu = new JMenu("Configuration");
		this.toolsMenu = new JMenu("Outils");
		this.helpMenu = new JMenu("Aide");
		
		this.itemMenuOpen = new JMenuItem("Ouvrir");
		this.itemMenuOpen.addActionListener(this);
		this.itemMenuClose = new JMenuItem("Fermer");
		this.itemMenuClose.addActionListener(this);
		this.itemMenuQuit = new JMenuItem("Quitter");
		this.itemMenuQuit.addActionListener(this);
		
		this.itemMenuStart = new JMenuItem("Démarrer");
		this.itemMenuStart.addActionListener(this);
		this.itemMenuStop = new JMenuItem("Stopper");
		this.itemMenuStop.addActionListener(this);
		this.itemMenuReset = new JMenuItem("Redémarrer");
		this.itemMenuReset.addActionListener(this);
		
		this.itemMenuSound = new JMenuItem("Audio");
		this.itemMenuInput = new JMenuItem("Controle");
		this.itemMenuGraphics = new JMenuItem("Graphismes");
		this.itemMenuCpu = new JMenuItem("Processeur");
		this.itemMenuDebug = new JMenuItem("Debugger");
		this.itemMenuDebug.addActionListener(this);
		this.itemMenuDisasembler = new JMenuItem("Désassembleur");
		this.itemMenuHelp = new JMenuItem("Aide");
		this.itemMenuAbout = new JMenuItem("A propos");
		this.itemMenuAbout.addActionListener(this);
		
		this.fileMenu.add(itemMenuOpen);
		this.fileMenu.add(itemMenuClose);
		this.fileMenu.addSeparator();
		this.fileMenu.add(itemMenuQuit);
		
		this.emulatorMenu.add(itemMenuStart);
		this.emulatorMenu.add(itemMenuStop);
		this.emulatorMenu.add(itemMenuReset);
		
		this.configMenu.add(itemMenuSound);
		this.configMenu.add(itemMenuInput);
		this.configMenu.add(itemMenuGraphics);
		this.configMenu.add(itemMenuCpu);
		
		this.toolsMenu.add(itemMenuDebug);
		this.toolsMenu.add(itemMenuDisasembler);
		
		this.helpMenu.add(itemMenuHelp);
		this.helpMenu.add(itemMenuAbout);
		
		this.menuBar.add(fileMenu);
		this.menuBar.add(emulatorMenu);
		this.menuBar.add(configMenu);
		this.menuBar.add(toolsMenu);
		this.menuBar.add(helpMenu);
		
		this.setJMenuBar(menuBar);
	}
	
	private void initializeComponents() {
		
		this.rendererPanel = new Chip8RendererPanel(chip8);
		this.add(rendererPanel, BorderLayout.CENTER);
		
		this.statusBar = new StatusBar();
		this.add(statusBar, BorderLayout.PAGE_END);
	}

	// Temporaire
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == itemMenuOpen) {
			JFileChooser openRom = new JFileChooser();
			openRom.addChoosableFileFilter(new Ch8RomFileFilter(".c8k", "Fichier rom Chip-8"));
			openRom.addChoosableFileFilter(new Ch8RomFileFilter(".ch8", "Fichier rom Chip-8"));
			openRom.setDialogTitle("Ouvrir un fichier rom");
			openRom.setMultiSelectionEnabled(false);
			
			File rom = null;
			if (openRom.showOpenDialog(this) ==  JFileChooser.APPROVE_OPTION) {
				rom = openRom.getSelectedFile();
				statusBar.setStatus("Rom " + rom.getName() + " charg�e");
				chip8.resetEmulator(true);
				// On charge la rom dans la m�moire
				chip8.loadRom(rom);
			}
			
			openRom = null;
			
			if (!threadCpu.isAlive()) {
				chip8.startEmulator();
				threadCpu.start();
				statusBar.setStatus("Emulation en cours");
			}
		}
		
		else if (e.getSource() == itemMenuClose) {
			statusBar.setStatus("Stop");
		}
		
		else if (e.getSource() == itemMenuQuit) {
			System.exit(0);
		}
		
		else if (e.getSource() == itemMenuStart) {
			chip8.startEmulator();
			threadCpu.start();
			statusBar.setStatus("Emulation en cours");
		}
		
		else if (e.getSource() == itemMenuStop) {
			chip8.stopEmulator();
			threadCpu.interrupt();
			statusBar.setStatus("Stop");
		}
		
		else if (e.getSource() == itemMenuReset) {
			chip8.resetEmulator(false);
			statusBar.setStatus("Reset");
		}
		
		else if (e.getSource() == itemMenuAbout) {
			JOptionPane.showMessageDialog(this, "Jeepy-8 (jeepy height) est un émulateur Chip-8 écrit à titre expérimental.", "A propos de Jeepy-8", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (e.getSource() == itemMenuDebug) {
			DebugWindow debug = new DebugWindow(chip8);
			debug.setVisible(true);
		}
	}

	public void executeEmulation() {
		while (chip8.getCpu().isRunning()) {
			for (int instructionCounter = 0; instructionCounter < Cpu.operationPerSecond; instructionCounter++)
	        {
				chip8.getCpu().interpretOpcode();

				//System.out.println(chip8.getCpu().toString());
	        }
			
			rendererPanel.repaint();
			chip8.getCpu().updateTimers();
		}	
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_SPACE:
				for (int x = 0; x < 64; x++)
				{
					for (int y = 0; y < 32; y++)
					{
						Pixel [][] gfx = chip8.getScreen().getPixelBuffer();
						if (gfx[x][y].getColor() == 1)
							System.out.print("B ");
						else
							System.out.print("W ");
					}
					System.out.println("");
				}
				break;
			case KeyEvent.VK_NUMPAD0:
				input.setKeyAt(0x0, 0x1);
				break;
			case KeyEvent.VK_NUMPAD1:
				input.setKeyAt(0x1, 0x1);		
				break;
			case KeyEvent.VK_NUMPAD2:
				input.setKeyAt(0x2, 0x1);
				break;
			case KeyEvent.VK_NUMPAD3:
				input.setKeyAt(0x3, 0x1);
				break;
			case KeyEvent.VK_NUMPAD4:
				input.setKeyAt(0x4, 0x1);
				break;
			case KeyEvent.VK_NUMPAD5:
				input.setKeyAt(0x5, 0x1);
				break;
			case KeyEvent.VK_NUMPAD6:
				input.setKeyAt(0x6, 0x1);
				break;
			case KeyEvent.VK_NUMPAD7:
				input.setKeyAt(0x7, 0x1);
				break;
			case KeyEvent.VK_NUMPAD8:
				input.setKeyAt(0x8, 0x1);
				break;
			case KeyEvent.VK_NUMPAD9:
				input.setKeyAt(0x9, 0x1);
				break;
			case KeyEvent.VK_A:
				input.setKeyAt(0xA, 0x1);
				break;
			case KeyEvent.VK_B:
				input.setKeyAt(0xB, 0x1);
				break;
			case KeyEvent.VK_C:
				input.setKeyAt(0xC, 0x1);
				break;
			case KeyEvent.VK_D:
				input.setKeyAt(0xD, 0x1);
				break;
			case KeyEvent.VK_E:
				input.setKeyAt(0xE, 0x1);
				break;
			case KeyEvent.VK_F:
				input.setKeyAt(0xF, 0x1);
				break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_NUMPAD0:
				input.setKeyAt(0x0, 0x0);
				break;
			case KeyEvent.VK_NUMPAD1:
				input.setKeyAt(0x0, 0x0);		
				break;
			case KeyEvent.VK_NUMPAD2:
				input.setKeyAt(0x2, 0x0);
				break;
			case KeyEvent.VK_NUMPAD3:
				input.setKeyAt(0x3, 0x0);
				break;
			case KeyEvent.VK_NUMPAD4:
				input.setKeyAt(0x4, 0x0);
				break;
			case KeyEvent.VK_NUMPAD5:
				input.setKeyAt(0x5, 0x0);
				break;
			case KeyEvent.VK_NUMPAD6:
				input.setKeyAt(0x6, 0x0);
				break;
			case KeyEvent.VK_NUMPAD7:
				input.setKeyAt(0x7, 0x0);
				break;
			case KeyEvent.VK_NUMPAD8:
				input.setKeyAt(0x8, 0x0);
				break;
			case KeyEvent.VK_NUMPAD9:
				input.setKeyAt(0x9, 0x0);
				break;
			case KeyEvent.VK_A:
				input.setKeyAt(0xA, 0x0);
				break;
			case KeyEvent.VK_B:
				input.setKeyAt(0xB, 0x0);
				break;
			case KeyEvent.VK_C:
				input.setKeyAt(0xC, 0x0);
				break;
			case KeyEvent.VK_D:
				input.setKeyAt(0xD, 0x0);
				break;
			case KeyEvent.VK_E:
				input.setKeyAt(0xE, 0x0);
				break;
			case KeyEvent.VK_F:
				input.setKeyAt(0xF, 0x0);
				break;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		executeEmulation();
	}
}

/**
 * Filtre utilisé lors du chargement d'un fichier rom
 * Fichier pris en compte : .ch8
 */
class Ch8RomFileFilter extends FileFilter {
	protected String extension = ".ch8";
	protected String description = "Rom Chip-8";
	
	public Ch8RomFileFilter(String extension, String description) {
		this.extension = extension;
		this.description = description;
	}
	
	public boolean accept(File file) {
		return (file.isDirectory() || file.getName().endsWith(extension));
	}
	
	public String getDescription() {
		return this.extension + " - " + this.description;
	}
}

/**
 * Barre de status personnalisée, hélas ça n'est pas un composant natif dans swing
 * ce n'est pourtant pas compliqué O_o ?
 */
@SuppressWarnings("serial")
class StatusBar extends JPanel {
	private JLabel statusLabel;
	
	public void setStatus(String status) {
		this.statusLabel.setText(status);
	}
	
	public StatusBar() {
		super();
		this.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.statusLabel = new JLabel("Stop");
		this.add(statusLabel);
	}
}
