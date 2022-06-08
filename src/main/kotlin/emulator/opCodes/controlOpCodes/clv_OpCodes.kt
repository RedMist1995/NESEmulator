package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class clv_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - CLV Group
    fun OP_B8(){
        cpu.resetOverflowFlag()
        cpu.incrementClockCycle(2)
    }
}
