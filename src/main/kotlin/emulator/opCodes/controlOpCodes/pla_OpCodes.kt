package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
class pla_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - PLA Group
    fun OP_68(){
        cpu.accumulatorRegister = cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()]
        cpu.decrementStackPointer()
    }
}
