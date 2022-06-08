import emulator.Constants.*
import emulator.hardware.PPU
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.WritablePixelFormat
import javafx.scene.paint.Color
import tornadofx.*

@OptIn(ExperimentalUnsignedTypes::class)
open class GraphicsPlatform(val ppu: PPU): Canvas() {
    private var scale = 0
    private var gc: GraphicsContext? = null
    private val format = WritablePixelFormat.getIntArgbInstance()

    open fun GraphicsPlatform() {
        setScale(Constants.DEFAULT_SCREEN_SCALE)
        gc = graphicsContext2D
    }

    open fun setScale(scale: Int) {
        this.scale = scale
        width = (Constants.NTSC_DISPLAY_WIDTH * scale).toDouble()
        height = (Constants.NTSC_DISPLAY_HEIGHT * scale).toDouble()
    }

    //TODO Used for actual buffering of the PPU
//    open fun draw(displayBuffer: DisplayBuffer) {
//        val writer = gc!!.pixelWriter
//        val pixels = IntArray(width.toInt() * height.toInt())
//        for (row in 0 until displayBuffer.getHeight()) {
//            for (col in 0 until displayBuffer.getWidth()) {
//                val argbColor: Int = displayBuffer.getPixel(row, col)
//                for (i in 0 until scale) {
//                    for (j in 0 until scale) {
//                        val index = (row * scale + j) * width.toInt() + col * scale + i
//                        pixels[index] = argbColor
//                    }
//                }
//            }
//        }
//        writer.setPixels(0, 0, width.toInt(), height.toInt(), format, pixels, 0, width.toInt())
//    }

//    open fun draw(){
//        val writer = gc!!.pixelWriter
//        val COLORS: Array<Color> = arrayOf(Color.BLACK, Color.DARKGRAY, Color.GRAY, Color.WHITE)
//        for (r in 0u until 256u) {
//            for (col in 0u until 128u) {
//                for (i in 0 until scale) {
//                    for (j in 0 until scale) {
//                        val adr: UShort = ((r / 8u * 0x100u) + (r % 8u) + (col / 8u) * 0x10u).toUShort()
//                        val pixel: UByte =
//                            (((ppu.ram[adr.toInt()].toInt() shr (7 - (col.toInt() % 8))) and 1) + ((ppu.ram[adr.toInt() + 8].toInt() shr (7 - (col.toInt() % 8))) and 1) * 2).toUByte()
//                        writer.setColor(col.toInt() * scale + i, r.toInt()*scale + j, COLORS[pixel.toInt()])
//                    }
//                }
//            }
//        }
//    }

    open fun draw(){
        val writer = gc!!.pixelWriter
        val COLORS: Array<Color> = arrayOf(Color.BLACK, Color.DARKGRAY, Color.GRAY, Color.WHITE)
        for (r in 0u until 1024u) {
            for (col in 0u until 256u) {
                for (i in 0 until scale) {
                    for (j in 0 until scale) {
                        val tile_nr: UShort = ppu.ram[0x2000 + ((r / 8u * 32u) + (col / 8u)).toInt()].toUShort()
                        val tile_attr: UShort = ppu.ram[0].toUShort()
                        val ppuCtrlAddr: UByte = getPpuCtrlNametable()
                        val adr: UShort =  (ppuCtrlAddr + (tile_nr * 0x10u) + (r % 8u)).toUShort()
                        val pixel: UByte =
                            (((ppu.ram[adr.toInt()].toInt() shr (7 - (col.toInt() % 8))) and 1) +
                                    (((ppu.ram[adr.toInt() + 8].toInt() shr (7 - (col.toInt() % 8))) and 1) * 2)).toUByte()
                        writer.setColor(col.toInt() * scale + i, r.toInt()*scale + j, COLORS[pixel.toInt()])
                    }
                }
            }
        }
    }

    private fun getPpuCtrlNametable(): UByte {
        if(ppu.ppuCtrl.toInt() and 16 != 0){
            return 1u
        } else {
            return 0u
        }
    }
}