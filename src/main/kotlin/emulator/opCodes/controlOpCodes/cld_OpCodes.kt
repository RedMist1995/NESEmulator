package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class cld_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - CLD Group
    fun OP_D8(){
        cpu.resetDecimalModeFlag()
        cpu.incrementClockCycle(2)
    }
}
