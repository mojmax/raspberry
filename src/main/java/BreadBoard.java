

import java.sql.Time;
import java.util.concurrent.TimeUnit;

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
		
		Thread t = new Thread(dht);
		t.start();
		
		System.out.println("Done!!");
	}

	
}