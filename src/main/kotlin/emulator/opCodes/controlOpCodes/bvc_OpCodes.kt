package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class bvc_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - BVC Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_50(){
        if(cpu.getOverflowFlag().toUInt() == 0u) {
            val temp: UByte = cpu.ram[cpu.programCounterRegister.toInt()]
            val newPC: UShort
            if(temp.toUInt() and 128u != 0u) {
                val posTemp: UByte = (temp.inv() + 1u).toUByte()
                newPC = (cpu.programCounterRegister - posTemp).toUShort()
            } else {
                newPC = (cpu.programCounterRegister + temp).toUShort()
            }
            if((newPC.toUInt()/256u) == (cpu.programCounterRegister.toUInt()/256u)){
                cpu.incrementClockCycle(3)
            } else {
                cpu.incrementClockCycle(4)
            }
            cpu.programCounterRegister = newPC
        }
    }
}
