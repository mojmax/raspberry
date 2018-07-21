package devices;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

public class Led {
	private int  pin ;
	private int  value = 100;


	public Led(int pin) {
		this.pin = pin;
		Gpio.wiringPiSetup();
		SoftPwm.softPwmCreate(pin,0,100);
		SoftPwm.softPwmWrite(pin, value);
	}
	public Led() {
		this.pin = 6;
		Gpio.wiringPiSetup();
		SoftPwm.softPwmCreate(pin,0,100);
		SoftPwm.softPwmWrite(pin, value);
		
	}
	public void on() {
		value = 100;
		SoftPwm.softPwmWrite(pin, value);	
	}	
	public void off() {
		value = 0;
		SoftPwm.softPwmWrite(pin, value);
	}
	public void toggle() {
		value = 100 - value;
		SoftPwm.softPwmWrite(pin, value);
	}
	public void setPwm(int value) {
		if (value >=  0 && value <= 100 ) { 
			SoftPwm.softPwmWrite(pin, value);
			this.value = value;
		}
	}
	public int getPwm() {
		return this.value ;		
	}
	
}
