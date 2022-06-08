package emulator.opCodes.rmwOpCodes

import emulator.hardware.CPU
import emulator.hardware.MMU

@OptIn(ExperimentalUnsignedTypes::class)
public open class sty_OpCodes (private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - STY Group
    //Addressing Modes
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_84(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        storeYInto(zeroPageAddress)
        cpu.incrementClockCycle(3)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_8C(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        storeYInto(src)
        cpu.incrementClockCycle(4)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexY the high address will always be 0x0000
    fun OP_94(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        cpu.incrementProgramCounter() //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexYRegister).toUShort()
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort()//pc+2
        }
        cpu.incrementProgramCounter()//pc+3
        storeYInto(zeroPageAddress)
        cpu.incrementClockCycle(4)
    }

    fun storeYInto(memory: UShort?){
        if(memory == null){
            if(cpu.opCode.toUInt() == 0x8Au){
                cpu.accumulatorRegister = cpu.indexYRegister
            } else {
                cpu.stackPointerRegister = cpu.indexYRegister
            }
        } else {
            mmu.writeToMemory(memory, cpu.indexYRegister)
        }
        if(cpu.indexYRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
        if((cpu.indexYRegister.toUInt() and 128u) != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
