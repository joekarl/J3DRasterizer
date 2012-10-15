/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public class FastMath {
    public static int ceilToInt(float f) {
        if (f > 0) {
            return (int) f + 1;
        } else {
            return (int) f;
        }
    }
    
}
