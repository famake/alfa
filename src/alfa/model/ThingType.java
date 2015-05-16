package alfa.model;

import java.util.List;

public class ThingType {

	public List<ChannelConfiguration> channels;
	public String description;
	public String label;
	public String UID;
	
	public List<ChannelConfiguration> getChannels() {
		return channels;
	}
	public String getDescription() {
		return description;
	}
	public String getLabel() {
		return label;
	}
	public String getUID() {
		return UID;
	}
	
}
