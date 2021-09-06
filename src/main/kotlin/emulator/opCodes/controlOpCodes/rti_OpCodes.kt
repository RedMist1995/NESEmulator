package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
public class rti_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - RTI Group
    fun OP_40(){
        addressLow = cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()]
        cpu.decrementStackPointer()
        addressHigh = cpu.ram[(cpu.stackStart - cpu.stackPointerRegister).toInt()]
        cpu.decrementStackPointer()
        cpu.programCounterRegister = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
    }
}
