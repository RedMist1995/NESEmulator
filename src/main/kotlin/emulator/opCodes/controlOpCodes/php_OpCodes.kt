package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class php_OpCodes(private val cpu: CPU) {

    //OP Codes - PHP Group
    fun OP_08(){
        var processorStatus: UByte = 0u
        for(i in cpu.processorStatusArray.indices){
            processorStatus = (processorStatus + (cpu.processorStatusArray[i].toInt() shl i).toUByte()).toUByte()
        }
        cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()] = processorStatus
        cpu.incrementStackPointer()
        cpu.incrementClockCycle(3)
    }
}
