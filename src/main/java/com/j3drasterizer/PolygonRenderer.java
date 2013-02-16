/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

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
    private boolean backFaceCulling;
    private boolean adaptiveSmoothing;
    private VertexShader currentVertexShader;
    private FragmentShader currentFragmentShader;
    private Transform3D cameraPosition;
    private ScanConverter scanConverter;
    private BufferedImage renderBuffer;
    private Color4f fragmentColor;
    private Color4f shaderFrontColor;
    private Color4f shaderBackColor;
    private Color4f shaderVertexColor;
    private Color4f defaultColor;
    private Vector3D fragmentPosition;
    int[] renderBufferRaster;
    int renderableHeight, renderableWidth;
    int polygonCount;

    public PolygonRenderer(ViewFrustum view) {
        this.view = view;
        tPolygon = new Polygon3D();
        tPolygon2 = new Polygon3D();
        fill = true;
        wireframe = false;
        wireframeColor = Color.WHITE;
        enableVertexShader = false;
        enableFragmentShader = false;
        adaptiveSmoothing = true;
        backFaceCulling = true;
        cameraPosition = new Transform3D();
        scanConverter = new ScanConverter(view);
        renderableWidth = view.getLeftOffset() * 2 + view.getWidth();
        renderableHeight = view.getTopOffset() * 2 + view.getHeight();
        renderBuffer = new BufferedImage(renderableWidth, renderableHeight, BufferedImage.TYPE_INT_ARGB);
        fragmentColor = new Color4f(1, 1, 1);
        shaderFrontColor = new Color4f(1, 1, 1);
        shaderBackColor = new Color4f(1, 1, 1);
        shaderVertexColor = new Color4f(1, 1, 1);
        defaultColor = new Color4f(1, 1, 1);
        fragmentPosition = new Vector3D();
        renderBufferRaster = ((DataBufferInt) renderBuffer.getRaster().getDataBuffer()).getData();
        g2d = (Graphics2D) renderBuffer.getGraphics();
    }

    public void startFrame() {
        polygonCount = 0;

        for (int y = 0; y < renderableHeight; ++y) {
            for (int x = 0; x < renderableWidth; ++x) {
                renderBufferRaster[y * renderableWidth + x] = 0xFF000000;
            }
        }

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
        g2d.setColor(Color.BLUE);
        g2d.drawString(String.format("Polygons : %d", polygonCount), 5, 45);
        return renderBuffer;
    }

    public void rasterize(Polygon3D p) {
        tPolygon.setTo(p, true);

        int vertNum = tPolygon.getVertNum();

        for (int i = 0; i < vertNum; i++) {
            if (currentVertexShader != null && enableVertexShader) {
                currentVertexShader.inVertex = tPolygon.getVertex(i);
                Color4f vertColor = p.getColor(i);
                shaderVertexColor.setTo(vertColor.r, vertColor.g,
                        vertColor.b, vertColor.a);
                currentVertexShader.inColor = shaderVertexColor;
                currentVertexShader.outColor = currentVertexShader.inColor;
                currentVertexShader.shade();
                currentVertexShader.outVertex.subtract(cameraPosition.getLocation());
                tPolygon.getColor(i).setTo(currentVertexShader.outColor.r,
                        currentVertexShader.outColor.g,
                        currentVertexShader.outColor.b,
                        currentVertexShader.outColor.a);
            } else {
                tPolygon.getVertex(i).subtract(cameraPosition.getLocation());
            }
        }




        tPolygon.calcNormal();

        if (!backFaceCulling || tPolygon.isFacing(cameraPosition.getLocation())) {

            if (tPolygon.clipNearPlane(view.getNearClipPlane())
                    & tPolygon.clipFarPlane(view.getFarClipPlane())) {

                Polygon3D.projectPolygonWithView(tPolygon, view);
                int subPolygonCount = 1;
                if (tPolygon.getVertNum() > 3) {
                    subPolygonCount = tPolygon.getVertNum() - 2;
                }
                for (int k = 0; k < subPolygonCount; k++) {
                    tPolygon2.setTo(tPolygon, 0, 1 + k, 2 + k);

                    if (fill) {
                        fillPolygon(tPolygon2);
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

                    if (tPolygon.isClipped) {
                        g2d.setColor(Color.RED);
                        g2d.drawString("CLIPPED", 5, 30);
                    }
                    
                    polygonCount++;
                }
            }
        }
    }

    private void fillPolygon(Polygon3D p) {
        scanConverter.convert(p);
        int scanTop = scanConverter.top;
        int scanBottom = scanConverter.bottom;
        boolean isColored = p.isColored();
        boolean isSolidColored = p.isSolidColored();
        Color4f solidColor = p.getSolidColor();
        for (int i = scanTop; i <= scanBottom; i++) {
            ScanConverter.Scan scan = scanConverter.getScan(i);
            drawScan(scan, i, isColored, isSolidColored, solidColor);
        }
    }

    private void drawScan(ScanConverter.Scan scan, int scanNumber,
            boolean isColored, boolean isSolidColored,
            Color4f solidColor) {
        if (scan.isValid()) {
            int scanLeft = scan.left;
            int scanRight = scan.right;
            Color4f colorLeft = scan.colorLeft;
            Color4f colorRight = scan.colorRight;
            int pixelLength = 1;
            int scanLength = scanRight - scanLeft;

            if (adaptiveSmoothing) {
                switch (scanLength - (scanLength % 50)) {
                    case 0:
                        pixelLength = 1;
                        break;
                    case 50:
                        pixelLength = 2;
                        break;
                    case 100:
                        pixelLength = 4;
                        break;
                    case 150:
                        pixelLength = 8;
                        break;
                    default:
                        pixelLength = 16;
                        break;
                }
            }

            for (int j = scanLeft; j <= scanRight; j += pixelLength) {
                if (scanLeft != scanRight && isColored && !isSolidColored) {
                    float lerp = (j - scanLeft)
                            / (float) (scanRight - scanLeft);
                    Color4f.lerp(colorLeft, colorRight,
                            lerp,
                            fragmentColor);
                } else if (isSolidColored) {
                    fragmentColor.setTo(solidColor.r, solidColor.g,
                            solidColor.b, solidColor.a);
                } else {
                    fragmentColor.setTo(colorLeft.r, colorLeft.g,
                            colorLeft.b, colorLeft.a);
                }

                if (enableFragmentShader) {
                    currentFragmentShader.fragmentColor = fragmentColor;
                    shaderFrontColor.setTo(fragmentColor.r, fragmentColor.g,
                            fragmentColor.b, fragmentColor.a);
                    shaderBackColor.setTo(fragmentColor.r, fragmentColor.g,
                            fragmentColor.b, fragmentColor.a);
                    fragmentPosition.setTo(j, scanNumber, - 1);
                    currentFragmentShader.position = fragmentPosition;
                    currentFragmentShader.frontColor = shaderFrontColor;
                    currentFragmentShader.backColor = shaderBackColor;
                    currentFragmentShader.shade();
                    fragmentColor.setTo(currentFragmentShader.fragmentColor.r,
                            currentFragmentShader.fragmentColor.g,
                            currentFragmentShader.fragmentColor.b,
                            currentFragmentShader.fragmentColor.a);
                }

                int drawLength = pixelLength;
                if (scanRight - j < pixelLength && scanRight != scanLeft) {
                    drawLength = scanRight - j;
                }

                if ((drawLength != 0 || pixelLength == 1)) {
                    int x = j > 0 ? j : 0;
                    int end = (j + drawLength) <= renderableWidth ? j + drawLength : renderableWidth;

                    for (; x < end; ++x) {
                        int r = (int) (fragmentColor.r * 255f);
                        int g = (int) (fragmentColor.g * 255f);
                        int b = (int) (fragmentColor.b * 255f);
                        int a = 255;//(int) (fragmentColor.a * 255f);
                        renderBufferRaster[scanNumber * renderableWidth + x] =
                                a << 24 | r << 16 | g << 8 | b;
                    }
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

    public void enableAdaptiveSmoothing() {
        this.adaptiveSmoothing = true;
    }

    public void disableAdaptiveSmoothing() {
        this.adaptiveSmoothing = false;
    }
}
