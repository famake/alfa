package alfa.rules;

import alfa.model.Channel;

public class SetOnOff extends Action {
	
	//OPP_NED
	//HeltOpp_HeltNed
	//Forrige Neste
	//Ingen forandring
	public boolean value;
	boolean toggle;
	
	public SetOnOff(Channel channel) {
		super(channel);
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public boolean isToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}
	
	
	
}
