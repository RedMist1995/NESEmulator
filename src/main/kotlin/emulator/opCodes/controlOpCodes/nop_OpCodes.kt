package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class nop_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    fun OP_NOP(){
        cpu.incrementClockCycle(2)
    }
}
