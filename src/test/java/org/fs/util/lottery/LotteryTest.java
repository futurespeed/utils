package org.fs.util.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LotteryTest {
    public static void main(String[] args) {
//        lottery(true);
        performance();
    }

    private static void lottery(boolean mustWin) {
        String activityId = "";
        String awardId = null;
        int step = getStep();
        LotteryContext lotteryContext = new LotteryContext();
        lotteryContext.setItems(getItems(step));
        LotteryManager lotteryManager = new LotteryManager(lotteryContext);

        if (mustWin) {
            // 获取库存（奖品1）
            long stock = getStock(activityId, "gift1", step);

            // 奖品1无的情况下，指定结果为奖品2
            if (stock <= 0) {
                awardId = "gift2";
                System.out.println("[info] award1 is empty, use award2");// FIXME
            } else {
                // 抽奖
                awardId = lotteryManager.lottery().getItemCode();
            }
        } else {
            awardId = lotteryManager.lottery().getItemCode();
        }

        if (null == awardId) {
            System.out.println("award: " + null + ", result: " + false);
            return;
        }

        // 扣减库存
        boolean deductSuccess = deductStock(activityId, awardId, step);

        if (mustWin && (!deductSuccess) && "gift1".equals(awardId)) {
            System.out.println("[info] award1 deduct stock fail, use award2");// FIXME
            awardId = "gift2";
            deductSuccess = deductStock(activityId, awardId, step);
        }

        System.out.println("award: " + awardId + ", result: " + deductSuccess);
    }

    private static List<LotteryItem> getItems(int step) {
        // FIXME test
        List<LotteryItem> items = new ArrayList<>();
        LotteryItem item1 = new LotteryItem();
        item1.setCode("gift1");
        item1.setProbability(3.00);
        items.add(item1);
        LotteryItem item2 = new LotteryItem();
        item2.setCode("gift2");
        item2.setProbability(97.00);
        items.add(item2);
        return items;
    }

    private static int getStep() {
        // TODO 根据时间获取当前阶段
        return 2;
    }

    private static long getStock(final String activityId, final String awardId, final int step) {
        int tmpStep = step;
        while (tmpStep > 0) {
            long stockNum = redisGetStock(activityId, awardId, step);
            if (stockNum > 0) {
                return stockNum;
            }
            tmpStep--;
        }
        return -1;
    }

    private static boolean deductStock(final String activityId, final String awardId, final int step) {
        int tmpStep = step;
        while (tmpStep > 0) {
            boolean deductSuccess = redisDeductStock(activityId, awardId, step);
            if (deductSuccess) {
                return true;
            }
            tmpStep--;
        }
        return false;
    }

    private static long redisGetStock(String activityId, String awardId, int step) {
        String key = "MB8.ACT:" + activityId + ".AWD:" + awardId + ".STEP:" + step;
        // TODO
        return 1;
    }

    private static boolean redisDeductStock(String activityId, String awardId, int step) {
        String key = "MB8.ACT:" + activityId + ".AWD:" + awardId + ".STEP:" + step;
        // TODO
        return false;
    }

    private static void performance() {
        long start = System.currentTimeMillis();
        Map<String, Integer> map = new HashMap<>();
        LotteryManager lotteryManager = new LotteryManager(getContext());
        for (int i = 0; i < 1000000; i++) {
            LotteryResult lotteryResult = lotteryManager.lottery();
            Integer num = map.get(lotteryResult.getItemCode());
            if (null == num) {
                num = 0;
            }
            map.put(lotteryResult.getItemCode(), num + 1);
        }
        long end = System.currentTimeMillis();
        System.out.println("used time: " + (end - start));
        System.out.println(map);
    }

    private static LotteryContext getContext() {
        LotteryContext lotteryContext = new LotteryContext();
        List<LotteryItem> items = new ArrayList<>();
        LotteryItem item1 = new LotteryItem();
        item1.setCode("gift1");
        item1.setProbability(30.00);
        items.add(item1);
        LotteryItem item2 = new LotteryItem();
        item2.setCode("gift2");
        item2.setProbability(20.01);
        items.add(item2);
        LotteryItem item3 = new LotteryItem();
        item3.setCode("gift3");
        item3.setProbability(49.99);
        items.add(item3);

        lotteryContext.setItems(items);
        return lotteryContext;
    }
}
