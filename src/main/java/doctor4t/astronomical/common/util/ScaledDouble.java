package doctor4t.astronomical.common.util;

public class ScaledDouble {
	private final double min;
	private final double max;
	private double value;
	private double scaledValue;

	public ScaledDouble(double value, double min, double max) {
		this.value = value;
		this.min = min;
		this.max = max;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
		this.scaledValue = value * (this.max - this.min) + this.min;
	}

	public double getScaledValue() {
		return this.scaledValue;
	}

	public void setScaledValue(double scaledValue) {
		this.scaledValue = scaledValue;
		this.value = (scaledValue - this.min) / (this.max - this.min);
	}
}
