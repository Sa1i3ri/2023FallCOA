package memory.cache.cacheReplacementStrategy;


import memory.cache.Cache;

/**
 * TODO 先进先出算法
 */
public class FIFOReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        //Cache.getCache().setTimeStampFIFO(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        //先检查有没有还没用的行
        for(int i=start;i<end;i++){
            if(Cache.getCache().isValid(i) == false){
                Cache.getCache().update(i,addrTag,input);
                Cache.getCache().setTimeStampFIFO(i);
                return i;
            }
        }

        for(int i=start;i<end;i++){
            if(Cache.getCache().getTimeStamp(i)==0L){
                Cache.getCache().update(i,addrTag,input);
                Cache.getCache().setTimeStampFIFO(i);
                return i;
            }
        }

        return -1;

    }

}
