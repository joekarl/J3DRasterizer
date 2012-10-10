/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public final class Transform3D {

    private Vector3D location;
    private float cosAngleX,
            sinAngleX,
            cosAngleY,
            sinAngleY,
            cosAngleZ,
            sinAngleZ;
    
    public Transform3D(float x, float y, float z) {
        location = new Vector3D(x, y, z);
        setAngle(0, 0, 0);
    }
    
    public Transform3D(Transform3D t) {
        location = new Vector3D();
        setTo(t);
    }
    
    public Transform3D() {
        this(0, 0, 0);
    }

    @Override
    public Object clone() {
        return new Transform3D(this);
    }
    public void setTo(Transform3D t) {
        location.setTo(t.location);
        this.cosAngleX = t.cosAngleX;
        this.sinAngleX = t.sinAngleX;
        this.cosAngleY = t.cosAngleY;
        this.sinAngleY = t.sinAngleY;
        this.cosAngleZ = t.cosAngleZ;
        this.sinAngleZ = t.sinAngleZ;
    }

    public Vector3D getLocation() {
        return location;
    }

    public float getCosAngleX() {
        return cosAngleX;
    }

    public float getSinAngleX() {
        return sinAngleX;
    }

    public float getCosAngleY() {
        return cosAngleY;
    }

    public float getSinAngleY() {
        return sinAngleY;
    }

    public float getCosAngleZ() {
        return cosAngleZ;
    }

    public float getSinAngleZ() {
        return sinAngleZ;
    }
    
    public float getAngleX() {
        return (float) Math.atan2(sinAngleX, cosAngleX);
    }
    
    public float getAngleY() {
        return (float) Math.atan2(sinAngleY, cosAngleY);
    }
    
    public float getAngleZ() {
        return (float) Math.atan2(sinAngleZ, cosAngleZ);
    }
    
    public void setAngleX(float angle) {
        cosAngleX = (float)Math.cos(angle);
        sinAngleX = (float)Math.sin(angle);
    }
    
    public void setAngleY(float angle) {
        cosAngleY = (float)Math.cos(angle);
        sinAngleY = (float)Math.sin(angle);
    }
    
    public void setAngleZ(float angle) {
        cosAngleZ = (float)Math.cos(angle);
        sinAngleZ = (float)Math.sin(angle);
    }
    
    public void setAngle(float angleX, float angleY, float angleZ) {
        setAngleX(angleX);
        setAngleY(angleY);
        setAngleZ(angleZ);
    }
    
    public void rotateAngleX(float angle) {
        if  (angle != 0) {
            setAngleX(getAngleX() + angle);
        }
    }
    
    public void rotateAngleY(float angle) {
        if  (angle != 0) {
            setAngleY(getAngleY() + angle);
        }
    }
    
    public void rotateAngleZ(float angle) {
        if  (angle != 0) {
            setAngleZ(getAngleZ() + angle);
        }
    }
    
    public void rotateAngle(float angleX, float angleY, float angleZ) {
        rotateAngleX(angleX);
        rotateAngleY(angleY);
        rotateAngleZ(angleZ);
    }
}
