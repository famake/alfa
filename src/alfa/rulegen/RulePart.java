package alfa.rulegen;

import java.util.List;

import alfa.rules.RuleComponent;

public interface RulePart {

	int size();
	List<RuleComponent> getBoxes();
	
}
