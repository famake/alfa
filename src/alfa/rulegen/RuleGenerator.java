package alfa.rulegen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import alfa.model.Channel;
import alfa.rules.Action;
import alfa.rules.ClockCondition;
import alfa.rules.ClockEvent;
import alfa.rules.Condition;
import alfa.rules.ContactSensorEvent;
import alfa.rules.Delay;
import alfa.rules.Event;
import alfa.rules.NumberCondition;
import alfa.rules.PlayerAction;
import alfa.rules.RuleComponent;
import alfa.rules.SetColor;
import alfa.rules.SetOnOff;
import alfa.rules.SimpleEvent;
import alfa.rules.BooleanCondition;
import alfa.rules.StartupEvent;
import alfa.rules.ThresholdEvent;

public class RuleGenerator {

	private final String ruleName;
	private final List<RuleComponent> events, conditions, actions;
		
	public RuleGenerator(String ruleName,
			List<RuleComponent> events,
			List<RuleComponent> conditions,
			List<RuleComponent> actions) {
		this.ruleName = ruleName;
		this.conditions = conditions;
		this.events = events;
		this.actions = actions;
	}
	
	
	private List<String> formatSimpleEvents(List<Event> events)
			throws RuleException {
		ArrayList<String> events_commands = new ArrayList<>();

		for (Event e : events) {
			if (e instanceof SimpleEvent) {
				events_commands.add("Item " + itemName(e.channel) + " received update");
			}
			else if (e instanceof StartupEvent) {
				events_commands.add("System started");
			}
			else if (e instanceof ClockEvent) {
				ClockEvent ce = (ClockEvent) e;
				Calendar cal = Calendar.getInstance();
				if (ce.time == null)
					throw new RuleException("No time");
				cal.setTime(ce.time);
				String cron = "Time cron \"0 ";
				cron += Integer.toString(cal.get(Calendar.MINUTE)) + " ";
				cron += Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + " ";
				cron += "? * ";
				cron += String.join(",", ce.selectedDays);
				cron += "\"";  // day of month | month
				events_commands.add(cron);
			}
			else if (e instanceof ContactSensorEvent) {
				String event;
				ContactSensorEvent ce = (ContactSensorEvent)e;
				if ("ANY".equals(ce.triggerOnState)) {
					event = "Item " + itemName(e.channel) + " received update";
				}
				else {
					event = "Item " + itemName(e.channel) + " changed";
					if ("OPEN".equals(ce.triggerOnState)) {
						event += " to OPEN";
					}
					else if ("CLOSED".equals(ce.triggerOnState)) {
						event += " to CLOSED";
					}	
				}
				
				events_commands.add(event);
			}
		}
		if (events_commands.size() == 0)
			throw new RuleException("There must be at least one event");
		return events_commands;
	}
	

	private String formatEventCondition(Event e) throws RuleException {
		if (e instanceof ThresholdEvent) {
			ThresholdEvent te = (ThresholdEvent)e;
			if (te.isValueIsGreater()) {
				return itemName(te.channel) + ".state < " + te.getThreshold();
			}
			else {
				return itemName(te.channel) + ".state > " + te.getThreshold();
			}
			
		}
		else {
			throw new RuleException("Event type not applicable");
		}
	}
	
	
	public String getConditionsAndActions() throws RuleException {
		String ca ="";
		if (conditions.size() > 0) {
			ca += "    if (" + formatConditions(conditions) + ") {\n";
		}
		for (String action : getActions())
			ca += "  	" + action + "\n";
		if (conditions.size() > 0) {
			ca += "    }\n";
		}
		return ca;
	}
	
	public String getRule() throws RuleException {

		String rules = "";
		String conditionsAndActions = getConditionsAndActions();
		
		
		ArrayList<Event> simpleEvents = new ArrayList<>();
		ArrayList<Event> complexEvents = new ArrayList<>();
		for (RuleComponent rc : events) {
			if (rc.events != null) {
				if (rc.events.size() >= 1) {
					Event e = rc.events.get(0);
					if (e instanceof SimpleEvent || e instanceof ClockEvent
							|| e instanceof ContactSensorEvent || e instanceof StartupEvent) 
						simpleEvents.add(e);
					else
						complexEvents.add(e);
				}
			}
		}
		
		if (!simpleEvents.isEmpty()) {
			String rule = "rule \"" + ruleName + "\"\n";
			rule += "when\n";
			boolean first = true;
			for (String ev : formatSimpleEvents(simpleEvents)) {
				if (!first) rule += " or\n";
				first = false;
				rule += "    " + ev;
			}
			rule += "\nthen\n";
			rule += conditionsAndActions;
			rule += "end\n\n";
			rules += rule;
		}
		
		int i = 0;
		for (Event ce : complexEvents) {
			String rule = "";
			String varname = "cond_" + (int)(100000 * Math.random());
			rule += "var " + varname + " = false\n";
			rule += "rule \"" + ruleName + "_cond" + (i++) + "\"\nwhen\n";
			rule += "Item " + itemName(ce.channel) + " changed\nthen\n";
			rule += "  var newcond = " + formatEventCondition(ce) + "\n";
			rule += "  if (!" + varname + " && newcond) {\n";
			rule += getConditionsAndActions();
			rule += "  }\n";
			rule += "  "+ varname +" = newcond\n";
			rule += "end\n\n";
			rules += rule;
		}
		
		return rules;
	}

	private String itemName(Channel channel) throws RuleException {
		if (channel.linkedItems.size() > 0) {
			return channel.linkedItems.get(0);
		}
		else {
			throw new RuleException("No linked items");
		}
	}

	
	
	private List<String> getActions() throws RuleException {
		ArrayList<String> action_defs = new ArrayList<>();
		for (RuleComponent rc : actions) {
			if (rc.actions == null)
				throw new RuleException(rc.channel.configuration.label  + 
						" is not an action");
			for (Action a : rc.actions) {
				if (a instanceof SetOnOff) {
					SetOnOff e = (SetOnOff)a;
					if (e.isToggle()) {
						String action = "sendCommand("
								+ itemName(e.channel) + ", "
								+ " if(" + itemName(e.channel) + ".state == ON)"
								+ "{OFF}else{ON})";
						action_defs.add(action);
					}
					else {
						String action = "sendCommand(" + itemName(e.channel) 
								+ ", ";
						if (e.value) {
							action += "ON";
						}
						else {
							action += "OFF";
						}
						action += ")";
						action_defs.add(action);
					}
				}
				if (a instanceof PlayerAction) {
					PlayerAction pa = (PlayerAction)a;
					if (pa.command == null)
						throw new RuleException("Command not specified", rc);
					if ("TOGGLE".equals(pa.command)) {
						String action = "sendCommand("
								+ itemName(pa.channel) + ", "
								+ " if(" + itemName(pa.channel) + ".state == PLAY)"
								+ "{PAUSE}else{PLAY})";
						action_defs.add(action);
					}
					else {
						String action = "sendCommand(" + itemName(pa.channel) 
								+ ", " + pa.command + ")";
						action_defs.add(action);
					}
				}
				else if (a instanceof Delay) {
					String action = "Thread.sleep(" + ((Delay)a).delay*1000 + ")";
					action_defs.add(action);
				}
				else if (a instanceof SetColor) {
					SetColor sc = (SetColor)a;
					if (sc.setOnOff) {
						String action = "sendCommand(" + itemName(sc.channel) + ", "
								+ (sc.power ? "ON" : "OFF") + ")";
						action_defs.add(action);	
					}
					if (sc.setToggle) {
						String action = "sendCommand("
								+ itemName(sc.channel) + ", "
								+ " if(" + itemName(sc.channel) + ".state == HSBType::ZERO)"
								+ "{ON}else{OFF})";
						action_defs.add(action);	
					}
					if (sc.setBrightness) {
						String action = "sendCommand(" + itemName(sc.channel) 
								+ ", " + sc.brightness + ")";
						action_defs.add(action);
					}
					if (sc.setColor) {
						if (sc.color == null || sc.color.isEmpty())
							throw new RuleException("No color specified");
						Color c = Color.decode("#" + sc.color);
						float hsb[] = new float[3];
						Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb); 
						String action = "sendCommand(" + itemName(sc.channel) 
								+ ", new HSBType(new DecimalType(" + hsb[0]*360 + 
								"), new PercentType(" + (int)(hsb[1]*100) + 
								"), new PercentType(" + (int)(hsb[2]*100) + ")))";
						action_defs.add(action);
					}
					if (sc.setIncreaseDecrease) {
						String action = "sendCommand(" + itemName(sc.channel) 
								+ ", " +
								(sc.increase ? "INCREASE" : "DECREASE") 
								+ ")";
						action_defs.add(action);
					}
					
				}
			}
		}
		if (action_defs.size() == 0)
			throw new RuleException("You must specify at least one action");
		return action_defs;
	}
	
	private String formatConditions(List<RuleComponent> boxes) throws RuleException {
		ArrayList<String> conditions_strings = new ArrayList<>();
		for (RuleComponent rc : boxes) {
			if (rc.conditions == null)
				throw new RuleException(rc.channel.configuration.label + " is not a condition");
			for (Condition c : rc.conditions) {
				if (c instanceof NumberCondition) {
					NumberCondition nc = (NumberCondition)c;
					if (nc.enableMax) {
						String cond = itemName(nc.channel) +
								".state < " + nc.maxValue;
						conditions_strings.add(cond);
					}
					if (nc.enableMin) {
						String cond = itemName(nc.channel) + 
								".state > " + nc.minValue;
						conditions_strings.add(cond);
					}
				}
				else if (c instanceof ClockCondition) {
					ClockCondition cc = (ClockCondition)c;
					conditions_strings.add(getClockCondition(cc));
				}
				else if (c instanceof BooleanCondition) {
					BooleanCondition sc = (BooleanCondition)c;
					String cond = itemName(c.channel) + ".state == " 
							+ (sc.isSwitchState() ? sc.onStateValue : sc.offStateValue);
					conditions_strings.add(cond);
				}
			}
		}
		boolean first = true;
		String result = "";
		if (conditions_strings.size() == 0)
			throw new RuleException("No conditions selected");
		for (String cond : conditions_strings) {
			if (!first) {
				result += " && ";
			}
			result += cond;
			first = false;
		}
		return result;
	}


	private String getClockCondition(ClockCondition cc) {
		
		ArrayList<String> or = new ArrayList<>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(cc.getStartTime());
		int startMin = cal.get(Calendar.MINUTE) + 60*cal.get(Calendar.HOUR_OF_DAY);
		cal.setTime(cc.getEndTime());
		int endMin = cal.get(Calendar.MINUTE) + 60*cal.get(Calendar.HOUR_OF_DAY);
		
		for (String day : cc.selectedDays) {
			int day_index = dayIndex(day);
			if (startMin < endMin) {
				or.add("(now.getDayOfWeek() == " + day_index + 
						" && now.getMinuteOfDay() >= " + startMin 
						+ " && now.getMinuteOfDay() < " + endMin + ")\n");
			}
			else {
				or.add("(now.getDayOfWeek() == " + day_index + 
						" && now.getMinuteOfDay() >= " + startMin + ")\n");
				int nextDay = day_index + 1;
				if (nextDay == 8) nextDay = 1;
				or.add("(now.getDayOfWeek() == " + nextDay + 
						" && now.getMinuteOfDay() < " + endMin + ")\n");
			}
		}
		return String.join("	|| ", or);
	}


	private int dayIndex(String day) {
		for (int i=1; i<=7; ++i) {
			if (ClockCondition.allDays.get(i-1).equals(day))
				return i;
		}
		return 0;
	}
	
}
