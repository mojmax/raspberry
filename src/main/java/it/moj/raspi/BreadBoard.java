package it.moj.raspi;

import it.moj.raspi.devices.dhtnn.DhtNN;
import it.moj.raspi.devices.dhtnn.MisureDthNN;
import it.moj.raspi.devices.dhtnn.NameDhtNN;

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