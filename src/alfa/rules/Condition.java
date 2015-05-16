package alfa.rules;

import alfa.model.Channel;

public abstract class Condition {

	public final Channel channel;
	
	public Condition(Channel chan) {
		this.channel = chan;
	}
	
	public Condition() {
		this.channel = null;
	}

	public Channel getChannel() {
		return channel;
	}
}
