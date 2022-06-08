package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
open class tay_OpCodes(private val cpu: CPU, val debugWriter: debugWriter) {

    //OP Codes - TAY Group
    fun OP_A8(){
        cpu.indexYRegister = cpu.accumulatorRegister

        if(cpu.indexYRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }

        if(cpu.indexYRegister.toUInt() and 128u != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }
        cpu.incrementClockCycle(2)
    }
}
