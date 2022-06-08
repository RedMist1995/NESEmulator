package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class php_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {

    //OP Codes - PHP Group
    fun OP_08(){
        var processorStatus: UByte = 0u
        for(i in cpu.processorStatusArray.indices){
            processorStatus = (processorStatus + (cpu.processorStatusArray[i].toInt() shl i).toUByte()).toUByte()
        }
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), processorStatus)
        cpu.incrementStackPointer()
        cpu.incrementClockCycle(3)
    }
}
