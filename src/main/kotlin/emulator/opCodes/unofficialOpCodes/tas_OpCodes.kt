package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
open class tas_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - CMP Group
    //Addressing Modes
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_9B(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister)//pc+2
        cpu.incrementProgramCounter()//pc+3

        val src: UShort
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
        }
        tasOperation(src)
        cpu.incrementClockCycle(5)
    }

    //No Good Method Name. This opcode ANDs the contents of the A and X registers (without changing
    //the contents of either register) and transfers the result to the stack
    //pointer.  It then ANDs that result with the contents of the high byte of
    //the target address of the operand +1 and stores that final result in
    //memory.
    fun tasOperation(address: UShort){
        cpu.stackPointerRegister = cpu.accumulatorRegister and cpu.indexXRegister
        val temp: UByte = cpu.stackPointerRegister and mmu.readFromMemory((addressHigh.toInt() + 1).toUShort())
        mmu.writeToMemory(address, temp)
    }
}
