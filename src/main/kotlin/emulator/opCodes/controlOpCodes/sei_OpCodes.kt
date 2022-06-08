package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class sei_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - SEI Group
    fun OP_61(){
        cpu.setInterruptDisableFlag(1u)
        cpu.incrementClockCycle(2)
    }
}
