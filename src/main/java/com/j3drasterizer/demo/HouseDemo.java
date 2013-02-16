/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer.demo;

import com.j3drasterizer.Polygon3D;
import com.j3drasterizer.PolygonRenderer;
import com.j3drasterizer.TimeCounter;
import com.j3drasterizer.Transform3D;
import com.j3drasterizer.Vector3D;
import com.j3drasterizer.VertexShader;
import com.j3drasterizer.ViewFrustum;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author kkirch
 */
public class HouseDemo {

    public static void main(String... args) {
        JFrame frame = new JFrame("Static Image Demo 1");

        final TimeCounter fpsCounter = new TimeCounter();

        final int width = 1024, height = 768;
        final float floorWidth = 10000;

        final ViewFrustum view = new ViewFrustum(0, 0, width, height,
                (float) Math.toRadians(75), -1, -100000);

        final PolygonRenderer polyRenderer = new PolygonRenderer(view);
        polyRenderer.getCameraPosition().rotateAngleX((float) Math.toRadians(45));
        polyRenderer.getCameraPosition().getLocation().y += 5000;

        final TransformShader transformShader = new TransformShader();
        polyRenderer.setVertexShader(transformShader);
        polyRenderer.enableVertexShader();
        polyRenderer.enableWireframe();
        polyRenderer.disableBackFaceCulling();

        final Polygon3D floor = new Polygon3D(
                new Vector3D(floorWidth, -1, floorWidth),
                new Vector3D(floorWidth, -1, -floorWidth),
                new Vector3D(-floorWidth, -1, -floorWidth),
                new Vector3D(-floorWidth, -1, floorWidth));
        floor.getColor(0).setTo(0, 1f, 0);

        
        
        final Polygon3D houseFront = new Polygon3D(
                new Vector3D(200, 0, 0),
                new Vector3D(200, 200, 0),
                new Vector3D(-200, 200, 0),
                new Vector3D(-200, 0, 0));
        houseFront.getColor(0).setTo(0.2f, 0.2f, 0.2f);

        final Transform3D housePosition = new Transform3D();
        housePosition.getLocation().subtract(0, 0, 0);

        final JPanel panel = new JPanel() {
            BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;

                polyRenderer.startFrame();
                fpsCounter.tick();

                polyRenderer.disableVertexShader();
                polyRenderer.rasterize(floor);

                transformShader.worldTransform.setTo(housePosition);
                polyRenderer.enableVertexShader();
                polyRenderer.rasterize(houseFront);

                g2d.drawImage(polyRenderer.endFrame(), 0, 0, null);

                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("FPS %.0f", fpsCounter.getCountsPerSecond()), 5, 17);

                g2d.setColor(Color.WHITE);
                g2d.drawString(String.format("FPS %.0f", fpsCounter.getCountsPerSecond()), 5, 15);

                this.repaint();
            }
        };

        panel.setPreferredSize(new Dimension(width, height));

        frame.setContentPane(panel);
        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        frame.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    polyRenderer.getCameraPosition().getLocation().z += 100;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    polyRenderer.getCameraPosition().getLocation().z -= 100;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static class TransformShader extends VertexShader {
        public TransformShader(){
            worldTransform = new Transform3D();
        }

        public void shade() {
            outVertex = inVertex.addTransform(worldTransform);
        }
    }
}
