package alfa.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClockCondition extends Condition {

	public static final List<String> allDays = 
			Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
	public List<String> selectedDays;
	private Date startTime;
	private Date endTime;
	
	public ClockCondition() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		setStartTime(cal.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		setEndTime(cal.getTime());
		selectedDays = new ArrayList<>(allDays);
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
}
