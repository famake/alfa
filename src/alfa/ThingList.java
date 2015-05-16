package alfa;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import alfa.model.Channel;
import alfa.model.ChannelConfiguration;
import alfa.model.Item;
import alfa.model.Thing;
import alfa.model.ThingType;
import alfa.rulegen.RuleException;
import alfa.rulegen.RuleGenerator;
import alfa.rules.BooleanCondition;
import alfa.rules.ClockCondition;
import alfa.rules.ClockEvent;
import alfa.rules.ContactSensorEvent;
import alfa.rules.Delay;
import alfa.rules.NumberCondition;
import alfa.rules.PlayerAction;
import alfa.rules.RuleComponent;
import alfa.rules.SetColor;
import alfa.rules.SetOnOff;
import alfa.rules.SimpleEvent;
import alfa.rules.StartupEvent;
import alfa.rules.ThresholdEvent;


@ManagedBean
@ViewScoped
public class ThingList {
	
	private OutputPanel menu;
	private DefaultDashboardModel dashboardModel;
	private static final String API_URI = "http://localhost:9080/rest";
	final WebTarget target = ClientBuilder.newClient().target(API_URI);
	String rule = "", ruleName = "";
	final static String RULE_FILE = "C:\\Users\\Mikael\\git\\openhab2\\distribution\\openhabhome\\conf\\rules\\alfa.rules";
	
	private List<Thing> things;
	List<ThingType> thingTypes;
	private Map<String,RuleComponent> ruleComponents;
	private RuleComponent rc_clock, rc_delay;
	private RuleComponent rc_startup;
	private Dashboard dashboard;
	
	private String theme;

	
	@PostConstruct
	public void init() {
        loadThingsAndThingTypes();
        dashboardModel = new DefaultDashboardModel();
        DashboardColumn thingList = new DefaultDashboardColumn();
        dashboardModel.addColumn(thingList);
        dashboardModel.addColumn(new DefaultDashboardColumn());
        dashboardModel.addColumn(new DefaultDashboardColumn());
        dashboardModel.addColumn(new DefaultDashboardColumn());
        
        for (String rc_id : ruleComponents.keySet()) {
        	thingList.addWidget(rc_id);
        }
        
		dashboard = new Dashboard();
	}
	
	private void loadThingsAndThingTypes() {
		if (things == null) {
	        ruleComponents = new HashMap<>();
			things = target.path("things")
					.request(MediaType.APPLICATION_JSON)
					.get(new GenericType<List<Thing>>() {});
					
			thingTypes = target.path("thing-types")
					.request(MediaType.APPLICATION_JSON)
					.get(new GenericType<List<ThingType>>() {});

			HashMap<String,ThingType> thingTypeMap = new HashMap<>();
			
			for (ThingType tt : thingTypes) {
				thingTypeMap.put(tt.UID, tt);
			}
			
			for (Iterator<Thing> iterator = things.iterator(); iterator.hasNext(); ) {
				Thing t = iterator.next();
				int lastColon = t.UID.lastIndexOf(":");
				String thingType = t.UID.substring(0, lastColon);
				t.thingType = thingTypeMap.get(thingType);
				if (t.thingType == null) {
					iterator.remove();
					continue;
				}
				if (t.channels == null || t.channels.isEmpty() ||
						("sonos:zoneplayer:RINCON_000E58EAB4EC01400").equals(t.UID)) {
					
					iterator.remove();
					continue;
				}
				if (t.UID.startsWith("lifx:light:")) {
					t.thingType.description = "LIFX wireless light bulb.";
				}
				
				HashMap<String,Item> memberItems = new HashMap<>();
				if (t.item != null && t.item.members != null) {
					for (Item i : t.item.members) {
						memberItems.put(i.name, i);
					}
				}
				
				
				for (Channel c : t.channels) {
					for (ChannelConfiguration cc : t.thingType.channels) {
						if (cc.id.equals(c.id)) {
							c.configuration = cc;
						}
					}
					c.thing = t;
					c.linkedItemObjects = new ArrayList<>();
					for (String li : c.linkedItems) {
						c.linkedItemObjects.add(memberItems.get(li));
					}
				}
				
				RuleComponent rc = new RuleComponent(t);
				if (!t.channels.isEmpty()) {
					rc.channel = t.channels.get(0);
					if (rc.channel.configuration == null) {
						System.out.println("Channel: " + t.UID + " " + rc.channel.id);
					}
					
					// HACK: Select "Control" for sonos zone player
					int channel_index = 0;
					if ("sonos:zoneplayer".equals(t.thingType.UID)) {
						while (!rc.channel.configuration.id.equals("control")) {
							rc.channel = t.channels.get(++channel_index);
						}
					}
						
					rc.conditions = new ArrayList<>();
					if (rc.channel.itemType.equals("Number")) {
						rc.conditions.add(new NumberCondition(rc.channel));
					}
					else if (rc.channel.itemType.equals("Switch")) {
						if (rc.channel.configuration.tags == null 
								|| ! rc.channel.configuration.tags.contains("stateless"))
							rc.conditions.add(new BooleanCondition(rc.channel, "On", "Off", "ON", "OFF"));
					}
					else if (rc.channel.itemType.equals("Contact")) {
						rc.conditions.add(new BooleanCondition(rc.channel, "Open", "Closed", "OPEN", "CLOSED"));
					}
					
					if (rc.channel.configuration.stateDescription != null
							&& rc.channel.configuration.stateDescription.readOnly) {
						rc.events = new ArrayList<>();
						if (rc.channel.itemType.equals("Switch")) {
							rc.events.add(new SimpleEvent(rc.channel, true));
						}
						else if (rc.channel.itemType.equals("Number")) {
							rc.events.add(new ThresholdEvent(rc.channel, true));
						}
						else if (rc.channel.itemType.equals("Contact")) {
							rc.events.add(new ContactSensorEvent(rc.channel, true));
						}
					}
					else {
						rc.actions = new ArrayList<>();
						if (rc.channel.itemType.equals("Switch")) {
							rc.actions.add(new SetOnOff(rc.channel));
						}
						else if (rc.channel.itemType.equals("Color")) {
							rc.actions.add(new SetColor(rc.channel));
						}
						else if (rc.channel.itemType.equals("Player")) {
							rc.actions.add(new PlayerAction(rc.channel));
						}
					}
				}
				rc.thing = t;
				ruleComponents.put(rc.getId(), rc);			
			}
			ruleComponents.put("clock", getClock());
			ruleComponents.put("delay", getDelay());
			ruleComponents.put("startup", getStartup());
		}
	}

	public RuleComponent getClock() {
		if (rc_clock == null) {
			rc_clock = new RuleComponent("clock");
			rc_clock.events = new ArrayList<>();
			rc_clock.events.add(new ClockEvent(true));
			rc_clock.conditions = new ArrayList<>();
			rc_clock.conditions.add(new ClockCondition());
		}
		return rc_clock;
	}

	public RuleComponent getDelay() {
		if (rc_delay == null) {
			rc_delay = new RuleComponent("delay");
			rc_delay.events = new ArrayList<>();
			rc_delay.conditions = new ArrayList<>();
			rc_delay.actions = new ArrayList<>();
			rc_delay.actions.add(new Delay());
		}
		return rc_delay;
	}
	
	public RuleComponent getStartup() {
		if (rc_startup == null) {
			rc_startup = new RuleComponent("startup");
			rc_startup.events = new ArrayList<>();
			rc_startup.events.add(new StartupEvent());
			rc_startup.conditions = new ArrayList<>();
			rc_startup.actions = new ArrayList<>();
		}
		return rc_startup;
	}


	public List<Channel> getSensors() {
		
		loadThingsAndThingTypes();
		ArrayList<Channel> sensors = new ArrayList<>();

		for (Thing t : things) {
			Channel ch = new Channel();
			ch.id = t.UID;
			ch.itemType = t.status;
			
			
			sensors.add(ch);
		}
		return sensors;
	}
	

	
	public OutputPanel setupMenu() {
		menu = new OutputPanel();
	    FacesContext context = FacesContext.getCurrentInstance();
	    for (Thing t : things) {
	    	RuleComponent rc = ruleComponents.get(t.UID.replace(":", "_"));
			Panel panel = new Panel();
			panel.setToggleable(true);
			panel.setToggleSpeed(2000);
			//panel.setCollapsed(true);
			String label;
			try {
				label = rc.thing.item.label;
			} catch (NullPointerException e) {
				label = "Thing";
			}
			panel.setHeader(label);
			panel.setId(rc.getId());
			
		    HashMap<String,Object> attributes = new HashMap<>();
		    attributes.put("rule_component", "#{thingList.ruleComponents['"
		    		+ rc.getId() + "']}");
		    UIComponent composite = context.getApplication().getViewHandler()
		        .getViewDeclarationLanguage(context, context.getViewRoot().getViewId())
		        .createComponent(context, "http://java.sun.com/jsf/composite/alfa", 
		        		"panel", attributes);
		    //String id = "box_" + rc.thing.UID.replace(":", "_");
		    //composite.setId(id);
		    /*Draggable drag = new Draggable();
		    drag.setFor(rc.thing.UID.replace(":", "_"));
		    drag.setHelper("clone");
		    drag.setDashboard("faboard");*/
		    panel.getChildren().add(composite);
			//menu.getChildren().add(panel);
		    //menu.getChildren().add(drag);
			dashboard.getChildren().add(panel);
		}
		return menu;
	}
	
	public OutputPanel getMenu() {
		if (menu == null)
			menu = setupMenu();
		return menu;
	}
	
	public void setMenu(OutputPanel menu) {
		this.menu = menu;
	}

	public DashboardModel getDashboardModel() {
		return dashboardModel;
	}

	public Map<String,RuleComponent> getRuleComponents() {
		return ruleComponents;
	}

	public void setRuleComponents(Map<String,RuleComponent> ruleComponents) {
		this.ruleComponents = ruleComponents;
	}
	
	public String save() {
		RuleGenerator rg = new RuleGenerator(ruleName, 
				ruleComponents(dashboardModel.getColumn(1)),
				ruleComponents(dashboardModel.getColumn(2)),
				ruleComponents(dashboardModel.getColumn(3)));
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	            new FileOutputStream(RULE_FILE, true), "utf-8"))) {
			rule = rg.getRule();
		    writer.write(rule);
		    writer.close();
		    RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlg2').show();");
		} catch (RuleException | IOException  e) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
					"Rule error", e.getMessage()));
			System.err.println("Rule create: "+e.getMessage());
		}
		return null;
	}
	
	public void dialogClosed() {
        dashboardModel = new DefaultDashboardModel();
        DashboardColumn thingList = new DefaultDashboardColumn();
        dashboardModel.addColumn(thingList);
        dashboardModel.addColumn(new DefaultDashboardColumn());
        dashboardModel.addColumn(new DefaultDashboardColumn());
        dashboardModel.addColumn(new DefaultDashboardColumn());
        for (RuleComponent rc : ruleComponents.values()) {
        	rc.action = rc.condition = rc.event = false;
        	thingList.addWidget(rc.getId());
        }
        ruleName = "";
	}
	
	private List<RuleComponent> ruleComponents(DashboardColumn column) {
		ArrayList<RuleComponent> rcs = new ArrayList<>();
		for (String id : column.getWidgets()) {
			rcs.add(ruleComponents.get(id));
		}
		return rcs;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	public void handleReorder(DashboardReorderEvent ev) {
		String id = ev.getWidgetId();
		if (id != null && !id.isEmpty()) {
			RuleComponent rc = ruleComponents.get(id);
			rc.event = rc.action = rc.condition = false;
			switch (ev.getColumnIndex()) {
			case 1:
				rc.event = true;
				break;
			case 2:
				rc.condition = true;
				break;
			case 3:
				rc.action = true;
				break;
			}
		}
	}
	

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	
}
