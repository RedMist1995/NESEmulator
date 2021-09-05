package emulator.opCodes.rmwOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)

class rol_OpCodes(private val cpu: CPU, private val ppu: PPU, private val apu: APU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - ROL Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_26(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        arithmeticRotateLeft(zeroPageAddress);
    }
    //Accumulator Shift
    fun OP_2A(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        arithmeticRotateLeft(null);
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_2E(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        arithmeticRotateLeft(src);
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_36(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        incrementProgramCounter(); //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexXRegister).toUShort();
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace;//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort();//pc+2
        }
        incrementProgramCounter();//pc+3
        arithmeticRotateLeft(zeroPageAddress);
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_3E(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()];//pc+2
        incrementProgramCounter();//pc+3

        val src: UShort;
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        }
        arithmeticRotateLeft(src);
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    private fun incrementProgramCounter(){
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort();
    }

    private fun arithmeticRotateLeft(address: UShort?){
        val temp: UByte
        if(address == null){
            val newCarry: UByte = ((cpu.accumulatorRegister.toInt() shr 7) and 0xFF).toUByte()
            temp = ((cpu.accumulatorRegister.toInt() shl 1) and 0xFF + cpu.getCarryFlag().toInt()).toUByte()
            cpu.accumulatorRegister = temp
            cpu.setCarryFlag(newCarry)
        } else {
            val src: UByte = cpu.ram[address.toInt()];
            val newCarry: UByte = ((src.toInt() shr 7) and 0xFF).toUByte()
            temp = ((src.toInt() shl 1) and 0xFF + cpu.getCarryFlag().toInt()).toUByte()
            cpu.ram[address.toInt()] = temp
            cpu.setCarryFlag(newCarry)
        }

        if(temp.toUInt() and 128u != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.setNegativeFlag(0u)
        }

        if(temp.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.setZeroFlag(0u)
        }
    }
}
