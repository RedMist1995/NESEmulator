package emulator.opCodes.rmwOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class ldx_OpCodes (private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - LDX Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_A2(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        loadIntoX(addressLow.toUShort())
        cpu.incrementClockCycle(2)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_A6(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        loadIntoX(zeroPageAddress)
        cpu.incrementClockCycle(3)
    }
    //Transfer Accumulator Into X
    fun OP_AA(){
        loadIntoX(null)
        cpu.incrementClockCycle(2)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_AE(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        loadIntoX(src)
        cpu.incrementClockCycle(4)
    }
    //Indirect Indexed
    fun OP_B6(){
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

        loadIntoX(indirectIndexedAddress)
        cpu.incrementClockCycle(4)
    }
    // Transfers Stack Pointer into X
    fun OP_BA(){
        loadIntoX(null)
        cpu.incrementClockCycle(2)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_BE(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister)//pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister)//pc+2
        cpu.incrementProgramCounter()//pc+3

        val src: UShort
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
            cpu.incrementClockCycle(4)
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort()
            cpu.incrementClockCycle(5)
        }
        loadIntoX(src)
    }

    fun loadIntoX(memory: UShort?){
        if(memory == null){
            if(cpu.opCode.toUInt() == 0xAAu){
                cpu.indexXRegister = cpu.accumulatorRegister
            } else {
                cpu.indexXRegister = cpu.stackPointerRegister
            }
        } else {
            cpu.indexXRegister = mmu.readFromMemory(memory)
        }
        if(cpu.indexXRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
        if((cpu.indexXRegister.toUInt() and 128u) != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
