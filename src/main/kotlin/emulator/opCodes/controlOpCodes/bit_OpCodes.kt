package emulator.opCodes.controlOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU
import emulator.hardware.MMU

@OptIn(ExperimentalUnsignedTypes::class)
open class bit_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - BIT Group
    //Addressing Modes
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_24(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        testBitsAgainstAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(3)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_2C(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        testBitsAgainstAccumulator(src)
        cpu.incrementClockCycle(4)
    }
    

    //Actual Add With Carry Operation
    private fun testBitsAgainstAccumulator(address: UShort){
        val mem: UByte = mmu.readFromMemory(address)

        if(mem.toUInt() and 128u != 0u) {
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }

        if(mem.toUInt() and 64u != 0u) {
            cpu.setOverflowFlag(1u)
        } else {
            cpu.resetOverflowFlag()
        }

        if(mem.toUInt() and cpu.accumulatorRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
