package com.jagex.util;

import javafx.scene.paint.Color;

public class ColourUtils {

	public static int getBlue(int rgb) {
		return rgb & 0xFF;
	}

	public static Color getColor(int rgb) {
		return Color.color(getRed(rgb) / 255.0, getGreen(rgb) / 255.0, getBlue(rgb) / 255.0);
	}

	public static int getGreen(int rgb) {
		return rgb >> 8 & 0xFF;
	}

	public static int getRed(int rgb) {
		return rgb >> 16 & 0xFF;
	}

	public static int getRGB(int alpha, int red, int green, int blue) {
		return alpha << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
	}

	public static int addAlpha(int rgb, int alpha) {
		return getRGB(alpha, getRed(rgb), getGreen(rgb), getBlue(rgb));
	}

	public static int toHsl(int hue, int saturation, int luminance) {
		if (luminance > 179) {
			saturation /= 2;
		}
		if (luminance > 192) {
			saturation /= 2;
		}
		if (luminance > 217) {
			saturation /= 2;
		}
		if (luminance > 243) {
			saturation /= 2;
		}
		return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
	}
	
	public static int checkedLight(int colour, int light) {
		if (colour == -2) {
			return 0xbc614e;
		}
		if (colour == -1) {
			if (light < 0) {
				light = 0;
			} else if (light > 127) {
				light = 127;
			}
			return 127 - light;
		}
		light = light * (colour & 0x7f) / 128;
		if (light < 2) {
			light = 2;
		} else if (light > 126) {
			light = 126;
		}
		return (colour & 0xff80) + light;
	}
	
	public static int exponent(int colour, double exponent) {
		int alpha = colour >> 24 & 0xff;
		double r = (colour >> 16 & 0xff) / 256D;
		double g = (colour >> 8 & 0xff) / 256D;
		double b = (colour & 0xff) / 256D;

		r = Math.pow(r, exponent);
		g = Math.pow(g, exponent);
		b = Math.pow(b, exponent);

		int newR = (int) (r * 256D);
		int newG = (int) (g * 256D);
		int newB = (int) (b * 256D);
		return alpha << 24 | (newR << 16) | (newG << 8) | newB;
	}

	public static int[] getARGB(int[] pixels) {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = addAlpha(pixels[i], 255);
		}
		return pixels;
	}

	public static String colorToHex(Color color) {
		String hex1 = Integer.toHexString(color.hashCode()).toUpperCase();
        return switch (hex1.length()) {
            case 2 -> "000000";
            case 3 -> String.format("00000%s", hex1.charAt(0));
            case 4 -> String.format("0000%s", hex1.substring(0, 2));
            case 5 -> String.format("000%s", hex1.substring(0, 3));
            case 6 -> String.format("00%s", hex1.substring(0, 4));
            case 7 -> String.format("0%s", hex1.substring(0, 5));
            default -> hex1.substring(0, 6);
        };
	}

	public static String rgbToHslStr(int rgb) {
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
		int hue = (int) (h * 256.0);
		int saturation = (int) (s * 256.0);
		int luminance = (int) (l * 256.0);
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
		int chroma;
		if (l > 0.5) {
			chroma = (int) ((1.0 - l) * s * 512.0);
		} else {
			chroma = (int) (l * s * 512.0);
		}
		if (chroma < 1) {
			chroma = 1;
		}
        return hue + ", " + saturation + ", " + luminance;
	}

	public static int rgbToJagHsl(int rgb) {
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
		int hue = (int) (h * 256.0);
		int saturation = (int) (s * 256.0);
		int luminance = (int) (l * 256.0);
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
		int chroma;
		if (l > 0.5) {
			chroma = (int) ((1.0 - l) * s * 512.0);
		} else {
			chroma = (int) (l * s * 512.0);
		}
		if (chroma < 1) {
			chroma = 1;
		}
		return toHsl(hue, saturation, luminance);
	}
}
