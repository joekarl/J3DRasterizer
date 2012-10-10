/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer.demo;

import com.j3drasterizer.PolygonRenderer;
import com.j3drasterizer.SolidColoredPolygon3D;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author karl
 */
public class StaticImageDemo1 {

    float zDepth = -500;

    public StaticImageDemo1() {
        JFrame frame = new JFrame("Static Image Demo 1");
        final int width = 640, height = 480;

        final ViewFrustum view = new ViewFrustum(0, 0, width, height,
                (float) Math.toRadians(75));

        final SolidColoredPolygon3D leaves = new SolidColoredPolygon3D(
                new Vector3D(-50, -35, 0),
                new Vector3D(50, -35, 0),
                new Vector3D(0, 150, 0));
        leaves.setColor(Color.GREEN);


        final SolidColoredPolygon3D trunk = new SolidColoredPolygon3D(
                new Vector3D(-5, -50, 0),
                new Vector3D(5, -50, 0),
                new Vector3D(5, -35, 0),
                new Vector3D(-5, -35, 0));
        trunk.setColor(Color.RED);

        final Transform3D transform1 = new Transform3D(0, 0, zDepth);
        final Transform3D transform2 = new Transform3D(0, 0, zDepth);
        transform2.setAngleY((float) Math.toRadians(90));

        final PolygonRenderer polyRenderer = new PolygonRenderer(view);

        polyRenderer.enableVertexShader();

        final TransformShader transformShader = new TransformShader();

        polyRenderer.setVertexShader(transformShader);

        //polyRenderer.enableWireframe();

        final JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, width, height);

                polyRenderer.startFrame(g2d);

                transformShader.transform = transform1;
                g2d.setColor(Color.GREEN);
                polyRenderer.rasterize(leaves);
                g2d.setColor(Color.red);
                polyRenderer.rasterize(trunk);

                transformShader.transform = transform2;
                g2d.setColor(Color.GREEN);
                polyRenderer.rasterize(leaves);
                g2d.setColor(Color.red);
                polyRenderer.rasterize(trunk);

                polyRenderer.endFrame();
            }
        };

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    float multiplier = 0.3f;
                    //transform.rotateAngleZ((float) Math.toRadians(5 * multiplier));
                    //transform.rotateAngleX((float) Math.toRadians(2 * multiplier));
                    transform1.rotateAngleY((float) Math.toRadians(8 * multiplier));
                    transform2.rotateAngleY((float) Math.toRadians(8 * multiplier));
                    
                    transform1.getLocation().z = zDepth;
                    transform2.getLocation().z = zDepth;

                    panel.repaint();
                    
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StaticImageDemo1.class.getName()).log(Level.SEVERE, null, ex);
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
                    zDepth += 50;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    zDepth -= 50;
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

    private static class TransformShader implements VertexShader {

        public Transform3D transform;

        public void shade(Vector3D vertex) {
            vertex.addTransform(transform);
        }
    }
}
