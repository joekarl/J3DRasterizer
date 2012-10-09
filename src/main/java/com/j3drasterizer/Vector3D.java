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
        setTo(x,y,z);
    }
    
    public void setTo(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void setTo(Vector3D v) {
        setTo(v.x, v.y, v.z);
    }
    
    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }
    
    public void add (Vector3D v) {
        add(v.x, v.y, v.z);
    }
    
    public void subtract(float x, float y, float z) {
        add(-x, -y, -z);
    }
    
    public void subtract(Vector3D v) {
        subtract(v.x, v.y, v.z);
    }
    
    public void multiply(float m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
    }
    
    public void divide(float m) {
        this.x /= m;
        this.y /= m;
        this.z /= m;
    }
    
    public float length() {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }
    
    public void normalize() {
        divide(length());
    }
    
    public void rotateAroundX(float cosAngle, float sinAngle) {
        float newY = (y * cosAngle) - (z * sinAngle);
        float newZ = (y * sinAngle) + (z * cosAngle);
        y = newY;
        z = newZ;
    }
    
    public void rotateAroundY(float cosAngle, float sinAngle) {
        float newZ = (z * cosAngle) - (x * sinAngle);
        float newX = (z * sinAngle) + (x * cosAngle);
        x = newX;
        z = newZ;
    }
    
    public void rotateAroundZ(float cosAngle, float sinAngle) {
        float newX = (x * cosAngle) - (y * sinAngle);
        float newY = (x * sinAngle) + (y * cosAngle);
        y = newY;
        x = newX;
    }
    
    public void addRotation(Transform3D xform) {
        rotateAroundX(xform.getCosAngleX(), xform.getSinAngleX());
        rotateAroundZ(xform.getCosAngleZ(), xform.getSinAngleZ());
        rotateAroundY(xform.getCosAngleY(), xform.getSinAngleY());
    }
    
    public void subtractRotation(Transform3D xform) {
        rotateAroundY(xform.getCosAngleY(), -xform.getSinAngleY());
        rotateAroundZ(xform.getCosAngleZ(), -xform.getSinAngleZ());
        rotateAroundX(xform.getCosAngleX(), -xform.getSinAngleX());
    }
    
    public void addTransform(Transform3D xform) {
        //rotate
        addRotation(xform);
        //translate
        add(xform.getLocation());
    }
    
    public void subtractTransform(Transform3D xform) {
        //rotate
        subtractRotation(xform);
        //translate
        subtract(xform.getLocation());
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

}
