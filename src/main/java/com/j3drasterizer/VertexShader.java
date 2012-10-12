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
    public Vector3D inVertex, outVertex;
    public Vector3D inColor, outColor;
    
    public abstract void shade();
    
}
