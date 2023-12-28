package cpu.nbcdu;

import util.DataType;
import util.Transformer;

public class NBCDU {

    //进位
    private int CF = 0;

    /**
     * @param src  A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest + src
     */
    DataType add(DataType src, DataType dest) {
        return new DataType(do_add(src.toString(),dest.toString()));
    }

    private String do_add(String src,String dest){
        this.CF=0;
        if(isPositive(src) && !isPositive(dest)){
            return do_sub("1100" + dest.substring(4),src);
        }else if(!isPositive(src) && isPositive(dest)){
            return do_sub("1100"+src.substring(4),dest);
        }else{
            //同号
            StringBuilder result = new StringBuilder();
            //符号
            result.append(src.substring(0,4));

            for(int i = src.length()-4;i>=4;i-=4){
                String curSrc_4 = src.substring(i,i+4);
                String curDest_4 = dest.substring(i,i+4);
                String resultStr = do_add_4(curSrc_4,curDest_4,this.CF);
                result.insert(4,resultStr);
            }
            return result.toString();

        }
    }


    private String do_add_4(String src_4,String dest_4,int c){
        String result = adder(src_4,dest_4,c);

        if(isLarger(result,oneIntToNBCD(9))){
            //溢出
            this.CF = 1;
            result = adder(result.substring(1),oneIntToNBCD(6),0);
        }else{
            this.CF = 0;
        }

        return result.substring(1);

    }


    private static String adder(String src,String dest,int c){
        int[] srcNum = new int[src.length()];
        int[] destNum = new int[dest.length()];
        String result;
        char[] resultChar = new char[src.length()+1];

        for(int i =0;i<srcNum.length;i++){
            srcNum[i] = src.charAt(i)-'0';
        }
        for(int i =0;i<destNum.length;i++){
            destNum[i] = dest.charAt(i)-'0';
        }
        int b = 0;
        for(int i =src.length()-1;i>=0;i--){
            b = srcNum[i] ^ destNum[i] ^ c;
            c = (c & srcNum[i]) | (c & destNum[i]) | (srcNum[i] & destNum[i]);
            resultChar[i+1] = (char) (b+'0');
        }
        resultChar[0] = (char)(c+'0');
        return String.valueOf(resultChar);
    }

    private int valueOf(String src){
        int result = 0;
        for(int i =0;i<src.length();i++){
            int temp = src.charAt(i)-'0';
            result = result * 2 + temp;
        }
        return result;
    }

    private boolean isLarger(String src,String dest){
        return valueOf(src) > valueOf(dest);
    }

    private String oneIntToNBCD(int n){
        return Transformer.intToBinary(String.valueOf(n)).substring(32-4);
    }

    /***
     *
     * @param src A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest - src
     */
    DataType sub(DataType src, DataType dest) {
        return new DataType(do_sub(src.toString(),dest.toString()));
    }

    private String do_sub(String src,String dest){
        this.CF=0;
        if(isPositive(src) && !isPositive(dest)){
            // - dest  - src
            return do_add("1101"+src.substring(4),dest);
        }else if(!isPositive(src) && isPositive(dest)){
            // dest + src
            return do_add("1100"+src.substring(4),dest);
        }else if(!isPositive(src) && !isPositive(dest)){
            // -dest -  (-src) = - (dest -src)=src - dest
            return do_sub("1100"+dest.substring(4),"1100"+src.substring(4));
        }else{

            //0需要特判
            if(src.substring(4).equals("0000000000000000000000000000") ){
                return dest;
            }else if(dest.substring(4).equals("0000000000000000000000000000")){
                if(isPositive(src)){
                    return "1101" + src.substring(4);
                }else{
                    return "1100" + src.substring(4);
                }
            }

            String result;
            String inverSrc = allGetInversion(src);
            result = do_add(inverSrc,dest);

            if(this.CF == 0){
                //没有进位，说明dest-src的结果是负数
                result = allGetInversion(result);
                StringBuilder resultStr = new StringBuilder(result);
                resultStr.replace(0,4,"1101");
                result = resultStr.toString();
            }
            return result;
        }
    }

    private String negation(String src){
        StringBuilder result = new StringBuilder();
        for(int i =0;i<src.length();i++){
            if(src.charAt(i)=='0'){
                result.append('1');
            }else if(src.charAt(i)=='1'){
                result.append('0');
            }
        }
        return result.toString();
    }

    private String inversion(String src_4){
        //取反+10
        String result = negation(src_4);
        result = adder(result,oneIntToNBCD(10),0);
        return result.substring(result.length()-4);
    }

    private String allGetInversion(String src){
        //取反步骤：1，对每个数(4位）取反 2.对每个数(4位）+10（单纯加） 3.最后在末尾+1
        StringBuilder result = new StringBuilder(src.substring(0,4));
        for(int i =4;i<=src.length()-4;i+=4 ){
            result.append(inversion(src.substring(i,i+4)));
        }
        //最后+1
        result = new StringBuilder(do_add(result.toString(), "11000000000000000000000000000001"));
        return result.toString();
    }

    private boolean isPositive(String src){
        return src.startsWith("1100");
    }

}
