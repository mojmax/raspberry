

import devices.dthnn.Dht11;
import devices.dthnn.MisureDhtNN;


/**
 * 
 * @author Massimo Mojetta
 *	04/02/2018
 */



public class BreadBoard {
	
	public static void main(final String ars[]) throws Exception {
		Dht11 dht = new Dht11();
		
		MisureDhtNN mis;

		for (int i = 0; i < 3; i++) {
			Thread.sleep(2000);
			dht.getRhTempValues();
		}
		for (int i = 0; i < 5000; i++) {
			Thread.sleep(2000);
			mis = dht.getRhTempValues();
			System.out.println(dht.getName() +" Misurati   " + mis);
		}
		System.out.println("Done!!");
	}
}