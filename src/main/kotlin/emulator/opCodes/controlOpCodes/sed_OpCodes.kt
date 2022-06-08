package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class sed_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - SED Group
    fun OP_F8(){
        cpu.setDecimalModeFlag(1u)
        cpu.incrementClockCycle(2)
    }
}
