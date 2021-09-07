package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class axs_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - CMP Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_CB(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()] //pc+1
        cpu.incrementProgramCounter() //pc+2
        andAXStoreMemory(addressLow.toUShort())
        cpu.incrementClockCycle(2)
    }

    

    fun andAXStoreMemory(address: UShort){
        cpu.ram[address.toInt()] = cpu.accumulatorRegister and cpu.indexXRegister
    }
}
