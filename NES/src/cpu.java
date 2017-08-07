
public class cpu {
	
	final int cpuFrequency = 1789773; // 1.79MHz 
	MMC1 mapper;
	
	//Constant Address Modes
	private static int mABSOLUTE = 1;
	private static int mABSOLUTEX = 2;
	private static int mABSOLUTEY = 3;
	private static int mACCUMULATOR = 4;
	private static int mIMMEDIATE  = 5;
	private static int mIMPLIED  = 6;
	private static int mINDEXEDINDIRECT  = 7;
	private static int mINDIRECT  = 8;
	private static int mINDIRECTINDEXED  = 9;
	private static int mRELATIVE  = 10;
	private static int mZEROPAGE  = 11;
	private static int mZEROPAGEX  = 12;
	private static int mZEROPAGEY  = 13;
	
	//Constant Interrupts 
	private static int iReset = 1;
	private static int iNMI = 2;
	private static int iIRQ = 3;
	
	// Registers 
	short PC; // Program Counter Register
	byte SP; // Stack Pointer Register
	byte A; // Accumulator Register
	byte X; // X Register
	byte Y; // Y Register
	
	//Status Register Flag  (8 bits)
	byte C; // Carry flag						bit 0
	byte Z; // Zero Flag						bit 1
	byte I; // Interrupt Disable Flag			bit 2
	byte D; // BCD flag  currently not using	bit 3
	byte B; // Break flag						bit 4
									  // ignore bit 5 
	byte V; // Overflow Flag					bit 6	
	byte N; // negative Flag					bit 7
	
	// Memory Interface to add later 
	// CPU execution variables
	int cycles = 0; // number of cycles
	int stall = 0; // number of cycles stalled
	byte interrupt = 0; // to check if interrupt is happening
	byte instruction = 0; //current instruction

	/*
		256 total opcodes in a 16 x 32 instruction matrix using http://nesdev.com/6502.txt and http://www.thealmightyguru.com/Games/Hacking/Wiki/index.php/6502_Opcodes 
	*/
	
	String[] instructionNames = {
			"BRK", "ORA", "KIL", "SLO", "NOP", "ORA", "ASL", "SLO", "PHP", "ORA", "ASL", "ANC", "NOP", "ORA", "ASL", "SLO",
			"BPL", "ORA", "KIL", "SLO", "NOP", "ORA", "ASL", "SLO", "CLC", "ORA", "NOP", "SLO", "NOP", "ORA", "ASL", "SLO",
			"JSR", "AND", "KIL", "RLA", "BIT", "AND", "ROL", "RLA", "PLP", "AND", "ROL", "ANC", "BIT", "AND", "ROL", "RLA",
			"BMI", "AND", "KIL", "RLA", "NOP", "AND", "ROL", "RLA", "SEC", "AND", "NOP", "RLA", "NOP", "AND", "ROL", "RLA",
			"RTI", "EOR", "KIL", "SRE", "NOP", "EOR", "LSR", "SRE", "PHA", "EOR", "LSR", "ALR", "JMP", "EOR", "LSR", "SRE",
			"BVC", "EOR", "KIL", "SRE", "NOP", "EOR", "LSR", "SRE", "CLI", "EOR", "NOP", "SRE", "NOP", "EOR", "LSR", "SRE",
			"RTS", "ADC", "KIL", "RRA", "NOP", "ADC", "ROR", "RRA", "PLA", "ADC", "ROR", "ARR", "JMP", "ADC", "ROR", "RRA",
			"BVS", "ADC", "KIL", "RRA", "NOP", "ADC", "ROR", "RRA", "SEI", "ADC", "NOP", "RRA", "NOP", "ADC", "ROR", "RRA",
			"NOP", "STA", "NOP", "SAX", "STY", "STA", "STX", "SAX", "DEY", "NOP", "TXA", "XAA", "STY", "STA", "STX", "SAX",
			"BCC", "STA", "KIL", "AHX", "STY", "STA", "STX", "SAX", "TYA", "STA", "TXS", "TAS", "SHY", "STA", "SHX", "AHX",
			"LDY", "LDA", "LDX", "LAX", "LDY", "LDA", "LDX", "LAX", "TAY", "LDA", "TAX", "LAX", "LDY", "LDA", "LDX", "LAX",
			"BCS", "LDA", "KIL", "LAX", "LDY", "LDA", "LDX", "LAX", "CLV", "LDA", "TSX", "LAS", "LDY", "LDA", "LDX", "LAX",
			"CPY", "CMP", "NOP", "DCP", "CPY", "CMP", "DEC", "DCP", "INY", "CMP", "DEX", "AXS", "CPY", "CMP", "DEC", "DCP",
			"BNE", "CMP", "KIL", "DCP", "NOP", "CMP", "DEC", "DCP", "CLD", "CMP", "NOP", "DCP", "NOP", "CMP", "DEC", "DCP",
			"CPX", "SBC", "NOP", "ISC", "CPX", "SBC", "INC", "ISC", "INX", "SBC", "NOP", "SBC", "CPX", "SBC", "INC", "ISC",
			"BEQ", "SBC", "KIL", "ISC", "NOP", "SBC", "INC", "ISC", "SED", "SBC", "NOP", "ISC", "NOP", "SBC", "INC", "ISC"
	};
	
	byte[] instructionModes = {
			6, 7, 6, 7, 11, 11, 11, 11, 6, 5, 4, 5, 1, 1, 1, 1, 10, 9, 6, 9, 12, 12, 12, 12, 6, 3, 6, 3, 2, 2, 2, 2,
			1, 7, 6, 7, 11, 11, 11, 11, 6, 5, 4, 5, 1, 1, 1, 1, 10, 9, 6, 9, 12, 12, 12, 12, 6, 3, 6, 3, 2, 2, 2, 2,
			6, 7, 6, 7, 11, 11, 11, 11, 6, 5, 4, 5, 1, 1, 1, 1, 10, 9, 6, 9, 12, 12, 12, 12, 6, 3, 6, 3, 2, 2, 2, 2,
			6, 7, 6, 7, 11, 11, 11, 11, 6, 5, 4, 5, 8, 1, 1, 1,	10, 9, 6, 9, 12, 12, 12, 12, 6, 3, 6, 3, 2, 2, 2, 2,
			5, 7, 5, 7, 11, 11, 11, 11, 6, 5, 6, 5, 1, 1, 1, 1, 10, 9, 6, 9, 12, 12, 13, 13, 6, 3, 6, 3, 2, 2, 3, 3,
			5, 7, 5, 7, 11, 11, 11, 11, 6, 5, 6, 5, 1, 1, 1, 1, 10, 9, 6, 9, 12, 12, 13, 13, 6, 3, 6, 3, 2, 2, 3, 3,
			5, 7, 5, 7, 11, 11, 11, 11, 6, 5, 6, 5, 1, 1, 1, 1, 10, 9, 6, 9, 12, 12, 12, 12, 6, 3, 6, 3, 2, 2, 2, 2,
			5, 7, 5, 7, 11, 11, 11, 11, 6, 5, 6, 5, 1, 1, 1, 1, 10, 9, 6, 9, 12, 12, 12, 12, 6, 3, 6, 3, 2, 2, 2, 2,
	};
	
	byte[] instructionSize = {
			1, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0,
			3, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0,
			1, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0,
			1, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0,
			2, 2, 0, 0, 2, 2, 2, 0, 1, 0, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 0, 3, 0, 0,
			2, 2, 2, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0,
			2, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0,
			2, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, 2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0,
	};
	
	byte[] instructionCycle = {
			7, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 4, 4, 6, 6, 2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
			6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 4, 4, 6, 6, 2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
			6, 6, 2, 8, 3, 3, 5, 5, 3, 2, 2, 2, 3, 4, 6, 6, 2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
			6, 6, 2, 8, 3, 3, 5, 5, 4, 2, 2, 2, 5, 4, 6, 6, 2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
			2, 6, 2, 6, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4, 2, 6, 2, 6, 4, 4, 4, 4, 2, 5, 2, 5, 5, 5, 5, 5,
			2, 6, 2, 6, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4, 2, 5, 2, 5, 4, 4, 4, 4, 2, 4, 2, 4, 4, 4, 4, 4,
			2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6, 2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7,
			2, 6, 2, 8, 3, 3, 5, 5, 2, 2, 2, 2, 4, 4, 6, 6, 2, 5, 2, 8, 4, 4, 6, 6, 2, 4, 2, 7, 4, 4, 7, 7
	};
	
	public cpu(MMC1 map){
		mapper = map;
	}
	
	public void setFlags(byte flags){
		C = (byte) (flags >> 0 & 1);
		Z = (byte) (flags >> 1 & 1);
		I = (byte) (flags >> 2 & 1);
		D = (byte) (flags >> 3 & 1);
		B = (byte) (flags >> 4 & 1);
		V = (byte) (flags >> 6 & 1);
		N = (byte) (flags >> 7 & 1);
	}
	
	public void printFlagsStatus(){
		System.out.println("Status Register : CZIDB-VN : " + C + "" + Z + "" + I + "" + D + "" + B + "-" + V + "" + N + "" );
	}
	
	// reset to power-up
	public void reset(){
		SP = (byte) 0xFD;
		setFlags((byte)0x34);
		A = 0;
		X = 0;
		Y = 0;
	}
	
	public void run(){
		// need to determine when it's interrupting or stalling
	}
	
	public byte read(short address){
		
		if (address < 0x2000){
			//return address in RAM
		}else if (address < 0x4000){
			// need to do PPU with 4014,4015,4016,4017
		} else if (address < 0x6000){
			//need to do I/O registers
		}
		else if (address >= 0x6000){
			return mapper.read(address);
		}else {
			System.out.println("Unable to read address in CPU : " + address);
		}
		return 0;
	}
	
	public void write(short address, byte value){
		if(address < 0x2000){
			//return address in RAM
		} else if (address < 0x4000){
			//need to write in PPU with 4014,4015,4016,4017
		} else if (address >= 0x6000){
			mapper.write(address, value);
		}else {
			System.out.println("Unable to write address in CPU : " + address);
		}
	}
	
}
