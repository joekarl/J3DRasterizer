/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public abstract class VertexShader extends Shader {

    public Transform3D worldTransform;
    public Vector3D inVertex, outVertex;
    public Color4f inColor, outColor;

    @Override
    public abstract void shade();
}
