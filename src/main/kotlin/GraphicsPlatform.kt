import emulator.Constants.*
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.WritablePixelFormat
import tornadofx.*

open class GraphicsPlatform: Canvas() {
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
}