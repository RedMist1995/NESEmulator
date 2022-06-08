package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class pla_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - PLA Group
    fun OP_68(){
        cpu.accumulatorRegister = mmu.readFromMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort())
        cpu.decrementStackPointer()
        cpu.incrementClockCycle(4)
    }
}
