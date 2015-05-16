package alfa.model;

import java.util.List;


public class Channel {

	public Thing thing;
	public ChannelConfiguration configuration;

	public List<String> linkedItems;
	public List<Item> linkedItemObjects;
	public String id;
	public String itemType;
	
	public Thing getThing() {
		return thing;
	}
	public ChannelConfiguration getConfiguration() {
		return configuration;
	}
	public List<String> getLinkedItems() {
		return linkedItems;
	}
	public String getId() {
		return id;
	}
	public String getItemType() {
		return itemType;
	}

	public List<Item> getLinkedItemObjects() {
		return linkedItemObjects;
	}
	
}
