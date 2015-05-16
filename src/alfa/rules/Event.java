package alfa.rules;

import alfa.model.Channel;

public abstract class Event {

	public final Channel channel;
	public boolean enabled;
	
	public Event(Channel channel, boolean enabled) {
		this.channel = channel;
		this.enabled = enabled;
	}

	public Event(boolean enabled) {
		this.channel = null;
		this.enabled = enabled;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
