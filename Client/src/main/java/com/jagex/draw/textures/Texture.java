package com.jagex.draw.textures;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Ints;
import com.jagex.util.ColourUtils;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.Getter;

public abstract class Texture {

    protected int[] palette;
    protected int[] paletteIndexes;
    protected int[] pixels;
    protected int[] originalPixels;
    @Getter protected int width;
    @Getter protected int height;
    protected int averageTextureColour;
    protected double brightness = 0.8;

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
        this.originalPixels = new int[width * height];
        this.pixels = new int[width * height];
    }

    private void generatePixels() {
        for (int i = 0; i < paletteIndexes.length; i++) {
            pixels[i] = palette[paletteIndexes[i]];
        }
    }

    public int getPixel(int index) {
        return pixels[index];
    }

    public WritableImage getAsFXImage() {
        int height = this.height;
        int width = this.width;
        WritableImage image = new WritableImage(this.width, this.height);
        PixelFormat<IntBuffer> format = PixelFormat.getIntArgbInstance();
        image.getPixelWriter().setPixels(0, 0, width, height, format, ColourUtils.getARGB(pixels), 0, width);
        return image;
    }

    public int averageTextureColour() {
        if (averageTextureColour > 0) {
            return averageTextureColour;
        }
        int rgb = averageColorForPixels(pixels);
        averageTextureColour = rgb;
        return rgb;
    }

    private static int averageColorForPixels(int[] pixels) {
        int redTotal = 0;
        int greenTotal = 0;
        int blueTotal = 0;

        int len = pixels.length;
        for (int i = 0; i < len; i++) {
            if (pixels[i] == 0xff00ff) {
                len--;
                continue;
            }

            redTotal += pixels[i] >> 16 & 0xff;
            greenTotal += pixels[i] >> 8 & 0xff;
            blueTotal += pixels[i] & 0xff;
        }

        int averageRGB = (redTotal / len << 16) + (greenTotal / len << 8) + blueTotal / len;
        if (averageRGB == 0) {
            averageRGB = 1;
        }
        return averageRGB;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
        generatePalette();
        generatePixels();
    }

    public void generatePalette() {
        List<Integer> colours = new ArrayList<>();
        for (int pixel : originalPixels) {
            int newPixel = ColourUtils.exponent(pixel, brightness);
            if (!colours.contains(newPixel)) {
                colours.add(newPixel);
            }
        }

        paletteIndexes = new int[originalPixels.length];
        for (int pixel = 0; pixel < originalPixels.length; pixel++) {
            int newPixel = ColourUtils.exponent(originalPixels[pixel], brightness);
            paletteIndexes[pixel] = colours.indexOf(newPixel);
        }

        palette = Ints.toArray(colours);
        averageTextureColour = 0;
        generatePixels();
    }

    public abstract boolean supportsAlpha();
}
