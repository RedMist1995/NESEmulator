package emulator.opCodes.rmwOpCodes;

import emulator.hardware.CPU

@OptIn(ExperimentalUnsignedTypes::class)
public open class ldy_OpCodes (private val cpu: CPU) {
    private var addressLow: UByte = 0u;
    private var addressHigh: UByte = 0u;

    //OP Codes - LDY Group
    //Addressing Modes
    //Immediate Addressing - Doesn't pull data from memory, uses OP Parameter as data
    fun OP_A0(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        loadIntoX(addressLow.toUShort());
    }
    //Zero Page Addressing - assumes Address High to be 0x00
    fun OP_A4(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        val zeroPageAddress: UShort = addressLow.toUShort();
        loadIntoX(zeroPageAddress);
    }
    //Transfer Accumulator Into X
    fun OP_A8(){
        loadIntoX(null)
    }
    //Absolute Addressing - Pulls addressLow and addressHigh from OP Params 1 and 2, combines to make 16bit mem address to pull data from
    fun OP_AC(){
        addressLow = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+1
        cpu.incrementProgramCounter(); //pc+2
        addressHigh = cpu.ram[cpu.programCounterRegister.toInt()]; //pc+2
        cpu.incrementProgramCounter(); //pc+3

        val src: UShort = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort();
        loadIntoX(src);
    }
    //Indirect Indexed
    fun OP_B4(){
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

        loadIntoX(indirectIndexedAddress);
    }

    //Absolute Y Indexed Addressing - If addressLow + indexX causes a carry (over 255) the carry is added to address High after the shift
    fun OP_BC(){
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
        loadIntoX(src);
    }

    fun loadIntoX(memory: UShort?){
        if(memory == null){
            if(cpu.opCode.toUInt() == 0xAAu){
                cpu.indexYRegister = cpu.accumulatorRegister
            } else {
                cpu.indexYRegister = cpu.stackPointerRegister
            }
        } else {
            cpu.indexYRegister = cpu.ram[memory.toInt()]
        }
        if(cpu.indexYRegister.toUInt() == 0u){
            cpu.setZeroFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
        if((cpu.indexYRegister.toUInt() and 128u) != 0u){
            cpu.setNegativeFlag(1u)
        } else {
            cpu.resetZeroFlag()
        }
    }
}
