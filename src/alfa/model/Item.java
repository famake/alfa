package alfa.model;

import java.util.List;

public class Item {

	public String type, name, label;
	public List<Item> members;
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public String getLabel() {
		return label;
	}
	public List<Item> getMembers() {
		return members;
	}
	
}
