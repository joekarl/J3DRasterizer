/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public final class Vector3D {

    public float x, y, z;

    public Vector3D() {
        this(0, 0, 0);
    }

    public Vector3D(Vector3D v) {
        this(v.x, v.y, v.z);
    }

    public Vector3D(float x, float y, float z) {
        setTo(x, y, z);
    }

    public Vector3D setTo(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3D setTo(Vector3D v) {
        setTo(v.x, v.y, v.z);
        return this;
    }

    public Vector3D setToCrossProduct(Vector3D u, Vector3D v) {
        float Xx = u.y * v.z - u.z * v.y;
        float Xy = u.z * v.x - u.x * v.z;
        float Xz = u.x * v.y - u.y * v.x;
        x = Xx;
        y = Xy;
        z = Xz;
        return this;
    }

    public Vector3D add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3D add(Vector3D v) {
        add(v.x, v.y, v.z);
        return this;
    }

    public Vector3D subtract(float x, float y, float z) {
        add(-x, -y, -z);
        return this;
    }

    public Vector3D subtract(Vector3D v) {
        subtract(v.x, v.y, v.z);
        return this;
    }

    public Vector3D multiply(float m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public Vector3D divide(float m) {
        this.x /= m;
        this.y /= m;
        this.z /= m;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D normalize() {
        divide(length());
        return this;
    }

    public Vector3D rotateAroundX(float cosAngle, float sinAngle) {
        float newY = (y * cosAngle) - (z * sinAngle);
        float newZ = (y * sinAngle) + (z * cosAngle);
        y = newY;
        z = newZ;
        return this;
    }

    public Vector3D rotateAroundY(float cosAngle, float sinAngle) {
        float newZ = (z * cosAngle) - (x * sinAngle);
        float newX = (z * sinAngle) + (x * cosAngle);
        x = newX;
        z = newZ;
        return this;
    }

    public Vector3D rotateAroundZ(float cosAngle, float sinAngle) {
        float newX = (x * cosAngle) - (y * sinAngle);
        float newY = (x * sinAngle) + (y * cosAngle);
        y = newY;
        x = newX;
        return this;
    }

    public Vector3D addRotation(Transform3D xform) {
        rotateAroundX(xform.getCosAngleX(), xform.getSinAngleX());
        rotateAroundZ(xform.getCosAngleZ(), xform.getSinAngleZ());
        rotateAroundY(xform.getCosAngleY(), xform.getSinAngleY());
        return this;
    }

    public Vector3D subtractRotation(Transform3D xform) {
        rotateAroundY(xform.getCosAngleY(), -xform.getSinAngleY());
        rotateAroundZ(xform.getCosAngleZ(), -xform.getSinAngleZ());
        rotateAroundX(xform.getCosAngleX(), -xform.getSinAngleX());
        return this;
    }

    public Vector3D addTransform(Transform3D xform) {
        //rotate
        addRotation(xform);
        //translate
        add(xform.getLocation());
        return this;
    }

    public Vector3D subtractTransform(Transform3D xform) {
        //rotate
        subtractRotation(xform);
        //translate
        subtract(xform.getLocation());
        return this;
    }

    public boolean equals(float x, float y, float z) {
        return this.x == x && this.y == y && this.z == z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector3D) {
            Vector3D v = (Vector3D) o;
            return equals(v.x, v.y, v.z);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Float.floatToIntBits(this.x);
        hash = 13 * hash + Float.floatToIntBits(this.y);
        hash = 13 * hash + Float.floatToIntBits(this.z);
        return hash;
    }

    @Override
    public String toString() {
        return "Vector3D{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    public float getDotProduct(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public static void lerp(Vector3D v1, Vector3D v2, float lerp, Vector3D destination) {
        destination.setTo((v2.x - v1.x) * lerp + v1.x, 
                (v2.y - v1.y) * lerp + v1.y,  
                (v2.z - v1.z) * lerp + v1.z);
    }
}
