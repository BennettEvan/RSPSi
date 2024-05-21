package com.jagex.cache.def;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Floor {

    private int texture;
    private int rgb;
    private boolean shadowed;
    private int anotherRgb;
    private int hue;
    private int saturation;
    private int luminance;
    private int anotherHue;
    private int anotherSaturation;
    private int anotherLuminance;
    private int weightedHue;
    private int chroma;
    private int colour;

    public Floor() {
        texture = -1;
        shadowed = true;
    }

    public static int hsl24To16(int h, int s, int l) {
        if (l > 179) {
            s /= 2;
        }
        if (l > 192) {
            s /= 2;
        }
        if (l > 217) {
            s /= 2;
        }
        if (l > 243) {
            s /= 2;
        }
        return (h / 4 << 10) + (s / 32 << 7) + l / 2;
    }

    public void generateHsl() {
        if (anotherRgb != -1) {
            rgbToHsl(anotherRgb);
            anotherHue = hue;
            anotherSaturation = saturation;
            anotherLuminance = luminance;
        }
        rgbToHsl(rgb);
    }

    private void rgbToHsl(int rgb) {
        double r = (rgb >> 16 & 0xff) / 256.0;
        double g = (rgb >> 8 & 0xff) / 256.0;
        double b = (rgb & 0xff) / 256.0;
        double min = r;
        if (g < min) {
            min = g;
        }
        if (b < min) {
            min = b;
        }
        double max = r;
        if (g > max) {
            max = g;
        }
        if (b > max) {
            max = b;
        }
        double h = 0.0;
        double s = 0.0;
        double l = (min + max) / 2.0;
        if (min != max) {
            if (l < 0.5) {
                s = (max - min) / (max + min);
            }
            if (l >= 0.5) {
                s = (max - min) / (2.0 - max - min);
            }
            if (r == max) {
                h = (g - b) / (max - min);
            } else if (g == max) {
                h = 2.0 + (b - r) / (max - min);
            } else if (b == max) {
                h = 4.0 + (r - g) / (max - min);
            }
        }
        h /= 6.0;
        hue = (int) (h * 256.0);
        saturation = (int) (s * 256.0);
        luminance = (int) (l * 256.0);
        if (saturation < 0) {
            saturation = 0;
        } else if (saturation > 255) {
            saturation = 255;
        }
        if (luminance < 0) {
            luminance = 0;
        } else if (luminance > 255) {
            luminance = 255;
        }
        if (l > 0.5) {
            chroma = (int) ((1.0 - l) * s * 512.0);
        } else {
            chroma = (int) (l * s * 512.0);
        }
        if (chroma < 1) {
            chroma = 1;
        }
        weightedHue = (int) (h * chroma);
        colour = hsl24To16(hue, saturation, luminance);
    }
}
