package cpu.alu;

import util.DataType;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    /**
     * 返回两个二进制整数的和
     * dest + src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType add(DataType src, DataType dest) {
        return new DataType(adder(src.toString(),dest.toString(),0,32));
    }

    /**
     * 返回两个二进制整数的差
     * dest - src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType sub(DataType src, DataType dest) {
        return new DataType(adder(negation(new StringBuilder(src.toString())).toString(),dest.toString(),1,32));
    }



    private StringBuilder negation(StringBuilder src){
        StringBuilder result = new StringBuilder();
        for(int i =0;i<src.length();i++){
            if(src.charAt(i)=='0'){
                result.append('1');
            }else if(src.charAt(i)=='1'){
                result.append('0');
            }
        }
        return result;
    }

    private String adder(String src,String dest,int c,int length){
        StringBuilder srcStr = formalize(src,length).reverse();
        StringBuilder destStr = formalize(dest,length).reverse();

        int[] srcNum = new int[srcStr.length()];
        int[] destNum = new int[dest.length()];
        char[] resultNum = new char[length];

        for(int i =0;i<srcNum.length;i++){
            srcNum[i] = srcStr.charAt(i)-'0';
        }

        for(int i =0;i<destNum.length;i++){
            destNum[i] = destStr.charAt(i)-'0';
        }

        int b = 0;

        for(int i =0;i<resultNum.length;i++){
            b = c ^ srcNum[i] ^ destNum[i];
            c = (c & srcNum[i]) | (c & destNum[i]) | (srcNum[i] & destNum[i]);
            resultNum[i] =(char) (b + '0');
        }

        StringBuilder result = new StringBuilder(String.valueOf(resultNum)).reverse();

        return result.toString();
    }

    private StringBuilder formalize(String src,int length){
        StringBuilder result = new StringBuilder(src);
        while (result.length() < length){
            result.insert(0,result.charAt(0));
        }
        return result;
    }

}
