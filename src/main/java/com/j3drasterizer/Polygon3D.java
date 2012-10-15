/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.util.Arrays;

/**
 *
 * @author karl
 *
 * Not thread safe due to use of temp vectors....
 *
 */
public class Polygon3D {

    private Vector3D[] vertices;
    private Color4f[] colors;
    private int vertNum;
    private Vector3D normal;
    private static Vector3D tempV1 = new Vector3D();
    private static Vector3D tempV2 = new Vector3D();
    public boolean isClipped;

    public Polygon3D(Vector3D... vertices) {
        vertNum = vertices.length;
        this.vertices = vertices;
        normal = new Vector3D();
        calcNormal();
        colors = new Color4f[vertNum];
        for (int i = 0; i < vertNum; i++) {
            colors[i] = new Color4f(-1, -1, -1, -1);
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
        isClipped = false;
        calcNormal();
    }
    
    public void setTo(Polygon3D p, int ... verticeIndexes) {
        vertNum = verticeIndexes.length;
        ensureCapacity(vertNum);
        for (int i = 0; i < vertNum; i++) {
            vertices[i].setTo(p.vertices[verticeIndexes[i]]);
            colors[i].setTo(p.colors[verticeIndexes[i]]);
        }
        isClipped = false;
        calcNormal();
    }

    protected void ensureCapacity(int capacity) {
        if (vertices.length < capacity) {
            Vector3D[] newVertices = new Vector3D[capacity];
            Color4f[] newColors = new Color4f[capacity];
            System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
            System.arraycopy(colors, 0, newColors, 0, colors.length);
            for (int i = vertices.length; i < newVertices.length; i++) {
                newVertices[i] = new Vector3D();
                newColors[i] = new Color4f(-1, -1, -1, -1);
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

    public Color4f getColor(int index) {
        return colors[index];
    }

    public static Polygon3D projectPolygonWithView(Polygon3D p, ViewFrustum view) {
        for (Vector3D vertex : p.vertices) {
            view.projectVector3D(vertex);
        }
        return p;
    }

    public boolean clipNearPlane(float clipZ) {
        if (clipZ == 0) {
            clipZ = -1;
        }
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
                isClipped = true;
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
        Color4f newColor = colors[colors.length - 1];
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
        Color4f color1 = colors[vert1];
        Color4f color2 = colors[vert2];

        float lerp = (y - v1.y) / (v2.y - v1.y);
        Color4f.lerp(color1, color2, lerp, newColor);

        colors[index] = newColor;
    }

    protected void deleteVertex(int index) {
        Vector3D v = vertices[index];
        Color4f color = colors[index];
        for (int i = index; i < vertices.length - 1; i++) {
            vertices[i] = vertices[1 + i];
            colors[i] = colors[1 + i];
        }
        vertices[vertices.length - 1] = v;
        colors[colors.length - 1] = color;
        vertNum--;
    }

    Color4f[] getColors() {
        return colors;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Arrays.deepHashCode(this.vertices);
        hash = 17 * hash + Arrays.deepHashCode(this.colors);
        hash = 17 * hash + this.vertNum;
        hash = 17 * hash + (this.normal != null ? this.normal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Polygon3D other = (Polygon3D) obj;
        if (!Arrays.deepEquals(this.vertices, other.vertices)) {
            return false;
        }
        if (!Arrays.deepEquals(this.colors, other.colors)) {
            return false;
        }
        if (this.vertNum != other.vertNum) {
            return false;
        }
        if (this.normal != other.normal && (this.normal == null
                || !this.normal.equals(other.normal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Polygon3D{" + "vertices=" + Arrays.toString(vertices)
                + ", colors=" + colors + ", vertNum=" + vertNum
                + ", normal=" + normal + ", isClipped=" + isClipped + '}';
    }
}
