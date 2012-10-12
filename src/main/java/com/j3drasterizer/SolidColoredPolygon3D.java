/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public class SolidColoredPolygon3D extends Polygon3D {

    private Vector3D color;

    public SolidColoredPolygon3D(Vector3D... vertices) {
        super(vertices);
        color = new Vector3D(1, 1, 1);
    }

    public SolidColoredPolygon3D() {
        super();
        color = new Vector3D(1, 1, 1);
    }

    public Vector3D getColor() {
        return color;
    }

    @Override
    public Vector3D getColor(int index) {
        return color;
    }
}
