package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU
import emulator.hardware.MMU

@OptIn(ExperimentalUnsignedTypes::class)
open class bne_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - BNE Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_D0(){
        if(cpu.getZeroFlag().toUInt() == 0u) {
            val temp: UByte = mmu.readFromMemory(cpu.programCounterRegister)
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
