import emulator.Constants.*
import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.PPU
import emulator.opCodes.OpCodeDecoder
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import tornadofx.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.experimental.and
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    launch<NESMain>(args)
}

@OptIn(ExperimentalUnsignedTypes::class)
class NESMain: App(){
    val cpu = CPU()
    val ppu = PPU()
    val apu = APU()
    private val decoder = OpCodeDecoder(cpu)
    val graphics = GraphicsPlatform()
    var lastTime: Long = 0

    override fun start(stage: Stage) {
        val root = VBox()
        graphics.GraphicsPlatform()
        root.children.add(graphics)
        val scene = Scene(root)
        scene.onKeyPressed = KeyPressedHandler()
        scene.onKeyReleased = KeyReleasedHandler()
        stage.title = "KotliNES"
        stage.scene = scene
        stage.isResizable = false
        stage.sizeToScene()
        stage.centerOnScreen()
        stage.show()
        val params: Parameters = parameters
        val paramList: List<String> = params.raw
        if (paramList.isEmpty()) {
            Platform.exit()
            exitProcess(1)
        }
        System.out.println(paramList[0])
        //loadRom(paramList[0])
        lastTime = System.nanoTime()
        //GameLoop().start()
    }

    class KeyPressedHandler : EventHandler<KeyEvent?> {
        override fun handle(event: KeyEvent?) {
//            val button: Int = ButtonAssignment.map.get(event.getCode())
//            if (button != null) {
//                nes.setKeyPressed(0, button)
//            }
        }
    }

    class KeyReleasedHandler : EventHandler<KeyEvent?> {
        override fun handle(event: KeyEvent?) {
//            val button: Int = ButtonAssignment.map.get(event.getCode())
//            if (button != null) {
//                nes.setKeyReleased(0, button)
//            }
        }
    }

    inner class GameLoop : AnimationTimer() {
        override fun handle(currentTime: Long) {
            val delta: Long = currentTime - lastTime
            lastTime = currentTime
            cycle(delta)
            //TODO add draw method for when PPU is implemented
//            if(render){
//                GraphicsPlatform.draw()
//            }
        }
    }

    private fun loadRom(fileName: String) {
        val file = File(fileName)
        val size = file.length()
        var iNESFormat = false

        var NES20Format = false
        try {
            val buffer = Files.readAllBytes(file.toPath())
            for (i in 0 until 0x10) {
                cpu.romHeader[i] = (buffer[i] and 0xFF.toByte()) as UByte
            }
            if (cpu.romHeader[0].toInt().toChar() == 'N' && cpu.romHeader[1].toInt().toChar() == 'E' &&
                cpu.romHeader[2].toInt().toChar() == 'S' && cpu.romHeader[3].toInt() == 0x1A){
                iNESFormat = true
            }
            if (iNESFormat && (cpu.romHeader[7] and 0x0Cu).toInt() == 0x08) {
                NES20Format = true
            }

            var progRomSize: Int
            if(NES20Format){
                progRomSize = 0
            } else {
                progRomSize = cpu.romHeader[4].toInt() * 16384
            }

            for(i in 0 until 0x4000){
                cpu.ram[0xC000+i] = buffer[i+0x10].toUByte()
            }

            for(i in 0 until 0x2000){
                ppu.ram[i] = buffer[0x4010 + i].toUByte()
            }

        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun cycle(delta: Long){
        val cycles: Int = (delta / Constants.CPU_TIME_PER_CYCLE).toInt()
        for(i in 0 until cycles){
            cpu.opCode = cpu.ram[cpu.programCounterRegister.toInt()]
            cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
            decoder.decodeOpCode()
        }
        cpu.cycles = 0
    }

}