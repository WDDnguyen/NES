import java.io.IOException;

public class emulator {

	// TESTING ENVIRONMENT

	final static String FILE_NAME = "D:/NESTEST/Tetris.nes";
	
	public static void main(String[] Args) throws IOException, InvalidFileException{
		
		Mapper mapper;
		NESFileReader fr = new NESFileReader();
		Cartridge cartridge = fr.loadFileDataToCartridge(FILE_NAME);
		mapper = Mapper.NewMapper(cartridge);
	}
	
}
