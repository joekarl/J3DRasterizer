/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.awt.Color;

/**
 *
 * @author karl
 */
public class SolidColoredPolygon3D extends Polygon3D {

    private Color color;

    public SolidColoredPolygon3D(Vector3D... vertices) {
        super(vertices);
    }

    public SolidColoredPolygon3D() {
        super();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        this.color = c;
    }
}
