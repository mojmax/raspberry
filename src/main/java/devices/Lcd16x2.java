package devices;

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.RaspiPin;

public class Lcd16x2 {
	
	private final static int LCD_ROWS = 2;
    private final static int LCD_ROW_1 = 0;
    private final static int LCD_ROW_2 = 1;
    private final static int LCD_COLUMNS = 16;
  
    final GpioLcdDisplay lcd;
    public Lcd16x2() {
	   lcd = new GpioLcdDisplay(LCD_ROWS,    // number of row supported by LCD
			   					LCD_COLUMNS,       // number of columns supported by LCD
			   					RaspiPin.GPIO_29,  // LCD RS pin
			   					RaspiPin.GPIO_27,  // LCD strobe pin
			   					RaspiPin.GPIO_21,  // LCD data bit 1
			   					RaspiPin.GPIO_22,  // LCD data bit 2
			   					RaspiPin.GPIO_23,  // LCD data bit 3
			   					RaspiPin.GPIO_24); // LCD data bit 4 
   }
   public void clear() {
	   lcd.clear();
	   
   }
   public void write(int row, String data, LCDTextAlignment alignment) {
	   lcd.write(row,data,alignment);
	   
   }
   public void writeR1Left(String data) {
	   lcd.writeln(LCD_ROW_1,data,LCDTextAlignment.ALIGN_LEFT);
	   
   }
   public void writeR1Center(String data) {
	   lcd.writeln(LCD_ROW_1,data,LCDTextAlignment.ALIGN_CENTER);
	   
   }
   public void writeR1Right(String data) {
	   lcd.writeln(LCD_ROW_1,data,LCDTextAlignment.ALIGN_RIGHT);
	   
   }
   public void writeR2Left(String data) {
	   lcd.writeln(LCD_ROW_2,data,LCDTextAlignment.ALIGN_LEFT);
	   
   }
   public void writeR2Center(String data) {
	   lcd.writeln(LCD_ROW_2,data,LCDTextAlignment.ALIGN_CENTER);
	   
   }
   public void writeR2Right(String data) {
	   lcd.writeln(LCD_ROW_2,data,LCDTextAlignment.ALIGN_RIGHT);
	   
   }
   public void writeln(int row, String data, LCDTextAlignment alignment) {
	   lcd.writeln(row, data, alignment);
   }
   

}
