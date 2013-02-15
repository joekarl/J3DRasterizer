/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

/**
 *
 * @author Karl Kirch
 */
public final class Color4f {

    private static Unsafe getUnsafe() {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Color4f.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Color4f.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Color4f.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Color4f.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private static final Unsafe unsafe = getUnsafe();
    protected float[] rgba = new float[4];
    private final long FLOAT_SIZE = 4L;
    private final long FLOAT_ARRAY_OFFSET = unsafe.arrayBaseOffset(float[].class);

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
        return setTo(
                unsafe.getFloat(c.rgba, FLOAT_ARRAY_OFFSET),
                unsafe.getFloat(c.rgba, 1L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET),
                unsafe.getFloat(c.rgba, 2L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET),
                unsafe.getFloat(c.rgba, 3L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET));
        //return setTo(c.getR(),c.getG(),c.getB(),c.getA());
    }

    public Color4f setTo(float r, float g, float b) {
        return setTo(r, g, b, 1);
    }

    public Color4f setTo(float r, float g, float b, float a) {
        unsafe.putFloat(rgba, FLOAT_ARRAY_OFFSET, r);
        unsafe.putFloat(rgba, 1L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET, g);
        unsafe.putFloat(rgba, 2L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET, b);
        unsafe.putFloat(rgba, 3L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET, a);
        return this;
    }

    public float getR() {
        return unsafe.getFloat(rgba, FLOAT_ARRAY_OFFSET);
    }

    public float getG() {
        return unsafe.getFloat(rgba, 1L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET);
    }

    public float getB() {
        return unsafe.getFloat(rgba, 2L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET);
    }

    public float getA() {
        return unsafe.getFloat(rgba, 3L * FLOAT_SIZE + FLOAT_ARRAY_OFFSET);
    }

    public void clamp() {
        for (long i = 0L; i < 4L; i++) {
            float x = unsafe.getFloat(rgba, i);
            if (x > 255f) {
                unsafe.putFloat(rgba, i * FLOAT_SIZE + FLOAT_ARRAY_OFFSET, 255f);
            } else if (x < 0f) {
                unsafe.putFloat(rgba, i * FLOAT_SIZE + FLOAT_ARRAY_OFFSET, 0f);
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
