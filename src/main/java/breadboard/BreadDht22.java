package breadboard;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import devices.Lcd16x2;
import devices.Led;
import devices.dthnn.Dht11;
import devices.dthnn.Dht22;
import devices.dthnn.DhtNnValues;

/**
 * 
 * @author Massimo Mojetta 04/02/2018
 */

public class BreadDht22 {

	public static void main(final String ars[]) throws Exception {
		GpioController gpio = GpioFactory.getInstance();
		
		Dht22 dht = new Dht22();
		
		Thread t = new Thread(dht);
		t.start();
		

		
		
	}

}