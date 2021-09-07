package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
open class pha_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - PHA Group
    fun OP_48(){
        cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()] = cpu.accumulatorRegister
        cpu.incrementStackPointer()
    }
}
