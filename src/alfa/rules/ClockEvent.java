package alfa.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ClockEvent extends Event {

	public Date time;
	public static final List<String> allDays = 
			Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
	public List<String> selectedDays;
	
	public ClockEvent(boolean enabled) {
		super(enabled);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		time = cal.getTime();
		selectedDays = new ArrayList<>(allDays);
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public List<String> getAllDays() {
		return allDays;
	}
	
	public List<String> getSelectedDays() {
		return selectedDays;
	}

	public void setSelectedDays(List<String> selectedDays) {
		this.selectedDays = selectedDays;
	}

}
