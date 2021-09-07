package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class rts_OpCodes (private val cpu: CPU) {

    //OP Codes - RTS Group
    fun OP_60(){
        cpu.decrementStackPointer()
        val pcLow: UByte = cpu.ram[(cpu.stackStart + cpu.stackPointerRegister).toInt()]
        cpu.decrementStackPointer()
        val pcHigh: UByte = cpu.ram[(cpu.stackStart + cpu.stackPointerRegister).toInt()]
        cpu.programCounterRegister = ((pcHigh.toInt() shl 8) + pcLow.toInt() + 1).toUShort()
    }
}
