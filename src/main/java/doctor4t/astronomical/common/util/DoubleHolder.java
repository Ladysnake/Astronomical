package doctor4t.astronomical.common.util;

import java.util.function.Consumer;

public class DoubleHolder {
	private Consumer<Double> onChange;
	private double value;

	public DoubleHolder(double value) {
		this.value = value;
	}

	public DoubleHolder(double value, Consumer<Double> onChange) {
		this.value = value;
		this.onChange = onChange;
	}

	public double get() {
		return this.value;
	}

	public void set(double value) {
		this.value = value;
		this.onChange.accept(value);
	}

	public void silentSet(double value) {
		this.value = value;
	}

	public void setOnChange(Consumer<Double> onChange) {
		this.onChange = onChange;
	}
}
