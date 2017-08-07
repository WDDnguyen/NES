import java.io.IOException;

public class emulator {

	// TESTING ENVIRONMENT

	final static String FILE_NAME = "D:/NESTEST/Tetris.nes";
	
	public static void main(String[] Args) throws IOException, InvalidFileException{
	
		NESFileReader fr = new NESFileReader();
		Cartridge cartridge = fr.loadFileDataToCartridge(FILE_NAME);
		MMC1 mapper = new MMC1(cartridge);
		
		cpu CPU = new cpu(mapper);
		byte flags = (byte) 0x01;
		CPU.setFlags(flags);
		CPU.printFlagsStatus();
		
	}
	
}
