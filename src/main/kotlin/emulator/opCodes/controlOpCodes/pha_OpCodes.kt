package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class pha_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {

    //OP Codes - PHA Group
    fun OP_48(){
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), cpu.accumulatorRegister)
        cpu.incrementStackPointer()
        cpu.incrementClockCycle(3)
    }
}
