package javacode.model;

public class Pixel {
	private int x;
    private int y;
	private Boolean status;

    public Pixel(int x, int y, Boolean status) {
        this.x = x;
        this.y = y;
		this.status = status;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

	public Boolean getStatus() {
		return status;
	}
}
