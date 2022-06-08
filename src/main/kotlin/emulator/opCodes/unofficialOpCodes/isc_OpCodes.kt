package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class isc_OpCodes (private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - STA Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_E3(){
        val bal: UByte = mmu.readFromMemory(cpu.programCounterRegister)//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter()//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort() //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort()//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = mmu.readFromMemory(zeroPageAddress) //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        incrementMemory(indexedIndirectAddress)
        subtractFromAccumulator(indexedIndirectAddress)
        cpu.incrementClockCycle(8)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_E7(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        incrementMemory(zeroPageAddress)
        subtractFromAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(5)
    }

    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_EF(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        incrementMemory(src)
        subtractFromAccumulator(src)
        cpu.incrementClockCycle(6)
    }
    //Indirect Indexed
    fun OP_F3(){
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

        incrementMemory(indirectIndexedAddress)
        subtractFromAccumulator(indirectIndexedAddress)
        cpu.incrementClockCycle(8)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_F7(){
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
        incrementMemory(zeroPageAddress)
        subtractFromAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(6)
    }
    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_FB(){
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
        incrementMemory(src)
        subtractFromAccumulator(src)
        cpu.incrementClockCycle(7)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_FF(){
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
        incrementMemory(src)
        subtractFromAccumulator(src)
        cpu.incrementClockCycle(7)
    }

    

    fun incrementMemory(address: UShort?){
        if(address == null) {
            if(cpu.opCode.toUInt() == 0xE8u){
                var src: UByte = cpu.indexXRegister
                src = (src + 1u).toUByte()
                if ((src and 128u).toUInt() != 0u) {
                    cpu.setNegativeFlag(1u)
                } else {
                    cpu.resetNegativeFlag()
                }
                if (src.toUInt() == 0u) {
                    cpu.setZeroFlag(1u)
                } else {
                    cpu.resetZeroFlag()
                }
                cpu.indexXRegister = (src and 0xFFu)
            } else {
                var src: UByte = cpu.indexYRegister
                src = (src + 1u).toUByte()
                if ((src and 128u).toUInt() != 0u) {
                    cpu.setNegativeFlag(1u)
                } else {
                    cpu.resetNegativeFlag()
                }
                if (src.toUInt() == 0u) {
                    cpu.setZeroFlag(1u)
                } else {
                    cpu.resetZeroFlag()
                }
                cpu.indexYRegister = (src and 0xFFu)
            }
        } else {
            var src: UByte = mmu.readFromMemory(address)
            src = (src + 1u).toUByte()
            if ((src and 128u).toUInt() != 0u) {
                cpu.setNegativeFlag(1u)
            } else {
                cpu.resetNegativeFlag()
            }
            if (src.toUInt() == 0u) {
                cpu.setZeroFlag(1u)
            } else {
                cpu.resetZeroFlag()
            }
            mmu.writeToMemory(address, (src and 0xFFu))
        }
    }

    fun subtractFromAccumulator(address: UShort){
        val src: UByte = mmu.readFromMemory(address)
        val temp: Int = cpu.accumulatorRegister.toInt() - src.toInt() - cpu.getCarryFlag().toInt()
        cpu.accumulatorRegister = (temp and 0x000000FF).toUByte()

        if(temp and 0xFFFFFF00.toInt() != 0){
            cpu.setOverflowFlag(1u)
        } else {
            cpu.resetOverflowFlag()
        }

        if(temp and 128 != 0) {
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }

        if(cpu.accumulatorRegister > 0u){
            cpu.setCarryFlag(1u)
            cpu.resetZeroFlag()
        } else if (cpu.accumulatorRegister < 0u){
            cpu.resetCarryFlag()
            cpu.resetZeroFlag()
        } else {
            cpu.setCarryFlag(1u)
            cpu.setZeroFlag(1u)
        }
    }
}
