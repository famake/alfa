package alfa.model;

import java.util.List;
import java.util.Map;


public class Thing {

	public Map<String, Object> configuration;
	public String status;
	public String UID;
	public List<Channel> channels;
	public ThingType thingType;
	public Item item;
	
	public String toString() {
		return "Thing(" + UID + ")";
	}

	public Map<String, Object> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Object> configuration) {
		this.configuration = configuration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	public ThingType getThingType() {
		return thingType;
	}

	public void setThingType(ThingType thingType) {
		this.thingType = thingType;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	
}
