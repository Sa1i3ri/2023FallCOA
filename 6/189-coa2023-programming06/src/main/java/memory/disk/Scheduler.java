package memory.disk;


import java.util.Arrays;

public class Scheduler {

    /**
     * 先来先服务算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double FCFS(int start, int[] request) {
        int result = 0;
        for(int i =0;i<request.length;i++){
            result += Math.abs(start-request[i]);
            start = request[i];
        }
        return (double) result/request.length;
    }

    /**
     * 最短寻道时间优先算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double SSTF(int start, int[] request) {
        int result = 0;
        for(int i =0;i<request.length;i++){
            int curIndex = findNearest(start,request);
            result += Math.abs(start - request[curIndex]);
            start = request[curIndex];

            request[curIndex] = -1;
        }


        return (double)result/request.length ;
    }

    private int findNearest(int index,int[] request){
        int min_index = 0;
        for(int i=0;i<request.length;i++){
            if(Math.abs(index - request[i]) < Math.abs(index - request[min_index]) && request[i]!=-1){
                min_index = i;
            }
        }
        return min_index;
    }

    /**
     * 扫描算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double SCAN(int start, int[] request, boolean direction) {
        int result = 0;
        Arrays.sort(request);
        if(direction){
            if(start <= request[0]){
                result = request[request.length-1]-start;
            }else{
                result = (255 - start) + (255-request[0]);
            }
        }else{
            if(start >= request[request.length-1]){
                result = start - request[0];
            }else{
                result = start + request[request.length-1];
            }
        }

        return (double) result /request.length;
    }

    /**
     * C-SCAN算法：默认磁头向磁道号增大方向移动
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CSCAN(int start,int[] request){
        int result = 0;
        Arrays.sort(request);

        if(start <= request[0]){
            result = request[request.length-1]-start;
        }else{
            int curIndex=0;
            for(int i =0;i<request.length-1;i++){
                if(request[i]<start && request[i+1]>=start){
                    curIndex = i;
                    break;
                }
            }
            result = 255-start+ 255 + request[curIndex];
        }


        return (double) result /request.length;
    }

    /**
     * LOOK算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double LOOK(int start,int[] request,boolean direction){
        int result = 0;
        Arrays.sort(request);
        if(direction){
            if(start <= request[0]){
                result = request[request.length-1]-start;
            }else{
                result = (request[request.length-1] - start) + (request[request.length-1]-request[0]);
            }
        }else{
            if(start >= request[request.length-1]){
                result = start - request[0];
            }else{
                result = start-request[0] + request[request.length-1]-request[0];
            }
        }

        return (double) result /request.length;
    }

    /**
     * C-LOOK算法：默认磁头向磁道号增大方向移动
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CLOOK(int start,int[] request){
        int result = 0;
        Arrays.sort(request);

        if(start <= request[0]){
            result = request[request.length-1]-start;
        }else{
            int curIndex=0;
            for(int i =0;i<request.length-1;i++){
                if(request[i]<start && request[i+1]>=start){
                    curIndex = i;
                    break;
                }
            }
            result = request[request.length-1] -start + (request[request.length-1]-request[0]) + request[curIndex] - request[0];
        }


        return (double) result /request.length;
    }

}
