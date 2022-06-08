package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU
import kotlin.experimental.and

@OptIn(ExperimentalUnsignedTypes::class)
public open class dcp_OpCodes (private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - SBC Group
    //OP Codes - ADC Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_C3(){
        val bal: UByte = mmu.readFromMemory(cpu.programCounterRegister)//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter()//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort() //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort()//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = mmu.readFromMemory(zeroPageAddress) //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        decrementMemory(indexedIndirectAddress)
        compareWithAccumulator(indexedIndirectAddress)
        cpu.incrementClockCycle(8)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_C7(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        decrementMemory(zeroPageAddress)
        compareWithAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(5)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_CF(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        decrementMemory(src)
        compareWithAccumulator(src)
        cpu.incrementClockCycle(6)
    }
    //Indirect Indexed
    fun OP_D3(){
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

        decrementMemory(indirectIndexedAddress)
        compareWithAccumulator(indirectIndexedAddress)
        cpu.incrementClockCycle(8)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_D7(){
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
        decrementMemory(zeroPageAddress)
        compareWithAccumulator(zeroPageAddress)
        cpu.incrementClockCycle(6)
    }
    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_DB(){
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
        decrementMemory(src)
        compareWithAccumulator(src)
        cpu.incrementClockCycle(7)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_DF(){
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
        decrementMemory(src)
        compareWithAccumulator(src)
        cpu.incrementClockCycle(7)
    }

    

    fun decrementMemory(address: UShort?){
        if(address == null){
            if(cpu.opCode.toUInt() == 0xCAu) {
                var src: Byte = cpu.indexXRegister.toByte()
                src = (src - 1).toByte()
                if (src and 128.toByte() != 0.toByte()) {
                    cpu.setNegativeFlag(1u)
                } else {
                    cpu.resetNegativeFlag()
                }
                if (src == 0.toByte()) {
                    cpu.setZeroFlag(1u)
                } else {
                    cpu.resetZeroFlag()
                }
                cpu.indexXRegister = (src and 0xFF.toByte()).toUByte()
            } else {
                var src: Byte = cpu.indexYRegister.toByte()
                src = (src - 1).toByte()
                if (src and 128.toByte() != 0.toByte()) {
                    cpu.setNegativeFlag(1u)
                } else {
                    cpu.resetNegativeFlag()
                }
                if (src == 0.toByte()) {
                    cpu.setZeroFlag(1u)
                } else {
                    cpu.resetZeroFlag()
                }
                cpu.indexYRegister = (src and 0xFF.toByte()).toUByte()
            }
        } else {
            var src: Byte = mmu.readFromMemory(address).toByte()
            src = (src - 1).toByte()
            if (src and 128.toByte() != 0.toByte()) {
                cpu.setNegativeFlag(1u)
            } else {
                cpu.resetNegativeFlag()
            }
            if (src == 0.toByte()) {
                cpu.setZeroFlag(1u)
            } else {
                cpu.resetZeroFlag()
            }
            mmu.writeToMemory(address, (src and 0xFF.toByte()).toUByte())
        }
    }

    fun compareWithAccumulator(address: UShort){
        val src: UByte = mmu.readFromMemory(address)
        val sub: Int = cpu.accumulatorRegister.toInt() - src.toInt()
        if(sub == 0){
            cpu.setZeroFlag(1u)
        } else if (sub < 0){
            cpu.resetCarryFlag()
            cpu.resetZeroFlag()
            cpu.setNegativeFlag(1u)
        } else {
            cpu.setCarryFlag(1u)
            cpu.resetZeroFlag()
        }
    }
}
