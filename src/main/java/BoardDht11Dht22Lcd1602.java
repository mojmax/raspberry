

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

public class BoardDht11Dht22Lcd1602 {

	public static void main(final String ars[]) throws Exception {
		GpioController gpio = GpioFactory.getInstance();
		
		Lcd16x2 lcd = new Lcd16x2();
		Dht22 dht = new Dht22();
		Dht11 dht11 = new Dht11(0);
		lcd.clear();
		Thread t = new Thread(dht);
		t.start();
		Thread t11 = new Thread(dht11);
		t11.start();
		lcd.write(0, "Dth22 Rh% + Temp" ,LCDTextAlignment.ALIGN_LEFT);
		lcd.write(1, "Dth11 Rh% + Temp" ,LCDTextAlignment.ALIGN_LEFT);
		while(true) {
			Thread.sleep(10000);
//			dht.getRhTempValues();
//			dht11.getRhTempValues();
			System.out.println("Leggo ?" +dht);
			if(dht.mis != null) {
				dht.mis.getTemp();
				dht.mis.getRh();
				dht.getRhTempValues();
				dht.mis.getTemp();
				dht.mis.getRh();
				String str22 = "Rh " +dht.mis.getRh()+"% T "+dht.mis.getTemp() + "C"; 
				lcd.write(0, str22 ,LCDTextAlignment.ALIGN_LEFT);
			}
			if(dht11.mis != null) {
				dht11.mis.getTemp();
				dht11.mis.getRh();
				dht11.getRhTempValues();
				dht11.mis.getTemp();
				dht11.mis.getRh();
				String str11 = "Rh " +dht11.mis.getRh()+"% T "+dht11.mis.getTemp()+ "C"; 
				lcd.write(1, str11 ,LCDTextAlignment.ALIGN_LEFT);
			}
//			System.out.println(dht11);
//			String str11 = dht11.toString(); 
//			lcd.write(0, str11 ,LCDTextAlignment.ALIGN_LEFT);
			
		}
		
		
	}

}