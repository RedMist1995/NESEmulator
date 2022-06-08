package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class cli_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - CLI Group
    fun OP_58(){
        cpu.resetInterruptDisableFlag()
        cpu.incrementClockCycle(2)
    }
}
