package me.paulbgd.blocks.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Timer {

    public final List<Long> right = new ArrayList<>();
    private final long start = new Date().getTime();
    private final List<String> left = new ArrayList<>();

    public void time(String message) {
        left.add(message);
        right.add(new Date().getTime());
    }

    public void time() {
        long time = new Date().getTime();
        left.add(Long.toString(time));
        right.add(time);
    }

    public void print() {
        long total = 0l;
        for (int i = 0; i < left.size(); i++) {
            String message = left.get(i);
            long time = right.get(i) - start;
            total += time;
            if (message.equals(Long.toString(time))) {
                message = "Step #" + i;
            }
            int seconds = (int) time / 1000;
            System.out.println(String.format("Took %ss (%sms) to execute %s.", seconds, time, message));
        }
        int seconds = (int) total / 1000;
        System.out.println(String.format("Took %ss (%sms) to fully execute.", seconds, total));
    }

}