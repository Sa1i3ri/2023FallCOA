package memory.cache.cacheReplacementStrategy;


import memory.cache.Cache;

/**
 * TODO 最近不经常使用算法
 */
public class LFUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache.getCache().addVisited(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        for(int i =start;i<end;i++){
            if(Cache.getCache().isValid(i)==false){
                Cache.getCache().update(i,addrTag,input);
                return i;
            }
        }

        int min = Cache.getCache().getVisited(start);
        int rowN0 = start;

        for(int i =start;i<end;i++){
            if(Cache.getCache().getVisited(i) < min){
                rowN0 = i;
            }
        }

        Cache.getCache().update(rowN0,addrTag,input);

        return rowN0;
    }

}
