package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class bcc_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - BCC Group
    //Addressing Modes
    //Relative Address
    fun OP_90() {
        if(cpu.getCarryFlag().toUInt() == 0u) {
            cpu.programCounterRegister =
                (cpu.programCounterRegister + cpu.ram[cpu.programCounterRegister.toInt()]).toUShort()
        }
    }

}
