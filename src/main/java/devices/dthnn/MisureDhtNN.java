package devices.dthnn;

public class MisureDhtNN {
	private double temp = 0.0;
	private double rh = 0.0;

	public double getRh() {
		return rh;
	}

	public void setRh(double rh) {
		this.rh = rh;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public String toString() {
		return " Umidity " + rh + " temp " + temp;
	};
}
