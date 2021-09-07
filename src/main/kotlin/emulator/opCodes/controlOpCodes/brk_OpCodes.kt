package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class brk_OpCodes(private val cpu: CPU) {

    //OP Codes - BRK Group
    fun OP_00(){
        cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()] = ((cpu.programCounterRegister.toInt() shr 8) and 0xFF).toUByte()
        cpu.incrementStackPointer()
        cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()] = ((cpu.programCounterRegister.toInt()) and 0xFF).toUByte()
        cpu.setBreakCommandFlag(1u)
        cpu.incrementStackPointer()
        var processorStatus: UByte = 0u
        for(i in cpu.processorStatusArray.indices){
            processorStatus = (processorStatus + (cpu.processorStatusArray[i].toInt() shl i).toUByte()).toUByte()
        }
        cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()] = processorStatus
        cpu.incrementStackPointer()
        cpu.setInterruptDisableFlag(1u)
        cpu.programCounterRegister = ((cpu.ram[0xFFFF].toInt() shl 8) + cpu.ram[0xFFFE].toInt()).toUShort()
        cpu.incrementClockCycle(7)
    }
}
