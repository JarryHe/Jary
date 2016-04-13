package com.jary.test;

import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * Created by Jary on 2016/4/8.
 */
public class MainTest {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {

            System.out.println(Objects.hash(i));
            System.out.println(Objects.hashCode(i));
            System.out.println(com.google.common.base.Objects.hashCode(i));
        }
    }
}
