package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public class las_OpCodes (private val cpu: CPU, private val ppu: PPU, private val apu: APU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - ORA Group
    //OP Codes - ADC Group
    //Addressing Modes
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_BB(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()];//pc+2
        incrementProgramCounter();//pc+3

        val src: UShort;
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        }
        loadAccumulator(src);
        loadIntoX(src)
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    private fun incrementProgramCounter(){
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort();
    }

    fun loadAccumulator(memory: UShort){
        cpu.accumulatorRegister = cpu.ram[memory.toInt()];
    }

    fun loadIntoX(memory: UShort?){
        if(memory == null){
            if(cpu.opCode.toUInt() == 0xAAu){
                cpu.indexXRegister = cpu.accumulatorRegister
            } else {
                cpu.indexXRegister = cpu.stackPointerRegister
            }
        } else {
            cpu.indexXRegister = cpu.ram[memory.toInt()]
        }
        if(cpu.indexXRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.setZeroFlag(0u)
        }
        if((cpu.indexXRegister.toUInt() and 128u) != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.setZeroFlag(0u)
        }
    }
}
