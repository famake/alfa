package alfa;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import alfa.rules.RuleComponent;

@FacesComponent("alfa.ItemPanel")
public class ItemPanel extends UIOutput implements NamingContainer {

	private RuleComponent _rc;
	
	private RuleComponent rc() {
		if (_rc == null) 
			_rc = (RuleComponent)getValueExpression("rule_component").getValue(FacesContext.getCurrentInstance().getELContext());
		return _rc;
	}
	
	
	
	@Override
	public String getFamily() {
	       return UINamingContainer.COMPONENT_FAMILY;
	}

}
