package jeepy8.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import jeepy8.Chip8;
import jeepy8.core.Pixel;
import jeepy8.core.Screen;

/**
 * Surface utilis�e pour dessiner les pixels � l'�cran
 * @author Yann
 *
 */
@SuppressWarnings("serial")
public class Chip8RendererPanel extends JPanel {
	private Chip8 chip8;
	
	public Chip8RendererPanel(Chip8 chip8) {
		super();
		this.setBackground(Color.BLACK);
		this.setSize(new Dimension(Screen.COLUMN_SIZE * Pixel.defaultPixelWidth, Screen.ROW_SIZE * Pixel.defaultPixelHeight));
		this.chip8 = chip8;
		this.setDoubleBuffered(true);
		this.setOpaque(true);
		this.setVisible(true);
	}
	
	// Rendu
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		clear(g);

		if (chip8.getCpu().isRunning()) {
			// render.update()
			Pixel [][] pixels = chip8.getScreen().getPixelBuffer();
			for (int x = 0; x < Screen.COLUMN_SIZE; x++) {
				for (int y = 0; y < Screen.ROW_SIZE; y++) {
					drawPixel(g, pixels[x][y]);
				}
			}
		}
	}
	
	public void drawPixel(Graphics g, Pixel pixel) {
		if (pixel.getColor() == Pixel.pixelColorBlack) {
			g.setColor(Color.black);
		}
		else {
			g.setColor(Color.white);
		}
		g.fillRect(pixel.getX(), pixel.getY(), pixel.getWidth(), pixel.getHeight());
	}
	
	public void clear(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	@SuppressWarnings("unused")
	private void initializeDemoView(Graphics g) {
		for(int x = 0; x < 64; x++)
	    {
	        for(int y = 0; y < 32; y++)
	        {
	            if(x % (y + 1) == 0) {
	              g.setColor(Color.black);
	            }
	            else {
	              g.setColor(Color.white);
	            }
	            g.fillRect(x * 8, y * 8, 8, 8);
	        }
	    }
	}
}
