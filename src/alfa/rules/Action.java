package alfa.rules;

import alfa.model.Channel;

public abstract class Action {

	public final Channel channel;

	public Action() {
		this.channel = null;
	}
	
	public Action(Channel channel) {
		this.channel = channel;
	}
	
	public Channel getChannel() {
		return channel;
	}

}