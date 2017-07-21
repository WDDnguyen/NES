import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public class NESFileReader {
	
	//reads INES (.nes) file format 
	// NES uses little endian format 
	//** Need to make sure the proper endian format is given to the header **
	
	//final int iNESIdentifier = 0x1A53354E; 
	final static int iNESIdentifier = 0x4E45531a;
	final static int MAX_FILE_SIZE = 65536;
	final static String FILE_NAME = "D:/NESTEST/Tetris.nes";
	final static String OUTPUT_FILE_NAME = "D:/NESTEST/binary.txt";
	
	static byte[] headerByte = new byte[16]; // Header : 16 bytes
	static byte[] trainer = new byte[512];  		     // if present (0 or 512 bytes)
	static byte[] PRGROM;   				 // required   (16384 * x units)
	static byte[] CHRROM;   				 // if present (16384 * x units)
	static byte[] PRGRAM;   				 // if present (0 or 8192 bytes)
	static int mirrorType;					 // 0 : horizontal  1 : vertical
	static int mapperType;					 // Mapper Number 
	static boolean hasBattery;				 // Check if ROM uses an battery pack
	
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
				
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			int byteRead = fc.read(byteBuffer);
			System.out.println("Number of bytes read : " + byteRead);
			byteBuffer.flip();
				
			byteBuffer.get(headerByte,0, 16);
			System.out.println("Byte buffer current position : " + byteBuffer.position());
			
			iNESHeader header = new iNESHeader(headerByte);
			if(headerByte.length != 16){
				System.out.println("Header was not proprely loaded");
			}
			if(header.identifier != iNESIdentifier){
				System.out.println("This is not a iNES file (.nes)");
			}
			
			if(header.hasTrainer()){
				trainer = new byte[512];
				byteBuffer.get(trainer, byteBuffer.position(), trainer.length);
				System.out.println("Byte buffer current position : " + byteBuffer.position());
			}
				
			PRGROM = new byte[header.getPRGROMSize()];
			CHRROM = new byte[header.getCHRROMSize()];
			PRGRAM = new byte[header.getPRGRAMSize()];
			
			byteBuffer.get(PRGROM, 0, header.getPRGROMSize());
			System.out.println("Byte buffer current position : " + byteBuffer.position());
			
			byteBuffer.get(CHRROM, 0, header.getCHRROMSize());
			System.out.println("Byte buffer current position : " + byteBuffer.position());
		
			byteBuffer.get(PRGRAM, 0, header.getPRGRAMSize());
			System.out.println("Byte buffer current position : " + byteBuffer.position());
			
			byteBuffer.clear();
			byteRead = fc.read(byteBuffer);
			fc.close();
	
			printLog(header);
		}
	}
	
	public static void printLog(iNESHeader header){
		System.out.println("--------------------HEADER------------------------");
		System.out.println(Arrays.toString(headerByte));
		System.out.println("Mirror Type           : " + header.getMirrorType());
		System.out.println("Battery Pack present  : " + header.hasBatteryPack());
		System.out.println("Trainer present       : " + header.hasTrainer());
		System.out.println("Mapper Type           : " + header.getMapperType());
		System.out.println("PRG ROM SIZE in bytes : " + header.getPRGROMSize());
		System.out.println("CHR ROM SIZE in bytes : " + header.getCHRROMSize());
		System.out.println("PRG RAM SIZE in bytes : " + header.getPRGRAMSize());
		System.out.println("---------------------MEMORY-----------------------");
		System.out.println("Trainer Memory : " + DatatypeConverter.printHexBinary(trainer));
		System.out.println("PRGROM Memory  : " + DatatypeConverter.printHexBinary(PRGROM));
		System.out.println("PRGROM Memory  : " + DatatypeConverter.printHexBinary(CHRROM));
		System.out.println("PRGROM Memory  : " + DatatypeConverter.printHexBinary(PRGRAM));
		
		
		
	}
}
