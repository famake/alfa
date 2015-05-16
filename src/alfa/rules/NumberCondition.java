package alfa.rules;

import alfa.model.Channel;

public class NumberCondition extends Condition {

	public double minValue, maxValue;
	public boolean enableMin, enableMax;
	
	public NumberCondition(Channel chan) {
		super(chan);
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public boolean isEnableMin() {
		return enableMin;
	}

	public void setEnableMin(boolean enableMin) {
		this.enableMin = enableMin;
	}

	public boolean isEnableMax() {
		return enableMax;
	}

	public void setEnableMax(boolean enableMax) {
		this.enableMax = enableMax;
	}
	
	
	
}
