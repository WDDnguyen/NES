
public class Cartridge {
	
	byte[] trainer;  		     // if present (0 or 512 bytes)
	byte[] PRGROM;   		     // required   (16384 * x units)
	byte[] CHRROM;   		     // if present (16384 * x units)
	byte[] PRGRAM;   		     // if present (0 or 8192 bytes)
	byte mirrorType;	         // 0 : horizontal  1 : vertical
	byte mapperType;	         // Mapper Number 
	byte batteryValue;			 // Check if ROM uses an battery pack
	byte[] SRAM;				 // SAVE RAM

	public Cartridge(byte[] trainer, byte[] prgROM, byte[] chrROM, byte[] prgRAM, byte mirror, byte mapper, byte battery){
		this.trainer = trainer;
		this.PRGROM = prgROM;
		this.CHRROM = chrROM;
		this.PRGRAM = prgRAM;
		this.mirrorType = mirror;
		this.mapperType = mapper;
		this.batteryValue = battery;
		SRAM = new byte[2000];
		
	}
	
	// to do  SAVE/LOAD State;
	
}