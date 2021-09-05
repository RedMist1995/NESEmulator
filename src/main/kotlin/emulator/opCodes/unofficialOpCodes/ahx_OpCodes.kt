package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
class ahx_OpCodes(private val cpu: CPU, private val ppu: PPU, private val apu: APU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - CMP Group
    //Addressing Modes
    //Indirect Indexed
    fun OP_93(){
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

        compareWithAccumulator(indirectIndexedAddress);
    }
    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_9F(){
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
        compareWithAccumulator(src);
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    private fun incrementProgramCounter(){
        cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort();
    }

    fun compareWithAccumulator(address: UShort){
        val src: UByte = cpu.ram[address.toInt()];
        val sub: Int = cpu.accumulatorRegister.toInt() - src.toInt();
        if(sub == 0){
            cpu.setZeroFlag(1u);
        } else if (sub < 0){
            cpu.setCarryFlag(0u);
            cpu.setZeroFlag(0u);
            cpu.setNegativeFlag(1u);
        } else {
            cpu.setCarryFlag(1u);
            cpu.setZeroFlag(0u);
        }
    }
}
