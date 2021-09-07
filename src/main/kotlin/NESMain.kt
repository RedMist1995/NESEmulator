import emulator.hardware.CPU
import emulator.opCodes.OpCodeDecoder

fun main(args: Array<String>) {
    val nesMain = NESMain()
    while(true){
        nesMain.cycle()
        nesMain.drawFrame()
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class NESMain {
    val cpu = CPU()
    val decoder = OpCodeDecoder(cpu)

    fun cycle(){
        while(cpu.cycles <= 27756){
            cpu.opCode = cpu.ram[cpu.programCounterRegister.toInt()]
            cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
            decoder.decodeOpCode()
        }
    }

    fun drawFrame(){

    }

}