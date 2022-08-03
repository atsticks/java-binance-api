/*
 * MIT License
 *
 * Copyright (c) 2017 Web Cerebrium
 * Copyright (c) 2021 Anatole Tresch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.webcerebrium.binance.api;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@ToString
@Slf4j
public final class RateLimiter {

    @Getter
    private com.google.common.util.concurrent.RateLimiter limiter;
    private AtomicInteger accessCount = new AtomicInteger();

    @Getter
    private long ratePeriod;


    private static Timer resetTimer = new Timer("RateLimiters", true);

    public RateLimiter(int accessLimit, TimeUnit timeUnit, int timeUnits) {
        this.ratePeriod = timeUnit.toMillis(timeUnits);
        // normalize to accesses / second
        long seconds = timeUnit.toSeconds(timeUnits);
        accessLimit = (int)(accessLimit / seconds);
        limiter = com.google.common.util.concurrent.RateLimiter.create(accessLimit);
        resetTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resetLimiter();
            }
        }, ratePeriod, ratePeriod);
    }

    public int getAccessCount(){
        return accessCount.get();
    }

    private void resetLimiter(){
        log.trace("Resetting rate limiter from {} tokens counted in {} ms...", accessCount.get(), ratePeriod);
        accessCount.set(0);
    }

    public boolean tryAcquire() throws InterruptedException {
        return tryAcquire(1);
    }

    public int acquire(int count) throws InterruptedException {
        limiter.acquire(count);
        return accessCount.addAndGet(count);
    }

    public int acquire() throws InterruptedException {
       return acquire(1);
    }

    public boolean tryAcquire(int count) throws InterruptedException {
        if(limiter.tryAcquire(count)) {
            accessCount.addAndGet(count);
            return true;
        }
        return false;
    }

    public int release() {
       return release(1);
    }

    public int release(int permits) {
        return accessCount.addAndGet(-permits);
    }

}
