package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class jmp_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - ADC Group
    //Addressing Modes
    //Absolute Addressing
    fun OP_4C(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]
        cpu.programCounterRegister = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        cpu.incrementClockCycle(3)
    }

    //Indirect Addressing
    fun OP_6C(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]
        val newProgramCounter: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        cpu.programCounterRegister = cpu.ram[newProgramCounter.toInt()].toUShort()
        cpu.incrementClockCycle(5)
    }
}
