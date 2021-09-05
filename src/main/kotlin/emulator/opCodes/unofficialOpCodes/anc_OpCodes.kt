package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
class anc_OpCodes(private val cpu: CPU, private val ppu:PPU, private val apu:APU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - ANC Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_0B(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        andWithAccumulator(addressLow.toUShort());
        arithmeticRotateLeft(addressLow.toUShort())
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    private fun incrementProgramCounter(){
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort();
    }

    fun andWithAccumulator(address: UShort){
        val src: UByte = cpu.ram[address.toInt()];
        cpu.accumulatorRegister = cpu.accumulatorRegister and src;
    }

    private fun arithmeticRotateLeft(address: UShort?){
        val temp: UByte
        if(address == null){
            val newCarry: UByte = ((cpu.accumulatorRegister.toInt() shr 7) and 0xFF).toUByte()
            temp = ((cpu.accumulatorRegister.toInt() shl 1) and 0xFF + cpu.getCarryFlag().toInt()).toUByte()
            cpu.accumulatorRegister = temp
            cpu.setCarryFlag(newCarry)
        } else {
            val src: UByte = cpu.ram[address.toInt()];
            val newCarry: UByte = ((src.toInt() shr 7) and 0xFF).toUByte()
            temp = ((src.toInt() shl 1) and 0xFF + cpu.getCarryFlag().toInt()).toUByte()
            cpu.ram[address.toInt()] = temp
            cpu.setCarryFlag(newCarry)
        }

        if(temp.toUInt() and 128u != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.setNegativeFlag(0u)
        }

        if(temp.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.setZeroFlag(0u)
        }
    }
}
