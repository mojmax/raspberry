package it.moj.raspi.devices.dhtnn;


public enum NameDhtNN {

	DHT11(11, "DHT11"), DHT22(22, "DHT22");
	private final int value;
	private final String name;

	private NameDhtNN(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public boolean isDHT11() {
		return this.value == 11;
	}

	public String getName() {
		return name;
	}

}