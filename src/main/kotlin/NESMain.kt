import emulator.hardware.CPU
import emulator.opCodes.OpCodeDecoder

fun main(args: Array<String>) {
    NESMain().nesMain(args)
}

@OptIn(ExperimentalUnsignedTypes::class)
class NESMain {
    val cpu = CPU()
    val decoder = OpCodeDecoder(cpu)

    fun nesMain(args: Array<String>){

        //TODO load rom

        while(true){
            cycle()
            drawFrame()
        }
    }

    fun cycle(){
        while(cpu.cycles <= 27756){
            cpu.opCode = cpu.ram[cpu.programCounterRegister.toInt()]
            cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
            decoder.decodeOpCode()
        }
        cpu.cycles = 0
    }

    fun drawFrame(){

    }

}