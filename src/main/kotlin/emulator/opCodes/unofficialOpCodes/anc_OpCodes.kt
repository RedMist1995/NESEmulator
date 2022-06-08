package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class anc_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - ANC Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_0B(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        andWithAccumulator(addressLow.toUShort())
        arithmeticRotateLeft(addressLow.toUShort())
        cpu.incrementClockCycle(2)
    }

    fun andWithAccumulator(address: UShort){
        val src: UByte = mmu.readFromMemory(address)
        cpu.accumulatorRegister = cpu.accumulatorRegister and src
    }

    private fun arithmeticRotateLeft(address: UShort?){
        val temp: UByte
        if(address == null){
            val newCarry: UByte = ((cpu.accumulatorRegister.toInt() shr 7) and 0xFF).toUByte()
            temp = ((cpu.accumulatorRegister.toInt() shl 1) and 0xFF + cpu.getCarryFlag().toInt()).toUByte()
            cpu.accumulatorRegister = temp
            cpu.setCarryFlag(newCarry)
        } else {
            val src: UByte = mmu.readFromMemory(address)
            val newCarry: UByte = ((src.toInt() shr 7) and 0xFF).toUByte()
            temp = ((src.toInt() shl 1) and 0xFF + cpu.getCarryFlag().toInt()).toUByte()
            mmu.writeToMemory(address, temp)
            cpu.setCarryFlag(newCarry)
        }

        if(temp.toUInt() and 128u != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }

        if(temp.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
