package emulator.opCodes.rmwOpCodes;

import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class sty_OpCodes (private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - STY Group
    //Addressing Modes
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_84(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        storeYInto(zeroPageAddress);
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_8C(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        cpu.incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        storeYInto(src);
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexY the high address will always be 0x0000
    fun OP_94(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        cpu.incrementProgramCounter(); //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexYRegister).toUShort();
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace;//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort();//pc+2
        }
        cpu.incrementProgramCounter();//pc+3
        storeYInto(zeroPageAddress);
    }

    fun storeYInto(memory: UShort?){
        if(memory == null){
            if(cpu.opCode.toUInt() == 0x8Au){
                cpu.accumulatorRegister = cpu.indexYRegister
            } else {
                cpu.stackPointerRegister = cpu.indexYRegister
            }
        } else {
            cpu.ram[memory.toInt()] = cpu.indexYRegister
        }
        if(cpu.indexYRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
        if((cpu.indexYRegister.toUInt() and 128u) != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
