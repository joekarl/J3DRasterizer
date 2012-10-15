/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author Karl Kirch
 */
public abstract class FragmentShader extends Shader {

    public Color4f frontColor, backColor, fragmentColor;
    
    @Override
    public abstract void shade();
    
}
