package alfa.model;

import java.util.List;

public class ChannelConfiguration {

	public String description;
	public String id;
	public String label;
	public List<String> tags;
	public String category;
	public StateDescription stateDescription;
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public StateDescription getStateDescription() {
		return stateDescription;
	}
	public void setStateDescription(StateDescription stateDescription) {
		this.stateDescription = stateDescription;
	}
	public String getId() {
		return id;
	}
	
}
