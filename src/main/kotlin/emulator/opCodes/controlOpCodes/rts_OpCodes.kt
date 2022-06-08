package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class rts_OpCodes (private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {

    //OP Codes - RTS Group
    fun OP_60(){
        cpu.decrementStackPointer()
        val pcLow: UByte = mmu.readFromMemory((cpu.stackStart + cpu.stackPointerRegister).toUShort())
        cpu.decrementStackPointer()
        val pcHigh: UByte = mmu.readFromMemory((cpu.stackStart + cpu.stackPointerRegister).toUShort())
        cpu.programCounterRegister = ((pcHigh.toInt() shl 8) + pcLow.toInt() + 1).toUShort()
        cpu.incrementClockCycle(6)
    }
}
