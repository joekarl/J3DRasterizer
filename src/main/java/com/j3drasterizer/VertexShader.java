/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public abstract class VertexShader {
    public Transform3D worldTransform;
    public Vector3D vertex;
    public Vector3D position;
    public Vector3D color;
    
    public abstract void shade();
    
}
