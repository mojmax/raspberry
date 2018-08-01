

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.temperature.TemperatureConversion;
import com.pi4j.temperature.TemperatureScale;


import static com.pi4j.temperature.TemperatureScale.*;

import java.math.BigDecimal;

import devices.Button;
import devices.ButtonListener;
import devices.Lcd16x2;
import devices.dthnn.Dht11;
import devices.dthnn.Dht22;
import devices.dthnn.DhtNn;



/**
 * 
 * @author Massimo Mojetta 04/02/2018
 */

public class BoardDht11Dht22Lcd1602Button {
	static TemperatureScale scale = CELSIUS;

	public static void main(final String ars[]) throws Exception {
		GpioController gpio = GpioFactory.getInstance();
		
		Lcd16x2 lcd = new Lcd16x2();
		Dht22 dht = new Dht22();
//		Dht11 dht11 = new Dht11(0);
		
		Button btnTemp  = new Button(gpio,RaspiPin.GPIO_03  );
		btnTemp.setDebounce(100);
		
		btnTemp.addListener(new ButtonListener() {
			@Override
			public void listenButtonEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getPin().getName().equals( btnTemp.getName()) && event.getState().isLow()) {
					dht.getMis().changeScale();
					print(dht, lcd);
				}
			}
		});
		
		lcd.clear();
		
		Thread t = new Thread(dht);
		t.start();
		
		
//		Thread t11 = new Thread(dht11);
//		t11.start();
//		
		while(true) {
			Thread.sleep(3000);
			if(dht.getMis() != null) {
				print(dht, lcd);
			}
		}
	}
	public static void print(DhtNn dht, Lcd16x2 lcd) {
		if(dht.getMis() != null) {
			String str = String.format("Rh %.1f%% T %.1f ",dht.getMis().getRh(),dht.getMis().getTemp());
			lcd.clear(); 
			lcd.write(0, str ,LCDTextAlignment.ALIGN_LEFT);
		}
		String name = dht.getMis().getScale().getName() + "       ";
		lcd.write(1, name ,LCDTextAlignment.ALIGN_LEFT);
	
	};
	
	

}