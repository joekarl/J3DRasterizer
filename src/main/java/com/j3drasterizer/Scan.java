/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.j3drasterizer;

/**
 *
 * @author karl
 */
public class Scan {
    public int left, right;
    
    public void setBoundary(int x) {
        if (x < left) {
            left = x;
        }
        if (x - 1 > right) {
            right = x - 1;
        }
    }
    
    public void clear() {
        left = Integer.MAX_VALUE;
        right = Integer.MIN_VALUE;
    }
    
    public boolean isValid() {
        return left <= right;
    }
    
    public void setTo(int left, int right) {
        this.left = left;
        this.right = right;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.left;
        hash = 29 * hash + this.right;
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
        final Scan other = (Scan) obj;
        if (this.left != other.left) {
            return false;
        }
        if (this.right != other.right) {
            return false;
        }
        return true;
    }
    
    
}
