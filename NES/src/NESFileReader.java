import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class NESFileReader {
	
	//reads INES (.nes) file format 
	// NES uses little endian format 
	
	//** Need to make sure the proper endian format is given to the header"
	
	// Header : 16 bytes
	// Trainer -- if present (0 or 512 bytes)
	// PRG_ROM -- required    (16384 * x bytes)
	// CHR_ROM -- if present  (16384 * x bytes)
	// INST_ROM -- if present (0 or 8192 bytes)
	// PROM -- if present    (16 bytes Dad, 16 bytes Counter Out) often missing

	//final int iNESIdentifier = 0x1A53354E; 
	final static int iNESIdentifier = 0x4E45531a;
	static byte[] headerByte = new byte[16];
	
	final static int MAX_FILE_SIZE = 65536;
	final static String FILE_NAME = "D:/NESTEST/Tetris.nes";
	final static String OUTPUT_FILE_NAME = "D:/NESTEST/binary.txt";
	
	// testing retrieving data from iNES file
	public static void main(String[] Args) throws IOException {
		NESFileReader binary = new NESFileReader();
		FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(FILE_NAME));
		
		if((int) fc.size() > MAX_FILE_SIZE){
			System.out.println("Memory Size in bytes : " +(int) fc.size());
		}else {
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
			if(byteBuffer == null){
				System.out.println("Byte Buffer is empty, did not load the file correctly");
			}
			
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
			int byteRead = fc.read(byteBuffer);
		
			System.out.println("Number of bytes read : " + byteRead);
			System.out.println("------------------------------------------");
			
			byteBuffer.flip();
				
			byteBuffer.get(headerByte,0, 16);
			
			System.out.println(Arrays.toString(headerByte));
			iNESHeader header = new iNESHeader(headerByte);
			
			if(headerByte.length != 16){
				System.out.println("Header was not proprely loaded");
			}
			
			if(header.identifier != iNESIdentifier){
				System.out.println("This is not a iNES file (.nes)");
			}
			
			System.out.println("Mirror Type : " + header.getMirrorType());
			
			
			byteBuffer.clear();
			byteRead = fc.read(byteBuffer);
			fc.close();
		}
		
	}
	
	byte[] readBinaryFile(String aFileName) throws IOException {
		Path path = Paths.get(aFileName);
		return Files.readAllBytes(path);
	}
	
	void writeBinaryFile(byte[] aBytes, String aFileName) throws IOException{
		Path path = Paths.get(aFileName);
		Files.write(path, aBytes);
	}
	
	private static void log (Object aMsg){
		System.out.println(String.valueOf(aMsg));
	}
	
}
