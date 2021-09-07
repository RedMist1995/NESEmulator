package emulator.opCodes.controlOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
open class bit_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - BIT Group
    //Addressing Modes
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_24(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        testBitsAgainstAccumulator(zeroPageAddress);
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_2C(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        cpu.incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        testBitsAgainstAccumulator(src);
    }
    

    //Actual Add With Carry Operation
    private fun testBitsAgainstAccumulator(address: UShort){
        val mem: UByte = cpu.ram[address.toInt()]

        if(mem.toUInt() and 128u != 0u) {
            cpu.setNegativeFlag(1u);
        } else {
            cpu.resetNegativeFlag()
        }

        if(mem.toUInt() and 64u != 0u) {
            cpu.setOverflowFlag(1u);
        } else {
            cpu.resetOverflowFlag()
        }

        if(mem.toUInt() and cpu.accumulatorRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
