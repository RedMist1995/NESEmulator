package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
class clc_OpCodes(private val cpu: CPU) {

    //OP Codes - CLC Group
    fun OP_18() {
        cpu.resetCarryFlag()
    }
}
