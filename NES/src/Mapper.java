
public class Mapper {
	
	public enum MirrorType{
		Horizontal,
		Vertical,
		SingleScreen0,
		SingleScreen1,
		FourScreen
	}
	
	Cartridge cartridge;
	byte[] cpuRAM = new byte[0x800]; 		 	// $0000-$07FF
	byte[][] RAMMirrors = new byte [3][0x800];  // $0800-$0FFF 3x(0x800) mirrors of internal cpu RAM  
	byte[] PPU = new byte [0x0008]; 			// $2000-$2007 PPU registers
	byte[][] PGRROM = new byte[2][0x8000];		//
	
	
}
