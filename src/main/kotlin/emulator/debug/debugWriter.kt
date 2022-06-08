package emulator.debug

import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU
import java.io.File
import java.io.PrintWriter

open class debugWriter(private val cpu: CPU, private val mmu: MMU, private val ppu: PPU){

    private var debugString: String = ""
    private var writer: PrintWriter = File("src/test/testroms/neslog.txt").printWriter()

    public fun writeProgramCounter(){
        debugString = Integer.toHexString(cpu.programCounterRegister.toInt()-1)
        debugString += "  "
    }

    public fun writeOPCode(opCode: String){
        debugString += opCode + " "
    }

    public fun writeLowAddress(addressLow: Int){
        debugString += Integer.toHexString(addressLow) + " "
    }

    public fun writeHighAddress(addressHigh: Int?, addressLow: Int, opCode:String){
        if(addressHigh != null) {
            debugString += Integer.toHexString(addressHigh) + "  " + opCode + " " + Integer.toHexString(addressHigh) + Integer.toHexString(addressLow)
        } else {
            debugString += opCode + " " + Integer.toHexString(addressLow)
        }
    }

    public fun writeRemainder(){
        debugString = debugString.padEnd(24, ' ')
        debugString += "A:" + cpu.accumulatorRegister.toInt().toString().padStart(2, '0')
        debugString += " X:" + cpu.indexXRegister.toInt().toString().padStart(2, '0')
        debugString += " Y:" + cpu.indexYRegister.toInt().toString().padStart(2, '0')
        debugString += " P:" + cpu.processorStatusValue.toInt().toString().padStart(2, '0')
        debugString += " SP:" + "%02x".format((cpu.stackPointerRegister - 0x100u).toByte())
        debugString += " PPU:" + ppu.scanline.toString().padStart(3, ' ') + ", " + ppu.cycles.toString().padStart(3, ' ')
        debugString += " CYC:" + cpu.cycles

        writer.use { write -> write.println(debugString) }
        writer.flush()
        debugString = ""
    }
}