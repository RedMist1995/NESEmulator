package emulator.opCodes.unofficialOpCodes

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class las_OpCodes (private val cpu: CPU) {
    private var addressLow: UByte = 0u
    private var addressHigh: UByte = 0u

    //OP Codes - ORA Group
    //OP Codes - ADC Group
    //Addressing Modes
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_BB(){
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
        loadAccumulator(src)
        andMemorySPStoreAXSP(src)
    }

    

    fun loadAccumulator(memory: UShort){
        cpu.accumulatorRegister = cpu.ram[memory.toInt()]
    }

    fun andMemorySPStoreAXSP(memory: UShort){
        val temp: UByte = cpu.stackPointerRegister and cpu.ram[memory.toInt()]
        cpu.accumulatorRegister = temp
        cpu.indexXRegister = temp
        cpu.stackPointerRegister = temp
        if(temp.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }

        if(temp.toUInt() and 128u != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }
    }
}
