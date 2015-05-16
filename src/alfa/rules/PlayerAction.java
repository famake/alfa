package alfa.rules;

import alfa.model.Channel;

public class PlayerAction extends Action {
	
	public String command;

	public PlayerAction(Channel channel) {
		super(channel);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	
}
