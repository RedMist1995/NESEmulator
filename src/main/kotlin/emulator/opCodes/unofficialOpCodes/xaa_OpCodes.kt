package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class xaa_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - CMP Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_8B(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        transferXToAANDWithAddress(addressLow.toUShort());
    }

    

    fun transferXToAANDWithAddress(address: UShort){
        cpu.accumulatorRegister = cpu.indexXRegister and cpu.ram[address.toInt()]
    }
}
