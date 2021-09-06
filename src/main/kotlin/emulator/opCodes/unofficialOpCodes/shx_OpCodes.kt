package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.CPU;

@OptIn(ExperimentalUnsignedTypes::class)
class shx_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - SHY Group
    //Addressing Modes
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_9E(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()];//pc+2
        cpu.incrementProgramCounter();//pc+3

        val src: UShort;
        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        } else {
            src = (((addressHigh.toInt() shl 8) + 1) + (addressLow.toInt() + cpu.indexYRegister.toInt())).toUShort();
        }
        andXHStoreMemory(src);
    }

    //Actual Add With Carry Operation
    fun andXHStoreMemory(address: UShort){
        cpu.ram[address.toInt()] = cpu.indexXRegister and cpu.ram[addressHigh.toInt()]
    }
}
