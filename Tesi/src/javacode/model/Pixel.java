package javacode.model;

public class Pixel {
	private int x;
    private int y;
	private Boolean status;
    private Boolean burned;

    public Pixel(int x, int y, Boolean status) {
        this.x = x;
        this.y = y;
		this.status = status;
        this.burned = false;
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

    public Boolean getBurned() {
        return burned;
    }

    public void setBurned() {
        this.burned = true;
    }
}
