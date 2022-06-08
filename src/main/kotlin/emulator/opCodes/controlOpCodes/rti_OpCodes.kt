package emulator.opCodes.controlOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class rti_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - RTI Group
    fun OP_40(){
        addressLow = mmu.readFromMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort())
        cpu.decrementStackPointer()
        addressHigh = mmu.readFromMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort())
        cpu.decrementStackPointer()
        cpu.programCounterRegister = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        cpu.incrementClockCycle(6)
    }
}
