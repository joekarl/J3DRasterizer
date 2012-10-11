/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public class TimeCounter {

    private long startTime, currentTime;
    private double countsPerSecond;
    private int count;

    public double getCountsPerSecond() {
        return countsPerSecond;
    }

    public void tick() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        currentTime = System.currentTimeMillis();
        count++;
        if (currentTime - startTime >= 500) {
            countsPerSecond = count << 1;
            startTime = 0;
            currentTime = 0;
            count = 0;
        }
    }
}