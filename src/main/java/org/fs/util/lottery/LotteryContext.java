package org.fs.util.lottery;

import java.util.List;

public class LotteryContext {
    private List<LotteryItem> items;

    private long totalRange = 10000;

    public List<LotteryItem> getItems() {
        return items;
    }

    public void setItems(List<LotteryItem> items) {
        this.items = items;
    }

    public long getTotalRange() {
        return totalRange;
    }

    public void setTotalRange(long totalRange) {
        this.totalRange = totalRange;
    }
}
