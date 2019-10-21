package org.fs.util.lottery;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LotteryManager {

    private LotteryContext lotteryContext;

    private static final int TOTAL_RANGE = 10000;

    private static final int PROBABILITY_SCALE = 100;

    public LotteryManager(LotteryContext lotteryContext) {
        this.lotteryContext = lotteryContext;
        init();
    }

    protected void init() {
        int range = 0;
        for (LotteryItem item : lotteryContext.getItems()) {
            item.setRangeStart(range);
            range = range + (int) (item.getProbability() * PROBABILITY_SCALE);
            if (range > TOTAL_RANGE) {
                throw new RuntimeException("Over Range");
            }
            item.setRangeEnd(range);
        }
    }

    public LotteryResult lottery() {
        LotteryResult lotteryResult = new LotteryResult();
        Random random = ThreadLocalRandom.current();
        int range = random.nextInt(TOTAL_RANGE);

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
