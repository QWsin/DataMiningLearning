package cn.qwsin.common;

import java.util.HashMap;
import java.util.Map;

public class Count {
    public static <T> Map<T,Integer> mapCount(T[] a){
        Map<T,Integer> count = new HashMap<>();
        for (T t : a) {
            if (count.containsKey(t)) {
                int k = count.get(t);
                count.put(t, k + 1);
            } else {
                count.put(t, 1);
            }
        }
        return count;
    }

    public static Map<Integer,Integer> mapCount(int[] a){
        Map<Integer,Integer> count = new HashMap<>();
        for (int t : a) {
            if (count.containsKey(t)) {
                int k = count.get(t);
                count.put(t, k + 1);
            } else {
                count.put(t, 1);
            }
        }
        return count;
    }

    public static Map<Double,Integer> mapCount(double[] a){
        Map<Double,Integer> count = new HashMap<>();
        for (double t : a) {
            if (count.containsKey(t)) {
                int k = count.get(t);
                count.put(t, k + 1);
            } else {
                count.put(t, 1);
            }
        }
        return count;
    }
}
