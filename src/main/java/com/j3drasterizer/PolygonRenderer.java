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
    private Polygon3D tPolygon2;
    private final ViewFrustum view;
    private Graphics2D g2d;
    private Color wireframeColor;
    private boolean wireframe;
    private boolean fill;
    private boolean enableVertexShader;
    private boolean enableFragmentShader;
    private VertexShader currentVertexShader;
    private FragmentShader currentFragmentShader;
    private boolean backFaceCulling;
    private Transform3D cameraPosition;
    private ScanConverter scanConverter;
    private BufferedImage renderBuffer;
    private Color4f fragmentColor;
    private Color4f shaderFrontColor;
    private Color4f shaderBackColor;
    private Color4f shaderVertexColor;
    private Color4f defaultColor;

    public PolygonRenderer(ViewFrustum view) {
        this.view = view;
        tPolygon = new Polygon3D();
        tPolygon2 = new Polygon3D();
        fill = true;
        wireframe = false;
        wireframeColor = Color.WHITE;
        enableVertexShader = false;
        enableFragmentShader = false;
        backFaceCulling = true;
        cameraPosition = new Transform3D();
        scanConverter = new ScanConverter(view);
        renderBuffer = new BufferedImage(view.getLeftOffset() * 2 + view.getWidth(),
                view.getTopOffset() * 2 + view.getHeight(), BufferedImage.TYPE_INT_ARGB);
        fragmentColor = new Color4f(1, 1, 1);
        shaderFrontColor = new Color4f(1, 1, 1);
        shaderBackColor = new Color4f(1, 1, 1);
        shaderVertexColor = new Color4f(1, 1, 1);
        defaultColor = new Color4f(1, 1, 1);
    }

    public void startFrame() {
        this.g2d = (Graphics2D) renderBuffer.getGraphics();
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, renderBuffer.getWidth(), renderBuffer.getHeight());
        defaultColor.setTo(1, 1, 1);
        if (shaderFrontColor == null) {
            shaderFrontColor = new Color4f(defaultColor);
        }
        if (shaderBackColor == null) {
            shaderBackColor = new Color4f(defaultColor);
        }
        if (fragmentColor == null) {
            fragmentColor = new Color4f(defaultColor);
        }
        if (shaderVertexColor == null) {
            shaderVertexColor = new Color4f(defaultColor);
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
                currentVertexShader.inVertex = tPolygon.getVertex(i);
                shaderVertexColor.setTo(p.getColor(i));
                currentVertexShader.inColor = shaderVertexColor;
                currentVertexShader.outColor = currentVertexShader.inColor;
                currentVertexShader.shade();
                currentVertexShader.outVertex.subtract(cameraPosition.getLocation());
                tPolygon.getColor(i).setTo(currentVertexShader.outColor);
            }
        }

        tPolygon.calcNormal();

        if (!backFaceCulling || tPolygon.isFacing(cameraPosition.getLocation())) {

            if (false || tPolygon.clipNearPlane(-200)) {


                Polygon3D.projectPolygonWithView(tPolygon, view);
                int subPolygonCount = 1;
                if (tPolygon.getVertNum() > 3) {
                    subPolygonCount = tPolygon.getVertNum() - 2;
                }
                for (int k = 0; k < subPolygonCount; k++) {
                    tPolygon2.setTo(tPolygon, 0, 1 + k, 2 + k);

                    if (fill) {
                        scanConverter.convert(tPolygon2);
                        for (int i = scanConverter.top; i <= scanConverter.bottom; i++) {
                            ScanConverter.Scan scan = scanConverter.getScan(i);
//                            for (int j = 0; j < tPolygon2.getVertNum(); j++) {
//                                g2d.setColor(Color.GREEN);
//                                g2d.drawString("" + j, tPolygon2.getVertex(j).x + 5, tPolygon2.getVertex(j).y + 15);
//                            }
                            if (scan.isValid()) {
                                for (int j = scan.left; j <= scan.right; j++) {
                                    //*
                                    if (scan.left != scan.right) {
                                        float lerp = (j - scan.left)
                                                / (float) (scan.right - scan.left);
                                        Color4f.lerp(scan.colorLeft, scan.colorRight,
                                                lerp,
                                                fragmentColor);
                                    } else {
                                        fragmentColor.setTo(scan.colorLeft);
                                    }
                                    if (enableFragmentShader) {
                                        currentFragmentShader.fragmentColor = fragmentColor;
                                        shaderFrontColor.setTo(fragmentColor);
                                        shaderBackColor.setTo(fragmentColor);
                                        currentFragmentShader.frontColor = shaderFrontColor;
                                        currentFragmentShader.backColor = shaderBackColor;
                                        currentFragmentShader.shade();
                                        fragmentColor.setTo(currentFragmentShader.fragmentColor);
                                    }

                                    g2d.setColor(new Color(
                                            fragmentColor.getR(),
                                            fragmentColor.getG(),
                                            fragmentColor.getB()));

                                    //*/
                                    g2d.drawLine(j, i, j, i);
                                }
                            }
                        }

                    }

                    if (wireframe) {
                        GeneralPath path = new GeneralPath();
                        Vector3D v = tPolygon2.getVertex(0);
                        path.moveTo(v.x, v.y);
                        for (int i = 1; i < tPolygon2.getVertNum(); i++) {
                            v = tPolygon2.getVertex(i);
                            path.lineTo(v.x, v.y);
                        }
                        path.closePath();
                        g2d.setColor(wireframeColor);
                        g2d.draw(path);
                    }
                }

                if (tPolygon.isClipped) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("CLIPPED", 5, 30);
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
        enableVertexShader = false;
    }

    public void enableFragmentShader() {
        enableFragmentShader = true;
    }

    public void setFragmentShader(FragmentShader currentFragmentShader) {
        this.currentFragmentShader = currentFragmentShader;
    }

    public void disableFragmentShader() {
        enableFragmentShader = false;
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
