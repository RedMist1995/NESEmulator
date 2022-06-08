package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class alr_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - CMP Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_4B(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        andWithAccumulator(addressLow.toUShort())
        logicalShiftRight()
        cpu.incrementClockCycle(2)
    }

    fun andWithAccumulator(address: UShort){
        val src: UByte = mmu.readFromMemory(address)
        cpu.accumulatorRegister = cpu.accumulatorRegister and src
    }

    private fun logicalShiftRight(){
        val newCarry: UByte = cpu.accumulatorRegister and 1u
        val temp: UByte = ((cpu.accumulatorRegister.toInt() shr 1) and 0xFF).toUByte()
        cpu.accumulatorRegister = temp
        cpu.setCarryFlag(newCarry)
    }
}
