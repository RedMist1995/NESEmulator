package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class sec_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - SEC Group
    fun OP_38(){
        cpu.setCarryFlag(1u)
        cpu.incrementClockCycle(2)
    }
}
