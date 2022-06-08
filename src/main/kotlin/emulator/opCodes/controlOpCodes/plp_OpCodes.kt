package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class plp_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {

    //OP Codes - PLP Group
    fun OP_28(){
        val stackProcessorStatus: UByte = mmu.readFromMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort())
        for(i in 7 downTo 0){
            cpu.processorStatusArray[i] = ((stackProcessorStatus.toInt() shr i) and 0x01).toUByte()
        }
        cpu.decrementStackPointer()
        cpu.incrementClockCycle(4)
    }
}
