package com.jagex.entity.model;

import com.jagex.io.Buffer;

public class MeshOldFormat extends Mesh {

    public MeshOldFormat(byte[] var1) {
        boolean var2 = false;
        boolean var3 = false;
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var4.setPosition(var1.length - 18);
        int var9 = var4.readUShort();
        int var10 = var4.readUShort();
        int var11 = var4.readUByte();
        int var12 = var4.readUByte();
        int var13 = var4.readUByte();
        int var14 = var4.readUByte();
        int var15 = var4.readUByte();
        int var16 = var4.readUByte();
        int var17 = var4.readUShort();
        int var18 = var4.readUShort();
        int var19 = var4.readUShort();
        int var20 = var4.readUShort();
        byte var21 = 0;
        int var22 = var21 + var9;
        int var23 = var22;
        var22 += var10;
        int var24 = var22;
        if (var13 == 255) {
            var22 += var10;
        }

        int var25 = var22;
        if (var15 == 1) {
            var22 += var10;
        }

        int var26 = var22;
        if (var12 == 1) {
            var22 += var10;
        }

        int var27 = var22;
        if (var16 == 1) {
            var22 += var9;
        }

        int var28 = var22;
        if (var14 == 1) {
            var22 += var10;
        }

        int var29 = var22;
        var22 += var20;
        int var30 = var22;
        var22 += var10 * 2;
        int var31 = var22;
        var22 += var11 * 6;
        int var32 = var22;
        var22 += var17;
        int var33 = var22;
        var22 += var18;
        int var10000 = var22 + var19;
        this.verticesCount = var9;
        this.triangleFaceCount = var10;
        this.texturesCount = var11;
        this.verticesX = new int[var9];
        this.verticesY = new int[var9];
        this.verticesZ = new int[var9];
        this.trianglePointsX = new int[var10];
        this.trianglePointsY = new int[var10];
        this.trianglePointsZ = new int[var10];
        if (var11 > 0) {
            this.textureRenderTypes = new byte[var11];
            this.texTriangleX = new int[var11];
            this.texTriangleY = new int[var11];
            this.texTriangleZ = new int[var11];
        }

        if (var16 == 1) {
            this.vertexSkins = new int[var9];
        }

        if (var12 == 1) {
            this.faceRenderType = new int[var10];
            this.textureCoords = new byte[var10];
            this.faceTextures = new short[var10];
        }

        if (var13 == 255) {
            this.faceRenderPriorities = new byte[var10];
        } else {
            this.priority = (byte) var13;
        }

        if (var14 == 1) {
            this.faceTransparencies = new int[var10];
        }

        if (var15 == 1) {
            this.triangleSkinValues = new int[var10];
        }

        this.faceColors = new int[var10];
        var4.setPosition(var21);
        var5.setPosition(var32);
        var6.setPosition(var33);
        var7.setPosition(var22);
        var8.setPosition(var27);
        int var35 = 0;
        int var36 = 0;
        int var37 = 0;

        int var38;
        int var39;
        int var40;
        int var41;
        int var42;
        for (var38 = 0; var38 < var9; ++var38) {
            var39 = var4.readUByte();
            var40 = 0;
            if ((var39 & 1) != 0) {
                var40 = var5.readSmart();
            }

            var41 = 0;
            if ((var39 & 2) != 0) {
                var41 = var6.readSmart();
            }

            var42 = 0;
            if ((var39 & 4) != 0) {
                var42 = var7.readSmart();
            }

            this.verticesX[var38] = var35 + var40;
            this.verticesY[var38] = var36 + var41;
            this.verticesZ[var38] = var37 + var42;
            var35 = this.verticesX[var38];
            var36 = this.verticesY[var38];
            var37 = this.verticesZ[var38];
            if (var16 == 1) {
                this.vertexSkins[var38] = var8.readUByte();
            }
        }

        var4.setPosition(var30);
        var5.setPosition(var26);
        var6.setPosition(var24);
        var7.setPosition(var28);
        var8.setPosition(var25);

        for (var38 = 0; var38 < var10; ++var38) {
            this.faceColors[var38] = (short) var4.readUShort();
            if (var12 == 1) {
                var39 = var5.readUByte();
                if ((var39 & 1) == 1) {
                    this.faceRenderType[var38] = 1;
                    var2 = true;
                } else {
                    this.faceRenderType[var38] = 0;
                }

                if ((var39 & 2) == 2) {
                    this.textureCoords[var38] = (byte) (var39 >> 2);
                    this.faceTextures[var38] = (short) this.faceColors[var38];
                    this.faceColors[var38] = 127;
                    if (this.faceTextures[var38] != -1) {
                        var3 = true;
                    }
                } else {
                    this.textureCoords[var38] = -1;
                    this.faceTextures[var38] = -1;
                }
            }

            if (var13 == 255) {
                this.faceRenderPriorities[var38] = var6.readByte();
            }

            if (var14 == 1) {
                this.faceTransparencies[var38] = var7.readByte();
                // TODO might need this shit
//				if (this.faceTransparencies[var38] < 0) {
//					this.faceTransparencies[var38] = (256 + this.faceTransparencies[var38]);
//				}
            }

            if (var15 == 1) {
                this.triangleSkinValues[var38] = var8.readUByte();
            }
        }

        var4.setPosition(var29);
        var5.setPosition(var23);
        var38 = 0;
        var39 = 0;
        var40 = 0;
        var41 = 0;

        int var43;
        int var44;
        for (var42 = 0; var42 < var10; ++var42) {
            var43 = var5.readUByte();
            if (var43 == 1) {
                var38 = var4.readSmart() + var41;
                var39 = var4.readSmart() + var38;
                var40 = var4.readSmart() + var39;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var39;
                this.trianglePointsZ[var42] = var40;
            }

            if (var43 == 2) {
                var39 = var40;
                var40 = var4.readSmart() + var41;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var39;
                this.trianglePointsZ[var42] = var40;
            }

            if (var43 == 3) {
                var38 = var40;
                var40 = var4.readSmart() + var41;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var39;
                this.trianglePointsZ[var42] = var40;
            }

            if (var43 == 4) {
                var44 = var38;
                var38 = var39;
                var39 = var44;
                var40 = var4.readSmart() + var41;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var44;
                this.trianglePointsZ[var42] = var40;
            }
        }

        var4.setPosition(var31);

        for (var42 = 0; var42 < var11; ++var42) {
            this.textureRenderTypes[var42] = 0;
            this.texTriangleX[var42] = (short) var4.readUShort();
            this.texTriangleY[var42] = (short) var4.readUShort();
            this.texTriangleZ[var42] = (short) var4.readUShort();
        }

        if (this.textureCoords != null) {
            boolean var45 = false;

            for (var43 = 0; var43 < var10; ++var43) {
                var44 = this.textureCoords[var43] & 255;
                if (var44 != 255) {
                    if (this.trianglePointsX[var43] == (this.texTriangleX[var44] & '\uffff') && this.trianglePointsY[var43] == (this.texTriangleY[var44] & '\uffff') && this.trianglePointsZ[var43] == (this.texTriangleZ[var44] & '\uffff')) {
                        this.textureCoords[var43] = -1;
                    } else {
                        var45 = true;
                    }
                }
            }

            if (!var45) {
                this.textureCoords = null;
            }
        }

        if (!var3) {
            this.faceTextures = null;
        }

        if (!var2) {
            this.faceRenderType = null;
        }

    }
}
