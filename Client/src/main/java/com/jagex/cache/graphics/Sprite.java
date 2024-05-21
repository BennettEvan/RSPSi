package com.jagex.cache.graphics;

import com.displee.cache.index.archive.Archive;
import com.jagex.draw.raster.GameRaster;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.io.Buffer;
import com.jagex.util.ByteBufferUtils;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class Sprite extends GameRaster {

    public static final int FLAG_VERTICAL = 0x01;
    public static final int FLAG_ALPHA = 0x02;

    @Getter private int width;
    @Getter private int height;
    @Getter private int[] raster;
    @Getter private int resizeHeight;
    @Getter private int resizeWidth;
    private int horizontalOffset;
    private int verticalOffset;
    private boolean hasAlpha;

    public Sprite(Archive archive, String name, int id) {
        Buffer sprite = new Buffer(archive.file(name + ".dat"));
        Buffer meta = new Buffer(archive.file("index.dat"));
        if (sprite.getPayload() == null) {
            return;
        }
        meta.setPosition(sprite.readUShort());

        resizeWidth = meta.readUShort();
        resizeHeight = meta.readUShort();

        int colours = meta.readUByte();
        int[] palette = new int[colours];

        for (int index = 0; index < colours - 1; index++) {
            int colour = meta.readUTriByte();
            if (colour == 0) {
                colour = 1;
            } else if (colour == 0xff00ff) {
                colour = 0;
            }
            palette[index + 1] = colour;
        }

        for (int i = 0; i < id; i++) {
            meta.setPosition(meta.getPosition() + 2);
            sprite.setPosition(sprite.getPosition() + meta.readUShort() * meta.readUShort());
            meta.setPosition(meta.getPosition() + 1);
        }

        horizontalOffset = meta.readUByte();
        verticalOffset = meta.readUByte();
        width = meta.readUShort();
        height = meta.readUShort();

        int format = meta.readUByte();
        int pixels = width * height;
        raster = new int[pixels];

        if (format == 0) {
            for (int index = 0; index < pixels; index++) {
                raster[index] = palette[sprite.readUByte()];
            }
        } else {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    raster[x + y * width] = palette[sprite.readUByte()];
                }
            }
        }
    }

    public Sprite(int width, int height) {
        raster = new int[width * height];
        this.width = resizeWidth = width;
        this.height = resizeHeight = height;
        horizontalOffset = verticalOffset = 0;
    }

    public static Sprite[] unpackAndDecode(ByteBuffer buffer) {
        buffer.position(buffer.limit() - 2);
        int size = buffer.getShort() & 0xFFFF;

        int[] offsetsX = new int[size];
        int[] offsetsY = new int[size];
        int[] subWidths = new int[size];
        int[] subHeights = new int[size];

        buffer.position(buffer.limit() - size * 8 - 7);
        int width = buffer.getShort() & 0xFFFF;
        int height = buffer.getShort() & 0xFFFF;
        int[] palette = new int[(buffer.get() & 0xFF) + 1];

        for (int i = 0; i < size; i++) {
            offsetsX[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            offsetsY[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            subWidths[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            subHeights[i] = buffer.getShort() & 0xFFFF;
        }

        buffer.position(buffer.limit() - size * 8 - 7 - (palette.length - 1) * 3);
        palette[0] = 0;
        for (int index = 1; index < palette.length; index++) {
            palette[index] = ByteBufferUtils.getUMedium(buffer);
            if (palette[index] == 0) {
                palette[index] = 1;
            }
        }

        Sprite[] sprites = new Sprite[size];
        buffer.position(0);
        for (int id = 0; id < size; id++) {
            Sprite set = new Sprite(subWidths[id], subHeights[id]);
            int subWidth = subWidths[id], subHeight = subHeights[id];
            int offsetX = offsetsX[id], offsetY = offsetsY[id];

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            int[][] indices = new int[subWidth][subHeight];

            int flags = buffer.get() & 0xFF;

            if ((flags & FLAG_VERTICAL) != 0) {
                for (int x = 0; x < subWidth; x++) {
                    for (int y = 0; y < subHeight; y++) {
                        indices[x][y] = buffer.get() & 0xFF;
                    }
                }
            } else {
                for (int y = 0; y < subHeight; y++) {
                    for (int x = 0; x < subWidth; x++) {
                        indices[x][y] = buffer.get() & 0xFF;
                    }
                }
            }

            set.hasAlpha = true;
            if ((flags & FLAG_ALPHA) != 0) {
                if ((flags & FLAG_VERTICAL) != 0) {
                    for (int x = 0; x < subWidth; x++) {
                        for (int y = 0; y < subHeight; y++) {
                            int alpha = buffer.get() & 0xFF;
                            image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
                        }
                    }
                } else {
                    for (int y = 0; y < subHeight; y++) {
                        for (int x = 0; x < subWidth; x++) {
                            int alpha = buffer.get() & 0xFF;
                            image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
                        }
                    }
                }
            } else {
                for (int x = 0; x < subWidth; x++) {
                    for (int y = 0; y < subHeight; y++) {
                        int index = indices[x][y];
                        if (index == 0) {
                            image.setRGB(x + offsetX, y + offsetY, 0);
                        } else {
                            image.setRGB(x + offsetX, y + offsetY, 0xFF000000 | palette[index]);
                        }
                    }
                }
            }

            image.getRGB(0, 0, subWidth, subHeight, set.raster, 0, subWidth);
            sprites[id] = set;
        }
        return sprites;
    }

    public static Sprite decode(ByteBuffer buffer) {
        buffer.position(buffer.limit() - 2);
        int size = buffer.getShort() & 0xFFFF;

        int[] offsetsX = new int[size];
        int[] offsetsY = new int[size];
        int[] subWidths = new int[size];
        int[] subHeights = new int[size];

        buffer.position(buffer.limit() - size * 8 - 7);
        int width = buffer.getShort() & 0xFFFF;
        int height = buffer.getShort() & 0xFFFF;
        int[] palette = new int[(buffer.get() & 0xFF) + 1];

        for (int i = 0; i < size; i++) {
            offsetsX[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            offsetsY[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            subWidths[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            subHeights[i] = buffer.getShort() & 0xFFFF;
        }
        Sprite set = new Sprite(subWidths[0], subHeights[0]);

        buffer.position(buffer.limit() - size * 8 - 7 - (palette.length - 1) * 3);
        palette[0] = 0;
        for (int index = 1; index < palette.length; index++) {
            palette[index] = ByteBufferUtils.getUMedium(buffer);
            if (palette[index] == 0) {
                palette[index] = 1;
            }
        }

        buffer.position(0);

        int subWidth = subWidths[0], subHeight = subHeights[0];
        int offsetX = offsetsX[0], offsetY = offsetsY[0];

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[][] indices = new int[subWidth][subHeight];

        int flags = buffer.get() & 0xFF;
        if ((flags & FLAG_VERTICAL) != 0) {
            for (int x = 0; x < subWidth; x++) {
                for (int y = 0; y < subHeight; y++) {
                    indices[x][y] = buffer.get() & 0xFF;
                }
            }
        } else {
            for (int y = 0; y < subHeight; y++) {
                for (int x = 0; x < subWidth; x++) {
                    indices[x][y] = buffer.get() & 0xFF;
                }
            }
        }

        set.hasAlpha = true;
        if ((flags & FLAG_ALPHA) != 0) {
            if ((flags & FLAG_VERTICAL) != 0) {
                for (int x = 0; x < subWidth; x++) {
                    for (int y = 0; y < subHeight; y++) {
                        int alpha = buffer.get() & 0xFF;
                        image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
                    }
                }
            } else {
                for (int y = 0; y < subHeight; y++) {
                    for (int x = 0; x < subWidth; x++) {
                        int alpha = buffer.get() & 0xFF;
                        image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
                    }
                }
            }
        } else {
            for (int x = 0; x < subWidth; x++) {
                for (int y = 0; y < subHeight; y++) {
                    int index = indices[x][y];
                    if (index == 0) {
                        image.setRGB(x + offsetX, y + offsetY, 0);
                    } else {
                        image.setRGB(x + offsetX, y + offsetY, 0xFF000000 | palette[index]);
                    }
                }
            }
        }
        image.getRGB(0, 0, subWidth, subHeight, set.raster, 0, subWidth);
        return set;
    }

    private static int[] resizePixels(int[] pixels, int w1, int h1, int w2, int h2) {
        int[] temp = new int[w2 * h2];
        double x_ratio = w1 / (double) w2;
        double y_ratio = h1 / (double) h2;
        double px, py;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                px = Math.floor(j * x_ratio);
                py = Math.floor(i * y_ratio);
                temp[(i * w2) + j] = pixels[(int) ((py * w1) + px)];
            }
        }
        return temp;
    }

    public void drawSprite(int x, int y) {
        drawSprite(GameRasterizer.getInstance(), x, y);
    }

    public void drawSprite(GameRasterizer rasterizer, int x, int y) {
        x += horizontalOffset;
        y += verticalOffset;
        int rasterClip = x + y * rasterizer.getWidth();
        int imageClip = 0;
        int height = this.height;
        int width = this.width;
        int rasterOffset = rasterizer.getWidth() - width;
        int imageOffset = 0;

        if (y < rasterizer.getClipBottom()) {
            int dy = rasterizer.getClipBottom() - y;
            height -= dy;
            y = rasterizer.getClipBottom();
            imageClip += dy * width;
            rasterClip += dy * rasterizer.getWidth();
        }

        if (y + height > rasterizer.getClipTop()) {
            height -= y + height - rasterizer.getClipTop();
        }

        if (x < rasterizer.getClipLeft()) {
            int dx = rasterizer.getClipLeft() - x;
            width -= dx;
            x = rasterizer.getClipLeft();
            imageClip += dx;
            rasterClip += dx;
            imageOffset += dx;
            rasterOffset += dx;
        }

        if (x + width > rasterizer.getClipRight()) {
            int dx = x + width - rasterizer.getClipRight();
            width -= dx;
            imageOffset += dx;
            rasterOffset += dx;
        }

        if (width > 0 && height > 0) {
            draw(rasterizer.getRaster(), raster, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
        }
    }

    public void drawSprite(GameRasterizer rasterizer, int x, int y, boolean selected) {
        x += horizontalOffset;
        y += verticalOffset;
        int[] raster = Arrays.copyOf(this.raster, this.raster.length);
        if (selected) {
            for (int i = 0; i < raster.length; i++) {
                raster[i] = raster[i] << 8;
            }
        }
        int rasterClip = x + y * rasterizer.getWidth();
        int imageClip = 0;
        int height = this.height;
        int width = this.width;
        int rasterOffset = rasterizer.getWidth() - width;
        int imageOffset = 0;

        if (y < rasterizer.getClipBottom()) {
            int dy = rasterizer.getClipBottom() - y;
            height -= dy;
            y = rasterizer.getClipBottom();
            imageClip += dy * width;
            rasterClip += dy * rasterizer.getWidth();
        }

        if (y + height > rasterizer.getClipTop()) {
            height -= y + height - rasterizer.getClipTop();
        }

        if (x < rasterizer.getClipLeft()) {
            int dx = rasterizer.getClipLeft() - x;
            width -= dx;
            x = rasterizer.getClipLeft();
            imageClip += dx;
            rasterClip += dx;
            imageOffset += dx;
            rasterOffset += dx;
        }

        if (x + width > rasterizer.getClipRight()) {
            int dx = x + width - rasterizer.getClipRight();
            width -= dx;
            imageOffset += dx;
            rasterOffset += dx;
        }

        if (width > 0 && height > 0) {
            draw(rasterizer.getRaster(), raster, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
        }
    }

    public void drawSprite(GameRasterizer rasterizer, int x, int y, double scale) {

        int height = (int) (this.height * scale);
        int width = (int) (this.width * scale);
        int[] raster = Sprite.resizePixels(this.raster, this.width, this.height, width, height);

        x += horizontalOffset;
        y += verticalOffset;
        int rasterClip = x + y * rasterizer.getWidth();
        int imageClip = 0;
        int rasterOffset = rasterizer.getWidth() - width;
        int imageOffset = 0;

        if (y < rasterizer.getClipBottom()) {
            int dy = rasterizer.getClipBottom() - y;
            height -= dy;
            y = rasterizer.getClipBottom();
            imageClip += dy * width;
            rasterClip += dy * rasterizer.getWidth();
        }

        if (y + height > rasterizer.getClipTop()) {
            height -= y + height - rasterizer.getClipTop();
        }

        if (x < rasterizer.getClipLeft()) {
            int dx = rasterizer.getClipLeft() - x;
            width -= dx;
            x = rasterizer.getClipLeft();
            imageClip += dx;
            rasterClip += dx;
            imageOffset += dx;
            rasterOffset += dx;
        }

        if (x + width > rasterizer.getClipRight()) {
            int dx = x + width - rasterizer.getClipRight();
            width -= dx;
            imageOffset += dx;
            rasterOffset += dx;
        }

        if (width > 0 && height > 0) {
            draw(rasterizer.getRaster(), raster, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
        }
    }

    private static void draw(int[] raster, int[] image, int sourceIndex, int destIndex, int width,
                             int height, int destStep, int sourceStep) {
        int minX = -(width >> 2);
        width = -(width & 3);

        for (int y = -height; y < 0; y++) {
            for (int x = minX; x < 0; x++) {
                int colour = image[sourceIndex++];
                if (colour != 0) {
                    raster[destIndex++] = colour;
                } else {
                    destIndex++;
                }
                colour = image[sourceIndex++];

                if (colour != 0) {
                    raster[destIndex++] = colour;
                } else {
                    destIndex++;
                }
                colour = image[sourceIndex++];

                if (colour != 0) {
                    raster[destIndex++] = colour;
                } else {
                    destIndex++;
                }
                colour = image[sourceIndex++];

                if (colour != 0) {
                    raster[destIndex++] = colour;
                } else {
                    destIndex++;
                }
            }

            for (int k2 = width; k2 < 0; k2++) {
                int colour = image[sourceIndex++];
                if (colour != 0) {
                    raster[destIndex++] = colour;
                } else {
                    destIndex++;
                }
            }

            destIndex += destStep;
            sourceIndex += sourceStep;
        }
    }

    public void initRaster(GameRasterizer rasterizer) {
        rasterizer.init(height, width, raster);
    }

    public void resize() {
        int[] raster = new int[resizeWidth * resizeHeight];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster[(y + verticalOffset) * resizeWidth + x + horizontalOffset] = this.raster[y * width + x];
            }
        }

        this.raster = raster;
        width = resizeWidth;
        height = resizeHeight;
        horizontalOffset = 0;
        verticalOffset = 0;
    }

    public boolean hasAlpha() {
        return hasAlpha;
    }

    public void resize(int width, int height) {
        resizeWidth = width;
        resizeHeight = height;
        resize();
    }
}