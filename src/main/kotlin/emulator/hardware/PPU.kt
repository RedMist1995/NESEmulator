package emulator.hardware

@OptIn(ExperimentalUnsignedTypes::class)
class PPU {
    //PPU Components
    //Memory Map
    //$0000 to $0FFF patternTable0
    //$1000 to $1FFF patternTable1
    //$2000 to $23BF nameTable0
    //$23C0 to $23FF attributeTable0
    //$2400 to $273BF nameTable1
    //$27C0 to $27FF attributeTable1
    //$2800 to $2BBF nameTable2
    //$2BCO to $2BFF attributeTable2
    //$2C00 to $2FBF nameTable3
    //$2FC0 to $2FFF attributeTable3
    //$3000 to $3EFF memoryTable1
    //$3F00 to $3F0F bgPallete
    //$3F10 to $3F1F spritePallete
    //$3F20 to $3FFF mirrorTable3
    var ram = UByteArray(16384)

    //Special Internal PPU Address Map for Sprite Rendering
    var objectAttributeMemory = UByteArray(256)

    var cycles: Int = 0
    var scanline: Int = 0
    var nmiFlag: Boolean = false

    //PPU Registers
    //PPU CTRL - PPU control register Access: write
    var ppuCtrl: UByte = 0u
    //PPU MASK - Description: PPU mask register Access: write
    var ppuMask: UByte = 0u
    //PPU STATUS - Description: PPU status register Access: read
    var ppuStatus: UByte = 0u
    //OAM ADDR - Description: OAM address port Access: write
    var oamAddr: UByte = 0u
    //OAM DATA - Description: OAM data port Access: read, write
    var oamData: UByte = 0u
    //PPU SCROLL - Description: PPU scrolling position register Access: write twice
    var ppuScroll: UByte = 0u
    //PPU ADDR - Description: PPU address register Access: write twice
    var ppuAddr: UShort = 0u
    //PPU Addr must be written too twice before the address is stored
    var ppuAddrWrite: Int = 0
    //PPU DATA - Description: PPU data port Access: read, write
    var ppuData: UByte = 0u
    //OAMDMA Description: OAM DMA register (high byte) Access: write
    var oamDma: UByte = 0u
}
