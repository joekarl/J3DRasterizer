/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author Karl Kirch
 */
public final class Color4f {

    public float r, g, b, a;

    public Color4f(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color4f(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color4f() {
        this(1, 1, 1);
    }

    public Color4f(Color4f c) {
        this();
        setTo(c.r, c.g, c.b, c.a);
    }

    public Color4f setTo(float r, float g, float b) {
        return setTo(r, g, b, 1);
    }

    public Color4f setTo(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public void clamp() {
        if (r > 255f) {
            r = 255f;
        } else if (r < 0f) {
            r = 0f;
        }
        if (g > 255f) {
            g = 255f;
        } else if (g < 0f) {
            g = 0f;
        }
        if (b > 255f) {
            b = 255f;
        } else if (b < 0f) {
            b = 0f;
        }
        if (a > 255f) {
            a = 255f;
        } else if (a < 0f) {
            a = 0f;
        }
    }

    public static void lerp(Color4f c1, Color4f c2, float lerp, Color4f destination) {
        destination.setTo(
                (c2.r - c1.r) * lerp + c1.r,
                (c2.g - c1.g) * lerp + c1.g,
                (c2.b - c1.b) * lerp + c1.b,
                (c2.a - c1.a) * lerp + c1.a);

        destination.clamp();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Float.floatToIntBits(this.r);
        hash = 61 * hash + Float.floatToIntBits(this.g);
        hash = 61 * hash + Float.floatToIntBits(this.b);
        hash = 61 * hash + Float.floatToIntBits(this.a);
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
        if (Float.floatToIntBits(this.r) != Float.floatToIntBits(other.r)) {
            return false;
        }
        if (Float.floatToIntBits(this.g) != Float.floatToIntBits(other.g)) {
            return false;
        }
        if (Float.floatToIntBits(this.b) != Float.floatToIntBits(other.b)) {
            return false;
        }
        if (Float.floatToIntBits(this.a) != Float.floatToIntBits(other.a)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Color4i{" + "rgba=[ " + r + ", "
                + g + ", "
                + b + ", "
                + a + "] " + '}';
    }
}
