package util;


public class Transformer {

    public static String intToBinary(String numStr) {
        int num = Integer.parseInt(numStr);
        boolean isNeg = false;
        if(num<0){
            isNeg = true;
            num *= -1;
        }
        StringBuilder result = new StringBuilder();

        //化为二进制
        while (num>0){
            if(num%2==1){
                result.insert(0,'1');
            }else{
                result.insert(0,'0');
            }
            num /= 2;
        }

        //化到32位
        while (result.length() < 32){
            result.insert(0,'0');
        }

        if(isNeg){
            //如果是负数，则取反+1，便可将正数转为负数
            result = oneAdder(negation(result));
        }


        return result.toString();
    }

    public static String binaryToInt(String binStr) {
        int result = 0;
        boolean isNeg = false;
        StringBuilder bin = new StringBuilder(binStr);
        if(bin.charAt(0)=='1'){
            //是负数
            bin = oneAdder(negation(bin));
            if(bin.charAt(0)=='1'){
                //溢出了，现在符号位也算是数字了
                bin.insert(0,'0');
            }
            isNeg = true;
        }
        result = valueOf(new StringBuilder(bin.substring(1)),2);

        if(isNeg){
            result *= -1;
        }

        return String.valueOf(result);
    }

    public static String decimalToNBCD(String decimalStr) {
        int num = Integer.parseInt(decimalStr);
        StringBuilder result = new StringBuilder();
        if(num >=0){
            result.append("1100");
        }else{
            result.append("1101");
        }

        for(int i =0;i<decimalStr.length();i++){
            result.append(oneIntToNBCD(decimalStr.charAt(i)-'0'));
        }

        while (result.length()<32){
            result.insert(4,'0');
        }

        return result.toString();
    }

    public static String NBCDToDecimal(String NBCDStr) {
        int result = 0;
        boolean isNge = NBCDStr.startsWith("1101");

        StringBuilder str = new StringBuilder(NBCDStr.substring(4));
        for(int i =0;i<str.length();i+=4){
            String curNBCD = str.substring(i,i+4);
            int curNum = oneNBCDToInt(curNBCD);
            result = result * 10 + curNum;
        }



        if(isNge){
            result *= -1;
        }
        return String.valueOf(result);
    }

    public static String floatToBinary(String floatStr) {
        int eLength = 8;
        int sLength = 23;
        int len = 1 + eLength + sLength;
        int bias = getBias(eLength);

        double num =Math.abs(Double.parseDouble(floatStr));
        boolean isNge = num < 0 || floatStr.charAt(0)=='-';

        //处理非数
        if(Float.isNaN((float) num)){
            return "Nan";
        }

        //处理极限
        if(!isFinite(num,eLength,sLength)){
            if(isNge){
                return "-Inf";
            }else{
                return "+Inf";
            }
        }

        StringBuilder result = new StringBuilder();
        if(isNge){
            result.append('1');
        }else{
            result.append('0');
        }

        //处理0
        if(num == 0){
            while (result.length() < len ){
                result.append('0');
            }
            return result.toString();
        }

        boolean isNormal = num >=minNormal(eLength);

        if(isNormal){
            //规格化
            int exp = getExp(num);
            String expStr = intToBinary(exp + bias,eLength);

            num /= Math.pow(2,exp);
            //num = 1.xxx

            String sigStr = getSig(num,sLength);

            result.append(expStr).append(sigStr);

        }else{
            //非规格化
            int exp = 1-bias;
            for(int i =0;i<eLength;i++){
                result.append('0');
            }

            num /= Math.pow(2,exp);
            String sigStr = getSig(num,sLength);
            result.append(sigStr);
        }



        return result.toString();
    }

    public static String binaryToFloat(String binStr) {
        int eLength = 8;
        int sLength = 23;
        int bias = getBias(eLength);
        boolean isNge = binStr.charAt(0)=='1';
        String exp = binStr.substring(1,1+eLength);
        String sig = binStr.substring(1+eLength);

        double result = 0;

        if(exp.equals("11111111")){
            if(sig.contains("1")){
                return "NaN";
            }
            if(isNge){
                return "-Inf";
            }else{
                return "Inf";
            }
        }

        //非规格化
        if(exp.equals("00000000")){
            //为0
            if(!sig.contains("1")){
                if(isNge){
                    return "-0.0";
                }else{
                    return "0.0";
                }
            }
            for(int i =0;i<sig.length();i++){
                result += (sig.charAt(i)-'0') * Math.pow(2,-(i+1));
            }
            result *= Math.pow(2,1-bias);
        }else{
            //规格化
            int expNum =Integer.parseInt(binaryToInt("0" + exp)) - bias;
            for(int i =0;i<sig.length();i++){
                result += (sig.charAt(i)-'0') * Math.pow(2,-(i+1));
            }
            result += 1;
            result *= Math.pow(2,expNum);
        }

        if(isNge){
            result *= -1;
        }

        return String.valueOf(result);
    }

    //取反
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

    //加一
    private static StringBuilder oneAdder(StringBuilder src){
        int[] num = new int[src.length()];
        num[0] = 0;
        for(int i =0;i<src.length();i++){
            num[i] = src.charAt(i)-'0';
        }

        int b = 0;
        int c = 1;
        char[] result = new char[num.length];
        for(int i = num.length-1;i>=0;i--){
            b = num[i] ^ c;
            c = num[i] & c;
            result[i] = (char) (b+'0');
        }
        return new StringBuilder(String.valueOf(result));

    }

    private static int valueOf(StringBuilder src,int radix){
        int result = 0;
        //从左到右，左移
        for(int i =0;i<src.length();i++){
            int temp = src.charAt(i)-'0';
            result = result * radix + temp;
        }


        return result;
    }

    private static String oneIntToNBCD(int num){
        String bit32String = intToBinary(String.valueOf(num));
        return bit32String.substring(bit32String.length()-4);
    }

    private static int oneNBCDToInt(String src){
        src = '0'+src;
        return Integer.parseInt(binaryToInt(src));
    }

    private static boolean isFinite(double num,int eLength,int sLength){
        int bias = getBias(eLength);
        int exp = maxValue(eLength) - bias - sLength;//假设尾数后没有小数点，在这里把多的位数减掉
        int sig = maxValue(sLength+1);
        double maxNum = sig * Math.pow(2,exp);
        return num >= -maxNum && num <= maxNum;
    }

    //得到指数的偏差
    private static int getBias(int eLength){
        return (int) ((Math.pow(2,eLength)-1)/2);
    }

    //得到一定二进制位数的最大值
    private static int maxValue(int length){
        return (int) (Math.pow(2,length)-1);
    }

    //最小的规格化浮点数
    private static double minNormal(int eLength){
        int bias = getBias(eLength);
        return Math.pow(2,1-bias);//尾数都是0，指数为1
    }

    private static int getExp(double num){
        //单独处理0
        if(num == 0){
            return 0;
        }
        int exp = 0;
        while (num >=2){
            exp++;
            num /= 2;
        }
        while (num<1){
            exp--;
            num *=2;
        }
        return exp;
    }

    private static String intToBinary(int num,int length){
        String temp = intToBinary(String.valueOf(num));
        return temp.substring(temp.length()-length);
    }

    private static String getSig(double num,int sLength){
        if(num >= 1){
            num = num - (int) num;
        }
        //num == 0.xxx
        StringBuilder result = new StringBuilder();


        while (num > 0 && result.length() < sLength){
            num *=2;
            if(num < 1){
                //num *=2;
                result.append('0');
            }else{
                num -=1;
                result.append('1');
            }
        }

        while (result.length() < sLength){
            result.append('0');
        }


        return result.toString();
    }

}
