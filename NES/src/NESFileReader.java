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
	final int iNESIdentifier = 0x4E45531a;
	final int MAX_FILE_SIZE = 65536;
	int byteRead;
	
	byte[] headerByte = new byte[16]; // Header : 16 bytes
	byte[] trainer = new byte[512];   // if present (0 or 512 bytes)
	byte[] PRGROM;   				  // required   (16384 * x units)
	byte[] CHRROM;   				  // if present (16384 * x units)
	byte[] PRGRAM;   				  // if present (0 or 8192 bytes)
	byte mirrorType;				  // 0 : horizontal  1 : vertical
	byte mapperType;				  // Mapper Number 
	byte hasBattery;				  // Check if ROM uses an battery pack
	
	public Cartridge loadFileDataToCartridge(String filePath) throws IOException, InvalidFileException{
		System.out.println("Location of file : " + Paths.get(filePath));
		FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(filePath));
		
		if((int) fc.size() > MAX_FILE_SIZE){
			throw new InvalidFileException("File is too big for the emulator");
		}
		
		ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
		
		if(byteBuffer == null){
			throw new InvalidFileException("Buffer did not load proprely");
		}
		
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteRead = fc.read(byteBuffer);
		byteBuffer.flip();
		byteBuffer.get(headerByte,0, 16);
		System.out.println("Byte buffer current position : " + byteBuffer.position());
		iNESHeader header = new iNESHeader(headerByte);
		
		if(headerByte.length != 16){
			throw new InvalidFileException("Header was not proprely loaded, should be 16 bytes but is : " + headerByte.length );
		}
		if(header.identifier != iNESIdentifier){
			throw new InvalidFileException("This is not a iNES file (.nes) since header does not match with identifier");
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
		System.out.println("Byte buffer current position after loading PRGROM : memory " + byteBuffer.position());
		
		byteBuffer.get(CHRROM, 0, header.getCHRROMSize());
		System.out.println("Byte buffer current position after loading CHRROM memory : " + byteBuffer.position());
	
		byteBuffer.get(PRGRAM, 0, header.getPRGRAMSize());
		System.out.println("Byte buffer current position after loading PRGRAM : " + byteBuffer.position());
		
		byteBuffer.clear();
		byteRead = fc.read(byteBuffer);
		fc.close();
		
		printLog(header);
		System.out.println("Successfully loaded file to Cartridge !");
		
		return new Cartridge(trainer,PRGROM,CHRROM,PRGRAM,mirrorType,mapperType,hasBattery);
	}
	public void printLog(iNESHeader header){
		System.out.println("Number of bytes read : " + byteRead);
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
		//System.out.println("Trainer Memory : " + DatatypeConverter.printHexBinary(trainer));
		System.out.println("PRGROM Memory  : " + DatatypeConverter.printHexBinary(PRGROM));
		System.out.println("PRGROM Memory  : " + DatatypeConverter.printHexBinary(CHRROM));
		//System.out.println("PRGROM Memory  : " + DatatypeConverter.printHexBinary(PRGRAM));
		System.out.println("--------------------------------------------------");
	}
}
