package emulator.opCodes

import emulator.hardware.CPU
import emulator.opCodes.aluOpCodes.*
import emulator.opCodes.controlOpCodes.*
import emulator.opCodes.rmwOpCodes.*
import emulator.opCodes.unofficialOpCodes.*

class OpCodeDecoder(private val cpu: CPU) {

    //Arithmetic Logic Unit Block Codes
    private val adc = adc_OpCodes(cpu)
    private val and = and_OpCodes(cpu)
    private val cmp = cmp_OpCodes(cpu)
    private val eor = eor_OpCodes(cpu)
    private val lda = lda_OpCodes(cpu)
    private val ora = ora_OpCodes(cpu)
    private val sbc = sbc_OpCodes(cpu)
    private val sta = sta_OpCodes(cpu)

    //Control Block Codes
    private val bcc = bcc_OpCodes(cpu)
    private val bcs = bcs_OpCodes(cpu)
    private val beq = beq_OpCodes(cpu)
    private val bit = bit_OpCodes(cpu)
    private val bmi = bmi_OpCodes(cpu)
    private val bne = bne_OpCodes(cpu)
    private val bpl = bpl_OpCodes(cpu)
    private val brk = brk_OpCodes(cpu)
    private val bvc = bvc_OpCodes(cpu)
    private val bvs = bvs_OpCodes(cpu)
    private val clc = clc_OpCodes(cpu)
    private val cld = cld_OpCodes(cpu)
    private val cli = cli_OpCodes(cpu)
    private val clv = clv_OpCodes(cpu)
    private val jmp = jmp_OpCodes(cpu)
    private val jsr = jsr_OpCodes(cpu)
    private val nop = nop_OpCodes(cpu)
    private val pha = pha_OpCodes(cpu)
    private val php = php_OpCodes(cpu)
    private val pla = pla_OpCodes(cpu)
    private val plp = plp_OpCodes(cpu)
    private val rti = rti_OpCodes(cpu)
    private val rts = rts_OpCodes(cpu)
    private val sec = sec_OpCodes(cpu)
    private val sed = sed_OpCodes(cpu)
    private val sei = sei_OpCodes(cpu)
    private val tay = tay_OpCodes(cpu)
    private val tya = tya_OpCodes(cpu)

    //Read-Modify-Write Block Codes
    private val asl = asl_OpCodes(cpu)
    private val cpx = cpx_OpCodes(cpu)
    private val cpy = cpy_OpCodes(cpu)
    private val dec = dec_OpCodes(cpu)
    private val inc = inc_OpCodes(cpu)
    private val ldx = ldx_OpCodes(cpu)
    private val ldy = ldy_OpCodes(cpu)
    private val lsr = lsr_OpCodes(cpu)
    private val rol = rol_OpCodes(cpu)
    private val ror = ror_OpCodes(cpu)
    private val stx = stx_OpCodes(cpu)
    private val sty = sty_OpCodes(cpu)

    //Unofficial Block Codes
    private val ahx = ahx_OpCodes(cpu)
    private val alr = alr_OpCodes(cpu)
    private val anc = anc_OpCodes(cpu)
    private val arr = arr_OpCodes(cpu)
    private val axs = axs_OpCodes(cpu)
    private val dcp = dcp_OpCodes(cpu)
    private val isc = isc_OpCodes(cpu)
    private val las = las_OpCodes(cpu)
    private val lax = lax_OpCodes(cpu)
    private val rla = rla_OpCodes(cpu)
    private val rra = rra_OpCodes(cpu)
    private val sax = sax_OpCodes(cpu)
    private val shx = shx_OpCodes(cpu)
    private val shy = shy_OpCodes(cpu)
    private val slo = slo_OpCodes(cpu)
    private val sre = sre_OpCodes(cpu)
    private val tas = tas_OpCodes(cpu)
    private val xaa = xaa_OpCodes(cpu)

    fun decodeOpCode(){
        when (cpu.opCode){
            //Control OP Block
            //BCC Ops
            0x90u.toUByte() -> bcc.OP_90()

            //BCS Ops
            0xB0u.toUByte() -> bcs.OP_B0()

            //BEQ Ops
            0xF0u.toUByte() -> beq.OP_F0()

            //BIT Ops
            0x24u.toUByte() -> bit.OP_24()
            0x2Cu.toUByte() -> bit.OP_2C()

            //BMI Ops
            0x30u.toUByte() -> bmi.OP_30()

            //BNE Ops
            0xD0u.toUByte() -> bne.OP_D0()

            //BPL Ops
            0x10u.toUByte() -> bpl.OP_10()

            //BRK Ops
            0x00u.toUByte() -> brk.OP_00()

            //BVC Ops
            0x50u.toUByte() -> bvc.OP_50()

            //BVS Ops
            0x70u.toUByte() -> bvs.OP_70()

            //CLC Ops
            0x18u.toUByte() -> clc.OP_18()

            //CLD Ops
            0xD8u.toUByte() -> cld.OP_D8()

            //CLI Ops
            0x58u.toUByte() -> cli.OP_58()

            //CLV Ops
            0xB8u.toUByte() -> clv.OP_B8()

            //JMP Ops
            0x4Cu.toUByte() -> jmp.OP_4C()
            0x6Cu.toUByte() -> jmp.OP_6C()

            //JSR Ops
            0x20u.toUByte() -> jsr.OP_20()

            //NOP Ops
            0x01u.toUByte() -> nop.OP_NOP()
            0x05u.toUByte() -> nop.OP_NOP()
            0x09u.toUByte() -> nop.OP_NOP()
            0x0Du.toUByte() -> nop.OP_NOP()
            0x11u.toUByte() -> nop.OP_NOP()
            0x15u.toUByte() -> nop.OP_NOP()
            0x19u.toUByte() -> nop.OP_NOP()
            0x1Du.toUByte() -> nop.OP_NOP()
            0x01u.toUByte() -> nop.OP_NOP()
            0x05u.toUByte() -> nop.OP_NOP()
            0x09u.toUByte() -> nop.OP_NOP()
            0x0Du.toUByte() -> nop.OP_NOP()
            0x11u.toUByte() -> nop.OP_NOP()
            0x15u.toUByte() -> nop.OP_NOP()
            0x19u.toUByte() -> nop.OP_NOP()
            0x1Du.toUByte() -> nop.OP_NOP()
            0x01u.toUByte() -> nop.OP_NOP()
            0x05u.toUByte() -> nop.OP_NOP()
            0x09u.toUByte() -> nop.OP_NOP()
            0x0Du.toUByte() -> nop.OP_NOP()
            0x11u.toUByte() -> nop.OP_NOP()
            0x15u.toUByte() -> nop.OP_NOP()
            0x19u.toUByte() -> nop.OP_NOP()
            0x1Du.toUByte() -> nop.OP_NOP()
            0x01u.toUByte() -> nop.OP_NOP()
            0x05u.toUByte() -> nop.OP_NOP()
            0x09u.toUByte() -> nop.OP_NOP()

            //PHA Ops
            0x01u.toUByte() -> pha.OP_48()

            //PHP Ops
            0x01u.toUByte() -> php.OP_08()

            //PLA Ops
            0x01u.toUByte() -> pla.OP_68()

            //PLP Ops
            0x01u.toUByte() -> plp.OP_28()

            //RTI Ops
            0x01u.toUByte() -> rti.OP_40()

            //RTS Ops
            0x01u.toUByte() -> rts.OP_60()

            //SEC Ops
            0x01u.toUByte() -> sec.OP_38()

            //SED Ops
            0x01u.toUByte() -> sed.OP_F8()

            //SEI Ops
            0x01u.toUByte() -> sei.OP_61()

            //TAY Ops
            0x01u.toUByte() -> tay.OP_A8()

            //TYA Ops
            0x01u.toUByte() -> tya.OP_A8()

            //ALU OP BLock
            //ORA Ops
            0x01u.toUByte() -> ora.OP_01()
            0x05u.toUByte() -> ora.OP_05()
            0x09u.toUByte() -> ora.OP_09()
            0x0Du.toUByte() -> ora.OP_0D()
            0x11u.toUByte() -> ora.OP_11()
            0x15u.toUByte() -> ora.OP_15()
            0x19u.toUByte() -> ora.OP_19()
            0x1Du.toUByte() -> ora.OP_1D()

            //AND Ops
            0x21u.toUByte() -> and.OP_21()
            0x25u.toUByte() -> and.OP_25()
            0x29u.toUByte() -> and.OP_29()
            0x2Du.toUByte() -> and.OP_2D()
            0x31u.toUByte() -> and.OP_31()
            0x35u.toUByte() -> and.OP_35()
            0x39u.toUByte() -> and.OP_39()
            0x3Du.toUByte() -> and.OP_3D()

            //EOR Ops
            0x41u.toUByte() -> eor.OP_41()
            0x45u.toUByte() -> eor.OP_45()
            0x49u.toUByte() -> eor.OP_49()
            0x4Du.toUByte() -> eor.OP_4D()
            0x51u.toUByte() -> eor.OP_51()
            0x55u.toUByte() -> eor.OP_55()
            0x59u.toUByte() -> eor.OP_59()
            0x5Du.toUByte() -> eor.OP_5D()

            //ADC Ops
            0x61u.toUByte() -> adc.OP_61()
            0x65u.toUByte() -> adc.OP_65()
            0x69u.toUByte() -> adc.OP_69()
            0x6Du.toUByte() -> adc.OP_6D()
            0x71u.toUByte() -> adc.OP_71()
            0x75u.toUByte() -> adc.OP_75()
            0x79u.toUByte() -> adc.OP_79()
            0x7Du.toUByte() -> adc.OP_7D()

            //STA Ops
            0x81u.toUByte() -> sta.OP_81()
            0x85u.toUByte() -> sta.OP_85()
            0x8Du.toUByte() -> sta.OP_8D()
            0x91u.toUByte() -> sta.OP_91()
            0x95u.toUByte() -> sta.OP_95()
            0x99u.toUByte() -> sta.OP_99()
            0x9Du.toUByte() -> sta.OP_9D()

            //LDA Ops
            0xA1u.toUByte() -> lda.OP_A1()
            0xA5u.toUByte() -> lda.OP_A5()
            0xA9u.toUByte() -> lda.OP_A9()
            0xADu.toUByte() -> lda.OP_AD()
            0xB1u.toUByte() -> lda.OP_B1()
            0xB5u.toUByte() -> lda.OP_B5()
            0xB9u.toUByte() -> lda.OP_B9()
            0xBDu.toUByte() -> lda.OP_BD()

            //CMP Ops
            0x21u.toUByte() -> cmp.OP_C1()
            0x25u.toUByte() -> cmp.OP_C5()
            0x29u.toUByte() -> cmp.OP_C9()
            0x2Du.toUByte() -> cmp.OP_CD()
            0x31u.toUByte() -> cmp.OP_D1()
            0x35u.toUByte() -> cmp.OP_D5()
            0x39u.toUByte() -> cmp.OP_D9()
            0x3Du.toUByte() -> cmp.OP_DD()

            //SBC Ops
            0x21u.toUByte() -> sbc.OP_E1()
            0x25u.toUByte() -> sbc.OP_E5()
            0x29u.toUByte() -> sbc.OP_E9()
            0x2Du.toUByte() -> sbc.OP_ED()
            0x31u.toUByte() -> sbc.OP_F1()
            0x35u.toUByte() -> sbc.OP_F5()
            0x39u.toUByte() -> sbc.OP_F9()
            0x3Du.toUByte() -> sbc.OP_FD()

            //Read-Modify-Write OP Block
            //ASL Ops
            0x06u.toUByte() -> asl.OP_06()
            0x0Au.toUByte() -> asl.OP_0A()
            0x0Eu.toUByte() -> asl.OP_0E()
            0x16u.toUByte() -> asl.OP_16()
            0x1Eu.toUByte() -> asl.OP_1E()

            //CPX Ops
            0xE0u.toUByte() -> cpx.OP_E0()
            0xE4u.toUByte() -> cpx.OP_E4()
            0xECu.toUByte() -> cpx.OP_EC()

            //CPY Ops
            0xC0u.toUByte() -> cpy.OP_C0()
            0xC4u.toUByte() -> cpy.OP_C4()
            0xCCu.toUByte() -> cpy.OP_CC()

            //DEC Ops
            0xC6u.toUByte() -> dec.OP_C6()
            0x88u.toUByte() -> dec.OP_88()
            0xCAu.toUByte() -> dec.OP_CA()
            0xCEu.toUByte() -> dec.OP_CE()
            0xD6u.toUByte() -> dec.OP_D6()
            0xDEu.toUByte() -> dec.OP_DE()

            //INC Ops
            0xC8u.toUByte() -> inc.OP_C8()
            0xE6u.toUByte() -> inc.OP_E6()
            0xE8u.toUByte() -> inc.OP_E8()
            0xEEu.toUByte() -> inc.OP_EE()
            0xF6u.toUByte() -> inc.OP_F6()
            0xFEu.toUByte() -> inc.OP_FE()

            //LDX Ops
            0xA2u.toUByte() -> ldx.OP_A2()
            0xA6u.toUByte() -> ldx.OP_A6()
            0xAAu.toUByte() -> ldx.OP_AA()
            0xAEu.toUByte() -> ldx.OP_AE()
            0xB6u.toUByte() -> ldx.OP_B6()
            0xBAu.toUByte() -> ldx.OP_BA()
            0xBEu.toUByte() -> ldx.OP_BE()

            //LDY Ops
            0xA0u.toUByte() -> ldy.OP_A0()
            0xA4u.toUByte() -> ldy.OP_A4()
            0xA8u.toUByte() -> ldy.OP_A8()
            0xACu.toUByte() -> ldy.OP_AC()
            0xB4u.toUByte() -> ldy.OP_B4()
            0xBCu.toUByte() -> ldy.OP_BC()

            //LSR Ops
            0x46u.toUByte() -> lsr.OP_46()
            0x4Au.toUByte() -> lsr.OP_4A()
            0x4Eu.toUByte() -> lsr.OP_4E()
            0x56u.toUByte() -> lsr.OP_56()
            0x5Eu.toUByte() -> lsr.OP_5E()

            //ROL Ops
            0x26u.toUByte() -> rol.OP_26()
            0x2Au.toUByte() -> rol.OP_2A()
            0x2Eu.toUByte() -> rol.OP_2E()
            0x36u.toUByte() -> rol.OP_36()
            0x3Eu.toUByte() -> rol.OP_3E()

            //ROR Ops
            0x66u.toUByte() -> ror.OP_66()
            0x6Au.toUByte() -> ror.OP_6A()
            0x6Eu.toUByte() -> ror.OP_6E()
            0x76u.toUByte() -> ror.OP_76()
            0x7Eu.toUByte() -> ror.OP_7E()

            //STX Ops
            0x82u.toUByte() -> stx.OP_82()
            0x86u.toUByte() -> stx.OP_86()
            0x8Au.toUByte() -> stx.OP_8A()
            0x8Eu.toUByte() -> stx.OP_8E()
            0x96u.toUByte() -> stx.OP_96()
            0x9Au.toUByte() -> stx.OP_9A()

            //STY Ops
            0x95u.toUByte() -> sty.OP_84()
            0x95u.toUByte() -> sty.OP_8C()
            0x95u.toUByte() -> sty.OP_94()

            //Illegal OP Block
            //AHX Ops
            0x93u.toUByte() -> ahx.OP_93()
            0x9Fu.toUByte() -> ahx.OP_9F()

            //ALR Ops
            0x4Bu.toUByte() -> alr.OP_4B()

            //ANC Ops
            0x0Bu.toUByte() -> anc.OP_0B()
            0x2Bu.toUByte() -> anc.OP_0B()

            //ARR Ops
            0x6Bu.toUByte() -> arr.OP_6B()

            //AXS Ops
            0xCBu.toUByte() -> axs.OP_CB()

            //DCP Ops
            0xC3u.toUByte() -> dcp.OP_C3()
            0xC7u.toUByte() -> dcp.OP_C7()
            0xCFu.toUByte() -> dcp.OP_CF()
            0xD3u.toUByte() -> dcp.OP_D3()
            0xD7u.toUByte() -> dcp.OP_D7()
            0xDBu.toUByte() -> dcp.OP_DB()
            0xDFu.toUByte() -> dcp.OP_DF()

            //ISC Ops
            0xE3u.toUByte() -> isc.OP_E3()
            0xE7u.toUByte() -> isc.OP_E7()
            0xEFu.toUByte() -> isc.OP_EF()
            0xF3u.toUByte() -> isc.OP_F3()
            0xF7u.toUByte() -> isc.OP_F7()
            0xFBu.toUByte() -> isc.OP_FB()
            0xFFu.toUByte() -> isc.OP_FF()

            //LAS Ops
            0xBBu.toUByte() -> las.OP_BB()

            //LAX Ops
            0xA3u.toUByte() -> lax.OP_A3()
            0xA7u.toUByte() -> lax.OP_A7()
            0xABu.toUByte() -> lax.OP_AB()
            0xAFu.toUByte() -> lax.OP_AF()
            0xB3u.toUByte() -> lax.OP_B3()
            0xB7u.toUByte() -> lax.OP_B7()
            0xBFu.toUByte() -> lax.OP_BF()

            //RLA Ops
            0x21u.toUByte() -> rla.OP_23()
            0x25u.toUByte() -> rla.OP_27()
            0x2Du.toUByte() -> rla.OP_2F()
            0x32u.toUByte() -> rla.OP_33()
            0x35u.toUByte() -> rla.OP_37()
            0x39u.toUByte() -> rla.OP_3B()
            0x3Du.toUByte() -> rla.OP_3F()

            //RRA Ops
            0x63u.toUByte() -> rra.OP_63()
            0x67u.toUByte() -> rra.OP_67()
            0x6Fu.toUByte() -> rra.OP_6F()
            0x73u.toUByte() -> rra.OP_73()
            0x77u.toUByte() -> rra.OP_77()
            0x7Bu.toUByte() -> rra.OP_7B()
            0x7Fu.toUByte() -> rra.OP_7F()

            //SAX Ops
            0xA1u.toUByte() -> sax.OP_83()
            0xA5u.toUByte() -> sax.OP_87()
            0xADu.toUByte() -> sax.OP_8F()
            0xB5u.toUByte() -> sax.OP_97()

            //SHX Ops
            0x9Eu.toUByte() -> shx.OP_9E()

            //SHY Ops
            0x9Cu.toUByte() -> shy.OP_9C()

            //slo Ops
            0x03u.toUByte() -> slo.OP_03()
            0x07u.toUByte() -> slo.OP_07()
            0x0Fu.toUByte() -> slo.OP_0F()
            0x13u.toUByte() -> slo.OP_13()
            0x17u.toUByte() -> slo.OP_17()
            0x1Bu.toUByte() -> slo.OP_1B()
            0x1Fu.toUByte() -> slo.OP_1F()

            //SRE Ops
            0x43u.toUByte() -> sre.OP_43()
            0x47u.toUByte() -> sre.OP_47()
            0x4Fu.toUByte() -> sre.OP_4F()
            0x53u.toUByte() -> sre.OP_53()
            0x57u.toUByte() -> sre.OP_57()
            0x5Bu.toUByte() -> sre.OP_5B()
            0x5Fu.toUByte() -> sre.OP_5F()

            //TAS Ops
            0x03u.toUByte() -> tas.OP_9B()

            //XAA Ops
            0x03u.toUByte() -> xaa.OP_8B()
        }
    }
}

