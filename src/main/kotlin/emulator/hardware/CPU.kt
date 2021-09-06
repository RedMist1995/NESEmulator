package emulator.hardware;
@OptIn(ExperimentalUnsignedTypes::class)
class CPU {
    //CPU Components
    //Memory Map (64kB of Memory - Addresses $0000 to $FFFF)
    //$0000 to $1FFF is work memory
    //$0100 to $01FF is used for stack
    //$2000 to $401F is ioRegister
    //$4020 to $5FFF is expansionRom
    //$6000 to $7FFF is saveRam
    //$8000 to $FFFF is prog rom
    var ram = UByteArray(65535)

//    uInt8[] cpuRam = new uInt8[Short.decode("0x2000")];
//    uInt8[] ioRegister = new uInt8[Short.decode("0x2020")];
//    uInt8[] expansionRom = new uInt8[Short.decode("0x1FE0")];
//    uInt8[] saveRam = new uInt8[Short.decode("0x2000")];
//    uInt8[] prgRom = new uInt8[Short.decode("0x8000")];

    //CPU Registers
    var programCounterRegister: UShort = 0u
    var stackPointerRegister: UByte = 0u;
    var accumulatorRegister: UByte = 0u;
    var indexXRegister: UByte = 0u;
    var indexYRegister: UByte = 0u;

    //Current Op Code
    var opCode: UByte = 0u;
    var stackStart: UShort = 0x01FFu

    //Processor Status Flags
//    carryFlag = [0];
//    zeroFlag = [1];
//    interruptDisableFlag = [2];
//    decimalModeFlag = [3]; shouldn't be used in the NES
//    breakCommandFlag = [4];
//    expansionBit = [5]; Should never be used, assumed to be 1 at all times
//    overflowFlag = [6];
//    negativeFlag = [7];
    var processorStatusArray = UByteArray(8)
    init {
        processorStatusArray[5] = 1u
    }

    fun setCarryFlag(varue:UByte){
        processorStatusArray[0] = varue;
    }
    fun resetCarryFlag(){
        processorStatusArray[0] = 0u;
    }
    fun getCarryFlag(): UByte{
        return processorStatusArray[0];
    }

    fun setZeroFlag(varue: UByte){
        processorStatusArray[1] = varue;
    }
    fun resetZeroFlag(){
        processorStatusArray[1] = 0u;
    }
    fun getZeroFlag(): UByte{
        return processorStatusArray[1];
    }

    fun setInterruptDisableFlag(varue: UByte){
        processorStatusArray[2] = varue;
    }
    fun resetInterruptDisableFlag(){
        processorStatusArray[2] = 0u;
    }
    fun getIntteruptDisableFlag(): UByte{
        return processorStatusArray[2];
    }

    fun setDecimalModeFlag(varue: UByte){
        processorStatusArray[3] = varue;
    }
    fun resetDecimalModeFlag(){
        processorStatusArray[3] = 0u;
    }
    fun getDecimalModeFlag(): UByte{
        return processorStatusArray[3];
    }

    fun setBreakCommandFlag(varue: UByte){
        processorStatusArray[4] = varue;
    }
    fun resetBreakCommandFlag(){
        processorStatusArray[4] = 0u;
    }
    fun getBreakCommandFlag(): UByte{
        return processorStatusArray[4];
    }

    fun setOverflowFlag(varue: UByte){
        processorStatusArray[6] = varue;
    }
    fun resetOverflowFlag(){
        processorStatusArray[6] = 0u;
    }
    fun getOverflowFlag(): UByte{
        return processorStatusArray[6];
    }

    fun setNegativeFlag(varue: UByte){
        processorStatusArray[7] = varue;
    }
    fun resetNegativeFlag(){
        processorStatusArray[7] = 0u;
    }
    fun getNegativeFLag(): UByte{
        return processorStatusArray[7];
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    fun incrementProgramCounter(){
        programCounterRegister = (programCounterRegister + 1u).toUShort();
    }

    fun decrementStackPointer(){
        stackPointerRegister = (stackPointerRegister - 1u).toUByte();
    }

    fun incrementStackPointer(){
        stackPointerRegister = (stackPointerRegister + 1u).toUByte();
    }
}
