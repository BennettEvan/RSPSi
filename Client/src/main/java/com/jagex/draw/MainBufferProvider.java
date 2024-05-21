package com.jagex.draw;

import com.jagex.draw.raster.GameRaster;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.util.Arrays;

@Getter
public class MainBufferProvider {

    private final int[] pixels;
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final GameRaster raster;
    private final WritableImage finalImage;

    public MainBufferProvider(GameRaster raster, int width, int height) {
        this.raster = raster;
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
        this.finalImage = new WritableImage(width, height);
        initializeRasterizer();
    }

    public void initializeRasterizer() {
        raster.init(pixels, width, height);
    }

    public void clear(int rgb) {
        Arrays.fill(pixels, rgb);
    }

    public void clearPixels(int rgb) {
        Arrays.fill(pixels, 0xFF000000 | rgb);
    }

    public void finalize() {
        int[] pixelCopy = Arrays.copyOf(pixels, pixels.length);
        for (int i = 0; i < pixelCopy.length; i++) {
            pixelCopy[i] = 0xFF000000 | pixelCopy[i];
        }
        finalImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), IntBuffer.wrap(pixelCopy), width);
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) image.getGraphics();
    }
}
