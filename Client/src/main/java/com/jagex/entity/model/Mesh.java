package com.jagex.entity.model;

import java.util.Arrays;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.major.cache.anim.FrameConstants;

import com.jagex.Client;
import com.jagex.cache.anim.Frame;
import com.jagex.cache.anim.FrameBase;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.Renderable;
import com.jagex.util.Constants;
import com.jagex.util.ObjectKey;
import com.rspsi.core.misc.ToolType;
import com.rspsi.options.Options;

@Slf4j
public class Mesh extends Renderable {

    public short ambient;
    public short contrast;
    public VertexNormal[] vertexNormals;
    public VertexNormal[] vertexNormalsOffsets;
    public FaceNormal[] faceNormals;

    public int id;
    protected byte[] textureCoords;
    public byte[] textureRenderTypes;
    public static int resourceCount;
    public static ObjectKey[] resourceIDTag = new ObjectKey[1000];
    public static int mouseX;
    public static int mouseY;

    static int centroidX;
    static int centroidY;
    static int centroidZ;
    public MeshRevision revision;
    protected int[][] animayaGroups;
    protected int[][] animayaScales;
    public boolean fitsOnSingleSquare;
    public int minimumX;
    public int maximumX;
    public int maximumZ;
    public int minimumZ;
    public int boundingPlaneRadius;
    public int minimumY;
    public int boundingSphereRadius;
    public int boundingCylinderRadius;
    public int anInt1654;
    public int[] colorsX;
    public int[] colorsY;
    public int[] colorsZ;
    public int[] faceTransparencies;
    public int[] faceColors;
    public short[] faceTextures;
    public int[][] faceGroups;
    public byte[] faceRenderPriorities;
    public int triangleFaceCount;
    public int[] triangleSkinValues;
    public int[] trianglePointsX;
    public int[] trianglePointsY;
    public int[] trianglePointsZ;
    public byte priority;
    public int texturesCount;
    public int[] texTriangleX;
    public int[] texTriangleY;
    public int[] texTriangleZ;
    public int[] faceRenderType;
    public int[][] vertexGroups;
    public int[] vertexSkins;
    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;
    public int verticesCount;
    private boolean translucent;

    protected Mesh() {
    }

    public Mesh(boolean contouredGround, Mesh model) {
        verticesCount = model.verticesCount;
        triangleFaceCount = model.triangleFaceCount;
        texturesCount = model.texturesCount;
        if (contouredGround) {
            verticesY = new int[verticesCount];

            System.arraycopy(model.verticesY, 0, verticesY, 0, verticesCount);
        } else {
            verticesY = model.verticesY;
        }

        colorsX = model.colorsX;
        colorsY = model.colorsY;
        colorsZ = model.colorsZ;
        faceRenderType = model.faceRenderType;
        verticesX = model.verticesX;
        verticesZ = model.verticesZ;
        faceColors = model.faceColors;
        faceTransparencies = model.faceTransparencies;
        faceTextures = model.faceTextures;
        faceRenderPriorities = model.faceRenderPriorities;
        priority = model.priority;
        trianglePointsX = model.trianglePointsX;
        trianglePointsY = model.trianglePointsY;
        trianglePointsZ = model.trianglePointsZ;
        textureRenderTypes = model.textureRenderTypes;
        texTriangleX = model.texTriangleX;
        texTriangleY = model.texTriangleY;
        texTriangleZ = model.texTriangleZ;
        textureCoords = model.textureCoords;
        contrast = model.contrast;
        ambient = model.ambient;
        animayaScales = model.animayaScales;
        animayaGroups = model.animayaGroups;
    }

    public Mesh(int length, Mesh[] parts) {
        this.verticesCount = 0;
        this.triangleFaceCount = 0;
        this.priority = 0;
        boolean var3 = false;
        boolean var4 = false;
        boolean var5 = false;
        boolean var6 = false;
        boolean var7 = false;
        boolean var8 = false;
        boolean var11 = false;
        this.verticesCount = 0;
        this.triangleFaceCount = 0;
        this.texturesCount = 0;
        this.priority = -1;

        int var9;
        Mesh var10;
        for (var9 = 0; var9 < length; var9++) {
            var10 = parts[var9];
            if (var10 != null) {
                verticesCount += var10.verticesCount;
                triangleFaceCount += var10.triangleFaceCount;
                texturesCount += var10.texturesCount;
                if (var10.faceRenderPriorities != null) {
                    var4 = true;
                } else {
                    if (priority == -1) {
                        priority = var10.priority;
                    }

                    if (priority != var10.priority) {
                        var4 = true;
                    }
                }

                var3 |= var10.faceRenderType != null;
                var5 |= var10.faceTransparencies != null;
                var6 |= var10.triangleSkinValues != null;
                var7 |= var10.faceTextures != null;
                var8 |= var10.textureCoords != null;
                var11 |= var10.animayaGroups != null;
            }
        }
        verticesX = new int[verticesCount];
        verticesY = new int[verticesCount];
        verticesZ = new int[verticesCount];
        vertexSkins = new int[verticesCount];
        trianglePointsX = new int[triangleFaceCount];
        trianglePointsY = new int[triangleFaceCount];
        trianglePointsZ = new int[triangleFaceCount];

        if (var3) {
            faceRenderType = new int[triangleFaceCount];
        }

        if (var4) {
            faceRenderPriorities = new byte[triangleFaceCount];
        }

        if (var5) {
            faceTransparencies = new int[triangleFaceCount];
        }

        if (var6) {
            triangleSkinValues = new int[triangleFaceCount];
        }

        if (var7) {
            faceTextures = new short[triangleFaceCount];
        }

        if (var8) {
            textureCoords = new byte[triangleFaceCount];
        }

        if (var11) {
            this.animayaGroups = new int[this.verticesCount][];
            this.animayaScales = new int[this.verticesCount][];
        }

        faceColors = new int[triangleFaceCount];
        if (texturesCount > 0) {
            textureRenderTypes = new byte[texturesCount];
            texTriangleX = new int[texturesCount];
            texTriangleY = new int[texturesCount];
            texTriangleZ = new int[texturesCount];
        }
        verticesCount = 0;
        triangleFaceCount = 0;
        texturesCount = 0;
        for (var9 = 0; var9 < length; var9++) {
            var10 = parts[var9];
            if (var10 != null) {
                for (int face = 0; face < var10.triangleFaceCount; face++) {
                    if (var3 && var10.faceRenderType != null) {
                        faceRenderType[triangleFaceCount] = var10.faceRenderType[face];
                    }

                    if (var4) {
                        if (var10.faceRenderPriorities == null) {
                            faceRenderPriorities[triangleFaceCount] = var10.priority;
                        } else {
                            faceRenderPriorities[triangleFaceCount] = var10.faceRenderPriorities[face];
                        }
                    }

                    if (var5 && var10.faceTransparencies != null) {
                        faceTransparencies[triangleFaceCount] = var10.faceTransparencies[face];
                    }

                    if (var6 && var10.triangleSkinValues != null) {
                        triangleSkinValues[triangleFaceCount] = var10.triangleSkinValues[face];
                    }

                    if (var7) {
                        if (var10.faceTextures != null) {
                            faceTextures[triangleFaceCount] = var10.faceTextures[face];
                        } else {
                            faceTextures[triangleFaceCount] = -1;
                        }
                    }
                    if (var8) {
                        if (var10.textureCoords != null && var10.textureCoords[face] != -1) {
                            textureCoords[triangleFaceCount] = (byte) (texturesCount + var10.textureCoords[face]);
                        } else {
                            textureCoords[triangleFaceCount] = -1;
                        }
                    }
                    faceColors[triangleFaceCount] = var10.faceColors[face];
                    trianglePointsX[triangleFaceCount] = findMatchingVertex(var10, var10.trianglePointsX[face]);
                    trianglePointsY[triangleFaceCount] = findMatchingVertex(var10, var10.trianglePointsY[face]);
                    trianglePointsZ[triangleFaceCount] = findMatchingVertex(var10, var10.trianglePointsZ[face]);
                    triangleFaceCount++;
                }
                for (int texture_edge = 0; texture_edge < var10.texturesCount; texture_edge++) {
                    final byte var12 = textureRenderTypes[texturesCount] = var10.textureRenderTypes[texture_edge];
                    if (var12 == 0) {
                        texTriangleX[texturesCount] = findMatchingVertex(var10, var10.texTriangleX[texture_edge]);
                        texTriangleY[texturesCount] = findMatchingVertex(var10, var10.texTriangleY[texture_edge]);
                        texTriangleZ[texturesCount] = findMatchingVertex(var10, var10.texTriangleZ[texture_edge]);
                    }
                    texturesCount++;
                }
            }
        }
    }

    public Mesh(Mesh model, boolean shareColours, boolean shareAlphas, boolean shareVertices, boolean shareTextures) {
        verticesCount = model.verticesCount;
        triangleFaceCount = model.triangleFaceCount;
        texturesCount = model.texturesCount;

        if (shareVertices) {
            verticesX = model.verticesX;
            verticesY = model.verticesY;
            verticesZ = model.verticesZ;
        } else {
            verticesX = new int[verticesCount];
            verticesY = new int[verticesCount];
            verticesZ = new int[verticesCount];

            for (int i = 0; i < verticesCount; i++) {
                verticesX[i] = model.verticesX[i];
                verticesY[i] = model.verticesY[i];
                verticesZ[i] = model.verticesZ[i];
            }
        }

        if (shareColours) {
            faceColors = model.faceColors;
        } else {
            faceColors = new int[triangleFaceCount];
            System.arraycopy(model.faceColors, 0, faceColors, 0, triangleFaceCount);
        }

        if (shareAlphas) {
            faceTransparencies = model.faceTransparencies;
        } else {
            faceTransparencies = new int[triangleFaceCount];
            if (model.faceTransparencies == null) {
                for (int i = 0; i < triangleFaceCount; i++) {
                    faceTransparencies[i] = 0;
                }
            } else {
                System.arraycopy(model.faceTransparencies, 0, faceTransparencies, 0, triangleFaceCount);
            }
        }

        if (shareTextures) {
            faceTextures = model.faceTextures;
        } else {
            if (model.faceTextures != null) {
                faceTextures = new short[triangleFaceCount];
                System.arraycopy(model.faceTextures, 0, faceTextures, 0, triangleFaceCount);
            }
        }

        vertexSkins = model.vertexSkins;
        triangleSkinValues = model.triangleSkinValues;
        faceRenderType = model.faceRenderType;
        trianglePointsX = model.trianglePointsX;
        trianglePointsY = model.trianglePointsY;
        trianglePointsZ = model.trianglePointsZ;
        faceRenderPriorities = model.faceRenderPriorities;
        textureCoords = model.textureCoords;
        priority = model.priority;
        textureRenderTypes = model.textureRenderTypes;
        texTriangleX = model.texTriangleX;
        texTriangleY = model.texTriangleY;
        texTriangleZ = model.texTriangleZ;
        vertexNormals = model.vertexNormals;
        faceNormals = model.faceNormals;
        vertexNormalsOffsets = model.vertexNormalsOffsets;
        animayaScales = model.animayaScales;
        animayaGroups = model.animayaGroups;
        ambient = model.ambient;
        contrast = model.contrast;
    }

    public void apply(int frame) {
        if (vertexGroups == null)
            return;
        else if (frame == -1)
            return;

        Frame animation = FrameLoader.lookup(frame);
        if (animation == null)
            return;

        FrameBase base = animation.getBase();
        centroidX = 0;
        centroidY = 0;
        centroidZ = 0;

        for (int transformation = 0; transformation < animation.getTransformationCount(); transformation++) {
            int group = animation.getTransformationIndex(transformation);
            transform(base.getTransformationType(group), base.getLabels(group), animation.getTransformX(transformation),
                    animation.getTransformY(transformation), animation.getTransformZ(transformation));
        }
    }

    public void computeBounds() {
        super.modelHeight = 0;
        boundingPlaneRadius = 0;
        minimumY = 0;
        minimumX = 0xf423f;
        maximumX = 0xfff0bdc1;
        maximumZ = 0xfffe7961;
        minimumZ = 0x1869f;

        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int x = verticesX[vertex];
            int y = verticesY[vertex];
            int z = verticesZ[vertex];

            if (x < minimumX) {
                minimumX = x;
            }

            if (x > maximumX) {
                maximumX = x;
            }

            if (z < minimumZ) {
                minimumZ = z;
            }

            if (z > maximumZ) {
                maximumZ = z;
            }

            if (-y > super.modelHeight) {
                super.modelHeight = -y;
            }

            if (y > minimumY) {
                minimumY = y;
            }

            int radius = x * x + z * z;
            if (radius > boundingPlaneRadius) {
                boundingPlaneRadius = radius;
            }
        }

        boundingPlaneRadius = (int) Math.sqrt(boundingPlaneRadius);
        boundingCylinderRadius = (int) Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + super.modelHeight * super.modelHeight);
        boundingSphereRadius = boundingCylinderRadius + (int) Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY);
    }

    public void computeCircularBounds() {
        super.modelHeight = 0;
        boundingPlaneRadius = 0;
        minimumY = 0;

        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int x = verticesX[vertex];
            int y = verticesY[vertex];
            int z = verticesZ[vertex];

            if (-y > super.modelHeight) {
                super.modelHeight = -y;
            }

            if (y > minimumY) {
                minimumY = y;
            }

            int radius = x * x + z * z;
            if (radius > boundingPlaneRadius) {
                boundingPlaneRadius = radius;
            }
        }

        boundingPlaneRadius = (int) (Math.sqrt(boundingPlaneRadius) + 0.99D);
        boundingCylinderRadius = (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + super.modelHeight * super.modelHeight) + 0.99D);
        boundingSphereRadius = boundingCylinderRadius + (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY) + 0.99D);
    }

    public void computeSphericalBounds() {
        super.modelHeight = 0;
        minimumY = 0;

        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int y = verticesY[vertex];
            if (-y > super.modelHeight) {
                super.modelHeight = -y;
            }

            if (y > minimumY) {
                minimumY = y;
            }
        }

        boundingCylinderRadius = (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
        boundingSphereRadius = boundingCylinderRadius + (int) (Math.sqrt(boundingPlaneRadius * boundingPlaneRadius + minimumY * minimumY) + 0.98999999999999999D);
    }

    private int findMatchingVertex(Mesh model, int vertex) {
        int matched = -1;
        int x = model.verticesX[vertex];
        int y = model.verticesY[vertex];
        int z = model.verticesZ[vertex];

        for (int index = 0; index < verticesCount; index++) {
            if (x == verticesX[index] && y == verticesY[index] && z == verticesZ[index]) {
                matched = index;
                break;
            }
        }

        if (matched == -1) {
            verticesX[verticesCount] = x;
            verticesY[verticesCount] = y;
            verticesZ[verticesCount] = z;

            if (model.vertexSkins != null) {
                vertexSkins[verticesCount] = model.vertexSkins[vertex];
            }

            matched = verticesCount++;
        }

        return matched;
    }

    public void invert() {
        for (int i = 0; i < verticesCount; i++) {
            verticesZ[i] = -verticesZ[i];
        }

        for (int i = 0; i < triangleFaceCount; i++) {
            int x = trianglePointsX[i];
            trianglePointsX[i] = trianglePointsZ[i];
            trianglePointsZ[i] = x;
        }
    }

    public void calculateVertexNormals() {
        if (vertexNormals == null) {
            vertexNormals = new VertexNormal[verticesCount];

            int var1;
            for (var1 = 0; var1 < verticesCount; ++var1) {
                vertexNormals[var1] = new VertexNormal();
            }

            for (var1 = 0; var1 < triangleFaceCount; ++var1) {
                final int var2 = trianglePointsX[var1];
                final int var3 = trianglePointsY[var1];
                final int var4 = trianglePointsZ[var1];
                final int var5 = verticesX[var3] - verticesX[var2];
                final int var6 = verticesY[var3] - verticesY[var2];
                final int var7 = verticesZ[var3] - verticesZ[var2];
                final int var8 = verticesX[var4] - verticesX[var2];
                final int var9 = verticesY[var4] - verticesY[var2];
                final int var10 = verticesZ[var4] - verticesZ[var2];
                int var11 = var6 * var10 - var9 * var7;
                int var12 = var7 * var8 - var10 * var5;

                int var13;
                for (var13 = var5 * var9 - var8 * var6;
                     var11 > 8192 || var12 > 8192 || var13 > 8192 || var11 < -8192 || var12 < -8192 || var13 < -8192; var13 >>= 1) {
                    var11 >>= 1;
                    var12 >>= 1;
                }

                int var14 = (int) Math.sqrt(var11 * var11 + var12 * var12 + var13 * var13);
                if (var14 <= 0) {
                    var14 = 1;
                }

                var11 = var11 * 256 / var14;
                var12 = var12 * 256 / var14;
                var13 = var13 * 256 / var14;
                final int var15;
                if (faceRenderType == null) {
                    var15 = 0;
                } else {
                    var15 = faceRenderType[var1];
                }

                if (var15 == 0) {
                    VertexNormal var16 = vertexNormals[var2];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = vertexNormals[var3];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = vertexNormals[var4];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                } else if (var15 == 1) {
                    if (faceNormals == null) {
                        faceNormals = new FaceNormal[triangleFaceCount];
                    }

                    final FaceNormal var17 = faceNormals[var1] = new FaceNormal();
                    var17.x = var11;
                    var17.y = var12;
                    var17.z = var13;
                }
            }
        }
    }

    public void light(final int ambient, final int contrast, final int x, final int y, final int z) {
        this.contrast = (short) contrast;
        this.ambient = (short) ambient;

        calculateVertexNormals();
        final int magnitude = (int) Math.sqrt(x * x + y * y + z * z);
        final int var7 = contrast * magnitude >> 8;
        colorsX = new int[triangleFaceCount];
        colorsY = new int[triangleFaceCount];
        colorsZ = new int[triangleFaceCount];

        for (int var16 = 0; var16 < this.triangleFaceCount; ++var16) {
            int var17;
            if (this.faceRenderType == null) {
                var17 = 0;
            } else {
                var17 = this.faceRenderType[var16];
            }

            int var18;
            if (this.faceTransparencies == null) {
                var18 = 0;
            } else {
                var18 = this.faceTransparencies[var16];
            }

            short var12;
            if (this.faceTextures == null) {
                var12 = -1;
            } else {
                var12 = this.faceTextures[var16];
            }

            if (var18 == -2) {
                var17 = 3;
            }

            if (var18 == -1) {
                var17 = 2;
            }

            VertexNormal var13;
            int var14;
            FaceNormal var19;
            if (var12 == -1) {
                if (var17 != 0) {
                    if (var17 == 1) {
                        var19 = this.faceNormals[var16];
                        var14 = (y * var19.y + z * var19.z + x * var19.x) / (var7 / 2 + var7) + ambient;
                        colorsX[var16] = method2792(this.faceColors[var16] & '\uffff', var14);
                        colorsZ[var16] = -1;
                    } else if (var17 == 3) {
                        colorsX[var16] = 128;
                        colorsZ[var16] = -1;
                    } else {
                        colorsZ[var16] = -2;
                    }
                } else {
                    int var15 = this.faceColors[var16] & '\uffff';
                    if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglePointsX[var16]] != null) {
                        var13 = this.vertexNormalsOffsets[this.trianglePointsX[var16]];
                    } else {
                        var13 = this.vertexNormals[this.trianglePointsX[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                    colorsX[var16] = method2792(var15, var14);
                    if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglePointsY[var16]] != null) {
                        var13 = this.vertexNormalsOffsets[this.trianglePointsY[var16]];
                    } else {
                        var13 = this.vertexNormals[this.trianglePointsY[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                    colorsY[var16] = method2792(var15, var14);
                    if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglePointsZ[var16]] != null) {
                        var13 = this.vertexNormalsOffsets[this.trianglePointsZ[var16]];
                    } else {
                        var13 = this.vertexNormals[this.trianglePointsZ[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                    colorsZ[var16] = method2792(var15, var14);
                }
            } else if (var17 != 0) {
                if (var17 == 1) {
                    var19 = this.faceNormals[var16];
                    var14 = (y * var19.y + z * var19.z + x * var19.x) / (var7 / 2 + var7) + ambient;
                    colorsX[var16] = method2820(var14);
                    colorsZ[var16] = -1;
                } else {
                    colorsZ[var16] = -2;
                }
            } else {
                if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglePointsX[var16]] != null) {
                    var13 = this.vertexNormalsOffsets[this.trianglePointsX[var16]];
                } else {
                    var13 = this.vertexNormals[this.trianglePointsX[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                colorsX[var16] = method2820(var14);
                if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglePointsY[var16]] != null) {
                    var13 = this.vertexNormalsOffsets[this.trianglePointsY[var16]];
                } else {
                    var13 = this.vertexNormals[this.trianglePointsY[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                colorsY[var16] = method2820(var14);
                if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglePointsZ[var16]] != null) {
                    var13 = this.vertexNormalsOffsets[this.trianglePointsZ[var16]];
                } else {
                    var13 = this.vertexNormals[this.trianglePointsZ[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                colorsZ[var16] = method2820(var14);
            }
        }

        computeCircularBounds();
    }

    private int method2820(int var0) {
        if (var0 < 2) {
            var0 = 2;
        } else if (var0 > 126) {
            var0 = 126;
        }
        return var0;
    }

    private int method2792(final int var0, int var1) {
        var1 = (var0 & 127) * var1 >> 7;
        if (var1 < 2) {
            var1 = 2;
        } else if (var1 > 126) {
            var1 = 126;
        }
        return (var0 & '\uff80') + var1;
    }


    private void renderFaces(GameRasterizer rasterizer, boolean flag, boolean multiTileFlag, ObjectKey key, int z) {
        for (int j = 0; j < boundingSphereRadius; j++) {
            rasterizer.depthListIndices[j] = 0;
        }

        for (int face = 0; face < triangleFaceCount; face++) {
            if (colorsZ[face] != -2) {
                int indexX = trianglePointsX[face];
                int indexY = trianglePointsY[face];
                int indexZ = trianglePointsZ[face];
                int i3 = rasterizer.vertexScreenX[indexX];
                int l3 = rasterizer.vertexScreenX[indexY];
                int k4 = rasterizer.vertexScreenX[indexZ];

                if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
                    rasterizer.cullFacesOther[face] = true;
                    int j5 = (rasterizer.vertexScreenZ[indexX] + rasterizer.vertexScreenZ[indexY] + rasterizer.vertexScreenZ[indexZ]) / 3 + boundingCylinderRadius;
                    rasterizer.faceList[j5][rasterizer.depthListIndices[j5]++] = face;
                } else {
                    if (key != null && multiTileFlag) {
                        if (insideTriangle(mouseX, mouseY, rasterizer.vertexScreenY[indexX], rasterizer.vertexScreenY[indexY], rasterizer.vertexScreenY[indexZ], i3, l3, k4)) {

                            boolean correctTool = (Options.currentTool.get() == ToolType.SELECT_OBJECT || Options.currentTool.get() == ToolType.DELETE_OBJECT);
                            if (Options.currentHeight.get() == z && correctTool) {
                                int type = key.getType();
                                int selectionType = Options.objectSelectionType.get() - 1;
                                if (selectionType == -1 || type == selectionType) {
                                    resourceIDTag[resourceCount++] = key;
                                    if (Client.hoveredUID == null) {
                                        Client.hoveredUID = key;
                                    }
                                }
                            }

                            multiTileFlag = false;
                        } else if (Objects.equals(Client.hoveredUID, key)) {
                            Client.hoveredUID = null;
                        }
                    }

                    translucent = false;
                    if (key != null) {
                        if (Options.currentTool.get() == ToolType.SELECT_OBJECT || Options.currentTool.get() == ToolType.DELETE_OBJECT)
                            translucent = Objects.equals(Client.hoveredUID, key) && z == Options.currentHeight.get();
                        else
                            translucent = false;
                    }
                    if ((i3 - l3) * (rasterizer.vertexScreenY[indexZ] - rasterizer.vertexScreenY[indexY])
                            - (rasterizer.vertexScreenY[indexX] - rasterizer.vertexScreenY[indexY]) * (k4 - l3) > 0) {
                        rasterizer.cullFacesOther[face] = false;
                        rasterizer.cullFaces[face] = i3 < 0 || l3 < 0 || k4 < 0 || i3 > rasterizer.getMaxRight() || l3 > rasterizer.getMaxRight()
                                || k4 > rasterizer.getMaxRight();
                        int k5 = (rasterizer.vertexScreenZ[indexX] + rasterizer.vertexScreenZ[indexY] + rasterizer.vertexScreenZ[indexZ]) / 3
                                + boundingCylinderRadius;
                        if (k5 >= 0 && k5 < rasterizer.faceList.length)
                            rasterizer.faceList[k5][rasterizer.depthListIndices[k5]++] = face;
                    }
                }
            }
        }

        if (faceRenderPriorities == null) {
            for (int i1 = boundingSphereRadius - 1; i1 >= 0; i1--) {
                int l1 = rasterizer.depthListIndices[i1];
                if (l1 > 0) {
                    int[] ai = rasterizer.faceList[i1];
                    for (int j3 = 0; j3 < l1; j3++) {
                        renderFace(rasterizer, ai[j3]);
                    }
                }
            }
            return;
        }
        for (int j1 = 0; j1 < 12; j1++) {
            rasterizer.anIntArray1673[j1] = 0;
            rasterizer.anIntArray1677[j1] = 0;
        }

        for (int i2 = boundingSphereRadius - 1; i2 >= 0; i2--) {
            int k2 = rasterizer.depthListIndices[i2];
            if (k2 > 0) {
                int[] ai1 = rasterizer.faceList[i2];
                for (int i4 = 0; i4 < k2; i4++) {
                    int l4 = ai1[i4];
                    int l5 = faceRenderPriorities[l4];
                    int j6 = rasterizer.anIntArray1673[l5]++;
                    rasterizer.anIntArrayArray1674[l5][j6] = l4;
                    if (l5 < 10) {
                        rasterizer.anIntArray1677[l5] += i2;
                    } else if (l5 == 10) {
                        rasterizer.anIntArray1675[j6] = i2;
                    } else {
                        rasterizer.anIntArray1676[j6] = i2;
                    }
                }

            }
        }

        int l2 = 0;
        if (rasterizer.anIntArray1673[1] > 0 || rasterizer.anIntArray1673[2] > 0) {
            l2 = (rasterizer.anIntArray1677[1] + rasterizer.anIntArray1677[2]) / (rasterizer.anIntArray1673[1] + rasterizer.anIntArray1673[2]);
        }
        int k3 = 0;
        if (rasterizer.anIntArray1673[3] > 0 || rasterizer.anIntArray1673[4] > 0) {
            k3 = (rasterizer.anIntArray1677[3] + rasterizer.anIntArray1677[4]) / (rasterizer.anIntArray1673[3] + rasterizer.anIntArray1673[4]);
        }
        int j4 = 0;
        if (rasterizer.anIntArray1673[6] > 0 || rasterizer.anIntArray1673[8] > 0) {
            j4 = (rasterizer.anIntArray1677[6] + rasterizer.anIntArray1677[8]) / (rasterizer.anIntArray1673[6] + rasterizer.anIntArray1673[8]);
        }
        int i6 = 0;
        int k6 = rasterizer.anIntArray1673[10];
        int[] ai2 = rasterizer.anIntArrayArray1674[10];
        int[] ai3 = rasterizer.anIntArray1675;
        if (i6 == k6) {
            i6 = 0;
            k6 = rasterizer.anIntArray1673[11];
            ai2 = rasterizer.anIntArrayArray1674[11];
            ai3 = rasterizer.anIntArray1676;
        }
        int i5;
        if (i6 < k6) {
            i5 = ai3[i6];
        } else {
            i5 = -1000;
        }
        for (int l6 = 0; l6 < 10; l6++) {
            while (l6 == 0 && i5 > l2) {
                renderFace(rasterizer, ai2[i6++]);
                if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = rasterizer.anIntArray1673[11];
                    ai2 = rasterizer.anIntArrayArray1674[11];
                    ai3 = rasterizer.anIntArray1676;
                }
                if (i6 < k6) {
                    i5 = ai3[i6];
                } else {
                    i5 = -1000;
                }
            }
            while (l6 == 3 && i5 > k3) {
                renderFace(rasterizer, ai2[i6++]);
                if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = rasterizer.anIntArray1673[11];
                    ai2 = rasterizer.anIntArrayArray1674[11];
                    ai3 = rasterizer.anIntArray1676;
                }
                if (i6 < k6) {
                    i5 = ai3[i6];
                } else {
                    i5 = -1000;
                }
            }
            while (l6 == 5 && i5 > j4) {
                renderFace(rasterizer, ai2[i6++]);
                if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = rasterizer.anIntArray1673[11];
                    ai2 = rasterizer.anIntArrayArray1674[11];
                    ai3 = rasterizer.anIntArray1676;
                }
                if (i6 < k6) {
                    i5 = ai3[i6];
                } else {
                    i5 = -1000;
                }
            }
            int i7 = rasterizer.anIntArray1673[l6];
            int[] ai4 = rasterizer.anIntArrayArray1674[l6];
            for (int j7 = 0; j7 < i7; j7++) {
                renderFace(rasterizer, ai4[j7]);
            }
        }

        while (i5 != -1000) {
            renderFace(rasterizer, ai2[i6++]);
            if (i6 == k6 && ai2 != rasterizer.anIntArrayArray1674[11]) {
                i6 = 0;
                ai2 = rasterizer.anIntArrayArray1674[11];
                k6 = rasterizer.anIntArray1673[11];
                ai3 = rasterizer.anIntArray1676;
            }
            if (i6 < k6) {
                i5 = ai3[i6];
            } else {
                i5 = -1000;
            }
        }
    }

    private void renderFace(GameRasterizer rasterizer, int idx) {
        if (rasterizer.cullFacesOther[idx]) {
            method485(rasterizer, idx);
            return;
        }

        int triangleX = trianglePointsX[idx];
        int triangleY = trianglePointsY[idx];
        int triangleZ = trianglePointsZ[idx];
        rasterizer.restrictEdges = rasterizer.cullFaces[idx];
        if (selected) {
            rasterizer.currentAlpha = translucent ? 100 : 50;
        }

        if (translucent) {
            rasterizer.currentAlpha = 140;
        } else if (!selected) {
            if (faceTransparencies == null) {
                rasterizer.currentAlpha = 0;
            } else {
                rasterizer.currentAlpha = faceTransparencies[idx];
            }
        }

        int type;
        if (faceRenderType == null) {
            type = 0;
        } else {
            type = faceRenderType[idx] & 3;

        }

        if (translucent || selected) {
            rasterizer.drawFlatTriangle(rasterizer.vertexScreenY[triangleX], rasterizer.vertexScreenY[triangleY], rasterizer.vertexScreenY[triangleZ],
                    rasterizer.vertexScreenX[triangleX], rasterizer.vertexScreenX[triangleY], rasterizer.vertexScreenX[triangleZ],
                    selected ? 0xc5dce6 : 16118771);
        } else {
            if (faceTextures != null && faceTextures[idx] != -1) {
                int textureX;
                int textureY;
                int textureZ;
                if (textureCoords != null && textureCoords[idx] != -1) {
                    int coordinate = textureCoords[idx] & 0xff;
                    textureX = texTriangleX[coordinate];
                    textureY = texTriangleY[coordinate];
                    textureZ = texTriangleZ[coordinate];
                } else {
                    textureX = triangleX;
                    textureY = triangleY;
                    textureZ = triangleZ;
                }

                if (colorsZ[idx] == -1) {
                    rasterizer.drawTexturedTriangle_Model(
                            rasterizer.vertexScreenY[triangleX], rasterizer.vertexScreenY[triangleY], rasterizer.vertexScreenY[triangleZ],
                            rasterizer.vertexScreenX[triangleX], rasterizer.vertexScreenX[triangleY], rasterizer.vertexScreenX[triangleZ],
                            colorsX[idx], colorsX[idx], colorsX[idx],
                            rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ],
                            rasterizer.camera_vertex_y[textureX], rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ],
                            rasterizer.camera_vertex_z[textureX], rasterizer.camera_vertex_z[textureY], rasterizer.camera_vertex_z[textureZ],
                            faceTextures[idx]);
                } else {
                    rasterizer.drawTexturedTriangle_Model(
                            rasterizer.vertexScreenY[triangleX], rasterizer.vertexScreenY[triangleY], rasterizer.vertexScreenY[triangleZ],
                            rasterizer.vertexScreenX[triangleX], rasterizer.vertexScreenX[triangleY], rasterizer.vertexScreenX[triangleZ],
                            colorsX[idx], colorsY[idx], colorsZ[idx],
                            rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ],
                            rasterizer.camera_vertex_y[textureX], rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ],
                            rasterizer.camera_vertex_z[textureX], rasterizer.camera_vertex_z[textureY], rasterizer.camera_vertex_z[textureZ],
                            faceTextures[idx]);
                }
            } else if (colorsZ[idx] == -1) {
                rasterizer.drawFlatTriangle(rasterizer.vertexScreenY[triangleX], rasterizer.vertexScreenY[triangleY], rasterizer.vertexScreenY[triangleZ],
                        rasterizer.vertexScreenX[triangleX], rasterizer.vertexScreenX[triangleY], rasterizer.vertexScreenX[triangleZ],
                        rasterizer.colourPalette[colorsX[idx]]);
            } else {
                if (type == 0) {
                    rasterizer.drawGouraudTriangle(rasterizer.vertexScreenY[triangleX], rasterizer.vertexScreenY[triangleY],
                            rasterizer.vertexScreenY[triangleZ], rasterizer.vertexScreenX[triangleX], rasterizer.vertexScreenX[triangleY],
                            rasterizer.vertexScreenX[triangleZ], colorsX[idx], colorsY[idx],
                            colorsZ[idx]);
                }
            }
        }
    }

    private void method485(GameRasterizer rasterizer, int idx) {
        int viewX = rasterizer.viewCenter.getX();
        int viewY = rasterizer.viewCenter.getY();
        int l = 0;
        int i1 = trianglePointsX[idx];
        int j1 = trianglePointsY[idx];
        int k1 = trianglePointsZ[idx];
        int l1 = rasterizer.camera_vertex_z[i1];
        int i2 = rasterizer.camera_vertex_z[j1];
        int j2 = rasterizer.camera_vertex_z[k1];
        if (l1 >= 50) {
            rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[i1];
            rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[i1];
            rasterizer.anIntArray1680[l++] = colorsX[idx];
        } else {
            int k2 = rasterizer.camera_vertex_x[i1];
            int k3 = rasterizer.camera_vertex_y[i1];
            int k4 = colorsX[idx];
            if (j2 >= 50) {
                int k5 = (50 - l1) * Constants.LIGHT_DECAY[j2 - l1];
                rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[k1] - k2) * k5 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[k1] - k3) * k5 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = k4 + ((colorsZ[idx] - k4) * k5 >> 16);
            }
            if (i2 >= 50) {
                int l5 = (50 - l1) * Constants.LIGHT_DECAY[i2 - l1];
                rasterizer.anIntArray1678[l] = viewX + (k2 + ((rasterizer.camera_vertex_x[j1] - k2) * l5 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (k3 + ((rasterizer.camera_vertex_y[j1] - k3) * l5 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = k4 + ((colorsY[idx] - k4) * l5 >> 16);
            }
        }
        if (i2 >= 50) {
            rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[j1];
            rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[j1];
            rasterizer.anIntArray1680[l++] = colorsY[idx];
        } else {
            int l2 = rasterizer.camera_vertex_x[j1];
            int l3 = rasterizer.camera_vertex_y[j1];
            int l4 = colorsY[idx];
            if (l1 >= 50) {
                int i6 = (50 - i2) * Constants.LIGHT_DECAY[l1 - i2];
                rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[i1] - l2) * i6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[i1] - l3) * i6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = l4 + ((colorsX[idx] - l4) * i6 >> 16);
            }
            if (j2 >= 50) {
                int j6 = (50 - i2) * Constants.LIGHT_DECAY[j2 - i2];
                rasterizer.anIntArray1678[l] = viewX + (l2 + ((rasterizer.camera_vertex_x[k1] - l2) * j6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (l3 + ((rasterizer.camera_vertex_y[k1] - l3) * j6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = l4 + ((colorsZ[idx] - l4) * j6 >> 16);
            }
        }
        if (j2 >= 50) {
            rasterizer.anIntArray1678[l] = rasterizer.vertexScreenX[k1];
            rasterizer.anIntArray1679[l] = rasterizer.vertexScreenY[k1];
            rasterizer.anIntArray1680[l++] = colorsZ[idx];
        } else {
            int i3 = rasterizer.camera_vertex_x[k1];
            int i4 = rasterizer.camera_vertex_y[k1];
            int i5 = colorsZ[idx];
            if (i2 >= 50) {
                int k6 = (50 - j2) * Constants.LIGHT_DECAY[i2 - j2];
                rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[j1] - i3) * k6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[j1] - i4) * k6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = i5 + ((colorsY[idx] - i5) * k6 >> 16);
            }
            if (l1 >= 50) {
                int l6 = (50 - j2) * Constants.LIGHT_DECAY[l1 - j2];
                rasterizer.anIntArray1678[l] = viewX + (i3 + ((rasterizer.camera_vertex_x[i1] - i3) * l6 >> 16) << 9) / 50;
                rasterizer.anIntArray1679[l] = viewY + (i4 + ((rasterizer.camera_vertex_y[i1] - i4) * l6 >> 16) << 9) / 50;
                rasterizer.anIntArray1680[l++] = i5 + ((colorsX[idx] - i5) * l6 >> 16);
            }
        }
        int j3 = rasterizer.anIntArray1678[0];
        int j4 = rasterizer.anIntArray1678[1];
        int j5 = rasterizer.anIntArray1678[2];
        int i7 = rasterizer.anIntArray1679[0];
        int j7 = rasterizer.anIntArray1679[1];
        int k7 = rasterizer.anIntArray1679[2];
        boolean ignoreTextures = translucent || selected;
        if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
            rasterizer.restrictEdges = false;
            int textureX = i1;
            int textureY = j1;
            int textureZ = k1;
            if (l == 3) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > rasterizer.getMaxRight() || j4 > rasterizer.getMaxRight() || j5 > rasterizer.getMaxRight()) {
                    rasterizer.restrictEdges = true;
                }

                int type;
                if (faceRenderType == null) {
                    type = 0;
                } else {
                    type = faceRenderType[idx] & 3;
                }

                if (ignoreTextures) {
                    rasterizer.drawFlatTriangle(i7, j7, k7, j3, j4, j5, selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[colorsX[idx]]);
                } else {
                    if (faceTextures != null && faceTextures[idx] != -1) {
                        if (textureCoords != null && textureCoords[idx] != -1) {
                            int coordinate = textureCoords[idx] & 0xff;
                            textureX = texTriangleX[coordinate];
                            textureY = texTriangleY[coordinate];
                            textureZ = texTriangleZ[coordinate];
                        }

                        if (colorsZ[idx] == -1) {
                            rasterizer.drawTexturedTriangle_Model(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1], rasterizer.anIntArray1680[2],
                                    rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ], rasterizer.camera_vertex_y[textureX],
                                    rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ], rasterizer.camera_vertex_z[textureX], rasterizer.camera_vertex_z[textureY],
                                    rasterizer.camera_vertex_z[textureZ], faceTextures[idx]);
                        } else {
                            rasterizer.drawTexturedTriangle_Model(i7, j7, k7, j3, j4, j5, colorsX[idx], colorsX[idx], colorsX[idx],
                                    rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ], rasterizer.camera_vertex_y[textureX],
                                    rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ], rasterizer.camera_vertex_z[textureX], rasterizer.camera_vertex_z[textureY],
                                    rasterizer.camera_vertex_z[textureZ], faceTextures[idx]);
                        }
                    } else if (colorsZ[idx] == -1) {
                        rasterizer.drawFlatTriangle(i7, j7, k7, j3, j4, j5, rasterizer.colourPalette[colorsX[idx]]);
                    } else {
                        if (type == 0) {
                            rasterizer.drawGouraudTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1], rasterizer.anIntArray1680[2]);
                        }
                    }
                }
            }
            if (l == 4) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > rasterizer.getMaxRight() || j4 > rasterizer.getMaxRight() || j5 > rasterizer.getMaxRight() || rasterizer.anIntArray1678[3] < 0
                        || rasterizer.anIntArray1678[3] > rasterizer.getMaxRight()) {
                    rasterizer.restrictEdges = true;
                }

                int type;
                if (faceRenderType == null) {
                    type = 0;
                } else {
                    type = faceRenderType[idx] & 3;
                }

                if (ignoreTextures) {
                    int l8 = selected ? 0xc5dce6 : translucent ? 16118771 : rasterizer.colourPalette[colorsX[idx]];
                    rasterizer.drawFlatTriangle(i7, j7, k7, j3, j4, j5, l8);
                    rasterizer.drawFlatTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], l8);
                } else {
                    if (faceTextures != null && faceTextures[idx] != -1) {
                        if (textureCoords != null && textureCoords[idx] != -1) {
                            int coordinate = textureCoords[idx] & 0xff;
                            textureX = texTriangleX[coordinate];
                            textureY = texTriangleY[coordinate];
                            textureZ = texTriangleZ[coordinate];
                        }

                        if (colorsZ[idx] == -1) {
                            rasterizer.drawTexturedTriangle_Model(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1], rasterizer.anIntArray1680[2],
                                    rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ], rasterizer.camera_vertex_y[textureX],
                                    rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ], rasterizer.camera_vertex_z[textureX], rasterizer.camera_vertex_z[textureY],
                                    rasterizer.camera_vertex_z[textureZ], faceTextures[idx]);
                            rasterizer.drawTexturedTriangle_Model(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2],
                                    rasterizer.anIntArray1680[3], rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ],
                                    rasterizer.camera_vertex_y[textureX], rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ], rasterizer.camera_vertex_z[textureX],
                                    rasterizer.camera_vertex_z[textureY], rasterizer.camera_vertex_z[textureZ], faceTextures[idx]);
                        } else {
                            rasterizer.drawTexturedTriangle_Model(i7, j7, k7, j3, j4, j5, colorsX[idx], colorsX[idx], colorsX[idx],
                                    rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ], rasterizer.camera_vertex_y[textureX],
                                    rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ], rasterizer.camera_vertex_z[textureX], rasterizer.camera_vertex_z[textureY],
                                    rasterizer.camera_vertex_z[textureZ], faceTextures[idx]);
                            rasterizer.drawTexturedTriangle_Model(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], colorsX[idx], colorsX[idx],
                                    colorsX[idx], rasterizer.camera_vertex_x[textureX], rasterizer.camera_vertex_x[textureY], rasterizer.camera_vertex_x[textureZ],
                                    rasterizer.camera_vertex_y[textureX], rasterizer.camera_vertex_y[textureY], rasterizer.camera_vertex_y[textureZ], rasterizer.camera_vertex_z[textureX],
                                    rasterizer.camera_vertex_z[textureY], rasterizer.camera_vertex_z[textureZ], faceTextures[idx]);
                        }
                    } else if (colorsZ[idx] == -1) {
                        int l8 = rasterizer.colourPalette[colorsX[idx]];
                        rasterizer.drawFlatTriangle(i7, j7, k7, j3, j4, j5, l8);
                        rasterizer.drawFlatTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], l8);
                    } else {
                        if (type == 0) {
                            rasterizer.drawGouraudTriangle(i7, j7, k7, j3, j4, j5, rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[1], rasterizer.anIntArray1680[2]);
                            rasterizer.drawGouraudTriangle(i7, k7, rasterizer.anIntArray1679[3], j3, j5, rasterizer.anIntArray1678[3], rasterizer.anIntArray1680[0], rasterizer.anIntArray1680[2], rasterizer.anIntArray1680[3]);
                        }
                    }
                }
            }
        }
    }

    public void pitch(int theta) {
        int sin = Constants.SINE[theta];
        int cos = Constants.COSINE[theta];

        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int y = verticesY[vertex] * cos - verticesZ[vertex] * sin >> 16;
            verticesZ[vertex] = verticesY[vertex] * sin + verticesZ[vertex] * cos >> 16;
            verticesY[vertex] = y;
        }
    }

    public void prepareSkeleton() {
        if (vertexSkins != null) {
            int[] sizes = new int[256];
            int maximumBoneId = 0;

            for (int vertex = 0; vertex < verticesCount; vertex++) {
                int bone = vertexSkins[vertex];
                sizes[bone]++;

                if (bone > maximumBoneId) {
                    maximumBoneId = bone;
                }
            }

            vertexGroups = new int[maximumBoneId + 1][];
            for (int index = 0; index <= maximumBoneId; index++) {
                vertexGroups[index] = new int[sizes[index]];
                sizes[index] = 0;
            }

            for (int index = 0; index < verticesCount; index++) {
                int bone = vertexSkins[index];
                vertexGroups[bone][sizes[bone]++] = index;
            }

            vertexSkins = null;
        }

        if (triangleSkinValues != null) {
            int[] sizes = new int[256];
            int count = 0;

            for (int index = 0; index < triangleFaceCount; index++) {
                int skin = triangleSkinValues[index];
                sizes[skin]++;

                if (skin > count) {
                    count = skin;
                }
            }

            faceGroups = new int[count + 1][];
            for (int index = 0; index <= count; index++) {
                faceGroups[index] = new int[sizes[index]];
                sizes[index] = 0;
            }

            for (int index = 0; index < triangleFaceCount; index++) {
                int skin = triangleSkinValues[index];
                faceGroups[skin][sizes[skin]++] = index;
            }

            triangleSkinValues = null;
        }
    }

    public void recolour(int oldColour, int newColour) {
        for (int index = 0; index < triangleFaceCount; index++) {
            if (faceColors[index] == oldColour) {
                faceColors[index] = newColour;
            }
        }
    }

    public void render(GameRasterizer rasterizer, int rotationX, int roll, int yaw, int pitch, int transX, int transY, int transZ, int plane) {
        int viewX = rasterizer.viewCenter.getX();
        int viewY = rasterizer.viewCenter.getY();
        int j2 = Constants.SINE[rotationX];
        int k2 = Constants.COSINE[rotationX];
        int l2 = Constants.SINE[roll];
        int i3 = Constants.COSINE[roll];
        int j3 = Constants.SINE[yaw];
        int k3 = Constants.COSINE[yaw];
        int sinXWorld = Constants.SINE[pitch];
        int cosXWorld = Constants.COSINE[pitch];
        int j4 = transY * sinXWorld + transZ * cosXWorld >> 16;
        for (int k4 = 0; k4 < verticesCount; k4++) {
            int x = verticesX[k4];
            int y = verticesY[k4];
            int z = verticesZ[k4];
            if (yaw != 0) {
                int k5 = y * j3 + x * k3 >> 16;
                y = y * k3 - x * j3 >> 16;
                x = k5;
            }
            if (rotationX != 0) {
                int l5 = y * k2 - z * j2 >> 16;
                z = y * j2 + z * k2 >> 16;
                y = l5;
            }
            if (roll != 0) {
                int i6 = z * l2 + x * i3 >> 16;
                z = z * i3 - x * l2 >> 16;
                x = i6;
            }
            x += transX;
            y += transY;
            z += transZ;
            int j6 = y * cosXWorld - z * sinXWorld >> 16;
            z = y * sinXWorld + z * cosXWorld >> 16;
            y = j6;
            rasterizer.vertexScreenZ[k4] = z - j4;
            rasterizer.vertexScreenX[k4] = viewX + (x << 9) / z;
            rasterizer.vertexScreenY[k4] = viewY + (y << 9) / z;
            if (texturesCount > 0) {
                rasterizer.camera_vertex_x[k4] = x;
                rasterizer.camera_vertex_y[k4] = y;
                rasterizer.camera_vertex_z[k4] = z;
            }
        }

        try {
            renderFaces(rasterizer, false, false, null, plane);
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
    }

    @Override
    public void render(GameRasterizer rasterizer, int x, int y, int orientation, int ySine, int yCosine, int xSine, int xCosine, int height, ObjectKey key, int z) {
        int j2 = y * xCosine - x * xSine >> 16;
        int k2 = height * ySine + j2 * yCosine >> 16;
        int l2 = boundingPlaneRadius * yCosine >> 16;
        int i3 = k2 + l2;

        if (i3 <= 50 || k2 >= 6500)
            return;

        int j3 = y * xSine + x * xCosine >> 16;
        int sceneLowerX = j3 - boundingPlaneRadius << 9;
        if (sceneLowerX / i3 >= rasterizer.getCentreX())
            return;

        int sceneMaximumX = j3 + boundingPlaneRadius << 9;
        if (sceneMaximumX / i3 <= -rasterizer.getCentreX())
            return;

        int i4 = height * yCosine - j2 * ySine >> 16;
        int j4 = boundingPlaneRadius * ySine >> 16;
        int sceneMaximumY = i4 + j4 << 9;

        if (sceneMaximumY / i3 <= -rasterizer.getCentreY())
            return;

        int l4 = j4 + (super.modelHeight * yCosine >> 16);
        int sceneLowerY = i4 - l4 << 9;
        if (sceneLowerY / i3 >= rasterizer.getCentreY())
            return;

        int j5 = l2 + (super.modelHeight * ySine >> 16);
        boolean flag = false;
        if (k2 - j5 <= 50) {
            flag = true;
        }

        boolean flag1 = false;
        int k5 = k2 - l2;
        if (k5 <= 50) {
            k5 = 50;
        }

        if (j3 > 0) {
            sceneLowerX /= i3;
            sceneMaximumX /= k5;
        } else {
            sceneMaximumX /= i3;
            sceneLowerX /= k5;
        }

        if (i4 > 0) {
            sceneLowerY /= i3;
            sceneMaximumY /= k5;
        } else {
            sceneMaximumY /= i3;
            sceneLowerY /= k5;
        }

        int mouseSceneX = mouseX - rasterizer.viewCenter.getX();
        int mouseSceneY = mouseY - rasterizer.viewCenter.getY();
        if (mouseSceneX > sceneLowerX && mouseSceneX < sceneMaximumX && mouseSceneY > sceneLowerY && mouseSceneY < sceneMaximumY) {
            if (fitsOnSingleSquare) {
                resourceIDTag[resourceCount++] = key;
                if (Client.hoveredUID == null) {
                    Client.hoveredUID = key;
                }
            } else {
                flag1 = true;
            }
        }

        int viewX = rasterizer.viewCenter.getX();
        int viewY = rasterizer.viewCenter.getY();
        int sine = 0;
        int cosine = 0;

        if (orientation != 0) {
            sine = Constants.SINE[orientation];
            cosine = Constants.COSINE[orientation];
        }

        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int xVertex = verticesX[vertex];
            int yVertex = verticesY[vertex];
            int zVertex = verticesZ[vertex];
            if (orientation != 0) {
                int j8 = zVertex * sine + xVertex * cosine >> 16;
                zVertex = zVertex * cosine - xVertex * sine >> 16;
                xVertex = j8;
            }

            xVertex += x;
            yVertex += height;
            zVertex += y;
            int k8 = zVertex * xSine + xVertex * xCosine >> 16;
            zVertex = zVertex * xCosine - xVertex * xSine >> 16;
            xVertex = k8;
            k8 = yVertex * yCosine - zVertex * ySine >> 16;
            zVertex = yVertex * ySine + zVertex * yCosine >> 16;
            yVertex = k8;
            rasterizer.vertexScreenZ[vertex] = zVertex - k2;

            if (zVertex >= getZVertexMax()) {
                rasterizer.vertexScreenX[vertex] = viewX + (xVertex << 9) / zVertex;
                rasterizer.vertexScreenY[vertex] = viewY + (yVertex << 9) / zVertex;
            } else {
                rasterizer.vertexScreenX[vertex] = -5000;
                flag = true;
            }

            if (flag || texturesCount > 0) {
                rasterizer.camera_vertex_x[vertex] = xVertex;
                rasterizer.camera_vertex_y[vertex] = yVertex;
                rasterizer.camera_vertex_z[vertex] = zVertex;
            }
        }

        try {
            renderFaces(rasterizer, flag, flag1, key, z);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void rotateClockwise() {
        for (int index = 0; index < verticesCount; index++) {
            int x = verticesX[index];
            verticesX[index] = verticesZ[index];
            verticesZ[index] = -x;
        }
    }

    public void offsetVertices(int x, int y, int z) {
        for (int index = 0; index < verticesCount; index++) {
            verticesX[index] += x;
            verticesX[index] += y;
            verticesZ[index] += z;
        }
    }

    public void scale(int x, int y, int z) {
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            verticesX[vertex] = verticesX[vertex] * x / 128;
            verticesY[vertex] = verticesY[vertex] * z / 128;
            verticesZ[vertex] = verticesZ[vertex] * y / 128;
        }
    }

    private void transform(int transformation, int[] groups, int dx, int dy, int dz) {
        int count = groups.length;
        if (transformation == FrameConstants.CENTROID_TRANSFORMATION) {
            int vertices = 0;
            centroidX = 0;
            centroidY = 0;
            centroidZ = 0;

            for (int index = 0; index < count; index++) {
                int group = groups[index];
                if (group < vertexGroups.length) {
                    for (int vertex : vertexGroups[group]) {
                        centroidX += verticesX[vertex];
                        centroidY += verticesY[vertex];
                        centroidZ += verticesZ[vertex];
                        vertices++;
                    }
                }
            }

            if (vertices > 0) {
                centroidX = centroidX / vertices + dx;
                centroidY = centroidY / vertices + dy;
                centroidZ = centroidZ / vertices + dz;
            } else {
                centroidX = dx;
                centroidY = dy;
                centroidZ = dz;
            }
        } else if (transformation == FrameConstants.POSITION_TRANSFORMATION) {
            for (int index = 0; index < count; index++) {
                int group = groups[index];

                if (group < vertexGroups.length) {
                    for (int vertex : vertexGroups[group]) {
                        verticesX[vertex] += dx;
                        verticesY[vertex] += dy;
                        verticesZ[vertex] += dz;
                    }
                }
            }
        } else if (transformation == FrameConstants.ROTATION_TRANSFORMATION) {
            for (int index = 0; index < count; index++) {
                int group = groups[index];

                if (group < vertexGroups.length) {
                    for (int vertex : vertexGroups[group]) {
                        verticesX[vertex] -= centroidX;
                        verticesY[vertex] -= centroidY;
                        verticesZ[vertex] -= centroidZ;
                        int pitch = (dx & 0xFF) * 8;
                        int roll = (dy & 0xFF) * 8;
                        int yaw = (dz & 0xFF) * 8;

                        if (yaw != 0) {
                            int sin = Constants.SINE[yaw];
                            int cos = Constants.COSINE[yaw];
                            int x = verticesY[vertex] * sin + verticesX[vertex] * cos >> 16;
                            verticesY[vertex] = verticesY[vertex] * cos - verticesX[vertex] * sin >> 16;
                            verticesX[vertex] = x;
                        }

                        if (pitch != 0) {
                            int sin = Constants.SINE[pitch];
                            int cos = Constants.COSINE[pitch];
                            int y = verticesY[vertex] * cos - verticesZ[vertex] * sin >> 16;
                            verticesZ[vertex] = verticesY[vertex] * sin + verticesZ[vertex] * cos >> 16;
                            verticesY[vertex] = y;
                        }

                        if (roll != 0) {
                            int sin = Constants.SINE[roll];
                            int cos = Constants.COSINE[roll];
                            int x = verticesZ[vertex] * sin + verticesX[vertex] * cos >> 16;
                            verticesZ[vertex] = verticesZ[vertex] * cos - verticesX[vertex] * sin >> 16;
                            verticesX[vertex] = x;
                        }

                        verticesX[vertex] += centroidX;
                        verticesY[vertex] += centroidY;
                        verticesZ[vertex] += centroidZ;
                    }
                }
            }
        } else if (transformation == FrameConstants.SCALE_TRANSFORMATION) {
            for (int index = 0; index < count; index++) {
                int group = groups[index];

                if (group < vertexGroups.length) {
                    for (int vertex : vertexGroups[group]) {
                        verticesX[vertex] -= centroidX;
                        verticesY[vertex] -= centroidY;
                        verticesZ[vertex] -= centroidZ;

                        verticesX[vertex] = verticesX[vertex] * dx / 128;
                        verticesY[vertex] = verticesY[vertex] * dy / 128;
                        verticesZ[vertex] = verticesZ[vertex] * dz / 128;

                        verticesX[vertex] += centroidX;
                        verticesY[vertex] += centroidY;
                        verticesZ[vertex] += centroidZ;
                    }
                }
            }
        } else if (transformation == FrameConstants.ALPHA_TRANSFORMATION && faceGroups != null && faceTransparencies != null) {
            for (int index = 0; index < count; index++) {
                int group = groups[index];

                if (group < faceGroups.length) {
                    for (int face : faceGroups[group]) {
                        faceTransparencies[face] += dx * 8;

                        if (faceTransparencies[face] < 0) {
                            faceTransparencies[face] = 0;
                        } else if (faceTransparencies[face] > 255) {
                            faceTransparencies[face] = 255;
                        }
                    }
                }
            }
        }
    }

    public void translate(int x, int y, int z) {
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            verticesX[vertex] += x;
            verticesY[vertex] += y;
            verticesZ[vertex] += z;
        }
    }

    public int getZVertexMax() {
        return 50;
    }

    @Override
    public Mesh copy() {
        Mesh mesh = new Mesh();

        Mesh model = this;
        mesh.fitsOnSingleSquare = (model.fitsOnSingleSquare);
        mesh.minimumX = (model.minimumX);
        mesh.maximumX = (model.maximumX);
        mesh.maximumZ = (model.maximumZ);
        mesh.minimumZ = (model.minimumZ);
        mesh.ambient = (model.ambient);
        mesh.contrast = (model.contrast);
        mesh.boundingPlaneRadius = (model.boundingPlaneRadius);
        mesh.minimumY = (model.minimumY);
        mesh.boundingSphereRadius = (model.boundingSphereRadius);
        mesh.boundingCylinderRadius = (model.boundingCylinderRadius);
        mesh.anInt1654 = (model.anInt1654);
        mesh.colorsX = copyArray(model.colorsX);
        mesh.colorsY = copyArray(model.colorsY);
        mesh.colorsZ = copyArray(model.colorsZ);
        mesh.faceTransparencies = copyArray(model.faceTransparencies);
        mesh.faceColors = copyArray(model.faceColors);
        mesh.faceTextures = copyArray(model.faceTextures);
        mesh.textureCoords = copyArray(model.textureCoords);
        mesh.textureRenderTypes = copyArray(model.textureRenderTypes);
        mesh.faceGroups = copyArray(model.faceGroups);
        mesh.faceRenderPriorities = copyArray(model.faceRenderPriorities);
        mesh.triangleFaceCount = (model.triangleFaceCount);
        mesh.triangleSkinValues = copyArray(model.triangleSkinValues);
        mesh.trianglePointsX = copyArray(model.trianglePointsX);
        mesh.trianglePointsY = copyArray(model.trianglePointsY);
        mesh.trianglePointsZ = copyArray(model.trianglePointsZ);
        mesh.vertexNormals = (model.vertexNormals);
        mesh.vertexNormalsOffsets = (model.vertexNormalsOffsets);
        mesh.faceNormals = (model.faceNormals);
        mesh.priority = (model.priority);
        mesh.texturesCount = model.texturesCount;
        mesh.texTriangleX = copyArray(model.texTriangleX);
        mesh.texTriangleY = copyArray(model.texTriangleY);
        mesh.texTriangleZ = copyArray(model.texTriangleZ);
        mesh.faceRenderType = copyArray(model.faceRenderType);
        mesh.vertexGroups = copyArray(model.vertexGroups);
        mesh.vertexSkins = copyArray(model.vertexSkins);
        mesh.verticesX = copyArray(model.verticesX);
        mesh.verticesY = copyArray(model.verticesY);
        mesh.verticesZ = copyArray(model.verticesZ);
        mesh.verticesCount = model.verticesCount;
        return mesh;
    }

    protected static short[] copyArray(short[] a) {
        if (a == null) {
            return null;
        }
        return Arrays.copyOf(a, a.length);
    }

    protected static int[] copyArray(int[] a) {
        if (a == null) {
            return null;
        }
        return Arrays.copyOf(a, a.length);
    }

    protected static byte[] copyArray(byte[] a) {
        if (a == null) {
            return null;
        }
        return Arrays.copyOf(a, a.length);
    }

    protected static int[][] copyArray(int[][] a) {
        if (a == null) {
            return null;
        }
        return Arrays.copyOf(a, a.length);
    }

    public void retexture(short src, short dst) {
        if (faceTextures != null) {
            for (int i = 0; i < faceTextures.length; i++) {
                if (faceTextures[i] == src) {
                    faceTextures[i] = dst;
                }
            }
        }
    }

    private static boolean insideTriangle(int x, int y, int k, int l, int i1, int j1, int k1, int l1) {
        if (y < k && y < l && y < i1)
            return false;
        if (y > k && y > l && y > i1)
            return false;
        if (x < j1 && x < k1 && x < l1)
            return false;
        return x <= j1 || x <= k1 || x <= l1;
    }
}
