package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)

open class jsr_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - AND Group
    fun OP_20(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter();//pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //gets the high address byte by accessing the memory location of the zero paged low address byte
        val currentPC: UShort = cpu.programCounterRegister
        cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()] = ((currentPC.toInt() shr 8) and 0xFF).toUByte()
        cpu.incrementStackPointer()
        cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()] = ((currentPC.toInt()) and 0xFF).toUByte()
        cpu.incrementStackPointer()
        cpu.programCounterRegister = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort() //creates the final indexed indirect address
    }

}
