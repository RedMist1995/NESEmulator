package emulator.opCodes.rmwOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
public class stx_OpCodes (private val cpu: CPU, private val ppu: PPU, private val apu: APU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - STX Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_82(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_86(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        storeXInto(zeroPageAddress);
    }
    //Transfer X Into Accumulator
    fun OP_8A(){
        storeXInto(null)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_8E(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        storeXInto(src);
    }
    //Indirect Indexed
    fun OP_96(){
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

        storeXInto(indirectIndexedAddress);
    }
    // Transfers X Into Stack Pointer
    fun OP_9A(){
        storeXInto(null)
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_9E(){
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
        storeXInto(src);
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    private fun incrementProgramCounter(){
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort();
    }

    fun storeXInto(memory: UShort?){
        if(memory == null){
            if(cpu.opCode.toUInt() == 0x8Au){
                cpu.accumulatorRegister = cpu.indexXRegister
            } else {
                cpu.stackPointerRegister = cpu.indexXRegister
            }
        } else {
            cpu.ram[memory.toInt()] = cpu.indexXRegister
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
