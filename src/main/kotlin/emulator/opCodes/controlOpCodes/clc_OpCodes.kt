package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class clc_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - CLC Group
    fun OP_18() {
        cpu.resetCarryFlag()
        cpu.incrementClockCycle(2)
    }
}
