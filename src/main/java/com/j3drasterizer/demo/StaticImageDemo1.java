/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer.demo;

import com.j3drasterizer.FragmentShader;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author karl
 */
public class StaticImageDemo1 {

    float zDepth = 0;

    public StaticImageDemo1() {
        JFrame frame = new JFrame("Static Image Demo 1");


        final TimeCounter fpsCounter = new TimeCounter();

        final int width = 1024, height = 768;

        final ViewFrustum view = new ViewFrustum(0, 0, width, height,
                (float) Math.toRadians(75), -1, -2000);

        final int scale = 5;
        final Polygon3D leaves = new Polygon3D(
                new Vector3D(0, 100, 0).multiply(scale),
                new Vector3D(-50, -35, 0).multiply(scale),
                new Vector3D(50, -35, 0).multiply(scale));
        leaves.getColor(0).setTo(1, 0, 0);
        leaves.getColor(1).setTo(0, 1, 0);
        leaves.getColor(2).setTo(0, 0, 1, 0);

        final Polygon3D trunk = new Polygon3D(
                new Vector3D(-5, -50, 0).multiply(scale),
                new Vector3D(5, -50, 0).multiply(scale),
                new Vector3D(5, -35, 0).multiply(scale),
                new Vector3D(-5, -35, 0).multiply(scale));
        trunk.getColor(0).setTo(1, 0.5f, 0);

        final Transform3D transform1 = new Transform3D(0, 0, -420);
        transform1.setAngleY((float) Math.toRadians(45));

        final PolygonRenderer polyRenderer = new PolygonRenderer(view);

        final TransformShader transformShader = new TransformShader();
        polyRenderer.setVertexShader(transformShader);
        polyRenderer.enableVertexShader();

        final FragShader fragmentShader = new FragShader();
        polyRenderer.setFragmentShader(fragmentShader);
        polyRenderer.disableFragmentShader();

        //polyRenderer.disableFill();
        //polyRenderer.enableWireframe();
        polyRenderer.disableBackFaceCulling();
        //polyRenderer.disableAdaptiveSmoothing();

        final JPanel panel = new JPanel() {
            BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                
                //g2d.setColor(Color.yellow);
                //g2d.fillRect(0, 0, width, height);

                polyRenderer.startFrame();
                fpsCounter.tick();
                Transform3D transform = (Transform3D) transform1.clone();

                transformShader.worldTransform = transform;
                polyRenderer.enableFragmentShader();
                polyRenderer.rasterize(leaves);

                polyRenderer.disableFragmentShader();
                polyRenderer.rasterize(trunk);

                g2d.drawImage(polyRenderer.endFrame(), 0, 0, null);

                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("FPS %f", fpsCounter.getCountsPerSecond()), 5, 17);

                g2d.setColor(Color.WHITE);
                g2d.drawString(String.format("FPS %f", fpsCounter.getCountsPerSecond()), 5, 15);
                
                this.repaint();
            }
        };

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    float multiplier = 0.3f;
                    //transform1.rotateAngleY((float) Math.toRadians(30 * multiplier));

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StaticImageDemo1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StaticImageDemo1.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();

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
                    polyRenderer.getCameraPosition().getLocation().z += 10 * scale;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    polyRenderer.getCameraPosition().getLocation().z -= 10 * scale;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        StaticImageDemo1 staticImageDemo1 = new StaticImageDemo1();
    }

    private static class TransformShader extends VertexShader {

        public void shade() {
            outVertex = inVertex.addTransform(worldTransform);
        }
    }

    private static class FragShader extends FragmentShader {

        public void shade() {
        }
    }
}
