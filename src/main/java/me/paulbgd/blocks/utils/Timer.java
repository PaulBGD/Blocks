/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

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