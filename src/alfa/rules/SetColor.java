package alfa.rules;

import alfa.model.Channel;

public class SetColor extends Action {

	public boolean setOnOff, setBrightness, setColor, setIncreaseDecrease, setToggle;
	public String color;
	public boolean power, increase;
	public double brightness;

	public SetColor(Channel channel) {
		super(channel);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}


	public boolean isPower() {
		return power;
	}


	public void setPower(boolean power) {
		this.power = power;
	}


	public double getBrightness() {
		return brightness;
	}


	public void setBrightness(double brightness) {
		this.brightness = brightness;
	}


	public boolean isSetOnOff() {
		return setOnOff;
	}


	public void setSetOnOff(boolean setOnOff) {
		this.setOnOff = setOnOff;
	}


	public boolean isSetBrightness() {
		return setBrightness;
	}


	public void setSetBrightness(boolean setBrightness) {
		this.setBrightness = setBrightness;
	}


	public boolean isSetColor() {
		return setColor;
	}


	public void setSetColor(boolean setColor) {
		this.setColor = setColor;
	}


	public boolean isSetIncreaseDecrease() {
		return setIncreaseDecrease;
	}


	public void setSetIncreaseDecrease(boolean setIncreaseDecrease) {
		this.setIncreaseDecrease = setIncreaseDecrease;
	}


	public boolean isIncrease() {
		return increase;
	}


	public void setIncrease(boolean increase) {
		this.increase = increase;
	}


	public boolean isSetToggle() {
		return setToggle;
	}


	public void setSetToggle(boolean setToggle) {
		this.setToggle = setToggle;
	}
	
}
