package emulator.opCodes.unofficialOpCodes;

import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU

@OptIn(ExperimentalUnsignedTypes::class)
class axs_OpCodes(private val cpu: CPU, private val ppu: PPU, private val apu: APU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - CMP Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_CB(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        incrementProgramCounter(); //pc+2
        compareWithAccumulator(addressLow.toUShort());
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
