package org.aurora.launcher.resource.texture;

import java.awt.Image;
import java.util.List;

public class TexturePreview {
    
    private Image texture;
    private String texturePath;
    private int width;
    private int height;
    private boolean animated;
    private List<Image> animationFrames;
    
    public TexturePreview() {
    }
    
    public TexturePreview(Image texture, String texturePath) {
        this.texture = texture;
        this.texturePath = texturePath;
        if (texture != null) {
            this.width = texture.getWidth(null);
            this.height = texture.getHeight(null);
        }
    }
    
    public Image getTexture() {
        return texture;
    }
    
    public void setTexture(Image texture) {
        this.texture = texture;
        if (texture != null) {
            this.width = texture.getWidth(null);
            this.height = texture.getHeight(null);
        }
    }
    
    public String getTexturePath() {
        return texturePath;
    }
    
    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public boolean isAnimated() {
        return animated;
    }
    
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }
    
    public List<Image> getAnimationFrames() {
        return animationFrames;
    }
    
    public void setAnimationFrames(List<Image> animationFrames) {
        this.animationFrames = animationFrames;
    }
}