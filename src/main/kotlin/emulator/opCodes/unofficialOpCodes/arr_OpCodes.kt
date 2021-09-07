package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class arr_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - CMP Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_6B(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()] //pc+1
        cpu.incrementProgramCounter() //pc+2
        andWithAccumulator(addressLow.toUShort())
        arithmeticRotateRight()
        cpu.incrementClockCycle(2)
    }

    

    fun andWithAccumulator(address: UShort){
        val src: UByte = cpu.ram[address.toInt()]
        cpu.accumulatorRegister = cpu.accumulatorRegister and src
    }

    private fun arithmeticRotateRight(){
        val temp: UByte
        val newCarry: UByte = cpu.accumulatorRegister and 1u
        temp = (((cpu.accumulatorRegister.toInt() + (cpu.getCarryFlag().toInt() shl 8)) shr 1) and 0xFF).toUByte()
        cpu.accumulatorRegister = temp
        cpu.setCarryFlag(newCarry)
    }
}
