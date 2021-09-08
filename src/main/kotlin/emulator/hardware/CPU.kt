package emulator.hardware
@OptIn(ExperimentalUnsignedTypes::class)
open class CPU() {
    //CPU Components
    //Memory Map (64kB of Memory - Addresses $0000 to $FFFF)
    //$0000 to $1FFF is work memory
    //$0100 to $01FF is used for stack
    //$2000 to $401F is ioRegister
    //$4020 to $5FFF is expansionRom
    //$6000 to $7FFF is saveRam
    //$8000 to $FFFF is prog rom
    var ram = UByteArray(65536)

    var romHeader = UByteArray(16)

    //CPU Registers
    var programCounterRegister: UShort = 0u
    var stackPointerRegister: UByte = 0u
    var accumulatorRegister: UByte = 0u
    var indexXRegister: UByte = 0u
    var indexYRegister: UByte = 0u

    //Current Op Code
    var opCode: UByte = 0u

    //Stack starts at highest section of memory allocated for stack
    var stackStart: UShort = 0x01FFu

    //CPU Cycle Counter
    var cycles: Int = 0

    //Processor Status Flags
//    carryFlag = [0]
//    zeroFlag = [1]
//    interruptDisableFlag = [2]
//    decimalModeFlag = [3] shouldn't be used in the NES
//    breakCommandFlag = [4]
//    expansionBit = [5] Should never be used, assumed to be 1 at all times
//    overflowFlag = [6]
//    negativeFlag = [7]
    var processorStatusArray = UByteArray(8)
    init {
        processorStatusArray[5] = 1u
    }

    fun setCarryFlag(value:UByte){
        processorStatusArray[0] = value
    }
    fun resetCarryFlag(){
        processorStatusArray[0] = 0u
    }
    fun getCarryFlag(): UByte{
        return processorStatusArray[0]
    }

    fun setZeroFlag(value: UByte){
        processorStatusArray[1] = value
    }
    fun resetZeroFlag(){
        processorStatusArray[1] = 0u
    }
    fun getZeroFlag(): UByte{
        return processorStatusArray[1]
    }

    fun setInterruptDisableFlag(value: UByte){
        processorStatusArray[2] = value
    }
    fun resetInterruptDisableFlag(){
        processorStatusArray[2] = 0u
    }
    fun getIntteruptDisableFlag(): UByte{
        return processorStatusArray[2]
    }

    fun setDecimalModeFlag(value: UByte){
        processorStatusArray[3] = value
    }
    fun resetDecimalModeFlag(){
        processorStatusArray[3] = 0u
    }
    fun getDecimalModeFlag(): UByte{
        return processorStatusArray[3]
    }

    fun setBreakCommandFlag(value: UByte){
        processorStatusArray[4] = value
    }
    fun resetBreakCommandFlag(){
        processorStatusArray[4] = 0u
    }
    fun getBreakCommandFlag(): UByte{
        return processorStatusArray[4]
    }

    fun setOverflowFlag(value: UByte){
        processorStatusArray[6] = value
    }
    fun resetOverflowFlag(){
        processorStatusArray[6] = 0u
    }
    fun getOverflowFlag(): UByte{
        return processorStatusArray[6]
    }

    fun setNegativeFlag(value: UByte){
        processorStatusArray[7] = value
    }
    fun resetNegativeFlag(){
        processorStatusArray[7] = 0u
    }
    fun getNegativeFLag(): UByte{
        return processorStatusArray[7]
    }

    //increments the program counter by 1 after a memory fetch operation using the program counter is performed
    fun incrementProgramCounter(){
        programCounterRegister = (programCounterRegister + 1u).toUShort()
    }

    fun decrementStackPointer(){
        stackPointerRegister = (stackPointerRegister - 1u).toUByte()
    }

    fun incrementStackPointer(){
        stackPointerRegister = (stackPointerRegister + 1u).toUByte()
    }
    
    fun incrementClockCycle(value: Int){
        cycles += value
    }
}
