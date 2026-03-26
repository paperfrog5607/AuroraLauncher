package org.aurora.launcher.ui.component;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * Switch风格横向滚动容器
 * 支持惯性滑动和平滑滚动
 */
public class HorizontalScrollPane {

    private final DoubleProperty scrollOffset = new SimpleDoubleProperty(0);
    private final DoubleProperty targetOffset = new SimpleDoubleProperty(0);
    private Timeline scrollAnimation;
    private double viewportWidth;
    private double contentWidth;
    private double friction = 0.95;
    private double velocity = 0;
    private boolean isAnimating = false;

    public HorizontalScrollPane() {
        scrollOffset.addListener((obs, old, newVal) -> {
            if (!isAnimating) {
                updateScroll((Double) newVal);
            }
        });
    }

    public void configure(double viewportWidth, double contentWidth) {
        this.viewportWidth = viewportWidth;
        this.contentWidth = contentWidth;
    }

    public void handleScroll(ScrollEvent event) {
        event.consume();
        double delta = event.getDeltaX() != 0 ? event.getDeltaX() : -event.getDeltaY();
        
        if (event.getDeltaX() != 0) {
            delta = -event.getDeltaX();
        } else {
            delta = -delta * 3;
        }

        double newOffset = scrollOffset.get() + delta;
        clampOffset(newOffset);
        
        scrollOffset.set(newOffset);
        velocity = delta;
    }

    public void handleSwipe(double velocityX) {
        if (Math.abs(velocityX) < 50) return;
        
        this.velocity = -velocityX * 0.5;
        startMomentumScroll();
    }

    private void startMomentumScroll() {
        if (scrollAnimation != null) {
            scrollAnimation.stop();
        }

        isAnimating = true;
        final double startOffset = scrollOffset.get();
        final double startVelocity = velocity;

        scrollAnimation = new Timeline();
        final double maxOffset = contentWidth - viewportWidth;
        final double minOffset = 0;

        long durationMs = 800;
        final double target = clampValue(
            startOffset + startVelocity * 2,
            minOffset,
            maxOffset
        );

        KeyFrame kf = new KeyFrame(
            Duration.millis(durationMs),
            new KeyValue(scrollOffset, target)
        );

        scrollAnimation.getKeyFrames().add(kf);
        scrollAnimation.setOnFinished(e -> {
            isAnimating = false;
            snapToNearest(startOffset, target);
        });
        scrollAnimation.play();
    }

    private void snapToNearest(double from, double to) {
        if (contentWidth <= viewportWidth) return;

        double cardWidth = 220;
        double gap = 16;
        double itemWidth = cardWidth + gap;

        double targetPos = to > from ? to + itemWidth / 2 : to - itemWidth / 2;
        int index = (int) Math.round(targetPos / itemWidth);
        index = Math.max(0, index);

        double snappedOffset = index * itemWidth;
        snappedOffset = clampValue(snappedOffset, 0, contentWidth - viewportWidth);

        if (Math.abs(snappedOffset - to) < 1) return;

        animateTo(snappedOffset, 150);
    }

    public void animateTo(double targetOffset, long durationMs) {
        if (scrollAnimation != null) {
            scrollAnimation.stop();
        }

        isAnimating = true;
        double clampedTarget = clampValue(targetOffset, 0, Math.max(0, contentWidth - viewportWidth));

        scrollAnimation = new Timeline();
        KeyFrame kf = new KeyFrame(
            Duration.millis(durationMs),
            new KeyValue(this.scrollOffset, clampedTarget)
        );
        scrollAnimation.getKeyFrames().add(kf);
        scrollAnimation.setOnFinished(e -> isAnimating = false);
        scrollAnimation.play();
    }

    private void clampOffset(double offset) {
        double maxOffset = Math.max(0, contentWidth - viewportWidth);
        scrollOffset.set(clampValue(offset, 0, maxOffset));
    }

    private double clampValue(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    protected void updateScroll(double offset) {
        // 由子类实现具体滚动逻辑
    }

    public DoubleProperty scrollOffsetProperty() {
        return scrollOffset;
    }

    public double getScrollOffset() {
        return scrollOffset.get();
    }

    public void scrollToStart() {
        animateTo(0, 200);
    }

    public void scrollToEnd() {
        animateTo(Math.max(0, contentWidth - viewportWidth), 200);
    }

    public void scrollToIndex(int index) {
        double cardWidth = 220;
        double gap = 16;
        double offset = index * (cardWidth + gap);
        animateTo(offset, 200);
    }

    public void stopAnimation() {
        if (scrollAnimation != null) {
            scrollAnimation.stop();
            isAnimating = false;
        }
    }
}
