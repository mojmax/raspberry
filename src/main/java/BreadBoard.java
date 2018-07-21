

import devices.dthnn.Dht11;
import devices.dthnn.MisureDhtNN;
import devices.dthnn.NameDhtNN;

/**
 * 
 * @author Massimo Mojetta
 *	04/02/2018
 */



public class BreadBoard {
	
	public static void main(final String ars[]) throws Exception {
		Dht11 dht = new Dht11();
		dht.setName(NameDhtNN.DHT11.getName());
		MisureDhtNN mis;

		for (int i = 0; i < 3; i++) {
			Thread.sleep(3000);
			dht.getRhTempValues();
		}
		for (int i = 0; i < 10; i++) {
			Thread.sleep(2000);
			mis = dht.getRhTempValues();
			System.out.println("Misurati   " + mis);
		}
		System.out.println("Done!!");
	}
}