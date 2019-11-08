package org.fs.util.lottery;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LotteryManager {

    private LotteryContext lotteryContext;

    public LotteryManager(LotteryContext lotteryContext) {
        this.lotteryContext = lotteryContext;
        init();
    }

    protected void init() {
        long range = 0;
        for (LotteryItem item : lotteryContext.getItems()) {
            item.setRangeStart(range);
            range = range + (long) (item.getProbability() * lotteryContext.getTotalRange());
            if (range > lotteryContext.getTotalRange()) {
                throw new RuntimeException("Over Range");
            }
            item.setRangeEnd(range);
        }
    }

    public LotteryResult lottery() {
        LotteryResult lotteryResult = new LotteryResult();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long range = random.nextLong(lotteryContext.getTotalRange());

        for (LotteryItem item : lotteryContext.getItems()) {
            if (range >= item.getRangeStart() && range < item.getRangeEnd()) {
                lotteryResult.setSuccess(true);
                lotteryResult.setItemCode(item.getCode());
                break;
            }
        }
        return lotteryResult;
    }
}
