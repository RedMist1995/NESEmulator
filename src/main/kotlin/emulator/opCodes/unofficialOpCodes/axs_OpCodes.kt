package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class axs_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - CMP Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_CB(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        andAXStoreMemory(addressLow.toUShort())
        cpu.incrementClockCycle(2)
    }

    fun andAXStoreMemory(address: UShort){
        mmu.writeToMemory(address, cpu.accumulatorRegister and cpu.indexXRegister)
    }
}
