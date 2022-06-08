package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU
import emulator.hardware.MMU

@OptIn(ExperimentalUnsignedTypes::class)
open class brk_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {

    //OP Codes - BRK Group
    fun OP_00(){
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), ((cpu.programCounterRegister.toInt() shr 8) and 0xFF).toUByte())
        cpu.incrementStackPointer()
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), ((cpu.programCounterRegister.toInt()) and 0xFF).toUByte())
        cpu.setBreakCommandFlag(1u)
        cpu.incrementStackPointer()
        var processorStatus: UByte = 0u
        for(i in cpu.processorStatusArray.indices){
            processorStatus = (processorStatus + (cpu.processorStatusArray[i].toInt() shl i).toUByte()).toUByte()
        }
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), processorStatus)
        cpu.incrementStackPointer()
        cpu.setInterruptDisableFlag(1u)
        cpu.programCounterRegister = ((mmu.readFromMemory(0xFFFFu.toUShort()).toInt() shl 8) + mmu.readFromMemory(0xFFFEu.toUShort()).toInt()).toUShort()
        cpu.incrementClockCycle(7)
    }
}
