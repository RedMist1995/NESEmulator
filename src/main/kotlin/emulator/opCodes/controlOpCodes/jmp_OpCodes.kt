package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class jmp_OpCodes(private val cpu: CPU, private val mmu: MMU, private val ppu: PPU, private val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - JMP Group
    //Addressing Modes
    //Absolute Addressing
    fun OP_4C(){
        debugWriter.writeProgramCounter()
        debugWriter.writeOPCode("4C")
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)
        debugWriter.writeLowAddress(addressLow.toInt())
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister)
        debugWriter.writeHighAddress(addressHigh.toInt(), addressLow.toInt(), "JMP")
        cpu.programCounterRegister = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        debugWriter.writeRemainder()
        cpu.incrementClockCycle(3)
    }

    //Indirect Addressing
    fun OP_6C(){
        debugWriter.writeProgramCounter()
        debugWriter.writeOPCode("6C")
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)
        debugWriter.writeLowAddress(addressLow.toInt())
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister)
        debugWriter.writeHighAddress(addressHigh.toInt(), addressLow.toInt(), "JMP")
        val newProgramCounter: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        cpu.programCounterRegister = mmu.readFromMemory(newProgramCounter).toUShort()
        debugWriter.writeRemainder()
        cpu.incrementClockCycle(5)
    }
}
