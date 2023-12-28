package cpu.controller;

import cpu.alu.ALU;
import memory.Memory;
import util.DataType;
import util.Transformer;


public class Controller {
    // general purpose register
    char[][] GPR = new char[32][32];

    // program counter
    char[] PC = new char[32];

    // instruction register
    char[] IR = new char[32];

    // memory address register
    char[] MAR = new char[32];

    // memory buffer register
    char[] MBR =  new char[32];

    char[] ICC = new char[2];

    // 单例模式
    private static final Controller controller = new Controller();

    private Controller(){
        //规定第0个寄存器为zero寄存器
        GPR[0] = new char[]{'0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0'};
        ICC = new char[]{'0','0'}; // ICC初始化为00
    }

    public static Controller getController(){
        return controller;
    }

    public void reset(){
        PC = new char[32];
        IR = new char[32];
        MAR = new char[32];
        GPR[0] = new char[]{'0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0'};
        ICC = new char[]{'0','0'}; // ICC初始化为00
        interruptController.reset();
    }

    public InterruptController interruptController = new InterruptController();
    public ALU alu = new ALU();

    public void tick(){
        String icc = String.valueOf(ICC);
        if(icc.equals("00")){
            getInstruct();
            ICC = new char[]{'0','1'};
        }else if(icc.equals("01")){
            String opcode = getOpcode();
            if(opcode.equals("0111011")){
                findOperand();
            }

            ICC = new char[]{'1','0'};
            tick();
        }else if(icc.equals("10")){
            operate();
            if(this.interruptController.signal){
                ICC = new char[]{'1','1'};
                tick();
            }else{
                ICC = new char[]{'0','0'};
            }
        }else if(icc.equals("11")){
            interrupt();
            ICC = new char[]{'0','0'};
        }
    }

    /** 执行取指操作 */
    private void getInstruct(){
        MAR = PC;

        MBR = readFromMemory(MAR);

        IR = MBR;

        PC = alu.add(new DataType(String.valueOf(PC)),new DataType(Transformer.intToBinary(String.valueOf(4)))).toString().toCharArray();
        return;

    }

    char[] readFromMemory(char[] pAddr){
        StringBuilder content = new StringBuilder();
        byte[] readContent = Memory.getMemory().read(String.valueOf(pAddr),4);
        for(int i =0;i<readContent.length;i++){
            String temp = Transformer.intToBinary(String.valueOf(readContent[i])).substring(32-8);
            content.append(temp);
        }
        return content.toString().toCharArray();
    }

    /** 执行间址操作 */
    private void findOperand(){
        int rs2 = getIrPartNum(20,25);
        MAR = GPR[rs2];

        MBR = readFromMemory(MAR);

        GPR[rs2] = MBR;
    }

    /** 执行周期 */
    private void operate(){
        //`add`,`addi`,`lw`,`lui`,`jalr`,`ecall` 'addc'
        String opcode = getOpcode();
        switch (opcode) {
            case "0110011":
            case "0111011":
                add();
                break;
            case "0010011":
                addi();
                break;
            case "0000011":
                lw();
                break;
            case "0110111":
                lui();
                break;
            case "1100111":
                jalr();
                break;
            case "1110011":
                ecall();
                break;
        }

    }

    /** 执行中断操作 */
    private void interrupt(){
        this.interruptController.handleInterrupt();;
        this.interruptController.signal=false;
    }

    private void add(){
        int rd = getIrPartNum(7,12);
        int r1 = getIrPartNum(15,20);
        int r2 = getIrPartNum(20,25);

        String rs1 = String.valueOf(GPR[r1]);
        String rs2 = String.valueOf(GPR[r2]);
        String result = alu.add(new DataType(rs1),new DataType(rs2)).toString();
        GPR[rd] = result.toCharArray();
    }

    private void addi(){
        int rd = getIrPartNum(7,12);
        int r1 = getIrPartNum(15,20);
        String imm = getIRPart(20,32);

        StringBuilder immStr = new StringBuilder(imm);
        while (immStr.length()<32){
            immStr.insert(0,immStr.charAt(0));
        }
        String rs1 =String.valueOf(GPR[r1]);

        String result = alu.add(new DataType(rs1),new DataType(immStr.toString())).toString();
        GPR[rd] = result.toCharArray();

    }

    private void lw(){
        int rd = getIrPartNum(7,12);
        int r1 = getIrPartNum(15,20);
        String imm = getIRPart(20,32);
        StringBuilder immStr = new StringBuilder(imm);
        while (immStr.length()<32){
            immStr.insert(0,immStr.charAt(0));
        }
        String rs1 =String.valueOf(GPR[r1]);

        String result = alu.add(new DataType(rs1),new DataType(immStr.toString())).toString();

        GPR[rd] = readFromMemory(result.toCharArray());
    }

    private void lui(){
        int rd = getIrPartNum(7,12);
        String imm = getIRPart(12,32);
        StringBuilder immStr = new StringBuilder(imm);
        for(int i =0;i<12;i++){
            immStr.append('0');
        }
        GPR[rd] = immStr.toString().toCharArray();
    }

    private void jalr(){
        int rd = getIrPartNum(7,12);
        int r1 = getIrPartNum(15,20);
        String imm = getIRPart(20,32);

        GPR[rd] = PC;
        GPR[1] = PC;

        StringBuilder immStr = new StringBuilder(imm);
        while (immStr.length()<32){
            immStr.insert(0,immStr.charAt(0));
        }
        String rs1 =String.valueOf(GPR[r1]);

        String result = alu.add(new DataType(rs1),new DataType(immStr.toString())).toString();

        PC = result.toCharArray();
    }

    private void ecall(){
        this.interruptController.signal = true;
    }

    private String getIRPart(int start, int end){
        return String.valueOf(IR).substring(start,end);
    }

    private String getOpcode(){
        return new StringBuilder(String.valueOf(IR).substring(0,7)).reverse().toString();
    }

    private int getIrPartNum(int start, int end){
        return Integer.parseInt(Transformer.binaryToInt("0" + getIRPart(start,end)));
    }

    public class InterruptController{
        // 中断信号：是否发生中断
        public boolean signal;
        public StringBuffer console = new StringBuffer();
        /** 处理中断 */
        public void handleInterrupt(){
            console.append("ecall ");
        }
        public void reset(){
            signal = false;
            console = new StringBuffer();
        }
    }

    // 以下一系列的get方法用于检查寄存器中的内容进行测试，请勿修改

    // 假定代码程序存储在主存起始位置，忽略系统程序空间
    public void loadPC(){
        PC = GPR[0];
    }

    public char[] getRA() {
        //规定第1个寄存器为返回地址寄存器
        return GPR[1];
    }

    public char[] getGPR(int i) {
        return GPR[i];
    }
}
