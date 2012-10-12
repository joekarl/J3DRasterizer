/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

/**
 *
 * @author karl
 */
public class PolygonRenderer {

    private Polygon3D tPolygon;
    private final ViewFrustum view;
    private Graphics2D g2d;
    private VertexShader currentVertexShader;
    private Color wireframeColor;
    private boolean wireframe;
    private boolean fill;
    private boolean enableVertexShader;
    private boolean backFaceCulling;
    private Transform3D cameraPosition;
    private ScanConverter scanConverter;
    private BufferedImage renderBuffer;

    public PolygonRenderer(ViewFrustum view) {
        this.view = view;
        tPolygon = new Polygon3D(new Vector3D(), new Vector3D(), new Vector3D());
        fill = true;
        wireframe = false;
        wireframeColor = Color.WHITE;
        enableVertexShader = false;
        backFaceCulling = true;
        cameraPosition = new Transform3D();
        scanConverter = new ScanConverter(view);
        renderBuffer = new BufferedImage(view.getLeftOffset() * 2 + view.getWidth(),
                view.getTopOffset() * 2 + view.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public void startFrame() {
        this.g2d = (Graphics2D) renderBuffer.getGraphics();
        if (true) {
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, renderBuffer.getWidth(), renderBuffer.getHeight());
        }
    }

    public BufferedImage endFrame() {
        g2d = null;
        return renderBuffer;
    }

    public void rasterize(Polygon3D p) {
        tPolygon.setTo(p);

        int vertNum = tPolygon.getVertNum();
        
        if (currentVertexShader != null) {
            for (int i = 0; i < vertNum; i++) {
                currentVertexShader.vertex = tPolygon.getVertex(i);
                currentVertexShader.shade();
                currentVertexShader.vertex.subtract(cameraPosition.getLocation());
            }
        }
        
        tPolygon.calcNormal();

        if (!backFaceCulling || tPolygon.isFacing(cameraPosition.getLocation())) {

            if (false || tPolygon.clipNearPlane(-1)) {

                Polygon3D.projectPolygonWithView(tPolygon, view);

                if (fill) {
                    scanConverter.convert(tPolygon);
                    for (int i = scanConverter.top; i <= scanConverter.bottom; i++) {
                        ScanConverter.Scan scan = scanConverter.getScan(i);
                        if (scan.isValid()) {
                            g2d.drawLine(scan.left, i, scan.right, i);
                        }
                    }
                }

                if (wireframe) {
                    GeneralPath path = new GeneralPath();
                    Vector3D v = tPolygon.getVertex(0);
                    path.moveTo(v.x, v.y);

                    for (int i = 1; i < vertNum; i++) {
                        v = tPolygon.getVertex(i);
                        path.lineTo(v.x, v.y);
                    }
                    path.closePath();
                    g2d.setColor(wireframeColor);
                    g2d.draw(path);
                }
            }
        }
    }

    public void enableVertexShader() {
        enableVertexShader = true;
    }

    public void setVertexShader(VertexShader currentVertexShader) {
        this.currentVertexShader = currentVertexShader;
    }

    public void disableVertexShader() {
        this.currentVertexShader = null;
        enableVertexShader = false;
    }

    public void setWireframeColor(Color wireframeColor) {
        this.wireframeColor = wireframeColor;
    }

    public void enableWireframe() {
        this.wireframe = true;
    }

    public void disableWireframe() {
        this.wireframe = false;
    }

    public void enableFill() {
        this.fill = true;
    }

    public void disableFill() {
        this.fill = false;
    }

    public Transform3D getCameraPosition() {
        return cameraPosition;
    }

    public void enableBackFaceCulling() {
        this.backFaceCulling = true;
    }

    public void disableBackFaceCulling() {
        this.backFaceCulling = false;
    }
}
