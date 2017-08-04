
public class Mapper{
	
	public static final int MIRROR_HORIZONTAL = 0;
	public static final int MIRROR_VERTICAL = 1;
	public static final int MIRROR_SINGLE0 = 2;
	public static final int MIRROR_SINGLE1 = 3;
	public static final int MIRROR_FOUR = 4;

	//CPU Memory Map
	byte[] cpuRAM = new byte[0x800]; 		 	  // $0000-$07FF
	byte[][] RAMMirrors = new byte [3][0x800];    // $0800-$0FFF 3x(0x800) mirrors of internal cpu RAM  
	byte[] PPURegisters = new byte [0x0008];      // $2000-$2007 PPU registers
	byte[] PPUMirrors = new byte[0x0008];		  // $2008-$4000 repeats every 8 bytes of PPU
												  // $4020-$5FFF  use to store  more ROM,RAM,CHR depending on mappers
												  // $6000-$7FFF for save state, use it later when completing mapper (already in Cartridge)
	byte[][] PGRROM = new byte[2][0x8000];		  // ($8000-$BFFF) Lower Bank - $(C000 - $10000) Upper Bank
	
	//PPU Memory Map
	byte[][] patternTables = new byte[2][0x1000]; //$0000-$0FFF and $1000-$1FFF;  usually mapped to CHR-ROM with bank switching
	byte[][] nameTable = new byte[4][0x400];	  //$2000-$2FFF usually mapped to 2KB internal VRAM with 2 nametables with mirroring config by cartridge.
	byte[] nameTableMirrors = new byte[0x0F00];   //$3000-$3EFF // PPU doesn't render from this address range so no real utility
	byte[] paletteRAM = new byte[0x0020];		  //$3F00-$3F1F 
	byte[] paletteMirror = new byte[0x00E0];	  //$3F20-$3FFF // not configurable, alway map to internal palette control.
	byte[] OAM = new byte [256];				  // determine how sprites are rendered  from CPU $2003 (OAMADDR), $2004(OAMDATA), $4014 (OAMDMA)
	
	
	public static Mapper NewMapper(Cartridge cartridge){
		System.out.println("Creating new Mapper Type : " + cartridge.mapperType);
		switch (cartridge.mapperType){
		case 1: 
			return (new MMC1(cartridge));
		default : 
			System.out.println("Invalid Mapper");
			return null;
		}
			
	}

}
