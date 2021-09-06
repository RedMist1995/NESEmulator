package emulator.opCodes.rmwOpCodes;

import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
class cpy_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - CPY Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_C0(){
        val bal: UByte = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter();//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort(); //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort();//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = cpu.ram[zeroPageAddress.toInt()]; //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        compareMemAndX(indexedIndirectAddress);
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_C4(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        compareMemAndX(zeroPageAddress);
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_CC(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        cpu.incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        compareMemAndX(src);
    }

    

    fun compareMemAndX(address: UShort){
        val src: UByte = cpu.ram[address.toInt()];
        val temp: UInt = cpu.indexYRegister - src

        if(cpu.indexYRegister > src){
            cpu.setCarryFlag(1u)
        } else if (cpu.indexYRegister == src){
            cpu.setCarryFlag(1u)
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetCarryFlag()
            cpu.resetZeroFlag()
        }

        if(temp and 128u != 0u) {
            cpu.setNegativeFlag(1u);
        } else {
            cpu.resetNegativeFlag()
        }
    }
}
