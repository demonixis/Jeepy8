package jeepy8.tools;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jeepy8.Chip8;
import jeepy8.utils.TypeConverter;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.List;
import java.awt.ScrollPane;
import javax.swing.JScrollBar;
import java.awt.Font;

public class DebugWindow extends JFrame {

	private JPanel contentPane;

	private JTextArea textAreaMemory;
	private Chip8 chip8;

	/**
	 * Create the frame.
	 */
	public DebugWindow(Chip8 chip8) {
		setTitle("Memory Dumper");
		this.chip8 = chip8;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 419);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFichier = new JMenu("Mémoire");
		menuBar.add(mnFichier);
		
		JMenuItem mntmDumper = new JMenuItem("Dumper");
		mntmDumper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dumpMemory();
			}
		});
		mnFichier.add(mntmDumper);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 546, 362);
		contentPane.add(scrollPane);
		
		textAreaMemory = new JTextArea();
		textAreaMemory.setFont(new Font("Monospaced", Font.PLAIN, 15));
		scrollPane.setViewportView(textAreaMemory);
		textAreaMemory.setWrapStyleWord(true);
		textAreaMemory.setColumns(60);
		textAreaMemory.setRows(15);
	}
	
	private void close() {
		this.close();
	}
	
	private void dumpMemory() {
		short [] m = chip8.getCpu().getMemory().getEntireMemory();
		String s = "00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n";
		for (int i = 0; i < 4096; i++) {
			if (i % 15 == 0 || i == 0) { 
				String val = i == 0 ? "00 " : "";
				val = (i != 0 && i < 10) ? "0" + i + " " : "";
				s += "\n" + val + Integer.toHexString(i) + ":  "; 
			}
			if (m[i] == 0) {
				s += "00 ";
			}
			else if (m[i] > 0xA) {
				s += "0" + Integer.toHexString(TypeConverter.getUint16(m[i])).toUpperCase() + " "; 
			}
			else {
				s += Integer.toHexString(TypeConverter.getUint16(m[i])).toUpperCase() + " ";
			}
		}
		this.textAreaMemory.setText(s);
	}
}
