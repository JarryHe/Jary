package com.jary.datastructure;

/**
 * Created by Jary on 2016/3/31.
 */
public class UnionFind {
    private Integer count = 0;
    private int[] id = null;
    private int[] sz = null;
    public UnionFind(int n) {
        count = n;
        id = new int[n];
        sz = new int[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
            sz[i] = 1;
        }
    }

    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);
        if (i == j) {
            return;
        }
        if (sz[i] < sz[j]) { id[i] = j; sz[j] += sz[i]; }
        else { id[j] = i; sz[i] += sz[j]; }
        count--;
    }

    public int find(int p) {
        while (p != id[p]) {
            // 将p节点的父节点设置为它的爷爷节点
            id[p] = id[id[p]];
            p = id[p];
        }
        return p;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public int count() {
        return count;
    }
}
