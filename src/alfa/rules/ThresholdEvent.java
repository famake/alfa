package alfa.rules;

import alfa.model.Channel;

public class ThresholdEvent extends Event {

	double threshold;
	boolean valueIsGreater;
	
	public ThresholdEvent(Channel channel, boolean enabled) {
		super(channel, enabled);
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public boolean isValueIsGreater() {
		return valueIsGreater;
	}

	public void setValueIsGreater(boolean valueIsGreater) {
		this.valueIsGreater = valueIsGreater;
	}
	
	
	
}
