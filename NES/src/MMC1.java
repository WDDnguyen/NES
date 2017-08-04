
public class MMC1 extends Mapper {
	
	// PRG ROM capacity 512K
	// PRG ROM window 16K + 16K fixed or 32K
	// PRG RAM capacity : 32K
	// PRG RAM window 8k
	// CHR capacity 128K
	// CHR window  4K + 4K or 8K
	// no bus conflics
	// no IRQ
	// no audio
	// Mapper number 1,105,155 uses MMC1
	
	
	//BANKS
	// CPU $6000-$7FFFF : 8KB PRG RAM bank 
	// CPU $8000-$BFFF : 16 KB PRG ROM , switchable or fixed to the first bank
	// CPU $C000-$FFFF : 16 KB PRG ROM , fixed to the last bank or switchable
	
	// PPU $0000-$0FFF : 4 KB switchable CHR bank
	// PPU $1000-$1FFF: 4 KB switchable CHR bank
	
	//control register in MMC1, possible for he program to swap the fixed and switchable PRG ROM or set up a 32 KB PRG bankswitching, but most game use the default setup
	
	//REGISTERS
	// MMC1 is configured through a serial port in order to reduce pin count
	
	//$8000-$FFFF is connected to a shift register
	//Write a value with bit 7 set ($80 through $FF) to any address in $8000-$FFFF clears the shift register to its initial state.
	//To change the register's value, the CPU writes five times with bit 7 clear and a bit of the desired value in bit 0. The first 4 writes, the MMC1 shifts bit 0 into a shift register
	// on the fifth write, MMC1 copies bit 0 and the shift register contents into an internal register selected by bits 13 and 14 of the address
	//Only on the fith write does the address matter and even then, only bits 14 and 13 of the address matter because the mapper registers are incompletely decoded liek the PPU Registers. 
	//After 5th write, the shift register is cleared automatically, so a write to the shift register with bit 7 on to reset it is not needed.
	
	//When the CPU writes to the serial port on consecutive cycles, the MMC1 ignores all writes but the first. THis happens when the 6502 executes read-modify-write (RMW) instructions, such as DEC and ROR, by writing back the old value and then writing the new values on the next cycle.
	
	
	
	/*LOAD register ($8000-$FFFF)
	  Rxxx xxxD 
	  R  : 7th bit : if 1 : reset Shift register and write control with (Control OR $0C), locking PRG ROM at $C000-$FFFF to the last bank.
	  D : Data bit to be shifted into shift register ,LSB First
	*/
	
	/* Control (Internal, $8000-$9FFF)
	 	5 bit
	 	CPPMM
	 	C : CHR ROM bank mode (0: switch 8KB at a time; 1 : switch two seperate 4KB banks
	 	PP : PRG ROM bank mode : (0,1 : switch 32 KB at $8000, ignoring low bit of bank number
	 							  2 : fix first bank at $8000 and switch 16 KB at $C000)
	 							  3: fix last bank at $C000 and switch 16 KB bank at $8000)
	    MM : Mirroring (0 : one-screen, lower bank,
	    				1 : one-screen , upper bank
	    				2 : Vertical 
	    				3 : Horizontal
	  */
	
	/*
	 CHR bank 0 (internal, $A000-$BFFF)
	 5 bit 
	 Select 4 KB or 8 KB CHR bank at PPU $0000 (low bit ignored in 9KB mode)
	 
	 MMC1 can do CHR banking in 4KB chunks. Known carts with CHR RAM have 9 KiB, so that makes 2 banks. RAM vs ROM doesn't make any difference for addres lines. For carts with 8Kib of CHR (be it ROM or RAM), MMC1 follows the common behaviour
	 of using only the low-order bits : the bank number is in effect ANDed with 1.
	*/
	
	/*	CHR bank 1 (internal, $C000-$DFFF)
	 	5 bit
	 	Select 4KB CHR bank at PPU $1000 (ignored in 8 KB mode)
	*/
	
	/*
	 PRG bank (internal, $E000-$FFFF)
	 RPPPP
	 R : PRG RAM chip enable (0 : enabled;
	 						  1 : disabled, ignored on MMC1A)
	 PPPP: Select 16 KB PRG ROM bank ( low bit ignored 32 KB mode)					  
	*/
	
	/* TO DO 
	1-Determine whether PRG ROM is "large" (512 KiB) or "small" ( 256 KiB or less) and 
	whether CHR is "large" ( 16-128 KiB CHR ROM) or "small" (8 KiB CHR ROM or CHR RAM)
	2- When PRG ROM is large, the highest CHR line (CHR A16) swtiches 256 KiB PRG ROM banks as in SUROM
	3- When CHR is large, MMC1 registers act "normal"
	4- When CHR is small, the MMC1's CHR bank registers switch PRG RAM banks, if battery bit is present, only banks which are written to are save to the disk when the game is quit. WHen loading a game with the battery bit set, if a 8KB .sav file is present, it is repeated equally across all banks.
	This will lead to data being saved when it wasn't supposed to for SOROM games, but 9KB of hard disk space isn't a problem, and no known NES game had the copy protection based on PRG RAM size that was common in the Super NES era.
	5- When both PRG ROM and CHR are small, CHR A16 disables PRG RAM when turned on
	*/
	byte shiftRegister = 0x10;
	Cartridge cart;
	
	public MMC1(Cartridge cartridge){
		this.cart = cartridge;
	}
	
	
	
}
