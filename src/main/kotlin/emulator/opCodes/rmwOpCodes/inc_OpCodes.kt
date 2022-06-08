package emulator.opCodes.rmwOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class inc_OpCodes (private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - INC Group
    //Addressing Modes
    //increment index Y
    fun OP_C8(){
        cpu.incrementProgramCounter()//pc+2
        incrementMemory(null)
        cpu.incrementClockCycle(2)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_E6(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        incrementMemory(zeroPageAddress)
        cpu.incrementClockCycle(5)
    }
    //increment index X
    fun OP_E8(){
        cpu.incrementProgramCounter()//pc+2
        incrementMemory(null)
        cpu.incrementClockCycle(2)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_EE(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        incrementMemory(src)
        cpu.incrementClockCycle(6)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_F6(){
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
        cpu.incrementClockCycle(6)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_FE(){
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
                cpu.indexXRegister = (src and 0xFFu).toUByte()
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
                cpu.indexYRegister = (src and 0xFFu).toUByte()
            }
        } else {
            var src: UByte = mmu.readFromMemory(address).toUByte()
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
}
