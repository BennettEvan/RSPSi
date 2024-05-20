package com.jagex.entity.model;

public class PreviewModel extends Mesh {

    public PreviewModel(Mesh model) {
        super();
        fitsOnSingleSquare = (model.fitsOnSingleSquare);
        minimumX = (model.minimumX);
        maximumX = (model.maximumX);
        maximumZ = (model.maximumZ);
        minimumZ = (model.minimumZ);
        boundingPlaneRadius = (model.boundingPlaneRadius);
        minimumY = (model.minimumY);
        boundingSphereRadius = (model.boundingSphereRadius);
        boundingCylinderRadius = (model.boundingCylinderRadius);
        anInt1654 = (model.anInt1654);
        colorsX = copyArray(model.colorsX);
        colorsY = copyArray(model.colorsY);
        colorsZ = copyArray(model.colorsZ);
        faceTransparencies = copyArray(model.faceTransparencies);
        faceColors = copyArray(model.faceColors);
        faceTextures = copyArray(model.faceTextures);
        textureCoords = copyArray(model.textureCoords);
        textureRenderTypes = copyArray(model.textureRenderTypes);
        faceGroups = copyArray(model.faceGroups);
        faceRenderPriorities = copyArray(model.faceRenderPriorities);
        triangleFaceCount = (model.triangleFaceCount);
        triangleSkinValues = copyArray(model.triangleSkinValues);
        trianglePointsX = copyArray(model.trianglePointsX);
        trianglePointsY = copyArray(model.trianglePointsY);
        trianglePointsZ = copyArray(model.trianglePointsZ);
        vertexNormals = (model.vertexNormals);
        vertexNormalsOffsets = (model.vertexNormalsOffsets);
        faceNormals = (model.faceNormals);
        priority = (model.priority);
        texturesCount = model.texturesCount;
        texTriangleX = copyArray(model.texTriangleX);
        texTriangleY = copyArray(model.texTriangleY);
        texTriangleZ = copyArray(model.texTriangleZ);
        faceRenderType = copyArray(model.faceRenderType);
        vertexGroups = copyArray(model.vertexGroups);
        vertexSkins = copyArray(model.vertexSkins);
        verticesX = copyArray(model.verticesX);
        verticesY = copyArray(model.verticesY);
        verticesZ = copyArray(model.verticesZ);
        verticesCount = model.verticesCount;
    }

    @Override
    public int getZVertexMax() {
        return 500;
    }
}
