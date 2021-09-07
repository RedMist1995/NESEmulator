package emulator.hardware

@OptIn(ExperimentalUnsignedTypes::class)
class PPU {
    //PPU Components
    //Memory Map
    //$0000 to $0FFF
    private var patternTable0 = UByteArray(4096)
    //$1000 to $1FFF
    private var patternTable1 = UByteArray(4096)
    //$2000 to $23BF
    private var nameTable0 = UByteArray(960)
    //$23C0 to $23FF
    private var attributeTable0 = UByteArray(64)
    //$2400 to $273BF
    private var nameTable1 = UByteArray(960)
    //$27C0 to $27FF
    private var attributeTable1 = UByteArray(64)
    //$2800 to $2BBF
    private var nameTable2 = UByteArray(960)
    //$2BCO to $2BFF
    private var attributeTable2 = UByteArray(64)
    //$2C00 to $2FBF
    private var nameTable3 = UByteArray(960)
    //$2FC0 to $2FFF
    private var attributeTable3 = UByteArray(64)
    //$3000 to $3EFF
    private var memoryTable1 = UByteArray(3840)
    //$3F00 to $3F0F
    private var bgPallete = UByteArray(16)
    //$3F10 to $3F1F
    private var spritePallete = UByteArray(16)
    //$3F20 to $3FFF
    private var mirrorTable3 = UByteArray(224)

    //Special Internal PPU Address Map for Sprite Rendering
    private var objectAttributeMemory = UByteArray(256)

    //PPU Registers
    //PPU CTRL - PPU control register Access: write
    private var ppuCtrl: UByte = 0u
    //PPU MASK - Description: PPU mask register Access: write
    private var ppuMask: UByte = 0u
    //PPU STATUS - Description: PPU status register Access: read
    private var ppuStatus: UByte = 0u
    //OAM ADDR - Description: OAM address port Access: write
    private var oamAddr: UByte = 0u
    //OAM DATA - Description: OAM data port Access: read, write
    private var oamData: UByte = 0u
    //PPU SCROLL - Description: PPU scrolling position register Access: write twice
    private var ppuScroll: UByte = 0u
    //PPU ADDR - Description: PPU address register Access: write twice
    private var ppuAddr: UByte = 0u
    //PPU DATA - Description: PPU data port Access: read, write
    private var ppuData: UByte = 0u
    //OAMDMA Description: OAM DMA register (high byte) Access: write
    private var oamDma: UByte = 0u
}
