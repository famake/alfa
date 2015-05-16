package alfa.rules;

import java.util.List;

import alfa.model.Channel;
import alfa.model.Thing;

public class RuleComponent {

	public Thing thing;
	public Channel channel;
	private String id;
	
	public boolean event, condition, action;
	
	public List<Action> actions;
	public List<Condition> conditions;
	public List<Event> events;
	
	public RuleComponent(Thing thing) {
		this.thing = thing;
		this.id = thing.UID.replace(":", "_");
	}
	
	public RuleComponent(String rc_uid) {
		this.id = rc_uid;
	}
	
	public Thing getThing() {
		return thing;
	}
	public void setThing(Thing thing) {
		this.thing = thing;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public boolean isEvent() {
		return event;
	}
	public void setEvent(boolean event) {
		this.event = event;
	}
	public boolean isCondition() {
		return condition;
	}
	public void setCondition(boolean condition) {
		this.condition = condition;
	}
	public boolean isAction() {
		return action;
	}
	public void setAction(boolean action) {
		this.action = action;
	}
	public String getId() {
		return id;
	}
	public List<Action> getActions() {
		return actions;
	}
	public List<Condition> getConditions() {
		return conditions;
	}
	public List<Event> getEvents() {
		return events;
	}
	
}
