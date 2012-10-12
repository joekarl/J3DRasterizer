/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author karl
 */
public class ColoredPolygon3D extends Polygon3D {

    private ArrayList<Vector3D> colors;

    public ColoredPolygon3D(Vector3D... vertices) {
        super(vertices);
        colors = new ArrayList<Vector3D>();
    }

    public ColoredPolygon3D() {
        super();
        colors = new ArrayList<Vector3D>();
    }

    public List<Vector3D> getColors() {
        return colors;
    }

    @Override
    public Vector3D getColor(int index) {
        Vector3D color;
        if (colors.size() < index) {
            color = super.getColor(index);
        } else {
            color = colors.get(index);
        }
        return color;
    }
}
