package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransformerTest {

    @Test
    public void intToBinaryTest1() {
        assertEquals("00000000000000000000000000000010", Transformer.intToBinary("2"));
    }

    @Test
    public void binaryToIntTest1() {
        assertEquals("10", Transformer.binaryToInt("00000000000000000000000000001010"));
    }
    @Test
    public void binaryToIntTest2() {
        assertEquals("-10", Transformer.binaryToInt("11111111111111111111111111110110"));
    }
    @Test
    public void binaryToIntTest3() {
        assertEquals("-213", Transformer.binaryToInt("11111111111111111111111100101011"));
    }
    @Test
    public void binaryToIntTest4() {
        assertEquals("0", Transformer.binaryToInt("00000000000000000000000000000000"));
    }
    @Test
    public void binaryToIntTest5(){
        assertEquals("-2147483648",Transformer.binaryToInt("10000000000000000000000000000000"));
    }
    @Test
    public void binaryToIntTest6(){
        assertEquals("-192",Transformer.binaryToInt("11111111111111111111111101000000"));
    }

    @Test
    public void binaryToIntTest7(){
        assertEquals("2147483647",Transformer.binaryToInt("01111111111111111111111111111111"));
    }

    @Test
    public void decimalToNBCDTest1() {
        assertEquals("11000000000000000000000000010000", Transformer.decimalToNBCD("10"));
    }

    @Test
    public void NBCDToDecimalTest1() {
        assertEquals("10", Transformer.NBCDToDecimal("11000000000000000000000000010000"));
    }

    @Test
    public void NBCDToDecimalTest2() {
        assertEquals("127", Transformer.NBCDToDecimal("1100000100100111"));
    }
    @Test
    public void NBCDToDecimalTest3() {
        assertEquals("-127", Transformer.NBCDToDecimal("1101000100100111"));
    }
    @Test
    public void NBCDToDecimalTest4() {
        assertEquals("0", Transformer.NBCDToDecimal("11000000"));
    }

    @Test
    public void floatToBinaryTest1() {
        assertEquals("00000000010000000000000000000000", Transformer.floatToBinary(String.valueOf(Math.pow(2, -127))));
    }

    @Test
    public void floatToBinaryTest2() {
        assertEquals("+Inf", Transformer.floatToBinary("" + Double.MAX_VALUE)); // 对于float来说溢出
    }
    @Test
    public void floatToBinaryTest3() {
        assertEquals("01000000010010010000111111011011", Transformer.floatToBinary("3.14159265"));
    }





    @Test
    public void floatToBinaryTest5() {
        assertEquals("00000000010000000000000000000000", Transformer.floatToBinary(String.valueOf((float) Math.pow(2, -127))));
    }

    @Test
    public void floatToBinaryTest6() {
        assertEquals("01000001110000000000000000000000", Transformer.floatToBinary(String.valueOf((float) 24)));
    }

    @Test
    public void floatToBinaryTest7() {
        assertEquals("01000010100111001000000000000000", Transformer.floatToBinary("78.25"));
    }

    @Test
    public void floatToBinaryTest8() {
        assertEquals("01000000010010001111010111000011", Transformer.floatToBinary("3.14"));
    }

    @Test
    public void floatToBinaryTest9() {
        assertEquals("10111111000000000000000000000000", Transformer.floatToBinary("-0.5"));
    }

    @Test
    public void floatToBinaryTest10() {
        assertEquals("00111111100000000000000000000000", Transformer.floatToBinary("1.0"));
    }

    @Test
    public void floatToBinaryTest11() {
        assertEquals("01000111100000000000000000000000", Transformer.floatToBinary("65536.0"));
    }


    @Test
    public void floatToBinaryTest12() {
        assertEquals("00000000000000000000000000000000", Transformer.floatToBinary("0.0"));
    }

    @Test
    public void floatToBinaryTest13() {
        assertEquals("10000000000000000000000000000000", Transformer.floatToBinary("-0.0"));
    }
    @Test
    public void floatToBinaryTest14() {
        assertEquals("11000010111101101110100101111001", Transformer.floatToBinary("-123.456"));
    }
    @Test
    public void floatToBinaryTest15() {
        assertEquals("10000000000011010111000101001001", Transformer.floatToBinary("-0.0000000000000000000000000000000000000012345"));
    }
    @Test
    public void floatToBinaryTest16() {
        assertEquals("10110011100000000000000000000000", Transformer.floatToBinary("-0.000000059604644775390625"));
    }
    @Test
    public void floatToBinaryTest17() {
        assertEquals("10110011100000000000000000000000", Transformer.floatToBinary("-0.000000059604644775390625"));
    }
    @Test
    public void floatToBinaryTest18() {
        assertEquals("10110011100000000000000000000000", Transformer.floatToBinary("-0.000000059604644775390625"));
    }
    @Test
    public void floatToBinaryTest19() {
        assertEquals("00110100000000000000000000000001", Transformer.floatToBinary(String.valueOf(1.192093e-7)));
    }
    @Test
    public void floatToBinaryTest20() {
        assertEquals("-Inf", Transformer.floatToBinary(String.valueOf((-1) * Double.MAX_VALUE)));
    }
    @Test
    public void floatToBinaryTest21() {
        assertEquals("NaN", Transformer.floatToBinary(String.valueOf((-1) * Double.NaN)));
    }

    @Test
    public void floatToBinaryTest22() {
        assertEquals("00111111100111100000011001010010", Transformer.floatToBinary("1.23456789"));
    }

    @Test
    public void floatToBinaryTest23() {
        assertEquals("00000000000000000000000000000010", Transformer.floatToBinary(String.valueOf(2.802597e-45)));
    }

    @Test
    public void floatToBinaryTest24() {
        assertEquals("10000000000000000000000000000001", Transformer.floatToBinary(String.valueOf(-1.4E-45)));
    }

    @Test
    public void floatToBinaryTest25() {
        assertEquals("10000000000000000000000000000000", Transformer.floatToBinary("-0"));
    }
    @Test
    public void floatToBinaryTest26() {
        assertEquals("00000000011111111111111111111111", Transformer.floatToBinary(String.valueOf(Math.pow(2,-126)-Math.pow(2,-126-23))));
    }
    @Test
    public void floatToBinaryTest27() {
        assertEquals("00000000000000000000000000000001", Transformer.floatToBinary(String.valueOf(1.4 * Math.pow(10,-45))));
    }

    @Test
    public void floatToBinaryTest28() {
        assertEquals("01111111011111111111111111111111", Transformer.floatToBinary(String.valueOf(Math.pow(2,128) - Math.pow(2,127-23))));
    }







    @Test
    public void binaryToFloatTest1() {
        assertEquals(String.valueOf((float) Math.pow(2, -127)), Transformer.binaryToFloat("00000000010000000000000000000000"));
    }

    @Test
    public void binaryToFloatTest2() {
        assertEquals(String.valueOf((float) 24), Transformer.binaryToFloat("01000001110000000000000000000000"));
    }

    @Test
    public void binaryToFloatTest3() {
        assertEquals(String.valueOf(78.25), Transformer.binaryToFloat("01000010100111001000000000000000"));
    }

    @Test
    public void binaryToFloatTest4() {
        assertEquals(String.valueOf(3.14), Transformer.binaryToFloat("01000000010010001111010111000011"));
    }
    @Test
    public void binaryToFloatTest5() {
        assertEquals(String.valueOf(-0.5), Transformer.binaryToFloat("10111111000000000000000000000000"));
    }
    @Test
    public void binaryToFloatTest6() {
        assertEquals(String.valueOf(1.0), Transformer.binaryToFloat("00111111100000000000000000000000"));
    }

    @Test
    public void binaryToFloatTest7() {
        assertEquals(String.valueOf(65536.0), Transformer.binaryToFloat("01000111100000000000000000000000"));
    }

    @Test
    public void binaryToFloatTest8() {
        assertEquals(String.valueOf(0.0), Transformer.binaryToFloat("00000000000000000000000000000000"));
    }

    @Test
    public void binaryToFloatTest9() {
        assertEquals(String.valueOf(-0.0), Transformer.binaryToFloat("10000000000000000000000000000000"));
    }

    @Test
    public void binaryToFloatTest10() {
        assertEquals(String.valueOf(-1.4E-45), Transformer.binaryToFloat("10000000000000000000000000000001"));
    }






}
