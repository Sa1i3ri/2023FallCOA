package cpu.alu;

//import jdk.incubator.vector.VectorOperators;
import util.DataType;
import util.Transformer;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {
    /**
     * 返回两个二进制整数的乘积(结果低位截取后32位)
     * dest * src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType mul(DataType src, DataType dest) {
        int length = 32;
        //srcStr：33位，乘数
        StringBuilder srcStr = new StringBuilder(src.toString()+'0');
        //被乘数
        StringBuilder destStr = new StringBuilder(dest.toString());
        StringBuilder result = new StringBuilder();
        for(int i =0;i<length;i++){
            result.append('0');
        }

        for(int i = srcStr.length()-2;i>=0;i--){
            String reference = srcStr.substring(i,i+2);
            if(reference.equals("01")){
                //相减
                result = adder(result.substring(0,32),destStr.toString(),0,length).append(result.substring(length));
            }else if(reference.equals("10")){
                //相加
                result = adder(result.substring(0,32),negation(destStr).toString(),1,length).append(result.substring(length));
            }
            //右移
            result.insert(0,result.charAt(0));
        }



        return new DataType(result.substring(length));
    }

    DataType remainderReg = new DataType("00000000000000000000000000000000");

    /**
     * 返回两个二进制整数的除法结果
     * dest ÷ src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType div(DataType src, DataType dest) {
        int length = 64;

        StringBuilder srcStr = new StringBuilder(src.toString());
        StringBuilder destStr = new StringBuilder(dest.toString());
        //前32：余数 后32：商
        StringBuilder result = formalize(destStr.toString(),length);



        if(isZero(srcStr.toString())){
            throw new ArithmeticException();
        }
        if(isZero(destStr.toString())){
            return new DataType(result.substring(length/2));
        }

        for(int i = 0;i<length/2;i++){
            //左移空一位
            result.deleteCharAt(0);

            String remain = result.substring(0,length/2);
            if(remain.charAt(0)==srcStr.charAt(0)){
                remain = suber(srcStr.toString(),remain,length/2).toString();
            }else{
                remain = adder(srcStr.toString(),remain,0,length/2).toString();
            }

            //remain和以前保持一样
            if(remain.charAt(0) == result.charAt(0)){
                //够减
                result = new StringBuilder(remain).append(result.substring(length/2));
                result.append('1');
            }else{
                //不够减
                result.append('0');
            }

        }

        //检查余数绝对值会不会等于除数绝对值
        String remain = result.substring(0,length/2);
        if(remain.contentEquals(srcStr) || remain.contentEquals(oneAdder(negation(srcStr)))){
            //余数变成0
            result.delete(0,length/2);
            for(int i=0;i<length/2;i++){
                result.insert(0,'0');
            }
            //商+1
            StringBuilder quo = oneAdder(new StringBuilder(result.substring(length/2)));
            result.delete(length/2,length);
            result.append(quo);
        }

        //如果被除数和除数异号，则出来的结果是负的
        if(src.toString().charAt(0) != dest.toString().charAt(0)){
            StringBuilder quo = oneAdder(negation(new StringBuilder(result.substring(length/2))));
            result.delete(length/2,length);
            result.append(quo);
        }

        this.remainderReg = new DataType(result.substring(0,length/2));
        return new DataType(result.substring(length/2));
    }


    private static StringBuilder negation(StringBuilder src){
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

    private static StringBuilder oneAdder(StringBuilder src){
        int[] num = new int[src.length()];
        char[] result = new char[src.length()];
        for(int i =0;i<src.length();i++){
            num[i] = src.charAt(i)-'0';
        }
        int b =0;
        int c =1;

        for(int i = num.length-1;i>=0;i--){
            b = c ^ num[i];
            c = c & num[i];
            result[i] =(char) (b+'0');
        }

        return new StringBuilder(String.valueOf(result));

    }

    private static StringBuilder formalize(String src,int length){
        StringBuilder result = new StringBuilder(src);
        while (result.length()<length){
            result.insert(0,result.charAt(0));
        }
        return result;
    }

    private static StringBuilder adder(String src,String dest,int c,int length){
        StringBuilder srcStr = formalize(src,length);
        StringBuilder destStr = formalize(dest,length);

        int[] srcNum = new int[srcStr.length()];
        int[] destNum = new int[destStr.length()];
        char[] result = new char[length];

        for(int i =0;i<srcNum.length;i++){
            srcNum[i] = srcStr.charAt(i) -'0';
        }
        for(int i =0;i<destNum.length;i++){
            destNum[i] = destStr.charAt(i)-'0';
        }

        int b =0;
        for(int i =length-1;i>=0;i--){
            b = c ^ srcNum[i] ^ destNum[i];
            c = (c & srcNum[i]) | (c & destNum[i]) | (srcNum[i] & destNum[i]);
            result[i] = (char) (b+'0');
        }

        return new StringBuilder(String.valueOf(result));
    }

    //dest - src
    private static StringBuilder suber(String src,String dest,int length){
        return adder(negation(new StringBuilder(src)).toString(),dest,1,length);
    }

    private static boolean isNegative(String src){
        return src.charAt(0)=='1';
    }

    private static boolean isZero(String src){
        StringBuilder temp = new StringBuilder(src);
        if(isNegative(src)){
            temp = oneAdder(negation(new StringBuilder(src)));
        }

        return !temp.toString().contains("1");

    }



}
