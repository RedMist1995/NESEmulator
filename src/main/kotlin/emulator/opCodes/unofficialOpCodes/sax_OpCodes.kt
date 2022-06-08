package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class sax_OpCodes (private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - LDA Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_83(){
        val bal: UByte = mmu.readFromMemory(cpu.programCounterRegister)//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter()//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort() //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort()//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = mmu.readFromMemory(zeroPageAddress) //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        storeAAndXInMemory(indexedIndirectAddress)
        cpu.incrementClockCycle(6)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_87(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        storeAAndXInMemory(zeroPageAddress)
        cpu.incrementClockCycle(3)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_8F(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        storeAAndXInMemory(src)
        cpu.incrementClockCycle(4)
    }
    //Zero Page Indexed Addressing - speical case where Y register is allowed for Zero Page Indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_97(){
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
        storeAAndXInMemory(zeroPageAddress)
        cpu.incrementClockCycle(4)
    }

    fun storeAAndXInMemory(memory: UShort){
        val value: UByte = cpu.accumulatorRegister and cpu.indexXRegister
        mmu.writeToMemory(memory, value)
    }
}
