package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
open class bne_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - BNE Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_D0(){
        if(cpu.getZeroFlag().toUInt() == 0u) {
            cpu.programCounterRegister =
                (cpu.programCounterRegister + cpu.ram[cpu.programCounterRegister.toInt()]).toUShort()
        }
    }
}
