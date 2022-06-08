package emulator.hardware

@OptIn(ExperimentalUnsignedTypes::class)
open class MMU(private val cpu: CPU, private val ppu: PPU, private val apu: APU) {

    open fun writeToMemory(address: UShort, value: UByte){
        when(address){
            0x2000u.toUShort() -> writePPUCtrl(value)
            0x2001u.toUShort() -> writePPUMask(value)
            0x2002u.toUShort() -> writePPUStatus(value)
            0x2003u.toUShort() -> writeOAMAddr(value)
            0x2004u.toUShort() -> writeOAMData(value)
            0x2005u.toUShort() -> writePPUScroll(value)
            0x2006u.toUShort() -> writePPUAddr(value)
            0x2007u.toUShort() -> writePPUData(value)
            0x4014u.toUShort() -> writeOAMDma(value)
            else -> {
                cpu.ram[address.toInt()] = value
                //println(cpu.ram[address.toInt()])
            }
        }
    }

    open fun readFromMemory(address: UShort): UByte {
        when(address){
            0x2000u.toUShort() -> return ppu.ppuCtrl
            0x2001u.toUShort() -> return ppu.ppuMask
            0x2002u.toUShort() -> return ppu.ppuStatus
            0x2003u.toUShort() -> return ppu.oamAddr
            0x2004u.toUShort() -> return ppu.oamData
            0x2005u.toUShort() -> return ppu.ppuScroll
            //0x2006u.toUShort() -> return ppu.ppuAddr
            0x2007u.toUShort() -> return ppu.ppuData
            0x4014u.toUShort() -> return ppu.oamDma
            else -> {
                return cpu.ram[address.toInt()]
            }
        }
    }

    open fun writeToPPUMemory(address: UShort, value: UByte){
        ppu.ram[address.toInt()] = value
    }

    open fun readFromPPUMemory(address: UShort): UByte {
        return ppu.ram[address.toInt()]
    }

    private fun writePPUCtrl(value: UByte) {
        ppu.ppuCtrl = value
    }

    private fun writePPUMask(value: UByte) {
        ppu.ppuMask = value
    }

    private fun writePPUStatus(value: UByte) {
        ppu.ppuStatus = value
    }

    private fun writeOAMAddr(value: UByte) {
        ppu.oamAddr = value
    }

    private fun writeOAMData(value: UByte) {
        ppu.oamAddr = value
        ppu.objectAttributeMemory[ppu.oamAddr.toInt()] = ppu.oamData
    }

    private fun writePPUScroll(value: UByte) {
        ppu.ppuScroll = value
    }

    private fun writePPUAddr(value: UByte) {
        if(ppu.ppuAddrWrite == 2){
            ppu.ppuAddr = ((ppu.ppuAddr.toInt() shl 8) + value.toInt()).toUShort()
        } else {
            ppu.ppuAddr = value.toUShort()
            ppu.ppuAddrWrite++
        }
    }

    private fun writePPUData(value: UByte) {
        ppu.ppuData = value
        ppu.ram[ppu.ppuAddr.toInt()] = ppu.ppuData
        ppu.ppuAddr = (ppu.ppuAddr + 1u).toUShort()
    }

    private fun writeOAMDma(value: UByte) {
        ppu.oamDma = value
    }

}