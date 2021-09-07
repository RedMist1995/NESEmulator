package emulator.opCodes.rmwOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class asl_OpCodes (private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - ASL Codes
    //Addressing Modes
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_06(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        arithmeticShiftLeft(zeroPageAddress);
    }
    //Accumulator Shift
    fun OP_0A(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        arithmeticShiftLeft(null);
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_0E(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        cpu.incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        arithmeticShiftLeft(src);
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_16(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        cpu.incrementProgramCounter(); //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexXRegister).toUShort();
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace;//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort();//pc+2
        }
        cpu.incrementProgramCounter();//pc+3
        arithmeticShiftLeft(zeroPageAddress);
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_1E(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()];//pc+2
        cpu.incrementProgramCounter();//pc+3

        val src: UShort;
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        }
        arithmeticShiftLeft(src);
    }

    

    private fun arithmeticShiftLeft(address: UShort?){
        val temp: UByte
        if(address == null){
            cpu.setCarryFlag(((cpu.accumulatorRegister.toInt() shr 7) and 0xFF).toUByte())
            temp = ((cpu.accumulatorRegister.toInt() shl 1) and 0xFF).toUByte()
            cpu.accumulatorRegister = temp
        } else {
            val src: UByte = cpu.ram[address.toInt()];
            cpu.setCarryFlag(((src.toInt() shr 7) and 0xFF).toUByte())
            temp = ((src.toInt() shl 1) and 0xFF).toUByte()
            cpu.ram[address.toInt()] = temp
        }

        if(temp.toUInt() and 128u != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }

        if(temp.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
