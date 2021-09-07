package emulator.opCodes.aluOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class ora_OpCodes (private val cpu: CPU) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - ORA Group
    //OP Codes - ADC Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_01(){
        val bal: UByte = cpu.ram[cpu.programCounterRegister.toInt()]//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter()//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort() //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort()//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = cpu.ram[zeroPageAddress.toInt()] //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        orWithAccumulator(indexedIndirectAddress)
        cpu.incrementClockCycle(6)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_05(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()] //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        orWithAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(3)
    }
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_09(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()] //pc+1
        cpu.incrementProgramCounter() //pc+2
        orWithAccumulator(addressLow.toUShort())
        cpu.incrementClockCycle(2)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_0D(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()] //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()] //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        orWithAccumulator(src)
        cpu.incrementClockCycle(4)
    }
    //Indirect Indexed
    fun OP_11(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]//pc + 1 initial low address from OP Code Parameter
        cpu.incrementProgramCounter()//pc + 2
        val zeroPageAddress: UShort = (addressLow + 1u).toUShort()
        val indirectIndexedAddress: UShort
        if(zeroPageAddress <= 0xFFu){
            addressHigh = cpu.ram[zeroPageAddress.toInt()]
            indirectIndexedAddress = ((addressHigh.toInt() shl 8) + (zeroPageAddress.toInt() + cpu.indexYRegister.toInt())).toUShort()
        } else {
            addressHigh = cpu.ram[zeroPageAddress.toInt()]
            indirectIndexedAddress = (((addressHigh.toInt() + 1) shl 8) + (zeroPageAddress.toInt() + cpu.indexXRegister.toInt())).toUShort()
        }

        orWithAccumulator(indirectIndexedAddress)
        cpu.incrementClockCycle(5)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_15(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]//pc+1
        cpu.incrementProgramCounter() //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexXRegister).toUShort()
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort()//pc+2
        }
        cpu.incrementProgramCounter()//pc+3
        orWithAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(4)
    }
    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_19(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]//pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]//pc+2
        cpu.incrementProgramCounter()//pc+3

        val src: UShort
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
            cpu.incrementClockCycle(4)
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
            cpu.incrementClockCycle(5)
        }
        orWithAccumulator(src)
    }

    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_1D(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]//pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]//pc+2
        cpu.incrementProgramCounter()//pc+3

        val src: UShort

        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort()
            cpu.incrementClockCycle(4)
        } else {
            src = (((addressHigh.toInt() shl 8)+1) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort()
            cpu.incrementClockCycle(5)
        }
        orWithAccumulator(src)
    }

    fun orWithAccumulator(address: UShort){
        val src: UByte = cpu.ram[address.toInt()]
        cpu.accumulatorRegister = cpu.accumulatorRegister or src
    }
}
