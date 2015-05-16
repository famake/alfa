package alfa.rules;

import alfa.model.Channel;

public class ContactSensorEvent extends Event {

	public String triggerOnState = "ANY";
	
	public ContactSensorEvent(Channel channel, boolean enabled) {
		super(channel, enabled);
	}

	public String getTriggerOnState() {
		return triggerOnState;
	}

	public void setTriggerOnState(String triggerOnState) {
		this.triggerOnState = triggerOnState;
	}
	
	

}
