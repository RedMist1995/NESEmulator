package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.APU;
import emulator.hardware.CPU;
import emulator.hardware.PPU;

@OptIn(ExperimentalUnsignedTypes::class)
public class rra_OpCodes(private val cpu: CPU, private val ppu:PPU, private val apu:APU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - EOR Group
    //Addressing Modes
    //Indexed Indirect
    fun OP_63(){
        val bal: UByte = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1 initial low address from OP Parameter
        incrementProgramCounter();//pc+2
        var zeroPageAddress: UShort = (bal + cpu.indexXRegister + 1u).toUShort(); //creates a zero pages stand in BAL and is the real low address byte if under FF
        if(zeroPageAddress > 0xFFu) {
            zeroPageAddress = (zeroPageAddress - 0x100u).toUShort();//creates the real low address byte if over FF by stripping the carry and wrapping to the low zero page address
        }
        addressHigh = cpu.ram[zeroPageAddress.toInt()]; //gets the high address byte by accessing the memory location of the zero paged low address byte
        val indexedIndirectAddress: UShort = ((addressHigh.toInt() shl 8) + zeroPageAddress.toInt()).toUShort() //creates the final indexed indirect address
        arithmeticRotateRight(indexedIndirectAddress);
        addWithCarry(indexedIndirectAddress)
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_67(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        arithmeticRotateRight(zeroPageAddress);
        addWithCarry(zeroPageAddress)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_6F(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        arithmeticRotateRight(src);
        addWithCarry(src)
    }
    //Indirect Indexed
    fun OP_73(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc + 1 initial low address from OP Code Parameter
        incrementProgramCounter();//pc + 2
        val zeroPageAddress: UShort = (addressLow + 1u).toUShort();
        val indirectIndexedAddress: UShort;
        if(zeroPageAddress <= 0xFFu){
            addressHigh = cpu.ram[zeroPageAddress.toInt()];
            indirectIndexedAddress = ((addressHigh.toInt() shl 8) + (zeroPageAddress.toInt() + cpu.indexYRegister.toInt())).toUShort();
        } else {
            addressHigh = cpu.ram[zeroPageAddress.toInt()];
            indirectIndexedAddress = (((addressHigh.toInt() + 1) shl 8) + (zeroPageAddress.toInt() + cpu.indexXRegister.toInt())).toUShort();
        }

        arithmeticRotateRight(indirectIndexedAddress);
        addWithCarry(indirectIndexedAddress)
    }
    //Zero Page Indexed Addressing - only index x is allowed with Zero Page indexing, and regardless of a carry with the addressLow + indexX the high address will always be 0x0000
    fun OP_77(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        incrementProgramCounter(); //pc+2

        val zeroPageAddress: UShort
        val addressSpace: UShort = (addressLow + cpu.indexXRegister).toUShort();
        if(addressSpace <= 0xFFu) {
            zeroPageAddress = addressSpace;//pc+2
        } else {
            zeroPageAddress = (addressSpace - 0x100u).toUShort();//pc+2
        }
        incrementProgramCounter();//pc+3
        arithmeticRotateRight(zeroPageAddress);
        addWithCarry(zeroPageAddress)
    }
    //Absolute X Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_7B(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()];//pc+1
        incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()];//pc+2
        incrementProgramCounter();//pc+3

        val src: UShort;

        if((addressLow + cpu.indexXRegister) <= 0xFFu) {
            src = ((addressHigh.toInt() shl 8) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort();
        } else {
            src = (((addressHigh.toInt() shl 8)+1) + (addressLow.toInt() + cpu.indexXRegister.toInt())).toUShort();
        }
        arithmeticRotateRight(src);
        addWithCarry(src)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_7F(){
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
        arithmeticRotateRight(src);
        addWithCarry(src)
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    private fun incrementProgramCounter(){
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort();
    }

    private fun arithmeticRotateRight(address: UShort?){
        val temp: UByte
        if(address == null){
            val newCarry: UByte = cpu.accumulatorRegister and 1u
            temp = (((cpu.accumulatorRegister.toInt() + (cpu.getCarryFlag().toInt() shl 8)) shr 1) and 0xFF).toUByte()
            cpu.accumulatorRegister = temp
            cpu.setCarryFlag(newCarry)
        } else {
            val src: UByte = cpu.ram[address.toInt()];
            val newCarry: UByte = src and 1u
            temp = (((src.toInt() + (cpu.getCarryFlag().toInt() shl 8)) shr 1) and 0xFF).toUByte()
            cpu.ram[address.toInt()] = temp
            cpu.setCarryFlag(newCarry)
        }
        cpu.setNegativeFlag(0u)
        if(temp.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.setZeroFlag(0u)
        }
    }

    private fun addWithCarry(address: UShort){
        val src: UByte = cpu.ram[address.toInt()];
        val temp: UInt = src.toUInt() + cpu.accumulatorRegister.toUInt() + cpu.getCarryFlag().toUInt();
        cpu.accumulatorRegister = (temp and 0x000000FFu).toUByte();

        if((temp and 0xFFFFFF00u) != 0u){
            cpu.setCarryFlag(1u);
            cpu.setOverflowFlag(1u)
        } else {
            cpu.resetCarryFlag();
        }

        if(temp and 128u != 0u) {
            cpu.setNegativeFlag(1u);
        } else {
            cpu.setNegativeFlag(0u)
        }

        if(cpu.accumulatorRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.setZeroFlag(0u)
        }
    }
}
