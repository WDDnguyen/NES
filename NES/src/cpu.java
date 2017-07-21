
public class cpu {
	
	final int cpuFrequency = 1789773; // 1.79MHz 
	
	//Interrupts 
	public enum Interrupts {
		iReset,
		iNMI,
		iIRQ
	}
	
	// address modes
	public enum addressModes {
		mZeroPage,
		mIndexedZeroPage,
		mAbsolute,
		mIndexedAbsolute,
		mImplied,
		mAccumulator,
		mImmediate,
		mRelative,
		mIndexedIndirect,
		mIndirectIndexed,
	}
	
	// Registers 
	short PC; // Program Counter Register
	byte SP; // Stack Pointer Register
	byte A; // Accumulator Register
	byte X; // X Register
	byte Y; // Y Register
	
	//Status Register Flag
	byte C; // Carry flag
	byte Z; // Zero Flag
	byte I; // Interrupt Disable Flag
	byte D; // BCD flag  currently not using
	byte B; // Break flag
	byte V; // Overflow Flag
	byte N; // negative Flag
	
	// Memory Interface to add later 
	// CPU execution variables
	int cycles = 0; // number of cycles
	int stall = 0; // number of cycles stalled
	byte interrupt = 0; // to check if interrupt is happening
	byte instruction = 0; //current instruction
	
}
