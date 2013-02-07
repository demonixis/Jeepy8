package jeepy8.core;

import jeepy8.Chip8;

public class Screen {

	public static final int COLUMN_SIZE = 64;
	public static final int ROW_SIZE = 32;

	public static int waitTime = 14; // Rafraichissement
	public static int framePerSecond = 60;
	
	private int width;
	private int height;
	private Pixel [][] pixels;
	private Chip8 chip8;
	
	public int getWidth() {
		width = Pixel.defaultPixelWidth * COLUMN_SIZE;
		return width;
	}
	
	public int getHeight() {
		height = Pixel.defaultPixelHeight * ROW_SIZE;
		return height;
	}
	
	public Pixel [][] getPixelBuffer() {
		return this.pixels;
	}
	
	public Cpu getCpu() {
		return this.chip8.getCpu();
	}
	
	public Screen (Chip8 chip8) {
		this.chip8 = chip8;
		pixels = new Pixel[COLUMN_SIZE][ROW_SIZE];
	}
	
	public void reset()
	{
		for (int x = 0; x < COLUMN_SIZE; x++) {
            for (int y = 0; y < ROW_SIZE; y++) {
            	pixels[x][y] = new Pixel(x, y);
            }
		}
	}
	
	// TODO : M�thode � revoir en profondeur
	public void draw(int opcode_b1, int opcode_b2, int opcode_b3)
	{
		int code = 0;
        int x = 0;
        int y = 0;
        int offset = 0;

        Cpu cpu = chip8.getCpu();
        
        cpu.setRegisterV(0xF, 0x0);

        for (int k = 0; k < opcode_b1; k++)
        {
            if (cpu.getPointerI() + k <= 0xFFF)
            	code = cpu.getMemory().getByteAt(cpu.getPointerI() + k ); // Ligne à dessiner

            y = Math.abs((cpu.getRegisterV(opcode_b2) + k) % Screen.ROW_SIZE);
            offset = 7;
            for (int j = 0; j < 8; j++)
            {
                x = Math.abs((cpu.getRegisterV(opcode_b3) + j) % Screen.COLUMN_SIZE);
                
                
                if (((code) & (0x1 << offset)) != 0)
                {
                	System.out.println("x: " + x + " - y:" + y);
                    if (pixels[x][y].getColor() == Pixel.pixelColorWhite)
                    {
                        pixels[x][y].setColor(Pixel.pixelColorBlack);
                        cpu.setRegisterV(0xF, 0x1); // On retient 1 car deux pixels se chevauchent
                    }
                    else
                    	pixels[x][y].setColor(Pixel.pixelColorWhite);
                }
                offset--;
            }
        }
	}
	
	public void clear()
	{
		for (int x = 0; x < COLUMN_SIZE; x++) {
            for (int y = 0; y < ROW_SIZE; y++) {
            	pixels[x][y].setColor(Pixel.pixelColorBlack);
            }
		}
	}
}
