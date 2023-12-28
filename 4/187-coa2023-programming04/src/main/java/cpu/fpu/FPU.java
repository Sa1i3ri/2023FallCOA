package cpu.fpu;

import cpu.alu.ALU;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

import java.util.PrimitiveIterator;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用3位保护位进行计算
 */
public class FPU {

    private ALU alu = new ALU();

    private final String[][] addCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN}
    };

    private final String[][] subCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN}
    };

    private final String[][] mulCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN}
    };

    private final String[][] divCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
    };

    /**
     * compute the float add of (dest + src)
     */
    public DataType add(DataType src, DataType dest) {
        int eLength = 8;
        int sLength = 23;
        int bias = getBias(eLength);

        StringBuilder srcStr = new StringBuilder(src.toString());
        StringBuilder destStr = new StringBuilder(dest.toString());
        //检查：1.cornor 2. NAN 3.Inf 4. 0
        String conor = cornerCheck(addCorner,srcStr.toString(),destStr.toString());
        if(conor!=null){
            return new DataType(conor);
        }
        if(srcStr.toString().matches(IEEE754Float.NaN) || destStr.toString().matches(IEEE754Float.NaN)){
            return new DataType(IEEE754Float.NaN);
        }
        if(srcStr.toString().equals(IEEE754Float.P_ZERO) || src.toString().equals(IEEE754Float.N_ZERO)
        || destStr.toString().equals(IEEE754Float.P_INF) || destStr.toString().equals(IEEE754Float.N_INF)){
            return dest;
        }
        if(destStr.toString().equals(IEEE754Float.P_ZERO) || destStr.toString().equals(IEEE754Float.N_ZERO)
        || src.toString().equals(IEEE754Float.P_INF)||src.toString().equals(IEEE754Float.N_INF)){
            return src;
        }
        //检查结束

        //符号位
        char srcSign = srcStr.charAt(0);
        char destSign = destStr.charAt(0);
        char resultSign = '0';
        //指数
        StringBuilder srcExp = new StringBuilder(srcStr.substring(1,1+eLength));
        int srcE = valueOf(srcExp.toString(),2);
        StringBuilder destExp = new StringBuilder(destStr.substring(1,1+eLength));
        int destE = valueOf(destExp.toString(),2);
        StringBuilder resultExp = new StringBuilder();
        int resultE = 0;
        //重要
        //本质上来说，阶码为0时，其实等价于-126次，所以相当于为1
        //重要
        if(srcE==0){
            srcE=1;
        }
        if(destE==0){
            destE=1;
        }
        //尾数
        StringBuilder srcSig = new StringBuilder(srcStr.substring(1+eLength));
        StringBuilder destSig = new StringBuilder(destStr.substring(1+eLength));
        StringBuilder resultSig = new StringBuilder();
        //尾数前面插入3位，1位符号位，1位溢出位，1位隐藏位
        srcSig.insert(0,"00");
        destSig.insert(0,"00");
        if(srcExp.toString().contains("1")){
            //规格化
            srcSig.insert(2,'1');
        }else{
            //非规格化
            srcSig.insert(2,'0');
        }
        if(destExp.toString().contains("1")){
            //规格化
            destSig.insert(2,'1');
        }else{
            //非规格化
            destSig.insert(2,'0');
        }
        //grs位，现在sig一共有23+6=29位
        srcSig.append("000");
        destSig.append("000");
        int extraSLength = 6;


        //对阶
        if(srcE > destE){
            destSig = new StringBuilder(rightShift(destSig.toString(), srcE - destE));
            destE = srcE;
        }else{
            srcSig = new StringBuilder(rightShift(srcSig.toString(),destE - srcE));
            srcE = destE;
        }
        //阶数
        resultE = srcE;
        //尾数
        if(srcSign == destSign){
            resultSig = adder(srcSig.toString(),destSig.toString(),0,sLength + extraSLength);
            resultSign = srcSign;
        }else if(srcSign == '1'){
            resultSig = suber(srcSig.toString(),destSig.toString(),sLength+extraSLength);
        }else{
            resultSig = suber(destSig.toString(),srcSig.toString(),sLength+extraSLength);
        }
        //尾数的符号
        if(resultSig.charAt(0)=='1') {
            resultSig = oneAdder(negation(resultSig).toString(), sLength + extraSLength);
            resultSign = '1';
        }
        //尾数的溢出
        if(resultSig.charAt(1)=='1'){
            //溢出了
            resultSig = new StringBuilder(rightShift(resultSig.toString(), 1));
            resultE++;
        }else{
            //没溢出
                //规格化
            while (resultSig.charAt(2)!='1' && resultE >1){
                //阶数到1就停下，待会再判断是否规格化
                //左移
                resultSig = new StringBuilder(resultSig.substring(1)).append('0');
                resultE--;
            }
        }
        if(resultE==1 && resultSig.charAt(2)!='1'){
            //非规格化
            resultE=0;

        }



        resultExp = new StringBuilder(formalize(Transformer.intToBinary(String.valueOf(resultE)),8));

        return new DataType(round(resultSign,resultExp.toString(),resultSig.substring(2)));
    }

    /**
     * compute the float add of (dest - src)
     */
    public DataType sub(DataType src, DataType dest) {
        if(src.toString().matches(IEEE754Float.NaN) || dest.toString().matches(IEEE754Float.NaN)){
            return new DataType(IEEE754Float.NaN);
        }
        StringBuilder srcStr = new StringBuilder(src.toString());
        if(srcStr.charAt(0)=='1'){
            srcStr.replace(0,1, "0");
        }else if(srcStr.charAt(0)=='0'){
            srcStr.replace(0,1, "1");
        }
        return add(new DataType(srcStr.toString()),dest);
    }

    /**
     * compute the float mul of (dest * src)
     */
    public DataType mul(DataType src,DataType dest){
        int eLength = 8;
        int sLength = 23;
        int bias = getBias(eLength);

        StringBuilder srcStr = new StringBuilder(src.toString());
        StringBuilder destStr = new StringBuilder(dest.toString());

        //cornor nan inf 0
        String cornor = cornerCheck(mulCorner,srcStr.toString(),dest.toString());
        if(cornor!=null){
            return new DataType(cornor);
        }
        if(src.toString().matches(IEEE754Float.NaN)|| dest.toString().matches(IEEE754Float.NaN)){
            return new DataType(IEEE754Float.NaN);
        }
        if((isInfinite(src.toString()) && isZero(dest.toString())) || (isInfinite(dest.toString()) && isZero(src.toString()))){
            return new DataType(IEEE754Float.NaN);
        }
        //符号
        char srcSign = srcStr.charAt(0);
        char destSign = destStr.charAt(0);
        char resultSign = (char) ((srcSign-'0')^(destSign-'0')+'0');



        if(isInfinite(dest.toString()) || isZero(dest.toString())){
            return new DataType(resultSign + dest.toString().substring(1));
        }

        if(isInfinite(src.toString()) || isZero(src.toString())){
            return new DataType(resultSign + src.toString().substring(1));
        }
        //检查结束


        //阶数,此处的exp指的是实际上的数字
        StringBuilder srcExp = new StringBuilder(srcStr.substring(1,1+eLength));
        int srcE = valueOf(srcExp.toString(),2);
        StringBuilder destExp = new StringBuilder(destStr.substring(1,1+eLength));
        int destE = valueOf(destExp.toString(),2);
        StringBuilder resultExp = new StringBuilder();
        int resultE = 0;
        //尾数
        StringBuilder srcSig = new StringBuilder(srcStr.substring(1+eLength)+"000");
        StringBuilder destSig = new StringBuilder(destStr.substring(1+eLength)+"000");
        StringBuilder resultSig = new StringBuilder();
        if(srcE==0){
            srcSig.insert(0,'0');
            srcE=1;
        }else{
            srcSig.insert(0,'1');
        }
        if(destE==0){
            destSig.insert(0,'0');
            destE=1;
        }else{
            destSig.insert(0,'1');
        }

        int extraSLength = 4;

        resultE = srcE + destE - bias;
        resultSig = new StringBuilder(alu.mul(srcSig.toString(),destSig.toString(),sLength+extraSLength));
        resultE++;

        while (resultSig.charAt(0)!='1' && resultE >1){
            //左移
            resultSig = moveLeft(resultSig,1);
            resultE--;
        }
        while (resultE<=0 && resultSig.substring(0,sLength+extraSLength).contains("1")){
            resultSig = new StringBuilder(rightShift(resultSig.toString(), 1));
            resultE++;
        }
        if(resultE==1 && resultSig.charAt(0)!='1'){
            //非规格化
            resultE = 0;
        }

        //检查上溢出、下溢出
        if(resultE > maxOf(eLength)){
            if(resultSign=='0'){
                return new DataType(IEEE754Float.P_INF);
            }else{
                return new DataType(IEEE754Float.N_INF);
            }
        }else if(resultE<0){
            if(resultSign=='0'){
                return new DataType(IEEE754Float.P_ZERO);
            }else{
                return new DataType(IEEE754Float.N_ZERO);
            }

        }



        resultExp = new StringBuilder(formalize(Transformer.intToBinary(String.valueOf(resultE)), eLength));
        return new DataType(round(resultSign,resultExp.toString(),resultSig.toString())) ;
    }

    /**
     * compute the float mul of (dest / src)
     */
    public DataType div(DataType src,DataType dest){
        int eLength = 8;
        int sLength = 23;
        int bias = getBias(eLength);

        StringBuilder srcStr = new StringBuilder(src.toString());
        StringBuilder destStr = new StringBuilder(dest.toString());

        //cornor nan inf 0
        String cornor = cornerCheck(mulCorner,srcStr.toString(),dest.toString());
        if(cornor!=null){
            return new DataType(cornor);
        }
        if(src.toString().matches(IEEE754Float.NaN)|| dest.toString().matches(IEEE754Float.NaN)){
            return new DataType(IEEE754Float.NaN);
        }
        if(isZero(src.toString())){
            throw new ArithmeticException();
        }
        //符号
        char srcSign = srcStr.charAt(0);
        char destSign = destStr.charAt(0);
        char resultSign = (char) ((srcSign-'0')^(destSign-'0')+'0');



        if(isInfinite(dest.toString()) || isZero(dest.toString())){
            return new DataType(resultSign + dest.toString().substring(1));
        }

        if(isInfinite(src.toString())){
            return new DataType(resultSign + IEEE754Float.P_ZERO.substring(1));
        }
        //检查结束


        //阶数,此处的exp指的是实际上的数字
        StringBuilder srcExp = new StringBuilder(srcStr.substring(1,1+eLength));
        int srcE = valueOf(srcExp.toString(),2);
        StringBuilder destExp = new StringBuilder(destStr.substring(1,1+eLength));
        int destE = valueOf(destExp.toString(),2);
        StringBuilder resultExp = new StringBuilder();
        int resultE = 0;
        //尾数
        StringBuilder srcSig = new StringBuilder(srcStr.substring(1+eLength)+"000");
        StringBuilder destSig = new StringBuilder(destStr.substring(1+eLength)+"000");
        StringBuilder resultSig = new StringBuilder();
        if(srcE==0){
            srcSig.insert(0,'0');
            srcE=1;
        }else{
            srcSig.insert(0,'1');
        }
        if(destE==0){
            destSig.insert(0,'0');
            destE=1;
        }else{
            destSig.insert(0,'1');
        }

        int extraSLength = 4;

        resultE = destE - srcE + bias;
        resultSig = new StringBuilder(alu.div(srcSig.toString(),destSig.toString()));
        //resultE++;

        while (resultSig.charAt(0)!='1' && resultE >1){
            //左移
            resultSig = moveLeft(resultSig,1);
            resultE--;
        }
        while (resultE<=0 && resultSig.substring(0,sLength+extraSLength).contains("1")){
            resultSig = new StringBuilder(rightShift(resultSig.toString(), 1));
            resultE++;
        }
        if(resultE==1 && resultSig.charAt(0)!='1'){
            //非规格化
            resultE = 0;
        }

        //检查上溢出、下溢出
        if(resultE > maxOf(eLength)){
            if(resultSign=='0'){
                return new DataType(IEEE754Float.P_INF);
            }else{
                return new DataType(IEEE754Float.N_INF);
            }
        }else if(resultE<0){
            if(resultSign=='0'){
                return new DataType(IEEE754Float.P_ZERO);
            }else{
                return new DataType(IEEE754Float.N_ZERO);
            }

        }



        resultExp = new StringBuilder(formalize(Transformer.intToBinary(String.valueOf(resultE)), eLength));
        return new DataType(round(resultSign,resultExp.toString(),resultSig.toString())) ;
    }

    /**
     * check corner cases of mul and div
     *
     * @param cornerMatrix corner cases pre-stored
     * @param oprA first operand (String)
     * @param oprB second operand (String)
     * @return the result of the corner case (String)
     */
    private String cornerCheck(String[][] cornerMatrix, String oprA, String oprB) {
        for (String[] matrix : cornerMatrix) {
            if (oprA.equals(matrix[0]) && oprB.equals(matrix[1])) {
                return matrix[2];
            }
        }
        return null;
    }

    /**
     * right shift a num without considering its sign using its string format
     *
     * @param operand to be moved
     * @param n       moving nums of bits
     * @return after moving
     */
    private String rightShift(String operand, int n) {
        StringBuilder result = new StringBuilder(operand);  //保证位数不变
        boolean sticky = false;
        for (int i = 0; i < n; i++) {
            sticky = sticky || result.toString().endsWith("1");
            result.insert(0, "0");
            result.deleteCharAt(result.length() - 1);
        }
        if (sticky) {
            result.replace(operand.length() - 1, operand.length(), "1");
        }
        return result.substring(0, operand.length());
    }

    /**
     * 对GRS保护位进行舍入
     *
     * @param sign    符号位
     * @param exp     阶码
     * @param sig_grs 带隐藏位和保护位的尾数
     * @return 舍入后的结果
     */
    private String round(char sign, String exp, String sig_grs) {
        int grs = Integer.parseInt(sig_grs.substring(24, 27), 2);
        if ((sig_grs.substring(27).contains("1")) && (grs % 2 == 0)) {
            grs++;
        }
        String sig = sig_grs.substring(0, 24); // 隐藏位+23位
        if (grs > 4) {
            sig = oneAdder(sig);
        } else if (grs == 4 && sig.endsWith("1")) {
            sig = oneAdder(sig);
        }

        if (Integer.parseInt(sig.substring(0, sig.length() - 23), 2) > 1) {
            sig = rightShift(sig, 1);
            exp = oneAdder(exp).substring(1);
        }
        if (exp.equals("11111111")) {
            return sign == '0' ? IEEE754Float.P_INF : IEEE754Float.N_INF;
        }

        return sign + exp + sig.substring(sig.length() - 23);
    }

    /**
     * add one to the operand
     *
     * @param operand the operand
     * @return result after adding, the first position means overflow (not equal to the carry to the next)
     *         and the remains means the result
     */
    private String oneAdder(String operand) {
        int len = operand.length();
        StringBuilder temp = new StringBuilder(operand);
        temp.reverse();
        int[] num = new int[len];
        for (int i = 0; i < len; i++) num[i] = temp.charAt(i) - '0';  //先转化为反转后对应的int数组
        int bit = 0x0;
        int carry = 0x1;
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
            bit = num[i] ^ carry;
            carry = num[i] & carry;
            res[i] = (char) ('0' + bit);  //显示转化为char
        }
        String result = new StringBuffer(new String(res)).reverse().toString();
        return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;  //注意有进位不等于溢出，溢出要另外判断
    }

    private int valueOf(String src,int radix){
        int result = 0;
        for(int i =0;i<src.length();i++){
            int curNum = src.charAt(i) - '0';
            result = result * radix + curNum;
        }
        return result;
    }

    private int getBias(int eLength){
        return (int) ((Math.pow(2,eLength)-1)/2);
    }

    private String formalize(String src,int length){
        StringBuilder result = new StringBuilder(src);
        while (result.length()<length){
            result.insert(0,result.charAt(0));
        }
        if(result.length() > length){
            result = new StringBuilder(result.substring(result.length()-length));
        }
        return result.toString();
    }

    private StringBuilder adder(String src,String dest,int c,int length){
        String srcStr = formalize(src,length);
        String destStr = formalize(dest,length);

        int[] srcNum = new int[length];
        for(int i =0;i<srcStr.length();i++){
            srcNum[i] = srcStr.charAt(i)-'0';
        }
        int[] destNum = new int[length];
        for(int i =0;i<destStr.length();i++){
            destNum[i] = destStr.charAt(i)-'0';
        }
        char[] result = new char[length];

        int b = 0;
        for(int i =length-1;i>=0;i--){
            b = c ^ srcNum[i] ^ destNum[i];
            c = (c & srcNum[i]) | (c & destNum[i]) | (destNum[i] & srcNum[i]);
            result[i] = (char)(b+'0');
        }
        return new StringBuilder(String.valueOf(result));

    }

    private StringBuilder negation(StringBuilder src){
        StringBuilder result = new StringBuilder();
        for(int i =0;i<src.length();i++){
            if(src.charAt(i)=='0'){
                result.append('1');
            } else if (src.charAt(i) == '1') {
                result.append('0');
            }
        }
        return result;
    }

    private StringBuilder oneAdder(String src,int length){
        return adder(src,"01",0,length);
    }

    private StringBuilder suber(String src,String dest,int length){
        return adder(negation(new StringBuilder(src)).toString(),dest,1,length);
    }

    private StringBuilder moveLeft(StringBuilder src,int len){
        StringBuilder result = new StringBuilder(src);
        result.delete(0,len);
        for(int i =0;i<len;i++){
            result.append('0');
        }
        return result;
    }

    private boolean isInfinite(String src){
        return src.matches(IEEE754Float.P_INF) || src.matches(IEEE754Float.N_INF);
    }

    private boolean isZero(String src){
        return src.matches(IEEE754Float.N_ZERO) || src.matches(IEEE754Float.P_ZERO);
    }

    private int maxOf(int length){
        return (int) (Math.pow(2,length)-1);
    }



}
