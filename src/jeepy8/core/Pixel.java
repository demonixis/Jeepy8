package jeepy8.core;

/**
 * Cette classe est charg�e de repr�senter un pixel sur l'�cran
 * Un pixel poss�de une taille, une couleur et position
 * 
 * @author Yannick Comte
 */
public class Pixel {

	public static int pixelColorBlack = 1;		// Code de repr�sentation d'un pixel noir
	public static int pixelColorWhite = 0;		// Code de repr�sentation d'un pixel blanc
	public static int defaultPixelWidth = 8;	// Taille d'un pixel à l'écran (en px) 
	public static int defaultPixelHeight = 8;	// Idem
	
	private int x;								
	private int y;								
	private int width;							
	private int height;
	private int color;
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Définie la couleur du pixel en cours
	 * Un pixel ne peux être que blanc ou noir
	 * @param color
	 */
	public void setColor(int color) {
		if (color == 0) {
			this.color =  Pixel.pixelColorWhite; 
		}
		else {
			this.color = Pixel.pixelColorBlack;
		}
	}
	
	public int getColor() {
		return this.color;
	}
	
	public Pixel() {
		this.x = 0;
		this.y = 0;
		this.width = Pixel.defaultPixelWidth;
		this.height = Pixel.defaultPixelHeight;
		this.color = Pixel.pixelColorBlack;
	}
	
	/**
	 * Constructeur
	 * Initialise un pixel avec les paramétres par défaut � la position passée
	 * en paramétre
	 * @param x : emplacement en X
	 * @param y : emplacement en Y
	 */
	public Pixel(int x, int y) {
		this.x = x * Pixel.defaultPixelWidth;
		this.y = y * Pixel.defaultPixelHeight;
		this.width = Pixel.defaultPixelWidth;
		this.height = Pixel.defaultPixelHeight;
		this.color = Pixel.pixelColorBlack; 
	}
}
