
public class iNESHeader {
	
	//iNES header uses 16 bytes
	
	int identifier; // first 4 bytes of the header display : NES.
	byte PRG_ROM_UNITS; // byte 4
	byte CHR_ROM_UNITS; // byte 5
	byte flag6; // byte 6
	    /*
	    Flag 6 bits representation
	    bit 0 : Mirroring : 0 (horizontal), 1 (vertical)
	    bit 1 : if cartridge contain battery-backed PRG RAM($6000-$7FFF) or persistent memory
	    bit 2 : 512-byte trainer at $7000-$71FF (store before PRG DATA)
	    bit 3 : bit set ignore mirroring control or above mirroring bit; instead provide four-screen VRAM
	    bit 4-7 : lower nybble ( set of four bits or preferably called a hex ) of mapper number 
	    */
	byte flag7; // byte 7
		/*
		 Flag 7 bits representation that determine which NES format is used
		 bit 0 : VS Unisystem
		 bit 1 : PlayChoice-10 (8KB of Hint Screen Data stored after CHR data)
		 bit 2-3 : If  bit 3 == bit 2 , flags 8-15 are in NES 2.0 format.
		 bit 4-7 : upper hex of mapper number
		 */
	byte PRG_RAM_UNITS; // in 8KB units  byte 8
	byte flag9; // byte 9 
	    /* Flag 9 bits representation
	    bit 0 : TV System 0(NTSC), 1(PAL) // not practical for emulator since no ROM image uses this bit
	    bit 1-7 : Reserved, set to zero
	    */
	byte flag10; // byte 10
		/* Flag 10 bits representation . This byte is not part of the official specification and not many emulator uses it.
		bit 0-1 : TV System : 0 (NTSC), 1 (PAL), 1/3 (dual compatible)
		bit 4-5 : PRG RAM ($6000 - $7FFF) : 0 (present), 1 (not present)
	 	bit 6 : 1 (board has no bus conflicts), 1 (Board has bus conflitcs
		*/
	
	byte[] zeroFilled = new byte[5]; // byte 11-15 are zero filled
		/* If detected format is NES 2.0 then these bytes are used
		 	byte 11 : VRAM size 
		 	byte 12 : TV system
		 	byte 13 : Vs. PPU variant
		*/
	
	public int getMirrorType(){
		int mirror = this.flag6 & 1;
		int ignoreMirror = (this.flag6 >> 3) & 1;
		System.out.println("mirror (h : 0 / v : 1) : " + mirror);
		System.out.println("Ignore mirror : " + ignoreMirror);
		return mirror | (ignoreMirror << 1);
	}
	
	public boolean hasBatteryPack(){
		return (((this.flag6 >> 2) & 1) == 1 ? true : false);
	}
	
	public boolean hasTrainer(){
		return (((this.flag6 >> 1) & 1) == 1 ? true : false);
	}
	
	public int getPRGROMSize(){
		return this.PRG_ROM_UNITS * 16384;
	}
	
	public int getCHRROMSize(){
		return this.CHR_ROM_UNITS * 8192;
	}
	
	public int getPRGRAMSize(){
		return this.PRG_RAM_UNITS * 8192;
	}
	
	public byte getMapperType(){
		byte lowerMapperBits = (byte) (this.flag6 >> 4);
		byte upperMapperBits = (byte) (this.flag7 >> 4);
		return (byte) (upperMapperBits << 4 | lowerMapperBits);
	}

	/* potential use byte 7 and $0C = $08 to determine 2.0 NES format
	 				 byte 7 and $0C = $00 to determine iNES
	 				 Otherwise archaic iNES
	*/
	
	public iNESHeader(byte[] bytes){
		this.identifier = (bytes[0] << 8 * 3) | (bytes[1] << 8 * 2) | + (bytes[2] << 8) | + (bytes[3]);
		this.PRG_ROM_UNITS = bytes[4];
		this.CHR_ROM_UNITS = bytes[5];
		this.flag6 = bytes[6];
		this.flag7 = bytes[7];
		this.PRG_RAM_UNITS = bytes[8];
		this.flag9 = bytes[9];
		this.flag10 = bytes[10];
	}
	
	
}
