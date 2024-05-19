package com.jagex.draw.raster;

import com.jagex.map.MapRegion;

import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.util.ColourUtils;
import com.jagex.util.Constants;
import com.jagex.util.Point2D;
import lombok.Getter;
import lombok.Setter;

public class GameRasterizer extends GameRaster {

    @Setter @Getter private static GameRasterizer instance;

    public boolean[] cullFaces = new boolean[6500];
    public boolean[] cullFacesOther = new boolean[6500];
    public int[] vertexScreenX = new int[6500];
    public int[] vertexScreenY = new int[6500];
    public int[] vertexScreenZ = new int[6500];
    public int[] camera_vertex_x = new int[6500];
    public int[] camera_vertex_y = new int[6500];
    public int[] camera_vertex_z = new int[6500];
    public int[] depthListIndices = new int[6000];
    public int[] anIntArray1673 = new int[12];
    public int[] anIntArray1675 = new int[2000];
    public int[] anIntArray1676 = new int[2000];
    public int[] anIntArray1677 = new int[12];
    public int[] anIntArray1678 = new int[10];
    public int[] anIntArray1679 = new int[10];
    public int[] anIntArray1680 = new int[10];
    public int[][] faceList = new int[6000][512];
    public int[][] anIntArrayArray1674 = new int[12][2000];

    public boolean restrictEdges;
    public int[] colourPalette = new int[0x10000];
    public boolean approximateAlphaBlending = true;
    public int currentAlpha;
    public Point2D viewCenter;
    public int[] scanOffsets;
    private boolean currentTextureTransparent;

    public int getFuchsia() {
        return colourPalette[MapRegion.light(ColourUtils.toHsl(128, 255, 127), 96)];
    }

    public void drawGouraudTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int c1, int c2, int c3) {
        int j2 = 0;
        int k2 = 0;
        if (y2 != y1) {
            j2 = (x2 - x1 << 16) / (y2 - y1);
            k2 = (c2 - c1 << 15) / (y2 - y1);
        }

        int l2 = 0;
        int i3 = 0;
        if (y3 != y2) {
            l2 = (x3 - x2 << 16) / (y3 - y2);
            i3 = (c3 - c2 << 15) / (y3 - y2);
        }

        int j3 = 0;
        int k3 = 0;
        if (y3 != y1) {
            j3 = (x1 - x3 << 16) / (y1 - y3);
            k3 = (c1 - c3 << 15) / (y1 - y3);
        }

        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= this.getClipTop())
                return;
            if (y2 > this.getClipTop()) {
                y2 = this.getClipTop();
            }
            if (y3 > this.getClipTop()) {
                y3 = this.getClipTop();
            }
            if (y2 < y3) {
                x3 = x1 <<= 16;
                c3 = c1 <<= 15;
                if (y1 < 0) {
                    x3 -= j3 * y1;
                    x1 -= j2 * y1;
                    c3 -= k3 * y1;
                    c1 -= k2 * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                c2 <<= 15;
                if (y2 < 0) {
                    x2 -= l2 * y2;
                    c2 -= i3 * y2;
                    y2 = 0;
                }
                if (y1 != y2 && j3 < j2 || y1 == y2 && j3 > l2) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += this.width) {
                        drawGouraudScanline(this.raster, y1, x3 >> 16, x1 >> 16, c3 >> 7, c1 >> 7);
                        x3 += j3;
                        x1 += j2;
                        c3 += k3;
                        c1 += k2;
                    }

                    while (--y3 >= 0) {
                        drawGouraudScanline(this.raster, y1, x3 >> 16, x2 >> 16, c3 >> 7, c2 >> 7);
                        x3 += j3;
                        x2 += l2;
                        c3 += k3;
                        c2 += i3;
                        y1 += this.width;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += this.width) {
                    drawGouraudScanline(this.raster, y1, x1 >> 16, x3 >> 16, c1 >> 7, c3 >> 7);
                    x3 += j3;
                    x1 += j2;
                    c3 += k3;
                    c1 += k2;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(this.raster, y1, x2 >> 16, x3 >> 16, c2 >> 7, c3 >> 7);
                    x3 += j3;
                    x2 += l2;
                    c3 += k3;
                    c2 += i3;
                    y1 += this.width;
                }
                return;
            }
            x2 = x1 <<= 16;
            c2 = c1 <<= 15;
            if (y1 < 0) {
                x2 -= j3 * y1;
                x1 -= j2 * y1;
                c2 -= k3 * y1;
                c1 -= k2 * y1;
                y1 = 0;
            }
            x3 <<= 16;
            c3 <<= 15;
            if (y3 < 0) {
                x3 -= l2 * y3;
                c3 -= i3 * y3;
                y3 = 0;
            }
            if (y1 != y3 && j3 < j2 || y1 == y3 && l2 > j2) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += this.width) {
                    drawGouraudScanline(this.raster, y1, x2 >> 16, x1 >> 16, c2 >> 7, c1 >> 7);
                    x2 += j3;
                    x1 += j2;
                    c2 += k3;
                    c1 += k2;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(this.raster, y1, x3 >> 16, x1 >> 16, c3 >> 7, c1 >> 7);
                    x3 += l2;
                    x1 += j2;
                    c3 += i3;
                    c1 += k2;
                    y1 += this.width;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += this.width) {
                drawGouraudScanline(this.raster, y1, x1 >> 16, x2 >> 16, c1 >> 7, c2 >> 7);
                x2 += j3;
                x1 += j2;
                c2 += k3;
                c1 += k2;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(this.raster, y1, x1 >> 16, x3 >> 16, c1 >> 7, c3 >> 7);
                x3 += l2;
                x1 += j2;
                c3 += i3;
                c1 += k2;
                y1 += this.width;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= this.getClipTop())
                return;
            if (y3 > this.getClipTop()) {
                y3 = this.getClipTop();
            }
            if (y1 > this.getClipTop()) {
                y1 = this.getClipTop();
            }
            if (y3 < y1) {
                x1 = x2 <<= 16;
                c1 = c2 <<= 15;
                if (y2 < 0) {
                    x1 -= j2 * y2;
                    x2 -= l2 * y2;
                    c1 -= k2 * y2;
                    c2 -= i3 * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                c3 <<= 15;
                if (y3 < 0) {
                    x3 -= j3 * y3;
                    c3 -= k3 * y3;
                    y3 = 0;
                }
                if (y2 != y3 && j2 < l2 || y2 == y3 && j2 > j3) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += this.width) {
                        drawGouraudScanline(this.raster, y2, x1 >> 16, x2 >> 16, c1 >> 7, c2 >> 7);
                        x1 += j2;
                        x2 += l2;
                        c1 += k2;
                        c2 += i3;
                    }

                    while (--y1 >= 0) {
                        drawGouraudScanline(this.raster, y2, x1 >> 16, x3 >> 16, c1 >> 7, c3 >> 7);
                        x1 += j2;
                        x3 += j3;
                        c1 += k2;
                        c3 += k3;
                        y2 += this.width;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += this.width) {
                    drawGouraudScanline(this.raster, y2, x2 >> 16, x1 >> 16, c2 >> 7, c1 >> 7);
                    x1 += j2;
                    x2 += l2;
                    c1 += k2;
                    c2 += i3;
                }

                while (--y1 >= 0) {
                    drawGouraudScanline(this.raster, y2, x3 >> 16, x1 >> 16, c3 >> 7, c1 >> 7);
                    x1 += j2;
                    x3 += j3;
                    c1 += k2;
                    c3 += k3;
                    y2 += this.width;
                }
                return;
            }
            x3 = x2 <<= 16;
            c3 = c2 <<= 15;
            if (y2 < 0) {
                x3 -= j2 * y2;
                x2 -= l2 * y2;
                c3 -= k2 * y2;
                c2 -= i3 * y2;
                y2 = 0;
            }
            x1 <<= 16;
            c1 <<= 15;
            if (y1 < 0) {
                x1 -= j3 * y1;
                c1 -= k3 * y1;
                y1 = 0;
            }
            if (j2 < l2) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += this.width) {
                    drawGouraudScanline(this.raster, y2, x3 >> 16, x2 >> 16, c3 >> 7, c2 >> 7);
                    x3 += j2;
                    x2 += l2;
                    c3 += k2;
                    c2 += i3;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(this.raster, y2, x1 >> 16, x2 >> 16, c1 >> 7, c2 >> 7);
                    x1 += j3;
                    x2 += l2;
                    c1 += k3;
                    c2 += i3;
                    y2 += this.width;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += this.width) {
                drawGouraudScanline(this.raster, y2, x2 >> 16, x3 >> 16, c2 >> 7, c3 >> 7);
                x3 += j2;
                x2 += l2;
                c3 += k2;
                c2 += i3;
            }

            while (--y3 >= 0) {
                drawGouraudScanline(this.raster, y2, x2 >> 16, x1 >> 16, c2 >> 7, c1 >> 7);
                x1 += j3;
                x2 += l2;
                c1 += k3;
                c2 += i3;
                y2 += this.width;
            }
            return;
        }
        if (y3 >= this.getClipTop())
            return;
        if (y1 > this.getClipTop()) {
            y1 = this.getClipTop();
        }
        if (y2 > this.getClipTop()) {
            y2 = this.getClipTop();
        }
        if (y1 < y2) {
            x2 = x3 <<= 16;
            c2 = c3 <<= 15;
            if (y3 < 0) {
                x2 -= l2 * y3;
                x3 -= j3 * y3;
                c2 -= i3 * y3;
                c3 -= k3 * y3;
                y3 = 0;
            }
            x1 <<= 16;
            c1 <<= 15;
            if (y1 < 0) {
                x1 -= j2 * y1;
                c1 -= k2 * y1;
                y1 = 0;
            }
            if (l2 < j3) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += this.width) {
                    drawGouraudScanline(this.raster, y3, x2 >> 16, x3 >> 16, c2 >> 7, c3 >> 7);
                    x2 += l2;
                    x3 += j3;
                    c2 += i3;
                    c3 += k3;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(this.raster, y3, x2 >> 16, x1 >> 16, c2 >> 7, c1 >> 7);
                    x2 += l2;
                    x1 += j2;
                    c2 += i3;
                    c1 += k2;
                    y3 += this.width;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += this.width) {
                drawGouraudScanline(this.raster, y3, x3 >> 16, x2 >> 16, c3 >> 7, c2 >> 7);
                x2 += l2;
                x3 += j3;
                c2 += i3;
                c3 += k3;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(this.raster, y3, x1 >> 16, x2 >> 16, c1 >> 7, c2 >> 7);
                x2 += l2;
                x1 += j2;
                c2 += i3;
                c1 += k2;
                y3 += this.width;
            }
            return;
        }
        x1 = x3 <<= 16;
        c1 = c3 <<= 15;
        if (y3 < 0) {
            x1 -= l2 * y3;
            x3 -= j3 * y3;
            c1 -= i3 * y3;
            c3 -= k3 * y3;
            y3 = 0;
        }
        x2 <<= 16;
        c2 <<= 15;
        if (y2 < 0) {
            x2 -= j2 * y2;
            c2 -= k2 * y2;
            y2 = 0;
        }
        if (l2 < j3) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += this.width) {
                drawGouraudScanline(this.raster, y3, x1 >> 16, x3 >> 16, c1 >> 7, c3 >> 7);
                x1 += l2;
                x3 += j3;
                c1 += i3;
                c3 += k3;
            }

            while (--y1 >= 0) {
                drawGouraudScanline(this.raster, y3, x2 >> 16, x3 >> 16, c2 >> 7, c3 >> 7);
                x2 += j2;
                x3 += j3;
                c2 += k2;
                c3 += k3;
                y3 += this.width;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += this.width) {
            drawGouraudScanline(this.raster, y3, x3 >> 16, x1 >> 16, c3 >> 7, c1 >> 7);
            x1 += l2;
            x3 += j3;
            c1 += i3;
            c3 += k3;
        }

        while (--y1 >= 0) {
            drawGouraudScanline(this.raster, y3, x3 >> 16, x2 >> 16, c3 >> 7, c2 >> 7);
            x2 += j2;
            x3 += j3;
            c2 += k2;
            c3 += k3;
            y3 += this.width;
        }
    }

	public void drawGouraudScanline(int[] dest, int offset, int x1, int x2, int c1, int c2) {
		int length;
		if (approximateAlphaBlending) {
			int l1;
			if (restrictEdges) {
				if (x2 - x1 > 3) {
					l1 = (c2 - c1) / (x2 - x1);
				} else {
					l1 = 0;
				}
				if (x2 > this.getMaxRight()) {
					x2 = this.getMaxRight();
				}
				if (x1 < 0) {
					c1 -= x1 * l1;
					x1 = 0;
				}
				if (x1 >= x2) {
					return;
				}
				offset += x1;
				length = x2 - x1 >> 2;
				l1 <<= 2;
			} else {
				if (x1 >= x2) {
					return;
				}
				offset += x1;
				length = x2 - x1 >> 2;
				if (length > 0) {
					l1 = (c2 - c1) * Constants.SHADOW_DECAY[length] >> 15;
				} else {
					l1 = 0;
				}
			}
			if (currentAlpha == 0) {
				while (--length >= 0) {
					int rgb = colourPalette[c1 >> 8];
					c1 += l1;
					dest[offset++] = rgb;
					dest[offset++] = rgb;
					dest[offset++] = rgb;
					dest[offset++] = rgb;
				}
				length = x2 - x1 & 3;
				if (length > 0) {
					int rgb = colourPalette[c1 >> 8];
					do {
						dest[offset++] = rgb;
					} while (--length > 0);
					return;
				}
			} else {
				int a1 = currentAlpha;
				int a2 = 256 - currentAlpha;
				while (--length >= 0) {
					int rgb = colourPalette[c1 >> 8];
					c1 += l1;
					rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
					dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
					dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
					dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
					dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
				}
				length = x2 - x1 & 3;
				if (length > 0) {
					int rgb = colourPalette[c1 >> 8];
					rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
					do {
						dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
					} while (--length > 0);
				}
			}
			return;
		}
		if (x1 >= x2) {
			return;
		}
		int i2 = (c2 - c1) / (x2 - x1);
		if (restrictEdges) {
			if (x2 > this.getMaxRight()) {
				x2 = this.getMaxRight();
			}
			if (x1 < 0) {
				c1 -= x1 * i2;
				x1 = 0;
			}
			if (x1 >= x2) {
				return;
			}
		}
		offset += x1;
		length = x2 - x1;
		if (currentAlpha == 0) {
			do {
				dest[offset++] = colourPalette[c1 >> 8];
				c1 += i2;
			} while (--length > 0);
			return;
		}
		int a1 = currentAlpha;
		int a2 = 256 - currentAlpha;
		do {
			int rgb = colourPalette[c1 >> 8];
			c1 += i2;
			rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
			dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
		} while (--length > 0);
	}

    public void drawGouraudTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int rgb) {
        int l1 = 0;
        if (y2 != y1) {
            l1 = (x2 - x1 << 16) / (y2 - y1);
        }
        int i2 = 0;
        if (y3 != y2) {
            i2 = (x3 - x2 << 16) / (y3 - y2);
        }
        int j2 = 0;
        if (y3 != y1) {
            j2 = (x1 - x3 << 16) / (y1 - y3);
        }
        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= this.getClipTop())
                return;
            if (y2 > this.getClipTop()) {
                y2 = this.getClipTop();
            }
            if (y3 > this.getClipTop()) {
                y3 = this.getClipTop();
            }
            if (y2 < y3) {
                x3 = x1 <<= 16;
                if (y1 < 0) {
                    x3 -= j2 * y1;
                    x1 -= l1 * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                if (y2 < 0) {
                    x2 -= i2 * y2;
                    y2 = 0;
                }
                if (y1 != y2 && j2 < l1 || y1 == y2 && j2 > i2) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += this.width) {
                        drawGouraudScanline(this.raster, y1, rgb, x3 >> 16, x1 >> 16);
                        x3 += j2;
                        x1 += l1;
                    }

                    while (--y3 >= 0) {
                        drawGouraudScanline(this.raster, y1, rgb, x3 >> 16, x2 >> 16);
                        x3 += j2;
                        x2 += i2;
                        y1 += this.width;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += this.width) {
                    drawGouraudScanline(this.raster, y1, rgb, x1 >> 16, x3 >> 16);
                    x3 += j2;
                    x1 += l1;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(this.raster, y1, rgb, x2 >> 16, x3 >> 16);
                    x3 += j2;
                    x2 += i2;
                    y1 += this.width;
                }
                return;
            }
            x2 = x1 <<= 16;
            if (y1 < 0) {
                x2 -= j2 * y1;
                x1 -= l1 * y1;
                y1 = 0;
            }
            x3 <<= 16;
            if (y3 < 0) {
                x3 -= i2 * y3;
                y3 = 0;
            }
            if (y1 != y3 && j2 < l1 || y1 == y3 && i2 > l1) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += this.width) {
                    drawGouraudScanline(this.raster, y1, rgb, x2 >> 16, x1 >> 16);
                    x2 += j2;
                    x1 += l1;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(this.raster, y1, rgb, x3 >> 16, x1 >> 16);
                    x3 += i2;
                    x1 += l1;
                    y1 += this.width;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += this.width) {
                drawGouraudScanline(this.raster, y1, rgb, x1 >> 16, x2 >> 16);
                x2 += j2;
                x1 += l1;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(this.raster, y1, rgb, x1 >> 16, x3 >> 16);
                x3 += i2;
                x1 += l1;
                y1 += this.width;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= this.getClipTop())
                return;
            if (y3 > this.getClipTop()) {
                y3 = this.getClipTop();
            }
            if (y1 > this.getClipTop()) {
                y1 = this.getClipTop();
            }
            if (y3 < y1) {
                x1 = x2 <<= 16;
                if (y2 < 0) {
                    x1 -= l1 * y2;
                    x2 -= i2 * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                if (y3 < 0) {
                    x3 -= j2 * y3;
                    y3 = 0;
                }
                if (y2 != y3 && l1 < i2 || y2 == y3 && l1 > j2) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += this.width) {
                        drawGouraudScanline(this.raster, y2, rgb, x1 >> 16, x2 >> 16);
                        x1 += l1;
                        x2 += i2;
                    }

                    while (--y1 >= 0) {
                        drawGouraudScanline(this.raster, y2, rgb, x1 >> 16, x3 >> 16);
                        x1 += l1;
                        x3 += j2;
                        y2 += this.width;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += this.width) {
                    drawGouraudScanline(this.raster, y2, rgb, x2 >> 16, x1 >> 16);
                    x1 += l1;
                    x2 += i2;
                }

                while (--y1 >= 0) {
                    drawGouraudScanline(this.raster, y2, rgb, x3 >> 16, x1 >> 16);
                    x1 += l1;
                    x3 += j2;
                    y2 += this.width;
                }
                return;
            }
            x3 = x2 <<= 16;
            if (y2 < 0) {
                x3 -= l1 * y2;
                x2 -= i2 * y2;
                y2 = 0;
            }
            x1 <<= 16;
            if (y1 < 0) {
                x1 -= j2 * y1;
                y1 = 0;
            }
            if (l1 < i2) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += this.width) {
                    drawGouraudScanline(this.raster, y2, rgb, x3 >> 16, x2 >> 16);
                    x3 += l1;
                    x2 += i2;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(this.raster, y2, rgb, x1 >> 16, x2 >> 16);
                    x1 += j2;
                    x2 += i2;
                    y2 += this.width;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += this.width) {
                drawGouraudScanline(this.raster, y2, rgb, x2 >> 16, x3 >> 16);
                x3 += l1;
                x2 += i2;
            }

            while (--y3 >= 0) {
                drawGouraudScanline(this.raster, y2, rgb, x2 >> 16, x1 >> 16);
                x1 += j2;
                x2 += i2;
                y2 += this.width;
            }
            return;
        }
        if (y3 >= this.getClipTop())
            return;
        if (y1 > this.getClipTop()) {
            y1 = this.getClipTop();
        }
        if (y2 > this.getClipTop()) {
            y2 = this.getClipTop();
        }
        if (y1 < y2) {
            x2 = x3 <<= 16;
            if (y3 < 0) {
                x2 -= i2 * y3;
                x3 -= j2 * y3;
                y3 = 0;
            }
            x1 <<= 16;
            if (y1 < 0) {
                x1 -= l1 * y1;
                y1 = 0;
            }
            if (i2 < j2) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += this.width) {
                    drawGouraudScanline(this.raster, y3, rgb, x2 >> 16, x3 >> 16);
                    x2 += i2;
                    x3 += j2;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(this.raster, y3, rgb, x2 >> 16, x1 >> 16);
                    x2 += i2;
                    x1 += l1;
                    y3 += this.width;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += this.width) {
                drawGouraudScanline(this.raster, y3, rgb, x3 >> 16, x2 >> 16);
                x2 += i2;
                x3 += j2;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(this.raster, y3, rgb, x1 >> 16, x2 >> 16);
                x2 += i2;
                x1 += l1;
                y3 += this.width;
            }
            return;
        }
        x1 = x3 <<= 16;
        if (y3 < 0) {
            x1 -= i2 * y3;
            x3 -= j2 * y3;
            y3 = 0;
        }
        x2 <<= 16;
        if (y2 < 0) {
            x2 -= l1 * y2;
            y2 = 0;
        }
        if (i2 < j2) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += this.width) {
                drawGouraudScanline(this.raster, y3, rgb, x1 >> 16, x3 >> 16);
                x1 += i2;
                x3 += j2;
            }

            while (--y1 >= 0) {
                drawGouraudScanline(this.raster, y3, rgb, x2 >> 16, x3 >> 16);
                x2 += l1;
                x3 += j2;
                y3 += this.width;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += this.width) {
            drawGouraudScanline(this.raster, y3, rgb, x3 >> 16, x1 >> 16);
            x1 += i2;
            x3 += j2;
        }

        while (--y1 >= 0) {
            drawGouraudScanline(this.raster, y3, rgb, x3 >> 16, x2 >> 16);
            x2 += l1;
            x3 += j2;
            y3 += this.width;
        }
    }

    public void drawGouraudScanline(int[] dest, int offset, int rgb, int x1, int x2) {
        if (restrictEdges) {
            if (x2 > this.getMaxRight()) {
                x2 = this.getMaxRight();
            }
            if (x1 < 0) {
                x1 = 0;
            }
        }
        if (x1 >= x2) {
            return;
        }
        offset += x1;
        int length = x2 - x1 >> 2;
        if (currentAlpha == 0) {
            while (--length >= 0) {
                dest[offset++] = rgb;
                dest[offset++] = rgb;
                dest[offset++] = rgb;
                dest[offset++] = rgb;
            }
            for (length = x2 - x1 & 3; --length >= 0; ) {
                dest[offset++] = rgb;
            }
            return;
        }
        int a1 = currentAlpha;
        int a2 = 256 - currentAlpha;
        rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
        while (--length >= 0) {
            dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
            dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
            dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
            dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
        }
        for (length = x2 - x1 & 3; --length >= 0; ) {
            dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
        }
    }

    public void drawTexturedTriangle(int faceYX, int faceYY, int faceYZ, int faceXX, int faceXY, int faceXZ, int k1, int l1, int i2, int j2,
                                     int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4, int textureId) {

        int[] pixels = TextureLoader.getTexturePixels(textureId);
        if (pixels == null) {
            drawGouraudTriangle(faceYX, faceYY, faceYZ, faceXX, faceXY, faceXZ, k1, l1, i2);
            return;
        }
        currentTextureTransparent = !TextureLoader.getTextureTransparent(textureId);
        k2 = j2 - k2;
        j3 = i3 - j3;
        i4 = l3 - i4;
        l2 -= j2;
        k3 -= i3;
        j4 -= l3;
        int l4 = l2 * i3 - k3 * j2 << 14;
        int i5 = k3 * l3 - j4 * i3 << 8;
        int j5 = j4 * j2 - l2 * l3 << 5;
        int k5 = k2 * i3 - j3 * j2 << 14;
        int l5 = j3 * l3 - i4 * i3 << 8;
        int i6 = i4 * j2 - k2 * l3 << 5;
        int j6 = j3 * l2 - k2 * k3 << 14;
        int k6 = i4 * k3 - j3 * j4 << 8;
        int l6 = k2 * j4 - i4 * l2 << 5;
        int i7 = 0;
        int j7 = 0;
        if (faceYY != faceYX) {
            i7 = (faceXY - faceXX << 16) / (faceYY - faceYX);
            j7 = (l1 - k1 << 16) / (faceYY - faceYX);
        }
        int k7 = 0;
        int l7 = 0;
        if (faceYZ != faceYY) {
            k7 = (faceXZ - faceXY << 16) / (faceYZ - faceYY);
            l7 = (i2 - l1 << 16) / (faceYZ - faceYY);
        }
        int i8 = 0;
        int j8 = 0;
        if (faceYZ != faceYX) {
            i8 = (faceXX - faceXZ << 16) / (faceYX - faceYZ);
            j8 = (k1 - i2 << 16) / (faceYX - faceYZ);
        }
        if (faceYX <= faceYY && faceYX <= faceYZ) {
            if (faceYX >= this.getClipTop())
                return;
            if (faceYY > this.getClipTop()) {
                faceYY = this.getClipTop();
            }
            if (faceYZ > this.getClipTop()) {
                faceYZ = this.getClipTop();
            }
            if (faceYY < faceYZ) {
                faceXZ = faceXX <<= 16;
                i2 = k1 <<= 16;
                if (faceYX < 0) {
                    faceXZ -= i8 * faceYX;
                    faceXX -= i7 * faceYX;
                    i2 -= j8 * faceYX;
                    k1 -= j7 * faceYX;
                    faceYX = 0;
                }
                faceXY <<= 16;
                l1 <<= 16;
                if (faceYY < 0) {
                    faceXY -= k7 * faceYY;
                    l1 -= l7 * faceYY;
                    faceYY = 0;
                }
                int k8 = faceYX - viewCenter.getY();
                l4 += j5 * k8;
                k5 += i6 * k8;
                j6 += l6 * k8;
                if (faceYX != faceYY && i8 < i7 || faceYX == faceYY && i8 > k7) {
                    faceYZ -= faceYY;
                    faceYY -= faceYX;
                    faceYX = scanOffsets[faceYX];
                    while (--faceYY >= 0) {
                        drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5,
                                l5, k6);
                        faceXZ += i8;
                        faceXX += i7;
                        i2 += j8;
                        k1 += j7;
                        faceYX += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--faceYZ >= 0) {
                        drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5,
                                l5, k6);
                        faceXZ += i8;
                        faceXY += k7;
                        i2 += j8;
                        l1 += l7;
                        faceYX += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                faceYZ -= faceYY;
                faceYY -= faceYX;
                faceYX = scanOffsets[faceYX];
                while (--faceYY >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXZ += i8;
                    faceXX += i7;
                    i2 += j8;
                    k1 += j7;
                    faceYX += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--faceYZ >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXZ += i8;
                    faceXY += k7;
                    i2 += j8;
                    l1 += l7;
                    faceYX += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            faceXY = faceXX <<= 16;
            l1 = k1 <<= 16;
            if (faceYX < 0) {
                faceXY -= i8 * faceYX;
                faceXX -= i7 * faceYX;
                l1 -= j8 * faceYX;
                k1 -= j7 * faceYX;
                faceYX = 0;
            }
            faceXZ <<= 16;
            i2 <<= 16;
            if (faceYZ < 0) {
                faceXZ -= k7 * faceYZ;
                i2 -= l7 * faceYZ;
                faceYZ = 0;
            }
            int l8 = faceYX - viewCenter.getY();
            l4 += j5 * l8;
            k5 += i6 * l8;
            j6 += l6 * l8;
            if (faceYX != faceYZ && i8 < i7 || faceYX == faceYZ && k7 > i7) {
                faceYY -= faceYZ;
                faceYZ -= faceYX;
                faceYX = scanOffsets[faceYX];
                while (--faceYZ >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXY += i8;
                    faceXX += i7;
                    l1 += j8;
                    k1 += j7;
                    faceYX += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--faceYY >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXZ += k7;
                    faceXX += i7;
                    i2 += l7;
                    k1 += j7;
                    faceYX += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            faceYY -= faceYZ;
            faceYZ -= faceYX;
            faceYX = scanOffsets[faceYX];
            while (--faceYZ >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
                faceXY += i8;
                faceXX += i7;
                l1 += j8;
                k1 += j7;
                faceYX += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--faceYY >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYX, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
                faceXZ += k7;
                faceXX += i7;
                i2 += l7;
                k1 += j7;
                faceYX += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (faceYY <= faceYZ) {
            if (faceYY >= this.getClipTop())
                return;
            if (faceYZ > this.getClipTop()) {
                faceYZ = this.getClipTop();
            }
            if (faceYX > this.getClipTop()) {
                faceYX = this.getClipTop();
            }
            if (faceYZ < faceYX) {
                faceXX = faceXY <<= 16;
                k1 = l1 <<= 16;
                if (faceYY < 0) {
                    faceXX -= i7 * faceYY;
                    faceXY -= k7 * faceYY;
                    k1 -= j7 * faceYY;
                    l1 -= l7 * faceYY;
                    faceYY = 0;
                }
                faceXZ <<= 16;
                i2 <<= 16;
                if (faceYZ < 0) {
                    faceXZ -= i8 * faceYZ;
                    i2 -= j8 * faceYZ;
                    faceYZ = 0;
                }
                int i9 = faceYY - viewCenter.getY();
                l4 += j5 * i9;
                k5 += i6 * i9;
                j6 += l6 * i9;
                if (faceYY != faceYZ && i7 < k7 || faceYY == faceYZ && i7 > i8) {
                    faceYX -= faceYZ;
                    faceYZ -= faceYY;
                    faceYY = scanOffsets[faceYY];
                    while (--faceYZ >= 0) {
                        drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5,
                                l5, k6);
                        faceXX += i7;
                        faceXY += k7;
                        k1 += j7;
                        l1 += l7;
                        faceYY += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--faceYX >= 0) {
                        drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5,
                                l5, k6);
                        faceXX += i7;
                        faceXZ += i8;
                        k1 += j7;
                        i2 += j8;
                        faceYY += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                faceYX -= faceYZ;
                faceYZ -= faceYY;
                faceYY = scanOffsets[faceYY];
                while (--faceYZ >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXX += i7;
                    faceXY += k7;
                    k1 += j7;
                    l1 += l7;
                    faceYY += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--faceYX >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXX += i7;
                    faceXZ += i8;
                    k1 += j7;
                    i2 += j8;
                    faceYY += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            faceXZ = faceXY <<= 16;
            i2 = l1 <<= 16;
            if (faceYY < 0) {
                faceXZ -= i7 * faceYY;
                faceXY -= k7 * faceYY;
                i2 -= j7 * faceYY;
                l1 -= l7 * faceYY;
                faceYY = 0;
            }
            faceXX <<= 16;
            k1 <<= 16;
            if (faceYX < 0) {
                faceXX -= i8 * faceYX;
                k1 -= j8 * faceYX;
                faceYX = 0;
            }
            int j9 = faceYY - viewCenter.getY();
            l4 += j5 * j9;
            k5 += i6 * j9;
            j6 += l6 * j9;
            if (i7 < k7) {
                faceYZ -= faceYX;
                faceYX -= faceYY;
                faceYY = scanOffsets[faceYY];
                while (--faceYX >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXZ += i7;
                    faceXY += k7;
                    i2 += j7;
                    l1 += l7;
                    faceYY += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--faceYZ >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXX += i8;
                    faceXY += k7;
                    k1 += j8;
                    l1 += l7;
                    faceYY += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            faceYZ -= faceYX;
            faceYX -= faceYY;
            faceYY = scanOffsets[faceYY];
            while (--faceYX >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
                faceXZ += i7;
                faceXY += k7;
                i2 += j7;
                l1 += l7;
                faceYY += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--faceYZ >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYY, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
                faceXX += i8;
                faceXY += k7;
                k1 += j8;
                l1 += l7;
                faceYY += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (faceYZ >= this.getClipTop())
            return;
        if (faceYX > this.getClipTop()) {
            faceYX = this.getClipTop();
        }
        if (faceYY > this.getClipTop()) {
            faceYY = this.getClipTop();
        }
        if (faceYX < faceYY) {
            faceXY = faceXZ <<= 16;
            l1 = i2 <<= 16;
            if (faceYZ < 0) {
                faceXY -= k7 * faceYZ;
                faceXZ -= i8 * faceYZ;
                l1 -= l7 * faceYZ;
                i2 -= j8 * faceYZ;
                faceYZ = 0;
            }
            faceXX <<= 16;
            k1 <<= 16;
            if (faceYX < 0) {
                faceXX -= i7 * faceYX;
                k1 -= j7 * faceYX;
                faceYX = 0;
            }
            int k9 = faceYZ - viewCenter.getY();
            l4 += j5 * k9;
            k5 += i6 * k9;
            j6 += l6 * k9;
            if (k7 < i8) {
                faceYY -= faceYX;
                faceYX -= faceYZ;
                faceYZ = scanOffsets[faceYZ];
                while (--faceYX >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXY += k7;
                    faceXZ += i8;
                    l1 += l7;
                    i2 += j8;
                    faceYZ += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--faceYY >= 0) {
                    drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXY >> 16, faceXX >> 16, l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5,
                            k6);
                    faceXY += k7;
                    faceXX += i7;
                    l1 += l7;
                    k1 += j7;
                    faceYZ += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            faceYY -= faceYX;
            faceYX -= faceYZ;
            faceYZ = scanOffsets[faceYZ];
            while (--faceYX >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
                faceXY += k7;
                faceXZ += i8;
                l1 += l7;
                i2 += j8;
                faceYZ += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--faceYY >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXX >> 16, faceXY >> 16, k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
                faceXY += k7;
                faceXX += i7;
                l1 += l7;
                k1 += j7;
                faceYZ += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        faceXX = faceXZ <<= 16;
        k1 = i2 <<= 16;
        if (faceYZ < 0) {
            faceXX -= k7 * faceYZ;
            faceXZ -= i8 * faceYZ;
            k1 -= l7 * faceYZ;
            i2 -= j8 * faceYZ;
            faceYZ = 0;
        }
        faceXY <<= 16;
        l1 <<= 16;
        if (faceYY < 0) {
            faceXY -= i7 * faceYY;
            l1 -= j7 * faceYY;
            faceYY = 0;
        }
        int l9 = faceYZ - viewCenter.getY();
        l4 += j5 * l9;
        k5 += i6 * l9;
        j6 += l6 * l9;
        if (k7 < i8) {
            faceYX -= faceYY;
            faceYY -= faceYZ;
            faceYZ = scanOffsets[faceYZ];
            while (--faceYY >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXX >> 16, faceXZ >> 16, k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
                faceXX += k7;
                faceXZ += i8;
                k1 += l7;
                i2 += j8;
                faceYZ += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--faceYX >= 0) {
                drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXY >> 16, faceXZ >> 16, l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
                faceXY += i7;
                faceXZ += i8;
                l1 += j7;
                i2 += j8;
                faceYZ += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        faceYX -= faceYY;
        faceYY -= faceYZ;
        faceYZ = scanOffsets[faceYZ];
        while (--faceYY >= 0) {
            drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXZ >> 16, faceXX >> 16, i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
            faceXX += k7;
            faceXZ += i8;
            k1 += l7;
            i2 += j8;
            faceYZ += this.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
        while (--faceYX >= 0) {
            drawTexturedLine(this.raster, pixels, 0, 0, faceYZ, faceXZ >> 16, faceXY >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
            faceXY += i7;
            faceXZ += i8;
            l1 += j7;
            i2 += j8;
            faceYZ += this.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
    }

    public void drawTexturedLine(int[] ai, int[] ai1, int i, int j, int k, int l, int i1, int j1, int k1, int l1,
                                 int i2, int j2, int k2, int l2, int i3) {
        if (l >= i1)
            return;
        int j3;
        int k3;
        if (restrictEdges) {
            j3 = (k1 - j1) / (i1 - l);
            if (i1 > this.getMaxRight()) {
                i1 = this.getMaxRight();
            }
            if (l < 0) {
                j1 -= l * j3;
                l = 0;
            }
            if (l >= i1)
                return;
            k3 = i1 - l >> 3;
            j3 <<= 12;
            j1 <<= 9;
        } else {
            if (i1 - l > 7) {
                k3 = i1 - l >> 3;
                j3 = (k1 - j1) * Constants.SHADOW_DECAY[k3] >> 6;
            } else {
                k3 = 0;
                j3 = 0;
            }
            j1 <<= 9;
        }
        k += l;
        int j4 = 0;
        int l4 = 0;
        int l6 = l - viewCenter.getX();
        l1 += (k2 >> 3) * l6;
        i2 += (l2 >> 3) * l6;
        j2 += (i3 >> 3) * l6;
        int l5 = j2 >> 14;
        if (l5 != 0) {
            i = l1 / l5;
            j = i2 / l5;
            if (i < 0) {
                i = 0;
            } else if (i > 16256) {
                i = 16256;
            }
        }
        l1 += k2;
        i2 += l2;
        j2 += i3;
        l5 = j2 >> 14;
        if (l5 != 0) {
            j4 = l1 / l5;
            l4 = i2 / l5;
            if (j4 < 7) {
                j4 = 7;
            } else if (j4 > 16256) {
                j4 = 16256;
            }
        }
        int j7 = j4 - i >> 3;
        int l7 = l4 - j >> 3;
        i += j1 & 0x600000;
        int j8 = j1 >> 23;
        if (currentTextureTransparent) {
            while (k3-- > 0) {
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i = j4;
                j = l4;
                l1 += k2;
                i2 += l2;
                j2 += i3;
                int i6 = j2 >> 14;
                if (i6 != 0) {
                    j4 = l1 / i6;
                    l4 = i2 / i6;
                    if (j4 < 7) {
                        j4 = 7;
                    } else if (j4 > 16256) {
                        j4 = 16256;
                    }
                }
                j7 = j4 - i >> 3;
                l7 = l4 - j >> 3;
                j1 += j3;
                i += j1 & 0x600000;
                j8 = j1 >> 23;
            }
            for (k3 = i1 - l & 7; k3-- > 0; ) {
                ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
                i += j7;
                j += l7;
            }

            return;
        }
        while (k3-- > 0) {
            int i9;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i += j7;
            j += l7;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i += j7;
            j += l7;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i += j7;
            j += l7;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i += j7;
            j += l7;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i += j7;
            j += l7;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i += j7;
            j += l7;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i += j7;
            j += l7;
            if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = i9;
            }
            k++;
            i = j4;
            j = l4;
            l1 += k2;
            i2 += l2;
            j2 += i3;
            int j6 = j2 >> 14;
            if (j6 != 0) {
                j4 = l1 / j6;
                l4 = i2 / j6;
                if (j4 < 7) {
                    j4 = 7;
                } else if (j4 > 16256) {
                    j4 = 16256;
                }
            }
            j7 = j4 - i >> 3;
            l7 = l4 - j >> 3;
            j1 += j3;
            i += j1 & 0x600000;
            j8 = j1 >> 23;
        }
        for (int l3 = i1 - l & 7; l3-- > 0; ) {
            int j9;
            if ((j9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0) {
                ai[k] = j9;
            }
            k++;
            i += j7;
            j += l7;
        }
    }

    public void setBrightness(double exponent) {
        int j = 0;
        for (int k = 0; k < 512; k++) {
            double d1 = (k / 8) / 64D + 0.0078125D;
            double d2 = (k & 7) / 8D + 0.0625D;

            for (int k1 = 0; k1 < 128; k1++) {
                double initial = k1 / 128D;
                double r = initial;
                double g = initial;
                double b = initial;

                if (d2 != 0.0D) {
                    double d7;
                    if (initial < 0.5D) {
                        d7 = initial * (1.0D + d2);
                    } else {
                        d7 = initial + d2 - initial * d2;
                    }

                    double d8 = 2D * initial - d7;
                    double d9 = d1 + 0.33333333333333331D;
                    if (d9 > 1.0D) {
                        d9--;
                    }

                    double d10 = d1;
                    double d11 = d1 - 0.33333333333333331D;
                    if (d11 < 0.0D) {
                        d11++;
                    }

                    if (6D * d9 < 1.0D) {
                        r = d8 + (d7 - d8) * 6D * d9;
                    } else if (2D * d9 < 1.0D) {
                        r = d7;
                    } else if (3D * d9 < 2D) {
                        r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
                    } else {
                        r = d8;
                    }

                    if (6D * d10 < 1.0D) {
                        g = d8 + (d7 - d8) * 6D * d10;
                    } else if (2D * d10 < 1.0D) {
                        g = d7;
                    } else if (3D * d10 < 2D) {
                        g = d8 + (d7 - d8) * (0.66666666666666663D - d10) * 6D;
                    } else {
                        g = d8;
                    }

                    if (6D * d11 < 1.0D) {
                        b = d8 + (d7 - d8) * 6D * d11;
                    } else if (2D * d11 < 1.0D) {
                        b = d7;
                    } else if (3D * d11 < 2D) {
                        b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
                    } else {
                        b = d8;
                    }
                }
                int newR = (int) (r * 256D);
                int newG = (int) (g * 256D);
                int newB = (int) (b * 256D);
                int rgb = (newR << 16) + (newG << 8) + newB;

                rgb = ColourUtils.exponent(rgb, exponent);
                if (rgb == 0) {
                    rgb = 1;
                }

                colourPalette[j++] = rgb;
            }
        }
    }

    public void setTextureBrightness(double exponent) {
        if (TextureLoader.instance != null) {
            TextureLoader.instance.setBrightness(exponent);
        }
    }

    public void useViewport() {
        scanOffsets = new int[this.height];
        for (int i = 0; i < this.height; i++) {
            scanOffsets[i] = this.width * i;
        }

        viewCenter = new Point2D(this.width / 2, this.height / 2);
    }
}