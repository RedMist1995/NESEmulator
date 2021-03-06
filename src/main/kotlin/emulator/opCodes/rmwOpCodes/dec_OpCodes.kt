package emulator.opCodes.rmwOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU
import kotlin.experimental.and

@OptIn(ExperimentalUnsignedTypes::class)
open class dec_OpCodes(private val cpu: CPU, private val mmu: MMU, val debugWriter: debugWriter) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - DEC Group
    //Addressing Modes
    //decrement index Y
    fun OP_88(){
        cpu.incrementProgramCounter()//pc+2
        decrementMemory(null)
        cpu.incrementClockCycle(2)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_C6(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort()
        decrementMemory(zeroPageAddress)
        cpu.incrementClockCycle(5)
    }
    fun OP_CA(){
        cpu.incrementProgramCounter()//pc+2
        decrementMemory(null)
        cpu.incrementClockCycle(2)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_CE(){
        addressLow = mmu.readFromMemory(cpu.programCounterRegister) //pc+1
        cpu.incrementProgramCounter() //pc+2
        addressHigh = mmu.readFromMemory(cpu.programCounterRegister) //pc+2
        cpu.incrementProgramCounter() //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
        decrementMemory(src)
        cpu.incrementClockCycle(6)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_D6(){
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
        cpu.incrementClockCycle(6)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_DE(){
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
}
