package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
class plp_OpCodes(private val cpu: CPU) {

    //OP Codes - PLP Group
    fun OP_28(){
        val stackProcessorStatus: UByte = cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()]
        for(i in 7 downTo 0){
            cpu.processorStatusArray[i] = ((stackProcessorStatus.toInt() shr i) and 0x01).toUByte()
        }
        cpu.decrementStackPointer()
    }
}
