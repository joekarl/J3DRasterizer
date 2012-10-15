/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.util.Arrays;

/**
 *
 * @author Karl Kirch
 */
public final class Color4f {

    private float[] rgba = new float[4];

    public Color4f(float r, float g, float b, float a) {
        rgba[0] = r;
        rgba[1] = g;
        rgba[2] = b;
        rgba[3] = a;
    }

    public Color4f(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color4f() {
        this(1, 1, 1);
    }

    public Color4f(Color4f c) {
        this();
        setTo(c);
    }

    public Color4f setTo(Color4f c) {
        rgba[0] = c.getR();
        rgba[1] = c.getG();
        rgba[2] = c.getB();
        rgba[3] = c.getA();
        return this;
    }

    public Color4f setTo(float r, float g, float b) {
        return setTo(r, g, b, 1);
    }

    public Color4f setTo(float r, float g, float b, float a) {
        rgba[0] = r;
        rgba[1] = g;
        rgba[2] = b;
        rgba[3] = a;
        return this;
    }

    public float getR() {
        return rgba[0];
    }

    public float getG() {
        return rgba[1];
    }

    public float getB() {
        return rgba[2];
    }

    public float getA() {
        return rgba[3];
    }

    public void clamp() {
        for (int i = 0; i < 4; i++) {
            if (rgba[i] > 255) {
                rgba[i] = 255;
            } else if (rgba[i] < 0) {
                rgba[i] = 0;
            }
        }
    }

    public static void lerp(Color4f c1, Color4f c2, float lerp, Color4f destination) {
        destination.setTo(
                (c2.getR() - c1.getR()) * lerp + c1.getR(),
                (c2.getG() - c1.getG()) * lerp + c1.getG(),
                (c2.getB() - c1.getB()) * lerp + c1.getB(),
                (c2.getA() - c1.getA()) * lerp + c1.getA());
        destination.clamp();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.rgba);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Color4f other = (Color4f) obj;
        if (!Arrays.equals(this.rgba, other.rgba)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Color4i{" + "rgba=[ " + rgba[0] + ", "
                + rgba[1] + ", "
                + rgba[2] + ", "
                + rgba[3] + "] " + '}';
    }
}
