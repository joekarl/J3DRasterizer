/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.awt.Rectangle;

/**
 *
 * @author karl
 */
public class ViewFrustum {

    private Rectangle bounds;
    private float angle, distanceToCamera;
    private int nearClipPlane, farClipPlane;

    public ViewFrustum(int left, int top, int width, int height, float angle, int nearClipPlane, int farClipPlane) {
        bounds = new Rectangle();
        this.angle = angle;
        this.nearClipPlane = nearClipPlane;
        this.farClipPlane = farClipPlane;
        setBounds(left, top, width, height);
    }

    public final void setBounds(int left, int top, int width, int height) {
        bounds.x = left;
        bounds.y = top;
        bounds.width = width;
        bounds.height = height;
        calculateDistanceToCamera();
    }

    public final void setAngle(float angle) {
        this.angle = angle;
        calculateDistanceToCamera();
    }

    public float calculateDistanceToCamera() {
        return distanceToCamera = (bounds.width / 2.0f) / (float) Math.tan(angle / 2.0);
    }

    public float getAngle() {
        return angle;
    }

    public float getDistanceToCamera() {
        return distanceToCamera;
    }

    public int getWidth() {
        return bounds.width;
    }

    public int getHeight() {
        return bounds.height;
    }

    public int getTopOffset() {
        return bounds.y;
    }

    public int getLeftOffset() {
        return bounds.x;
    }

    public int getNearClipPlane() {
        return nearClipPlane;
    }

    public void setNearClipPlane(int nearClipPlane) {
        this.nearClipPlane = nearClipPlane;
    }

    public int getFarClipPlane() {
        return farClipPlane;
    }

    public void setFarClipPlane(int farClipPlane) {
        this.farClipPlane = farClipPlane;
    }

    public final float convertFromViewXToScreenX(float x) {
        return x + bounds.x + bounds.width / 2.0f;
    }

    public final float convertFromViewYToScreenY(float y) {
        return -y + bounds.y + bounds.height / 2.0f;
    }

    public final float convertFromScreenXToViewX(float x) {
        return x - bounds.x - bounds.width / 2.0f;
    }

    public final float convertFromScreenYToViewY(float y) {
        return -y + bounds.y + bounds.height / 2.0f;
    }

    public void projectVector3D(Vector3D v) {
        float distanceOverZ = distanceToCamera / -v.z;
        v.x = distanceOverZ * v.x;
        v.y = distanceOverZ * v.y;

        v.x = convertFromViewXToScreenX(v.x);
        v.y = convertFromViewYToScreenY(v.y);
    }
}
