import emulator.Constants.*
import emulator.debug.debugWriter
import emulator.hardware.APU
import emulator.hardware.CPU
import emulator.hardware.MMU
import emulator.hardware.PPU
import emulator.opCodes.OpCodeDecoder
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
    val mmu = MMU(cpu, ppu, apu)
    val debugWriter = debugWriter(cpu, mmu, ppu)
    private val decoder = OpCodeDecoder(cpu, mmu, ppu, debugWriter)
    val graphics = GraphicsPlatform(ppu)
    var lastTime: Long = 0

    override fun start(stage: Stage) {
        val root = VBox()
        graphics.GraphicsPlatform()
        root.children.add(graphics)
        root.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
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
        bootNESCPU()
        bootNESPPU()
        loadRom(paramList[0])
        lastTime = System.nanoTime()
        GameLoop().start()
    }

    private fun bootNESCPU(){
        cpu.programCounterRegister = (0x34).toUShort()
        cpu.stackPointerRegister = (0xFD).toUByte()
        mmu.writeToMemory((0x4017).toUShort(), (0x00).toUByte())
        mmu.writeToMemory((0x4015).toUShort(), (0x00).toUByte())
        for(i in 0x4000 until 0x4014){
            mmu.writeToMemory(i.toUShort(), (0x00).toUByte())
        }
        cpu.cycles = 7
    }

    private fun bootNESPPU(){
        ppu.cycles = 21
    }

    private fun loadRom(fileName: String) {
        val file = File(fileName)
        val size = file.length()
        var iNESFormat = false

        var NES20Format = false
        try {
            val buffer = Files.readAllBytes(file.toPath())
            for (i in 0 until 0x10) {
                cpu.romHeader[i] = (buffer[i] and 0xFF.toByte()).toUByte()
            }
            if (cpu.romHeader[0].toInt().toChar() == 'N' && cpu.romHeader[1].toInt().toChar() == 'E' &&
                cpu.romHeader[2].toInt().toChar() == 'S' && cpu.romHeader[3].toInt() == 0x1A){
                iNESFormat = true
            }
            if (iNESFormat && ((cpu.romHeader[7] and 0x0Cu).toInt() == 0x08)) {
                NES20Format = true
            }

            var progRomSize: UInt
            var chrRomSize: UInt
            if(NES20Format){
                progRomSize = 0u
                chrRomSize = 0u
            } else {
                progRomSize = cpu.romHeader[4] * 16384u
                chrRomSize = cpu.romHeader[5] * 8192u
            }

            if(iNESFormat && !NES20Format) {
                if (progRomSize == 16384u) {
//                    print(buffer[(progRomSize-4u).toInt()])
//                    print(buffer[(progRomSize-3u).toInt()])
                    for (i in 0u until progRomSize) {
                        mmu.writeToMemory((0x8000u + i).toUShort(), buffer[(i + 0x10u).toInt()].toUByte())
                        mmu.writeToMemory((0xC000u + i).toUShort(), buffer[(i + 0x10u).toInt()].toUByte())
                    }
                } else if (progRomSize == 32768u) {
                    for (i in 0u until progRomSize) {
                        mmu.writeToMemory((0x8000u + i).toUShort(), buffer[(i + 0x10u).toInt()].toUByte())
                    }
                }
            }

            if(chrRomSize != 0u) {
                if(progRomSize == 16384u) {
                    for (i in 0u until chrRomSize) {
                        mmu.writeToPPUMemory(i.toUShort(), buffer[(0x4010u + i).toInt()].toUByte())
                    }
                } else {
                    for (i in 0u until chrRomSize) {
                        mmu.writeToPPUMemory(i.toUShort(), buffer[(0x8010u + i).toInt()].toUByte())
                    }
                }
            }

            var resetVector: UShort
            var resetVectorHigh: Int
            var resetVectorLow: Int
            /**
             * This is actually the correct way of finding the reset vector
             */
//            resetVectorHigh = mmu.readFromMemory(0xFFFDu).toInt()
//            resetVectorLow = mmu.readFromMemory(0xFFFCu).toInt()
//            println(resetVectorHigh shl 8)
//            resetVector = ((resetVectorHigh shl 8) + resetVectorLow).toUShort()
//            print(cpu.ram[49152])
//            cpu.programCounterRegister = resetVector

            /**
             * Test Case only
             *
             */
            resetVectorHigh = mmu.readFromMemory(0xFFFDu).toInt()
            resetVector = (resetVectorHigh shl 8).toUShort()
            cpu.programCounterRegister = resetVector

        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    inner class GameLoop : AnimationTimer() {
        override fun handle(currentTime: Long) {
            val delta: Long = currentTime - lastTime
            lastTime = currentTime
            cpuCycle(delta)
            for(j in 0 until cpu.cycles * 3){
                ppuCycle()
            }
            cpu.fullCycles += cpu.cycles
            cpu.cycles = 0
        }
    }

    fun cpuCycle(delta: Long){
        val cycles: Int = (delta / Constants.CPU_TIME_PER_CYCLE).toInt()
        for(i in 0 until cycles){
            cpu.opCode = cpu.ram[cpu.programCounterRegister.toInt()]
            cpu.programCounterRegister = (cpu.programCounterRegister + 1u).toUShort()
            decoder.decodeOpCode()
        }

    }

    fun ppuCycle(){
        ppu.cycles++
        if(ppu.cycles > 340){
            ppu.cycles -= 341
            ppu.scanline++
        }

        if (ppu.scanline in 0..239) { //  drawing

        }else if (ppu.scanline == 241 && ppu.cycles == 1) {    //  VBlank
            setVBlank()
            ppu.nmiFlag = true;
            graphics.draw()
            //handleWindowEvents();
        }
        else if (ppu.scanline == 261 && ppu.cycles == 1) {    //  VBlank off / pre-render line
            clearVBlank();
            ppu.nmiFlag = false;
            ppu.scanline = 0;
        }
    }

    private fun clearVBlank() {
        //reset VBlank Flag
        ppu.ppuStatus = ppu.ppuStatus and (1 shl 7).inv().toUByte()
        //Restore Prcess Status Array
        val stackProcessorStatus: UByte = mmu.readFromMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort())
        for(i in 7 downTo 0){
            cpu.processorStatusArray[i] = ((stackProcessorStatus.toInt() shr i) and 0x01).toUByte()
        }
        cpu.decrementStackPointer()
        //restore PC
        val addressLow = mmu.readFromMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort())
        cpu.decrementStackPointer()
        val addressHigh = mmu.readFromMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort())
        cpu.decrementStackPointer()
        cpu.programCounterRegister = ((addressHigh.toInt() shl 8) + addressLow.toInt()).toUShort()
    }

    private fun setVBlank() {
        //set the VBlank Flag
        ppu.ppuStatus = ppu.ppuStatus or (1 shl 7).toUByte()
        //Stores current PC on stack
        val address: UShort = 0xFFFAu
        val currentPC: UShort = cpu.programCounterRegister
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), ((currentPC.toInt() shr 8) and 0xFF).toUByte())
        cpu.incrementStackPointer()
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), ((currentPC.toInt()) and 0xFF).toUByte())
        cpu.incrementStackPointer()
        //Stores Current Process Status Array on Stack
        var processorStatus: UByte = 0u
        for(i in cpu.processorStatusArray.indices){
            processorStatus = (processorStatus + (cpu.processorStatusArray[i].toInt() shl i).toUByte()).toUByte()
        }
        mmu.writeToMemory((cpu.stackStart - cpu.stackPointerRegister).toUShort(), processorStatus)
        cpu.incrementStackPointer()

        //Set disable interrupt
        cpu.setInterruptDisableFlag(1u)
        //set program counter to address of NMI interrupt handler
        cpu.programCounterRegister = address //creates the final indexed indirect address
        cpu.incrementClockCycle(6)
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
}