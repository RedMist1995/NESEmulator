package emulator

class Constants {
    object Constants {
        // Emulator
        const val FPS_TIMER_INTERVAL = 1000
        const val DEFAULT_SCREEN_SCALE = 2

        // CPU
        const val CPU_CLOCK = 1789773 // 1.789773 MHz
        val CPU_TIME_PER_CYCLE = Math.round(1 / CPU_CLOCK.toDouble() * 1000000000).toInt()
        const val CPU_PPU_DIVISOR = 3
        const val NMI_VECTOR = 0xFFFA.toShort()
        const val RESET_VECTOR = 0xFFFC.toShort()
        const val IRQ_BRK_VECTOR = 0xFFFE.toShort()
        const val SP_START = 0xFD.toByte()
        const val SR_START = 0x34.toByte()

        // PPU
        const val PRIMARY_OAM_SIZE = 256
        const val SECONDARY_OAM_SIZE = 32
        const val VRAM_SIZE = 2048
        const val PALETTE_SIZE = 32

        // OAM DMA
        const val OAM_DMA_ADDR = 0x4014.toShort()
        const val PPU_OAM_RW = 0x2004.toShort()

        // Controller
        const val CONTROLLER_COUNT = 2
        const val BUTTON_COUNT = 8

        // Internal Ram
        const val RAM_SIZE = 2048

        // Display
        const val NTSC_DISPLAY_WIDTH = 256
        const val NTSC_DISPLAY_HEIGHT = 240
        const val DISPLAY_CLEAR_COLOR = 0x0F.toByte() // black

        // Rom
        val ROM_HEADER = byteArrayOf(0x4E.toByte(), 0x45.toByte(), 0x53.toByte(), 0x1A.toByte())
        const val TRAINER_SIZE = 256
    }
}