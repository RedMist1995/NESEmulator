package emulator.opCodes.aluOpCodes

import emulator.debug.debugWriter
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU
import tornadofx.dumpStylesheets

@OptIn(ExperimentalUnsignedTypes::class)
open class adc_OpCodes(private val cpu: CPU, private val ppu: PPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - ADC Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_61(){
        debugWriter.writeProgramCounter()
        val bal: UByte = mmu.readFromMemory(cpu.programCounterRegister)//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter()//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort() //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort()//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = mmu.readFromMemory(zeroPageAddress) //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        debugWriter.writeHighAddress(addressHigh.toInt(), zeroPageAddress.toInt(), "ADC")
        addWithCarry(indexedIndirectAddress)
        debugWriter.writeRemainder()
        cpu.incrementClockCycle(6)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_65(){
        debugWriter.writeProgramCounter()
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        debugWriter.writeLowAddress(addressLow.toInt())
        val zeroPageAddress: UShort = addressLow.toUShort()
        addWithCarry(zeroPageAddress)
        debugWriter.writeHighAddress(zeroPageAddress.toInt(), addressLow.toInt(), "ADC")
        debugWriter.writeRemainder()
        cpu.incrementClockCycle(3)
    }
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_69(){
        debugWriter.writeProgramCounter()
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        debugWriter.writeLowAddress(addressLow.toInt())
        debugWriter.writeHighAddress(0, addressLow.toInt(), "ADC")
        addWithCarry(addressLow.toUShort())
        debugWriter.writeRemainder()
        cpu.incrementClockCycle(2)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_6D(){
        debugWriter.writeProgramCounter()
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        debugWriter.writeLowAddress(addressLow.toInt())
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        debugWriter.writeHighAddress(addressHigh.toInt(), addressLow.toInt(), "ADC")
        debugWriter.writeRemainder()
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        addWithCarry(src)
        cpu.incrementClockCycle(4)
    }
    //Indirect Indexed
    fun OP_71(){
        debugWriter.writeProgramCounter()
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc + 1 initial low address from OP Code Parameter
        cpu.incrementProgramCounter()//pc + 2
        val zeroPageAddress: UShort = (addressLow + 1u).toUShort()
        val indirectIndexedAddress: UShort
        debugWriter.writeLowAddress(addressLow.toInt())
        if(zeroPageAddress <= 0xFFu){
            addressHigh = mmu.readFromMemory(zeroPageAddress)
            debugWriter.writeHighAddress(addressHigh.toInt(), addressLow.toInt(), "ADC")
            debugWriter.writeRemainder()
            indirectIndexedAddress = ((addressHigh.toInt() shl 8) + (zeroPageAddress.toInt() + cpu.indexYRegister.toInt())).toUShort()
            cpu.incrementClockCycle(5)
        } else {
            addressHigh = mmu.readFromMemory(zeroPageAddress)
            debugWriter.writeHighAddress(addressHigh.toInt(), addressLow.toInt(), "ADC")
            debugWriter.writeRemainder()
            indirectIndexedAddress = (((addressHigh.toInt() + 1) shl 8) + (zeroPageAddress.toInt() + cpu.indexXRegister.toInt())).toUShort()
            cpu.incrementClockCycle(6)
        }

        addWithCarry(indirectIndexedAddress)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_75(){
        debugWriter.writeProgramCounter()
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        debugWriter.writeLowAddress(addressLow.toInt())
        cpu.incrementProgramCounter() //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexXRegister).toUShort()
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort()//pc+2
        }
        debugWriter.writeHighAddress(zeroPageAddress.toInt(), addressLow.toInt(), "ADC")
        debugWriter.writeRemainder()
        cpu.incrementProgramCounter()//pc+3
        addWithCarry(zeroPageAddress)
        cpu.incrementClockCycle(5)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_79(){
        debugWriter.writeProgramCounter()
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister)//pc+2
        cpu.incrementProgramCounter()//pc+3

        debugWriter.writeLowAddress(addressLow.toInt())
        debugWriter.writeHighAddress(addressHigh.toInt(), addressLow.toInt(), "ADC")

        val src: UShort
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
            debugWriter.writeRemainder()
            cpu.incrementClockCycle(4)
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
            debugWriter.writeRemainder()
            cpu.incrementClockCycle(5)
        }
        addWithCarry(src)
    }

    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_7D(){
        debugWriter.writeProgramCounter()
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister)//pc+2
        cpu.incrementProgramCounter()//pc+3

        debugWriter.writeLowAddress(addressLow.toInt())
        debugWriter.writeHighAddress(addressHigh.toInt(), addressLow.toInt(), "ADC")

        val src: UShort

        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort()
            debugWriter.writeRemainder()
            cpu.incrementClockCycle(4)
        } else {
            src = (((addressHigh.toInt() shl 8)+1) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort()
            debugWriter.writeRemainder()
            cpu.incrementClockCycle(5)
        }
        addWithCarry(src)
    }

    //Actual Add With Carry Operation
    private fun addWithCarry(address: UShort){
        val src: UByte = mmu.readFromMemory(address)
        val temp: UInt = src.toUInt() + cpu.accumulatorRegister.toUInt() + cpu.getCarryFlag().toUInt()
        cpu.accumulatorRegister = (temp and 0x000000FFu).toUByte()

        if((temp and 0xFFFFFF00u) != 0u){
            cpu.setCarryFlag(1u)
            cpu.setOverflowFlag(1u)
        } else {
            cpu.resetCarryFlag()
        }

        if(temp and 128u != 0u) {
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }

        if(cpu.accumulatorRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
