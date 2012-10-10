/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 * 
 * Not thread safe due to use of temp vectors....
 * 
 */
public class Polygon3D {

    private Vector3D[] vertices;
    private int vertNum;
    private Vector3D normal;
    private static Vector3D tempV1, tempV2;

    public Polygon3D(Vector3D... vertices) {
        vertNum = vertices.length;
        this.vertices = vertices;
        tempV1 = new Vector3D();
        tempV2 = new Vector3D();
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
    
    
    public Vector3D calcNormal() {
        if (normal == null) {
            normal = new Vector3D();
        }
        tempV1.setTo(vertices[2]);
        tempV1.subtract(vertices[1]);
        tempV2.setTo(vertices[0]);
        tempV2.subtract(vertices[1]);
        normal.setToCrossProduct(tempV1, tempV2);
        normal.normalize();
        return normal;
    }
    
    public Vector3D getNormal() {
        return normal;
    }

    public void setNormal(Vector3D v) {
        if (normal == null) {
            normal = new Vector3D(v);
        } else {
            normal.setTo(v);
        }
    }
    
    public boolean isFacing (Vector3D v) {
        tempV1.setTo(v);
        tempV1.subtract(vertices[0]);
        return (normal.getDotProduct(tempV1) >= 0.0f);
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
