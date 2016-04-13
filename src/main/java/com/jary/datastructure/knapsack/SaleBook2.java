package com.jary.datastructure.knapsack;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jary on 2016/4/13.
 *  有一书店引进了一套书，共有3卷，每卷书定价是60元，书店为了搞促销，推出一个活动，活动如下：
 * 如果单独购买其中一卷，那么可以打9.5折。
 * 如果同时购买两卷不同的，那么可以打9折。
 * 如果同时购买三卷不同的，那么可以打8.5折。
 * 如果小明希望购买第1卷x本，第2卷y本，第3卷z本，那么至少需要多少钱呢？（x、y、z为三个已知整数）。
 *
 * SaleBook中有大量递归，可是这些递归中大部分已经算过，如何优化？
 * 可以用数组等数据结构来存储，减少运算量。
 * SaleBook userTime：841ms， SaleBook2 userTime：30ms
 */
public class SaleBook2 {

    private static BigDecimal max = BigDecimal.valueOf(99999);
    private Map<String, BigDecimal> map = new HashMap<>();

    public static void main(String[] args) {
        SaleBook2 sb = new SaleBook2();
        long start = System.currentTimeMillis();
        BigDecimal money = sb.minMoney(2, 3, 2);
        long end = System.currentTimeMillis();
        System.out.println(money + " user time(ms):"+ (end-start));
    }

    public BigDecimal minMoney(int x, int y, int z) {
        BigDecimal value = map.get(x + "-" + y + "-" + z);
        if (value != null) {
            return value;
        }
        if (x == 0 && y == 0 && z == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal s1 = x > 0 ? BigDecimal.valueOf(60 * 0.95).add(minMoney(x - 1, y, z)) : max;
        BigDecimal s2 = y > 0 ? BigDecimal.valueOf(60 * 0.95).add(minMoney(x, y - 1, z)) : max;
        BigDecimal s3 = z > 0 ? BigDecimal.valueOf(60 * 0.95).add(minMoney(x, y, z - 1)) : max;
        BigDecimal s4 = x > 0 && y > 0 ? BigDecimal.valueOf((60 + 60) * 0.9).add(minMoney(x - 1, y - 1, z)) : max;
        BigDecimal s5 = z > 0 && y > 0 ? BigDecimal.valueOf((60 + 60) * 0.9).add(minMoney(x, y - 1, z - 1)) : max;
        BigDecimal s6 = x > 0 && z > 0 ? BigDecimal.valueOf((60 + 60) * 0.9).add(minMoney(x - 1, y, z - 1)) : max;
        BigDecimal s7 = x > 0 && y > 0 && z > 0 ? BigDecimal.valueOf((60 + 60 + 60) * 0.85).add(minMoney(x - 1, y - 1, z - 1)) : max;
        System.out.println("xyz:" + x + "," + y + "," + z);
        System.out.println("s:" + s1 + "," + s2 + "," + s3 + "," + s4 + "," + s5 + "," + s6 + "," + s7);
        BigDecimal min = min(s1, s2, s3, s4, s5, s6, s7);
        map.put(x + "-" + y + "-" + z, min);
        return min;
    }

    public BigDecimal min(BigDecimal... s) {
        BigDecimal min = s[0];
        for (BigDecimal v : s) {
            if (v.compareTo(min) == -1) {
                min = v;
            }
        }
        return min;
    }
}
