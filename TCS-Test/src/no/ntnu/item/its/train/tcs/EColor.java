package no.ntnu.item.its.train.tcs;

public enum EColor {
	
//	BROWN(22, 10, 8, 5),
	GRAY(22, 7, 8, 7),
	GREEN(36, 7, 18, 11),
	BLUE(51, 6, 17, 30),
	RED(36, 24, 7, 7),
	YELLOW(138, 59, 56, 25);
	
	private int c;
	private int r;
	private int g;
	private int b;
	private EColor(int c, int r, int g, int b) {
		this.c = c;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public int getC() { return c; }
	public int getR() { return r; }
	public int getG() { return g; }
	public int getB() { return b; }
}
