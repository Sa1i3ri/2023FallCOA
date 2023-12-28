package memory.cache.cacheReplacementStrategy;

import memory.cache.Cache;


/**
 * TODO 最近最少用算法
 */
public class LRUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache.getCache().setTimeStamp(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {

        int rowNo = start;

        for(int i=start;i<end;i++){
            if(Cache.getCache().getTimeStamp(i) > Cache.getCache().getTimeStamp(rowNo)){
                rowNo = i;
            }
            if(Cache.getCache().isValid(i) == false){
                rowNo = i;
                break;
            }
        }
        Cache.getCache().update(rowNo,addrTag,input);
        return rowNo;
    }

}





























