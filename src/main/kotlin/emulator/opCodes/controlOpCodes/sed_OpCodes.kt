package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
class sed_OpCodes(private val cpu: CPU) {

    //OP Codes - SED Group

    fun OP_F8(){
        cpu.setDecimalModeFlag(1u)
    }
}
