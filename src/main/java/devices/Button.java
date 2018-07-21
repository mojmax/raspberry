package it.moj.raspi.devices;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Button {
	private GpioPinDigitalInput button;
	private static final Pin defaultPin = RaspiPin.GPIO_07;
			
	public Button(GpioController gpio,Pin pin) {
	 	this.button = gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
	 	
	}
	public Button(GpioController gpio) {
	 	this.button = gpio.provisionDigitalInputPin(defaultPin, PinPullResistance.PULL_DOWN);
	}
	public boolean isPressed() {
		return button.isHigh();
	}
	public synchronized void addListener(final ButtonListener listener) {
		GpioPinListenerDigital listen = new GpioPinListenerDigital() {
	        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
	        	listener.listenButtonEvent(event);
	        }
	    };
		button.addListener(listen); 
	}
	public void setName(String string) {
		button.setName(string);
		
	}
	public String getName() {
		return button.getName();
	}
}
