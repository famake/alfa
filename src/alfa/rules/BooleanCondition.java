package alfa.rules;

import alfa.model.Channel;

public class BooleanCondition extends Condition {

	boolean switchState;
	public final String onStateLabel, offStateLabel;
	public final String onStateValue, offStateValue;

	public BooleanCondition(Channel chan, String onStateLabel, 
			String offStateLabel, String onStateValue, 
			String offStateValue) {
		super(chan);
		this.onStateLabel = onStateLabel;
		this.offStateLabel = offStateLabel;
		this.onStateValue = onStateValue;
		this.offStateValue = offStateValue;
	}

	public boolean isSwitchState() {
		return switchState;
	}

	public void setSwitchState(boolean switchState) {
		this.switchState = switchState;
	}

	public String getOnStateLabel() {
		return onStateLabel;
	}

	public String getOffStateLabel() {
		return offStateLabel;
	}
}
