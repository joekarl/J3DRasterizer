/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public class Polygon3D {

    private Vector3D[] vertices;
    private int vertNum;

    public Polygon3D(Vector3D... vertices) {
        vertNum = vertices.length;
        this.vertices = vertices;
    }

    public Polygon3D() {
        this(new Vector3D[0]);
    }
    
    public void setTo(Polygon3D p) {
        vertNum = p.vertNum;
        ensureCapacity(vertNum);
        for (int i = 0; i < vertNum; i++) {
            vertices[i].setTo(p.vertices[i]);
        }
    }

    protected void ensureCapacity(int capacity) {
        if (vertices.length < capacity) {
            Vector3D[] newVertices = new Vector3D[capacity];
            System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
            for (int i = vertices.length; i < newVertices.length; i++) {
                newVertices[i] = new Vector3D();
            }
            vertices = newVertices;
        }
    }

    public int getVertNum() {
        return vertNum;
    }

    public Vector3D getVertex(int index) {
        return vertices[index];
    }

    public static Polygon3D projectPolygonWithView(Polygon3D p, ViewFrustum view) {
        for (Vector3D vertex : p.vertices) {
            view.projectVector3D(vertex);
        }
        return p;
    }
}
