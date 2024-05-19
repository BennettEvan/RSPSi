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

    public void drawTexturedTriangle_Model(int y1, int y2, int y3, int x1, int x2, int x3, int c1, int c2, int c3, int tx1, int tx2,
                                           int tx3, int ty1, int ty2, int ty3, int tz1, int tz2, int tz3, int textureId) {
        int[] texturePixels = TextureLoader.getTexturePixels(textureId);
        if (texturePixels == null) {
            int rgb = TextureLoader.getTexture(textureId).averageTextureColour();
            drawGouraudTriangle(y1, y2, y3, x1, x2, x3, method965(rgb, c1), method965(rgb, c2), method965(rgb, c3));
        } else {
            currentTextureTransparent = !TextureLoader.getTextureTransparent(textureId);
            int int_19 = x2 - x1;
            int int_20 = y2 - y1;
            int int_21 = x3 - x1;
            int int_22 = y3 - y1;
            int int_23 = c2 - c1;
            int int_24 = c3 - c1;
            int int_25 = 0;
            if (y1 != y2) {
                int_25 = (x2 - x1 << 14) / (y2 - y1);
            }

            int int_26 = 0;
            if (y3 != y2) {
                int_26 = (x3 - x2 << 14) / (y3 - y2);
            }

            int int_27 = 0;
            if (y1 != y3) {
                int_27 = (x1 - x3 << 14) / (y1 - y3);
            }

            int int_28 = int_19 * int_22 - int_21 * int_20;
            if (int_28 != 0) {
                int int_29 = (int_23 * int_22 - int_24 * int_20 << 9) / int_28;
                int int_30 = (int_24 * int_19 - int_23 * int_21 << 9) / int_28;
                tx2 = tx1 - tx2;
                ty2 = ty1 - ty2;
                tz2 = tz1 - tz2;
                tx3 -= tx1;
                ty3 -= ty1;
                tz3 -= tz1;

                final int FOV = 512;
                int int_31 = tx3 * ty1 - tx1 * ty3 << 14;
                int int_32 = (int) (((long) (tz1 * ty3 - tz3 * ty1) << 3 << 14) / (long) FOV);
                int int_33 = (int) (((long) (tz3 * tx1 - tx3 * tz1) << 14) / (long) FOV);
                int int_34 = tx2 * ty1 - ty2 * tx1 << 14;
                int int_35 = (int) (((long) (ty2 * tz1 - tz2 * ty1) << 3 << 14) / (long) FOV);
                int int_36 = (int) (((long) (tz2 * tx1 - tx2 * tz1) << 14) / (long) FOV);
                int int_37 = ty2 * tx3 - tx2 * ty3 << 14;
                int int_38 = (int) (((long) (tz2 * ty3 - ty2 * tz3) << 3 << 14) / (long) FOV);
                int int_39 = (int) (((long) (tz3 * tx2 - tx3 * tz2) << 14) / (long) FOV);
                int int_40;
                if (y1 <= y2 && y1 <= y3) {
                    if (y1 < this.getClipTop()) {
                        if (y2 > this.getClipTop()) {
                            y2 = this.getClipTop();
                        }

                        if (y3 > this.getClipTop()) {
                            y3 = this.getClipTop();
                        }

                        c1 = int_29 + ((c1 << 9) - x1 * int_29);
                        if (y2 < y3) {
                            x3 = x1 <<= 14;
                            if (y1 < 0) {
                                x3 -= y1 * int_27;
                                x1 -= y1 * int_25;
                                c1 -= y1 * int_30;
                                y1 = 0;
                            }

                            x2 <<= 14;
                            if (y2 < 0) {
                                x2 -= int_26 * y2;
                                y2 = 0;
                            }

                            int_40 = y1 - viewCenter.getY();
                            int_31 += int_33 * int_40;
                            int_34 += int_36 * int_40;
                            int_37 += int_39 * int_40;
                            if (y1 != y2 && int_27 < int_25 || y1 == y2 && int_27 > int_26) {
                                y3 -= y2;
                                y2 -= y1;
                                y1 = scanOffsets[y1];

                                while (true) {
                                    --y2;
                                    if (y2 < 0) {
                                        while (true) {
                                            --y3;
                                            if (y3 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y1, x3 >> 14, x2 >> 14, c1, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x3 += int_27;
                                            x2 += int_26;
                                            c1 += int_30;
                                            y1 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y1, x3 >> 14, x1 >> 14, c1, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x3 += int_27;
                                    x1 += int_25;
                                    c1 += int_30;
                                    y1 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            } else {
                                y3 -= y2;
                                y2 -= y1;
                                y1 = scanOffsets[y1];

                                while (true) {
                                    --y2;
                                    if (y2 < 0) {
                                        while (true) {
                                            --y3;
                                            if (y3 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y1, x2 >> 14, x3 >> 14, c1, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x3 += int_27;
                                            x2 += int_26;
                                            c1 += int_30;
                                            y1 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y1, x1 >> 14, x3 >> 14, c1, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x3 += int_27;
                                    x1 += int_25;
                                    c1 += int_30;
                                    y1 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            }
                        } else {
                            x2 = x1 <<= 14;
                            if (y1 < 0) {
                                x2 -= y1 * int_27;
                                x1 -= y1 * int_25;
                                c1 -= y1 * int_30;
                                y1 = 0;
                            }

                            x3 <<= 14;
                            if (y3 < 0) {
                                x3 -= int_26 * y3;
                                y3 = 0;
                            }

                            int_40 = y1 - viewCenter.getY();
                            int_31 += int_33 * int_40;
                            int_34 += int_36 * int_40;
                            int_37 += int_39 * int_40;
                            if (y1 != y3 && int_27 < int_25 || y1 == y3 && int_26 > int_25) {
                                y2 -= y3;
                                y3 -= y1;
                                y1 = scanOffsets[y1];

                                while (true) {
                                    --y3;
                                    if (y3 < 0) {
                                        while (true) {
                                            --y2;
                                            if (y2 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y1, x3 >> 14, x1 >> 14, c1, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x3 += int_26;
                                            x1 += int_25;
                                            c1 += int_30;
                                            y1 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y1, x2 >> 14, x1 >> 14, c1, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x2 += int_27;
                                    x1 += int_25;
                                    c1 += int_30;
                                    y1 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            } else {
                                y2 -= y3;
                                y3 -= y1;
                                y1 = scanOffsets[y1];

                                while (true) {
                                    --y3;
                                    if (y3 < 0) {
                                        while (true) {
                                            --y2;
                                            if (y2 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y1, x1 >> 14, x3 >> 14, c1, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x3 += int_26;
                                            x1 += int_25;
                                            c1 += int_30;
                                            y1 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y1, x1 >> 14, x2 >> 14, c1, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x2 += int_27;
                                    x1 += int_25;
                                    c1 += int_30;
                                    y1 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            }
                        }
                    }
                } else if (y2 <= y3) {
                    if (y2 < this.getClipTop()) {
                        if (y3 > this.getClipTop()) {
                            y3 = this.getClipTop();
                        }

                        if (y1 > this.getClipTop()) {
                            y1 = this.getClipTop();
                        }

                        c2 = int_29 + ((c2 << 9) - int_29 * x2);
                        if (y3 < y1) {
                            x1 = x2 <<= 14;
                            if (y2 < 0) {
                                x1 -= int_25 * y2;
                                x2 -= int_26 * y2;
                                c2 -= int_30 * y2;
                                y2 = 0;
                            }

                            x3 <<= 14;
                            if (y3 < 0) {
                                x3 -= int_27 * y3;
                                y3 = 0;
                            }

                            int_40 = y2 - viewCenter.getY();
                            int_31 += int_33 * int_40;
                            int_34 += int_36 * int_40;
                            int_37 += int_39 * int_40;
                            if (y3 != y2 && int_25 < int_26 || y3 == y2 && int_25 > int_27) {
                                y1 -= y3;
                                y3 -= y2;
                                y2 = scanOffsets[y2];

                                while (true) {
                                    --y3;
                                    if (y3 < 0) {
                                        while (true) {
                                            --y1;
                                            if (y1 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y2, x1 >> 14, x3 >> 14, c2, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x1 += int_25;
                                            x3 += int_27;
                                            c2 += int_30;
                                            y2 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y2, x1 >> 14, x2 >> 14, c2, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x1 += int_25;
                                    x2 += int_26;
                                    c2 += int_30;
                                    y2 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            } else {
                                y1 -= y3;
                                y3 -= y2;
                                y2 = scanOffsets[y2];

                                while (true) {
                                    --y3;
                                    if (y3 < 0) {
                                        while (true) {
                                            --y1;
                                            if (y1 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y2, x3 >> 14, x1 >> 14, c2, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x1 += int_25;
                                            x3 += int_27;
                                            c2 += int_30;
                                            y2 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y2, x2 >> 14, x1 >> 14, c2, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x1 += int_25;
                                    x2 += int_26;
                                    c2 += int_30;
                                    y2 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            }
                        } else {
                            x3 = x2 <<= 14;
                            if (y2 < 0) {
                                x3 -= int_25 * y2;
                                x2 -= int_26 * y2;
                                c2 -= int_30 * y2;
                                y2 = 0;
                            }

                            x1 <<= 14;
                            if (y1 < 0) {
                                x1 -= y1 * int_27;
                                y1 = 0;
                            }

                            int_40 = y2 - viewCenter.getY();
                            int_31 += int_33 * int_40;
                            int_34 += int_36 * int_40;
                            int_37 += int_39 * int_40;
                            if (int_25 < int_26) {
                                y3 -= y1;
                                y1 -= y2;
                                y2 = scanOffsets[y2];

                                while (true) {
                                    --y1;
                                    if (y1 < 0) {
                                        while (true) {
                                            --y3;
                                            if (y3 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y2, x1 >> 14, x2 >> 14, c2, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x1 += int_27;
                                            x2 += int_26;
                                            c2 += int_30;
                                            y2 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y2, x3 >> 14, x2 >> 14, c2, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x3 += int_25;
                                    x2 += int_26;
                                    c2 += int_30;
                                    y2 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            } else {
                                y3 -= y1;
                                y1 -= y2;
                                y2 = scanOffsets[y2];

                                while (true) {
                                    --y1;
                                    if (y1 < 0) {
                                        while (true) {
                                            --y3;
                                            if (y3 < 0) {
                                                return;
                                            }

                                            drawTexturedTriangle_Model(this.raster, texturePixels, y2, x2 >> 14, x1 >> 14, c2, int_29,
                                                    int_31, int_34, int_37, int_32, int_35, int_38);
                                            x1 += int_27;
                                            x2 += int_26;
                                            c2 += int_30;
                                            y2 += this.width;
                                            int_31 += int_33;
                                            int_34 += int_36;
                                            int_37 += int_39;
                                        }
                                    }

                                    drawTexturedTriangle_Model(this.raster, texturePixels, y2, x2 >> 14, x3 >> 14, c2, int_29, int_31,
                                            int_34, int_37, int_32, int_35, int_38);
                                    x3 += int_25;
                                    x2 += int_26;
                                    c2 += int_30;
                                    y2 += this.width;
                                    int_31 += int_33;
                                    int_34 += int_36;
                                    int_37 += int_39;
                                }
                            }
                        }
                    }
                } else if (y3 < this.getClipTop()) {
                    if (y1 > this.getClipTop()) {
                        y1 = this.getClipTop();
                    }

                    if (y2 > this.getClipTop()) {
                        y2 = this.getClipTop();
                    }

                    c3 = (c3 << 9) - x3 * int_29 + int_29;
                    if (y1 < y2) {
                        x2 = x3 <<= 14;
                        if (y3 < 0) {
                            x2 -= int_26 * y3;
                            x3 -= int_27 * y3;
                            c3 -= int_30 * y3;
                            y3 = 0;
                        }

                        x1 <<= 14;
                        if (y1 < 0) {
                            x1 -= y1 * int_25;
                            y1 = 0;
                        }

                        int_40 = y3 - viewCenter.getY();
                        int_31 += int_33 * int_40;
                        int_34 += int_36 * int_40;
                        int_37 += int_39 * int_40;
                        if (int_26 < int_27) {
                            y2 -= y1;
                            y1 -= y3;
                            y3 = scanOffsets[y3];

                            while (true) {
                                --y1;
                                if (y1 < 0) {
                                    while (true) {
                                        --y2;
                                        if (y2 < 0) {
                                            return;
                                        }

                                        drawTexturedTriangle_Model(this.raster, texturePixels, y3, x2 >> 14, x1 >> 14, c3, int_29, int_31,
                                                int_34, int_37, int_32, int_35, int_38);
                                        x2 += int_26;
                                        x1 += int_25;
                                        c3 += int_30;
                                        y3 += this.width;
                                        int_31 += int_33;
                                        int_34 += int_36;
                                        int_37 += int_39;
                                    }
                                }

                                drawTexturedTriangle_Model(this.raster, texturePixels, y3, x2 >> 14, x3 >> 14, c3, int_29, int_31, int_34,
                                        int_37, int_32, int_35, int_38);
                                x2 += int_26;
                                x3 += int_27;
                                c3 += int_30;
                                y3 += this.width;
                                int_31 += int_33;
                                int_34 += int_36;
                                int_37 += int_39;
                            }
                        } else {
                            y2 -= y1;
                            y1 -= y3;
                            y3 = scanOffsets[y3];

                            while (true) {
                                --y1;
                                if (y1 < 0) {
                                    while (true) {
                                        --y2;
                                        if (y2 < 0) {
                                            return;
                                        }

                                        drawTexturedTriangle_Model(this.raster, texturePixels, y3, x1 >> 14, x2 >> 14, c3, int_29, int_31,
                                                int_34, int_37, int_32, int_35, int_38);
                                        x2 += int_26;
                                        x1 += int_25;
                                        c3 += int_30;
                                        y3 += this.width;
                                        int_31 += int_33;
                                        int_34 += int_36;
                                        int_37 += int_39;
                                    }
                                }

                                drawTexturedTriangle_Model(this.raster, texturePixels, y3, x3 >> 14, x2 >> 14, c3, int_29, int_31, int_34,
                                        int_37, int_32, int_35, int_38);
                                x2 += int_26;
                                x3 += int_27;
                                c3 += int_30;
                                y3 += this.width;
                                int_31 += int_33;
                                int_34 += int_36;
                                int_37 += int_39;
                            }
                        }
                    } else {
                        x1 = x3 <<= 14;
                        if (y3 < 0) {
                            x1 -= int_26 * y3;
                            x3 -= int_27 * y3;
                            c3 -= int_30 * y3;
                            y3 = 0;
                        }

                        x2 <<= 14;
                        if (y2 < 0) {
                            x2 -= int_25 * y2;
                            y2 = 0;
                        }

                        int_40 = y3 - viewCenter.getY();
                        int_31 += int_33 * int_40;
                        int_34 += int_36 * int_40;
                        int_37 += int_39 * int_40;
                        if (int_26 < int_27) {
                            y1 -= y2;
                            y2 -= y3;
                            y3 = scanOffsets[y3];

                            while (true) {
                                --y2;
                                if (y2 < 0) {
                                    while (true) {
                                        --y1;
                                        if (y1 < 0) {
                                            return;
                                        }

                                        drawTexturedTriangle_Model(this.raster, texturePixels, y3, x2 >> 14, x3 >> 14, c3, int_29, int_31,
                                                int_34, int_37, int_32, int_35, int_38);
                                        x2 += int_25;
                                        x3 += int_27;
                                        c3 += int_30;
                                        y3 += this.width;
                                        int_31 += int_33;
                                        int_34 += int_36;
                                        int_37 += int_39;
                                    }
                                }

                                drawTexturedTriangle_Model(this.raster, texturePixels, y3, x1 >> 14, x3 >> 14, c3, int_29, int_31, int_34,
                                        int_37, int_32, int_35, int_38);
                                x1 += int_26;
                                x3 += int_27;
                                c3 += int_30;
                                y3 += this.width;
                                int_31 += int_33;
                                int_34 += int_36;
                                int_37 += int_39;
                            }
                        } else {
                            y1 -= y2;
                            y2 -= y3;
                            y3 = scanOffsets[y3];

                            while (true) {
                                --y2;
                                if (y2 < 0) {
                                    while (true) {
                                        --y1;
                                        if (y1 < 0) {
                                            return;
                                        }

                                        drawTexturedTriangle_Model(this.raster, texturePixels, y3, x3 >> 14, x2 >> 14, c3, int_29, int_31,
                                                int_34, int_37, int_32, int_35, int_38);
                                        x2 += int_25;
                                        x3 += int_27;
                                        c3 += int_30;
                                        y3 += this.width;
                                        int_31 += int_33;
                                        int_34 += int_36;
                                        int_37 += int_39;
                                    }
                                }

                                drawTexturedTriangle_Model(this.raster, texturePixels, y3, x3 >> 14, x1 >> 14, c3, int_29, int_31, int_34,
                                        int_37, int_32, int_35, int_38);
                                x1 += int_26;
                                x3 += int_27;
                                c3 += int_30;
                                y3 += this.width;
                                int_31 += int_33;
                                int_34 += int_36;
                                int_37 += int_39;
                            }
                        }
                    }
                }
            }
        }
    }

    private void drawTexturedTriangle_Model(int[] ints_0, int[] ints_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7,
                                           int int_8, int int_9, int int_10, int int_11, int int_12) {
        if (restrictEdges) {
            if (int_4 > this.getMaxRight()) {
                int_4 = this.getMaxRight();
            }

            if (int_3 < 0) {
                int_3 = 0;
            }
        }

        if (int_3 < int_4) {
            int_2 += int_3;
            int_5 += int_3 * int_6;
            int int_13 = int_4 - int_3;
            int int_14;
            int int_16;
            int int_17;
            int int_18;
            int int_19;
            int int_20;
            int int_21;
            int int_22;
            int_14 = int_3 - viewCenter.getX();
            int_7 += int_14 * (int_10 >> 3);
            int_8 += (int_11 >> 3) * int_14;
            int_9 += int_14 * (int_12 >> 3);
            int_16 = int_9 >> 14;
            if (int_16 != 0) {
                int_17 = int_7 / int_16;
                int_18 = int_8 / int_16;
                if (int_17 < 0) {
                    int_17 = 0;
                } else if (int_17 > 16256) {
                    int_17 = 16256;
                }
            } else {
                int_17 = 0;
                int_18 = 0;
            }

            int_7 += int_10;
            int_8 += int_11;
            int_9 += int_12;
            int_16 = int_9 >> 14;
            if (int_16 != 0) {
                int_19 = int_7 / int_16;
                int_20 = int_8 / int_16;
                if (int_19 < 0) {
                    int_19 = 0;
                } else if (int_19 > 16256) {
                    int_19 = 16256;
                }
            } else {
                int_19 = 0;
                int_20 = 0;
            }

            int int_0 = (int_17 << 18) + int_18;
            int int_1;
            int_21 = (int_20 - int_18 >> 3) + (int_19 - int_17 >> 3 << 18);
            int_13 >>= 3;
            int_6 <<= 3;
            int_22 = int_5 >> 8;
            if (currentTextureTransparent) {
                if (int_13 > 0) {
                    do {
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_17 = int_19;
                        int_18 = int_20;
                        int_7 += int_10;
                        int_8 += int_11;
                        int_9 += int_12;
                        int_16 = int_9 >> 14;
                        if (int_16 != 0) {
                            int_19 = int_7 / int_16;
                            int_20 = int_8 / int_16;
                            if (int_19 < 0) {
                                int_19 = 0;
                            } else if (int_19 > 16256) {
                                int_19 = 16256;
                            }
                        } else {
                            int_19 = 0;
                            int_20 = 0;
                        }

                        int_0 = (int_17 << 18) + int_18;
                        int_21 = (int_20 - int_18 >> 3) + (int_19 - int_17 >> 3 << 18);
                        int_5 += int_6;
                        int_22 = int_5 >> 8;
                        --int_13;
                    } while (int_13 > 0);
                }

                int_13 = int_4 - int_3 & 0x7;
                if (int_13 > 0) {
                    do {
                        int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)];
                        ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        int_0 += int_21;
                        --int_13;
                    } while (int_13 > 0);
                }
            } else {
                if (int_13 > 0) {
                    do {
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_17 = int_19;
                        int_18 = int_20;
                        int_7 += int_10;
                        int_8 += int_11;
                        int_9 += int_12;
                        int_16 = int_9 >> 14;
                        if (int_16 != 0) {
                            int_19 = int_7 / int_16;
                            int_20 = int_8 / int_16;
                            if (int_19 < 0) {
                                int_19 = 0;
                            } else if (int_19 > 16256) {
                                int_19 = 16256;
                            }
                        } else {
                            int_19 = 0;
                            int_20 = 0;
                        }

                        int_0 = (int_17 << 18) + int_18;
                        int_21 = (int_20 - int_18 >> 3) + (int_19 - int_17 >> 3 << 18);
                        int_5 += int_6;
                        int_22 = int_5 >> 8;
                        --int_13;
                    } while (int_13 > 0);
                }

                int_13 = int_4 - int_3 & 0x7;
                if (int_13 > 0) {
                    do {
                        if ((int_1 = ints_1[(int_0 & 0x3F80) + (int_0 >>> 25)]) != 0) {
                            ints_0[int_2] = (int_22 * (int_1 & 0xFF00) & 0xFF0000) + ((int_1 & 0xFF00FF) * int_22 & 0xFF00FF00) >> 8;
                        }

                        ++int_2;
                        int_0 += int_21;
                        --int_13;
                    } while (int_13 > 0);
                }
            }
        }
    }

    public void drawTexturedTriangle_Scene(int y1, int y2, int y3, int x1, int x2, int x3, int c1, int c2, int c3, int tx1, int tx2,
                                                  int tx3, int ty1, int ty2, int ty3, int tz1, int tz2, int tz3, int tex) {
        c1 = 0x7f - c1 << 1;
        c2 = 0x7f - c2 << 1;
        c3 = 0x7f - c3 << 1;

        int[] texturePixels = TextureLoader.getTexturePixels(tex);
        if (texturePixels == null) {
            return;
        }

        currentTextureTransparent = !TextureLoader.getTextureTransparent(tex);
        tx2 = tx1 - tx2;
        ty2 = ty1 - ty2;
        tz2 = tz1 - tz2;
        tx3 -= tx1;
        ty3 -= ty1;
        tz3 -= tz1;
        final int FOV = 512;
        int l4 = tx3 * ty1 - ty3 * tx1 << 14;
        int i5 = (int) (((long) (ty3 * tz1 - tz3 * ty1) << 3 << 14) / (long) FOV);
        int j5 = (int) (((long) (tz3 * tx1 - tx3 * tz1) << 14) / (long) FOV);
        int k5 = tx2 * ty1 - ty2 * tx1 << 14;
        int l5 = (int) (((long) (ty2 * tz1 - tz2 * ty1) << 3 << 14) / (long) FOV);
        int i6 = (int) (((long) (tz2 * tx1 - tx2 * tz1) << 14) / (long) FOV);
        int j6 = ty2 * tx3 - tx2 * ty3 << 14;
        int k6 = (int) (((long) (tz2 * ty3 - ty2 * tz3) << 3 << 14) / (long) FOV);
        int l6 = (int) (((long) (tx2 * tz3 - tz2 * tx3) << 14) / (long) FOV);
        int i7 = 0;
        int j7 = 0;

        if (y2 != y1) {
            i7 = (x2 - x1 << 16) / (y2 - y1);
            j7 = (c2 - c1 << 16) / (y2 - y1);
        }
        int k7 = 0;
        int l7 = 0;
        if (y3 != y2) {
            k7 = (x3 - x2 << 16) / (y3 - y2);
            l7 = (c3 - c2 << 16) / (y3 - y2);
        }
        int i8 = 0;
        int j8 = 0;
        if (y3 != y1) {
            i8 = (x1 - x3 << 16) / (y1 - y3);
            j8 = (c1 - c3 << 16) / (y1 - y3);
        }
        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= this.getClipTop()) {
                return;
            }
            if (y2 > this.getClipTop()) {
                y2 = this.getClipTop();
            }
            if (y3 > this.getClipTop()) {
                y3 = this.getClipTop();
            }
            if (y2 < y3) {
                x3 = x1 <<= 16;
                c3 = c1 <<= 16;
                if (y1 < 0) {
                    x3 -= i8 * y1;
                    x1 -= i7 * y1;
                    c3 -= j8 * y1;
                    c1 -= j7 * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                c2 <<= 16;
                if (y2 < 0) {
                    x2 -= k7 * y2;
                    c2 -= l7 * y2;
                    y2 = 0;
                }
                int k8 = y1 - viewCenter.getY();
                l4 += j5 * k8;
                k5 += i6 * k8;
                j6 += l6 * k8;
                if (y1 != y2 && i8 < i7 || y1 == y2 && i8 > k7) {
                    y3 -= y2;
                    y2 -= y1;
                    y1 = scanOffsets[y1];
                    while (--y2 >= 0) {
                        drawTexturedScanline_Scene(this.raster, texturePixels, y1, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
                        x3 += i8;
                        x1 += i7;
                        c3 += j8;
                        c1 += j7;
                        y1 += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y3 >= 0) {
                        drawTexturedScanline_Scene(this.raster, texturePixels, y1, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
                        x3 += i8;
                        x2 += k7;
                        c3 += j8;
                        c2 += l7;
                        y1 += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                y1 = scanOffsets[y1];
                while (--y2 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y1, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                    x3 += i8;
                    x1 += i7;
                    c3 += j8;
                    c1 += j7;
                    y1 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y3 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y1, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                    x3 += i8;
                    x2 += k7;
                    c3 += j8;
                    c2 += l7;
                    y1 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x2 = x1 <<= 16;
            c2 = c1 <<= 16;
            if (y1 < 0) {
                x2 -= i8 * y1;
                x1 -= i7 * y1;
                c2 -= j8 * y1;
                c1 -= j7 * y1;
                y1 = 0;
            }
            x3 <<= 16;
            c3 <<= 16;
            if (y3 < 0) {
                x3 -= k7 * y3;
                c3 -= l7 * y3;
                y3 = 0;
            }
            int l8 = y1 - viewCenter.getY();
            l4 += j5 * l8;
            k5 += i6 * l8;
            j6 += l6 * l8;
            if (y1 != y3 && i8 < i7 || y1 == y3 && k7 > i7) {
                y2 -= y3;
                y3 -= y1;
                y1 = scanOffsets[y1];
                while (--y3 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y1, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                    x2 += i8;
                    x1 += i7;
                    c2 += j8;
                    c1 += j7;
                    y1 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y2 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y1, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
                    x3 += k7;
                    x1 += i7;
                    c3 += l7;
                    c1 += j7;
                    y1 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            y1 = scanOffsets[y1];
            while (--y3 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y1, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                x2 += i8;
                x1 += i7;
                c2 += j8;
                c1 += j7;
                y1 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y2 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y1, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                x3 += k7;
                x1 += i7;
                c3 += l7;
                c1 += j7;
                y1 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= this.getClipTop()) {
                return;
            }
            if (y3 > this.getClipTop()) {
                y3 = this.getClipTop();
            }
            if (y1 > this.getClipTop()) {
                y1 = this.getClipTop();
            }
            if (y3 < y1) {
                x1 = x2 <<= 16;
                c1 = c2 <<= 16;
                if (y2 < 0) {
                    x1 -= i7 * y2;
                    x2 -= k7 * y2;
                    c1 -= j7 * y2;
                    c2 -= l7 * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                c3 <<= 16;
                if (y3 < 0) {
                    x3 -= i8 * y3;
                    c3 -= j8 * y3;
                    y3 = 0;
                }
                int i9 = y2 - viewCenter.getY();
                l4 += j5 * i9;
                k5 += i6 * i9;
                j6 += l6 * i9;
                if (y2 != y3 && i7 < k7 || y2 == y3 && i7 > i8) {
                    y1 -= y3;
                    y3 -= y2;
                    y2 = scanOffsets[y2];
                    while (--y3 >= 0) {
                        drawTexturedScanline_Scene(this.raster, texturePixels, y2, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                        x1 += i7;
                        x2 += k7;
                        c1 += j7;
                        c2 += l7;
                        y2 += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y1 >= 0) {
                        drawTexturedScanline_Scene(this.raster, texturePixels, y2, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                        x1 += i7;
                        x3 += i8;
                        c1 += j7;
                        c3 += j8;
                        y2 += this.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                y2 = scanOffsets[y2];
                while (--y3 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y2, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                    x1 += i7;
                    x2 += k7;
                    c1 += j7;
                    c2 += l7;
                    y2 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y1 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y2, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
                    x1 += i7;
                    x3 += i8;
                    c1 += j7;
                    c3 += j8;
                    y2 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x3 = x2 <<= 16;
            c3 = c2 <<= 16;
            if (y2 < 0) {
                x3 -= i7 * y2;
                x2 -= k7 * y2;
                c3 -= j7 * y2;
                c2 -= l7 * y2;
                y2 = 0;
            }
            x1 <<= 16;
            c1 <<= 16;
            if (y1 < 0) {
                x1 -= i8 * y1;
                c1 -= j8 * y1;
                y1 = 0;
            }
            int j9 = y2 - viewCenter.getY();
            l4 += j5 * j9;
            k5 += i6 * j9;
            j6 += l6 * j9;
            if (i7 < k7) {
                y3 -= y1;
                y1 -= y2;
                y2 = scanOffsets[y2];
                while (--y1 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y2, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
                    x3 += i7;
                    x2 += k7;
                    c3 += j7;
                    c2 += l7;
                    y2 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y3 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y2, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                    x1 += i8;
                    x2 += k7;
                    c1 += j8;
                    c2 += l7;
                    y2 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            y2 = scanOffsets[y2];
            while (--y1 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y2, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                x3 += i7;
                x2 += k7;
                c3 += j7;
                c2 += l7;
                y2 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y3 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y2, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                x1 += i8;
                x2 += k7;
                c1 += j8;
                c2 += l7;
                y2 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y3 >= this.getClipTop()) {
            return;
        }
        if (y1 > this.getClipTop()) {
            y1 = this.getClipTop();
        }
        if (y2 > this.getClipTop()) {
            y2 = this.getClipTop();
        }
        if (y1 < y2) {
            x2 = x3 <<= 16;
            c2 = c3 <<= 16;
            if (y3 < 0) {
                x2 -= k7 * y3;
                x3 -= i8 * y3;
                c2 -= l7 * y3;
                c3 -= j8 * y3;
                y3 = 0;
            }
            x1 <<= 16;
            c1 <<= 16;
            if (y1 < 0) {
                x1 -= i7 * y1;
                c1 -= j7 * y1;
                y1 = 0;
            }
            int k9 = y3 - viewCenter.getY();
            l4 += j5 * k9;
            k5 += i6 * k9;
            j6 += l6 * k9;
            if (k7 < i8) {
                y2 -= y1;
                y1 -= y3;
                y3 = scanOffsets[y3];
                while (--y1 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y3, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                    x2 += k7;
                    x3 += i8;
                    c2 += l7;
                    c3 += j8;
                    y3 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y2 >= 0) {
                    drawTexturedScanline_Scene(this.raster, texturePixels, y3, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                    x2 += k7;
                    x1 += i7;
                    c2 += l7;
                    c1 += j7;
                    y3 += this.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            y3 = scanOffsets[y3];
            while (--y1 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y3, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
                x2 += k7;
                x3 += i8;
                c2 += l7;
                c3 += j8;
                y3 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y2 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y3, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                x2 += k7;
                x1 += i7;
                c2 += l7;
                c1 += j7;
                y3 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        x1 = x3 <<= 16;
        c1 = c3 <<= 16;
        if (y3 < 0) {
            x1 -= k7 * y3;
            x3 -= i8 * y3;
            c1 -= l7 * y3;
            c3 -= j8 * y3;
            y3 = 0;
        }
        x2 <<= 16;
        c2 <<= 16;
        if (y2 < 0) {
            x2 -= i7 * y2;
            c2 -= j7 * y2;
            y2 = 0;
        }
        int l9 = y3 - viewCenter.getY();
        l4 += j5 * l9;
        k5 += i6 * l9;
        j6 += l6 * l9;
        if (k7 < i8) {
            y1 -= y2;
            y2 -= y3;
            y3 = scanOffsets[y3];
            while (--y2 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y3, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                x1 += k7;
                x3 += i8;
                c1 += l7;
                c3 += j8;
                y3 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y1 >= 0) {
                drawTexturedScanline_Scene(this.raster, texturePixels, y3, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                x2 += i7;
                x3 += i8;
                c2 += j7;
                c3 += j8;
                y3 += this.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        y3 = scanOffsets[y3];
        while (--y2 >= 0) {
            drawTexturedScanline_Scene(this.raster, texturePixels, y3, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
            x1 += k7;
            x3 += i8;
            c1 += l7;
            c3 += j8;
            y3 += this.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
        while (--y1 >= 0) {
            drawTexturedScanline_Scene(this.raster, texturePixels, y3, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
            x2 += i7;
            x3 += i8;
            c2 += j7;
            c3 += j8;
            y3 += this.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
    }

    private void drawTexturedScanline_Scene(int[] ai, int[] ai1, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3) {
        int i = 0;
        int j = 0;
        if (l >= i1) {
            return;
        }
        int j3 = (k1 - j1) / (i1 - l);
        int k3;
        if (restrictEdges) {
            if (i1 > this.getMaxRight()) {
                i1 = this.getMaxRight();
            }
            if (l < 0) {
                j1 -= l * j3;
                l = 0;
            }
        }
        if (l >= i1) {
            return;
        }
        k3 = i1 - l >> 3;
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
        if (currentTextureTransparent) {
            while (k3-- > 0) {
                int rgb;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
                rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
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
            }
            for (k3 = i1 - l & 7; k3-- > 0; ) {
                int rgb = ai1[(j & 0x3f80) + (i >> 7)];
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
                k++;
                i += j7;
                j += l7;
                j1 += j3;
            }
            return;
        }
        while (k3-- > 0) {
            int rgb;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
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
        }
        for (int l3 = i1 - l & 7; l3-- > 0; ) {
            int rgb;
            if ((rgb = ai1[(j & 0x3f80) + (i >> 7)]) != 0) {
                ai[k] = ((rgb & 0xff00ff) * (j1 >> 16) & ~0xff00ff) + ((rgb & 0xff00) * (j1 >> 16) & 0xff0000) >> 8;
            }
            k++;
            i += j7;
            j += l7;
            j1 += j3;
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

    private static int method965(int rgb, int hsl) {
        hsl = (rgb & 0x7F) * hsl >> 7;
        if (hsl < 2) {
            hsl = 2;
        } else if (hsl > 126) {
            hsl = 126;
        }
        return (rgb & 0xFF80) + hsl;
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