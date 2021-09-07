package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class nop_OpCodes(private val cpu: CPU) {

    fun OP_NOP(){
        cpu.incrementClockCycle(2)
    }
}
