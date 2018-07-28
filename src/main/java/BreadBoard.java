

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import devices.dthnn.Dht11;
import devices.dthnn.Dht22;
import devices.dthnn.DhtNnValues;


/**
 * 
 * @author Massimo Mojetta
 *	04/02/2018
 */



public class BreadBoard  {
	

	public static void main(final String ars[]) throws Exception {
		Dht22 dht = new Dht22();
		Dht11 dht11 = new Dht11(0);
		Thread t = new Thread(dht);
		Thread t1 = new Thread(dht11);
		t.start();
		t1.start();
		
		System.out.println("Done!!");
	}

	
}