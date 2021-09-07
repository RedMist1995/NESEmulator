package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.CPU;

@OptIn(ExperimentalUnsignedTypes::class)
open class shy_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - SHY Group
    //Addressing Modes
    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_9C(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()];//pc+2
        cpu.incrementProgramCounter();//pc+3

        val src: UShort;

        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort();
        } else {
            src = (((addressHigh.toInt() shl 8)+1) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort();
        }
        andYHStoreMemory(src);
    }

    //Actual Add With Carry Operation
    fun andYHStoreMemory(address: UShort){
        cpu.ram[address.toInt()] = cpu.indexYRegister and cpu.ram[addressHigh.toInt()]
    }
}
