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
 *
 * Not thread safe due to use of temp vectors....
 *
 */
public class Polygon3D {

    private Vector3D[] vertices;
    private Vector3D[] colors;
    private int vertNum;
    private Vector3D normal;
    private static Vector3D tempV1 = new Vector3D();
    private static Vector3D tempV2 = new Vector3D();

    public Polygon3D(Vector3D... vertices) {
        vertNum = vertices.length;
        this.vertices = vertices;
        normal = new Vector3D();
        calcNormal();
        colors = new Vector3D[vertNum];
        for (int i = 0; i < vertNum; i++) {
            colors[i] = new Vector3D(-1, -1, -1);
        }
    }

    public Polygon3D() {
        this(new Vector3D(), new Vector3D(), new Vector3D());
    }

    public void setTo(Polygon3D p) {
        vertNum = p.vertNum;
        ensureCapacity(vertNum);
        for (int i = 0; i < vertNum; i++) {
            vertices[i].setTo(p.vertices[i]);
            colors[i].setTo(p.colors[i]);
        }
        calcNormal();
    }

    protected void ensureCapacity(int capacity) {
        if (vertices.length < capacity) {
            Vector3D[] newVertices = new Vector3D[capacity];
            Vector3D[] newColors = new Vector3D[capacity];
            System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
            System.arraycopy(colors, 0, newColors, 0, colors.length);
            for (int i = vertices.length; i < newVertices.length; i++) {
                newVertices[i] = new Vector3D();
                newColors[i] = new Vector3D(-1, -1, -1);
            }
            vertices = newVertices;
            colors = newColors;
        }
    }

    public final Vector3D calcNormal() {
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

    public boolean isFacing(Vector3D v) {
        tempV1.setTo(v);
        tempV1.subtract(vertices[0]);
        float dotProduct = normal.getDotProduct(tempV1);
        return (dotProduct >= 0.0f);
    }

    public int getVertNum() {
        return vertNum;
    }

    public Vector3D getVertex(int index) {
        return vertices[index];
    }

    public Vector3D getColor(int index) {
        return colors[index];
    }

    public static Polygon3D projectPolygonWithView(Polygon3D p, ViewFrustum view) {
        for (Vector3D vertex : p.vertices) {
            view.projectVector3D(vertex);
        }
        return p;
    }

    public boolean clipNearPlane(float clipZ) {
        ensureCapacity(vertNum * 3);

        boolean isCompletelyHidden = true;
        for (int i = 0; i < vertNum; i++) {
            int next = (i + 1) % vertNum;
            Vector3D v1 = vertices[i];
            Vector3D v2 = vertices[next];

            if (v1.z < clipZ) {
                isCompletelyHidden = false;
            }

            if (v1.z > v2.z) {
                Vector3D temp = v1;
                v1 = v2;
                v2 = temp;
            }

            if (v1.z < clipZ && v2.z > clipZ) {
                float scale = (clipZ - v1.z) / (v2.z - v1.z);
                insertVertex(next,
                        v1.x + scale * (v2.x - v1.x),
                        v1.y + scale * (v2.y - v1.y),
                        clipZ);
                i++;
            }
        }

        if (isCompletelyHidden) {
            return false;
        }

        for (int i = vertNum - 1; i >= 0; i--) {
            if (vertices[i].z > clipZ) {
                deleteVertex(i);
            }
        }

        return vertNum >= 3;
    }

    protected void insertVertex(int index, float x, float y, float z) {
        Vector3D v = vertices[vertices.length - 1];
        Vector3D newColor = colors[colors.length - 1];
        v.x = x;
        v.y = y;
        v.z = z;
        for (int i = vertices.length - 1; i > index; i--) {
            vertices[i] = vertices[i - 1];
            colors[i] = colors[i - 1];
        }

        vertices[index] = v;
        vertNum++;

        int vert1, vert2;

        if (index == 0) {
            vert1 = vertNum - 1;
        } else {
            vert1 = index - 1;
        }
        if (index == vertNum - 1) {
            vert2 = 0;
        } else {
            vert2 = index + 1;
        }

        Vector3D v1 = vertices[vert1];
        Vector3D v2 = vertices[vert2];
        Vector3D color1 = colors[vert1];
        Vector3D color2 = colors[vert2];

        float lerp = (y - v1.y) / (v2.y - v1.y);
        Vector3D.lerp(color1, color2, lerp, newColor);

        colors[index] = newColor;
    }

    protected void deleteVertex(int index) {
        Vector3D v = vertices[index];
        Vector3D color = colors[index];
        for (int i = index; i < vertices.length - 1; i++) {
            vertices[i] = vertices[1 + i];
            colors[i] = colors[1 + i];
        }
        vertices[vertices.length - 1] = v;
        colors[colors.length - 1] = color;
        vertNum--;
    }

    Vector3D[] getColors() {
        return colors;
    }
}
