package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)

open class rla_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - AND Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_23(){
        val bal: UByte = mmu.readFromMemory(cpu.programCounterRegister)//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter()//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort() //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort()//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = mmu.readFromMemory(zeroPageAddress) //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        arithmeticRotateLeft(indexedIndirectAddress)
        andWithAccumulator(indexedIndirectAddress)
        cpu.incrementClockCycle(8)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_27(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        arithmeticRotateLeft(zeroPageAddress)
        andWithAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(5)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_2F(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        arithmeticRotateLeft(src)
        andWithAccumulator(src)
        cpu.incrementClockCycle(6)
    }
    //Indirect Indexed
    fun OP_33(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc + 1 initial low address from OP Code Parameter
        cpu.incrementProgramCounter()//pc + 2
        val zeroPageAddress: UShort = (addressLow + 1u).toUShort()
        val indirectIndexedAddress: UShort
        if(zeroPageAddress <= 0xFFu){
            addressHigh = mmu.readFromMemory(zeroPageAddress)
            indirectIndexedAddress = ((addressHigh.toInt() shl 8) + (zeroPageAddress.toInt() + cpu.indexYRegister.toInt())).toUShort()
        } else {
            addressHigh = mmu.readFromMemory(zeroPageAddress)
            indirectIndexedAddress = (((addressHigh.toInt() + 1) shl 8) + (zeroPageAddress.toInt() + cpu.indexXRegister.toInt())).toUShort()
        }
        arithmeticRotateLeft(indirectIndexedAddress)
        andWithAccumulator(indirectIndexedAddress)
        cpu.incrementClockCycle(8)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_37(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        cpu.incrementProgramCounter() //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexXRegister).toUShort()
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort()//pc+2
        }
        cpu.incrementProgramCounter()//pc+3
        arithmeticRotateLeft(zeroPageAddress)
        andWithAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(6)
    }
    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_3B(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister)//pc+2
        cpu.incrementProgramCounter()//pc+3

        val src: UShort

        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort()
        } else {
            src = (((addressHigh.toInt() shl 8)+1) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort()
        }
        arithmeticRotateLeft(src)
        andWithAccumulator(src)
        cpu.incrementClockCycle(7)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_3F(){
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
        arithmeticRotateLeft(src)
        andWithAccumulator(src)
        cpu.incrementClockCycle(7)
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

    fun andWithAccumulator(address: UShort){
        val src: UByte = mmu.readFromMemory(address)
        cpu.accumulatorRegister = cpu.accumulatorRegister and src
    }
}
