package breadboard;



import java.text.SimpleDateFormat;
import java.util.Date;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

import devices.*;


public class BreadBoardRotary implements ButtonListener {
	final GpioController gpio;
	final Led ledBlu;
	int oldA = 1;
	int oldB = 1; 
	int encoderVal = 0;
	int lastEncoded;
	final Led ledVerde;
	final Led ledRosso;
	final Led ledBianco;
	private Lcd16x2 lcd;
	private RotaryEncoder rotary;
	private final static String OFF = "off";
	private final static String ON = "on";
	private final static String TOGGLE = "toggle";
	private final static String FADE_OUT = "fadeOut";

	public BreadBoardRotary(String s) {
		gpio = GpioFactory.getInstance();
		ledVerde = new Led(0);
		ledBlu = new Led(6);
		ledBianco = new Led(2);
		ledRosso = new Led(1);
		
		
		rotary = new RotaryEncoder(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_05);
		lcd = new Lcd16x2();
		if (s == "softLed") {			
			ledBlu.on();
			ledVerde.on();
			ledRosso.off();
			ledBianco.on();
			lcd.clear();
			Button bottoneToggle = new Button(gpio, RaspiPin.GPIO_07);
			Button bottoneOff = new Button(gpio, RaspiPin.GPIO_03);
			Button bottoneOn = new Button(gpio, RaspiPin.GPIO_26);
			Button bottoneFadeOut = new Button(gpio, RaspiPin.GPIO_14);
			bottoneToggle.setName(TOGGLE);
			bottoneToggle.addListener(this);
			bottoneOff.setName(OFF);
			bottoneOff.addListener(this);
			bottoneOn.setName(ON);
			bottoneOn.addListener(this);
			bottoneFadeOut.setName(FADE_OUT);
			bottoneFadeOut.addListener(this);
		} else if (s == "rotary") {
			
			
			
			

		}
	}

	public void stop() {
		ledBianco.off();
		ledBlu.off();
		ledRosso.off();
		ledVerde.off();
		gpio.shutdown();
	}
	public void run() {
		long start = System.currentTimeMillis();
		
		int change =0; 
		ledRosso.off();
		ledVerde.on();
		
        while (System.currentTimeMillis() - start < 30000)	{
        	change = rotary.getCange();    	
    		encoderVal +=  change;
        	if ( change == +1 ) {
        		lcd.writeR1Left("val  : " + encoderVal);
				ledBlu.on();
				
				ledRosso.setPwm(ledRosso.getPwm()+change);
				ledVerde.setPwm(100-ledRosso.getPwm());
				ledBianco.off();
			
			} else if ( change == -1 ) {
				lcd.writeR1Left("val  : " + encoderVal);
				
				ledBlu.off();
				ledRosso.setPwm(ledRosso.getPwm()+change);
				ledVerde.setPwm(100-ledRosso.getPwm());
				ledBianco.on();		

			}        	
		}
	}
	 
	
     
	private void sleep(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (Exception e) {

		}
	}

	private static void sleeping(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (Exception e) {

		}
	}

	public void listenButtonEvent(GpioPinDigitalStateChangeEvent event) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		if (event.getPin().getName().equals(TOGGLE)) {
			if (event.getState().isHigh()) {
				lcd.writeR1Left("Fade Out Green");
				lcd.writeR2Left("time: " + formatter.format(new Date()));
				Thread worker = new Thread() {
					public void run() {
						try {
							spegniLed(ledVerde);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				};
				worker.start();
			}

		}
		if (event.getPin().getName().equals(OFF)) {
			if (event.getState().isHigh()) {
				lcd.writeR1Left("Fade Out Red");
				lcd.writeR2Left("time: " + formatter.format(new Date()));
				Thread worker = new Thread() {
					public void run() {
						try {
							spegniLed(ledRosso);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				};
				worker.start();
			}

		}
		if (event.getPin().getName().equals(ON)) {

			if (event.getState().isHigh()) {
				lcd.writeR1Left("Fade Out Bianco");
				lcd.writeR2Left("time: " + formatter.format(new Date()));
				Thread worker = new Thread() {
					public void run() {
						try {
							spegniLed(ledBianco);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				};
				worker.start();
			}
		}
		if (event.getPin().getName().equals(FADE_OUT)) {
			if (event.getState().isHigh()) {
				lcd.writeR1Left("Fade Out Blue");
				lcd.writeR2Left("time: " + formatter.format(new Date()));
				Thread worker = new Thread() {
					public void run() {
						try {
							spegniLed(ledBlu);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				};
				worker.start();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		// BreadBoard b = new BreadBoard("softLed");
		BreadBoardLed b = new BreadBoardLed("rotary");
		b.run();
		b.stop();
		
	}

	public void spegniLed(Led l) {
		for (int i = 0; i < 100; i++) {
			l.setPwm(l.getPwm() - 1);
			sleep(i + 1);
		}
		l.on();

	}

}
