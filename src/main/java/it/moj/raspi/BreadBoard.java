package it.moj.raspi;

import devices.dthnn.DhtNN;
import devices.dthnn.MisureDthNN;
import devices.dthnn.NameDhtNN;

/**
 * 
 * @author Massimo Mojetta
 *	04/02/2018
 */



public class BreadBoard {
	
	public static void main(final String ars[]) throws Exception {
		DhtNN dht = new DhtNN();
		dht.setName(NameDhtNN.DHT11.getName());
		MisureDthNN mis;

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