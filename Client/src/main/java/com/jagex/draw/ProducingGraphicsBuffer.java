package com.jagex.draw;

import com.jagex.draw.raster.GameRaster;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Arrays;

public class ProducingGraphicsBuffer implements ImageProducer, ImageObserver {

    protected ImageConsumer consumer;
    @Getter protected GameRaster raster;
    @Setter @Getter protected Image image;
    protected ColorModel model;
    protected int[] pixels;
    @Setter @Getter private int height;
    @Setter @Getter private int width;

    protected ProducingGraphicsBuffer() {

    }

    @Override
    public synchronized void addConsumer(ImageConsumer consumer) {
        this.consumer = consumer;
        consumer.setDimensions(width, height);
        consumer.setProperties(null);
        consumer.setColorModel(model);
        consumer.setHints(14);
    }

    public void clear(int colour) {
        Arrays.fill(pixels, colour);
    }

    @Override
    public boolean imageUpdate(Image image, int flags, int x, int y, int width, int height) {
        return true;
    }

    public void initializeRasterizer() {
        raster.init(height, width, pixels);
    }

    @Override
    public synchronized boolean isConsumer(ImageConsumer consumer) {
        return this.consumer == consumer;
    }

    @Override
    public synchronized void removeConsumer(ImageConsumer consumer) {
        if (this.consumer == consumer) {
            this.consumer = null;
        }
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
    }

    @Override
    public void startProduction(ImageConsumer consumer) {
        addConsumer(consumer);
    }
}