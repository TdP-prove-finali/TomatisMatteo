package javacode.model;

public class Pixel implements Comparable<Pixel>{
	private int x;
    private int y;
	private Boolean status;
    private double distance;

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

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }

    @Override
    public int compareTo(Pixel other) {
        if(this.distance<other.getDistance()) {
            return -1;
        } else {
            return 1;
        }
    }

}
