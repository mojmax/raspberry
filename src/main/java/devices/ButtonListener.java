package devices;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

public interface ButtonListener {
	public void listenButtonEvent(GpioPinDigitalStateChangeEvent event) ;

}
