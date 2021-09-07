package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
open class slo_OpCodes(private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - SLO Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_03(){
        val bal: UByte = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1 initial low address from OP Parameter
        cpu.incrementProgramCounter();//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort(); //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort();//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = cpu.ram[zeroPageAddress.toInt()]; //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        arithmeticShiftLeft(indexedIndirectAddress);
        orWithAccumulator(indexedIndirectAddress)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_07(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        arithmeticShiftLeft(zeroPageAddress);
        orWithAccumulator(zeroPageAddress)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_0F(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        cpu.incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        arithmeticShiftLeft(src);
        orWithAccumulator(src)
    }
    //Indirect Indexed
    fun OP_13(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc + 1 initial low address from OP Code Parameter
        cpu.incrementProgramCounter();//pc + 2
        val zeroPageAddress: UShort = (addressLow + 1u).toUShort();
        val indirectIndexedAddress: UShort;
        if(zeroPageAddress <= 0xFFu){
            addressHigh = cpu.ram[zeroPageAddress.toInt()];
            indirectIndexedAddress = ((addressHigh.toInt() shl 8) + (zeroPageAddress.toInt() + cpu.indexYRegister.toInt())).toUShort();
        } else {
            addressHigh = cpu.ram[zeroPageAddress.toInt()];
            indirectIndexedAddress = (((addressHigh.toInt() + 1) shl 8) + (zeroPageAddress.toInt() + cpu.indexXRegister.toInt())).toUShort();
        }

        arithmeticShiftLeft(indirectIndexedAddress);
        orWithAccumulator(indirectIndexedAddress)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_17(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        cpu.incrementProgramCounter(); //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexXRegister).toUShort();
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace;//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort();//pc+2
        }
        cpu.incrementProgramCounter();//pc+3
        arithmeticShiftLeft(zeroPageAddress);
        orWithAccumulator(zeroPageAddress)
    }
    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_1B(){
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
        arithmeticShiftLeft(src);
        orWithAccumulator(src)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_1F(){
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
        arithmeticShiftLeft(src);
        orWithAccumulator(src)
    }

    

    private fun arithmeticShiftLeft(address: UShort?){
        val temp: UByte
        if(address == null){
            cpu.setCarryFlag(((cpu.accumulatorRegister.toInt() shr 7) and 0xFF).toUByte())
            temp = ((cpu.accumulatorRegister.toInt() shl 1) and 0xFF).toUByte()
            cpu.accumulatorRegister = temp
        } else {
            val src: UByte = cpu.ram[address.toInt()];
            cpu.setCarryFlag(((src.toInt() shr 7) and 0xFF).toUByte())
            temp = ((src.toInt() shl 1) and 0xFF).toUByte()
            cpu.ram[address.toInt()] = temp
        }

        if(temp.toUInt() and 128u != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetNegativeFlag()
        }

        if(temp.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }

    fun orWithAccumulator(address: UShort){
        val src: UByte = cpu.ram[address.toInt()];
        cpu.accumulatorRegister = cpu.accumulatorRegister or src;
    }
}
