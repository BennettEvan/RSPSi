package com.jagex.cache.def;

import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.MeshLoader;
import com.rspsi.core.misc.FixedIntegerKeyMap;
import com.rspsi.core.misc.FixedLongKeyMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public final class ObjectDefinition {

    public static FixedIntegerKeyMap<Mesh> baseModels = new FixedIntegerKeyMap<>(500);
    public static FixedLongKeyMap<Mesh> models = new FixedLongKeyMap<>(500);

    private static Mesh[] parts = new Mesh[4];

    private int modelTries = 0;
    private byte ambientLighting;
    private int animation;
    private boolean castsShadow;
    private boolean contouredGround;
    private int decorDisplacement;
    private boolean delayShading;
    private byte[] description;
    private boolean hollow;
    private int id = -1;
    private boolean impenetrable;
    private String[] interactions;
    private boolean interactive;
    private boolean inverted;
    private int length;
    private byte lightDiffusion;
    private int mapscene;
    private int minimapFunction;
    private int[] modelIds;
    private int[] modelTypes;
    private int[] morphisms;
    private int varbit;
    private int varp;
    private String name;
    private boolean obstructsGround;
    private boolean occludes;
    private int[] originalColours;
    private int[] replacementColours;
    private short[] textureToFind;
    private short[] textureToReplace;
    private int scaleX;
    private int scaleY;
    private int scaleZ;
    private boolean solid;
    private int supportItems;
    private int surroundings;
    private int translateX;
    private int translateY;
    private int translateZ;
    private int width;
    private int category;
    private boolean randomizeAnimStart;
    private int areaId = -1;

    public int[] getModelIds() {
        if (modelIds != null && modelTypes != null && modelTypes[0] == 22 && modelIds[0] == 1105 && (areaId != -1 || minimapFunction != -1)) {
            return new int[]{111};
        }
        return modelIds;
    }

    public String getName() {
        return (name == null || name.isEmpty() ? minimapFunction != -1 ? "minimap-function:" + minimapFunction : "null" : name + (minimapFunction != -1 ? " minimap-function:" + minimapFunction : ""));
    }

    private Mesh model(int type, int frame, int orientation) {
        Mesh base = null;
        long key;
        if (modelTypes == null) {
            if (type != 10) {
                return null;
            }

            key = frame + 1L << 32 | ((inverted ? 1 : 0) << 16) | (long) id << 6 | orientation;
            Mesh model = models.get(key);
            if (model != null) {
                return model;
            }

            if (getModelIds() == null) {
                return null;
            }

            boolean invert = inverted ^ orientation > 3;
            int count = getModelIds().length;
            for (int index = 0; index < count; index++) {
                int id = getModelIds()[index];
                if (invert) {
                    id |= 0x10000;
                }

                base = baseModels.get(id);
                if (base == null) {
                    base = MeshLoader.getSingleton().lookup(id & 0xffff);
                    if (base == null) {
                        return null;
                    }
                    base = base.copy();

                    if (invert) {
                        base.invert();
                    }

                    baseModels.put(id, base);
                }

                if (count > 1) {
                    parts[index] = base;
                }
            }

            if (count > 1) {
                base = new Mesh(count, parts);
            }
        } else {
            int index = -1;
            for (int i = 0; i < modelTypes.length; i++) {
                if (modelTypes[i] != type) {
                    continue;
                }

                index = i;
                break;
            }

            if (index == -1) {
                return null;
            }

            key = frame + 1L << 32 | ((inverted ? 1 : 0) << 16) | (long) id << 6 | (long) index << 3 | orientation;
            Mesh model = models.get(key);
            if (model != null) {
                return model;
            }

            int id = getModelIds()[index];
            boolean invert = inverted ^ orientation > 3;
            if (invert) {
                id |= 0x10000;
            }

            base = baseModels.get(id);
            if (base == null) {
                base = MeshLoader.getSingleton().lookup(id & 0xffff);

                if (base == null) {
                    return null;
                }

                base = base.copy();

                if (invert) {
                    base.invert();
                }

                baseModels.put(id, base);
            }
        }

        boolean scale = scaleX != 128 || scaleY != 128 || scaleZ != 128;
        boolean translate = translateX != 0 || translateY != 0 || translateZ != 0;

        Mesh model = new Mesh(base, originalColours == null, FrameLoader.isInvalid(frame), orientation == 0 && frame == -1 && !scale && !translate, textureToFind == null);
        if (frame != -1) {
            model.prepareSkeleton();
            model.apply(frame);
            model.faceGroups = null;
            model.vertexGroups = null;
        }

        if (type == 4 && orientation > 3) {
            model.pitch(256);
            model.offsetVertices(45, 0, -45);
        }

        orientation &= 3;

        while (orientation-- > 0) {
            model.rotateClockwise();
        }

        if (originalColours != null) {
            for (int i = 0; i < originalColours.length; i++) {
                model.recolour(originalColours[i], replacementColours[i]);
            }
        }

        if (textureToFind != null) {
            for (int i = 0; i < textureToFind.length; i++) {
                model.retexture(textureToFind[i], textureToReplace[i]);
            }
        }

        if (scale) {
            model.scale(scaleX, scaleZ, scaleY);
        }

        if (translate) {
            model.translate(translateX, translateY, translateZ);
        }

        model.light(64 + ambientLighting, 768 + lightDiffusion * 25, -50, -10, -50);
        if (supportItems == 1) {
            model.anInt1654 = model.getModelHeight();
        }
        models.put(key, model);
        return model;
    }

    public Mesh modelAt(int type, int orientation, int aY, int bY, int cY, int dY, int frameId) {
        Mesh model = model(type, frameId, orientation);
        if (model == null) {
            return null;
        }

        if (contouredGround || delayShading) {
            model = new Mesh(contouredGround, model);
        }

        if (contouredGround) {
            int y = (aY + bY + cY + dY) / 4;
            for (int vertex = 0; vertex < model.verticesCount; vertex++) {
                int x = model.verticesX[vertex];
                int z = model.verticesZ[vertex];
                int l2 = aY + (bY - aY) * (x + 64) / 128;
                int i3 = dY + (cY - dY) * (x + 64) / 128;
                int j3 = l2 + (i3 - l2) * (z + 64) / 128;
                model.verticesY[vertex] += j3 - y;
            }

            model.computeSphericalBounds();
        }
        return model;
    }

    public boolean obstructsGround() {
        return obstructsGround;
    }

    public boolean ready() {
        if (getModelIds() == null) {
            return true;
        }
        boolean ready = true;
        for (int id : getModelIds()) {
            ready &= MeshLoader.getSingleton().loaded(id);
        }
        return ready;
    }

    public boolean readyOrThrow(int type) throws Exception {
        if (modelTypes == null) {
            if (getModelIds() == null || type != 10) {
                return true;
            }

            boolean ready = true;
            for (int id : getModelIds()) {
                ready &= MeshLoader.getSingleton().loaded(id);
            }
            if (ready) {
                modelTries = 0;
            } else {
                modelTries++;
            }
            if (modelTries > 500) {
                throw new Exception("Model missing");
            }
            return ready;
        }

        for (int index = 0; index < modelTypes.length; index++) {
            if (modelTypes[index] == type) {
                boolean ready = MeshLoader.getSingleton().loaded(getModelIds()[index]);
                if (ready) {
                    modelTries = 0;
                } else {
                    modelTries++;
                }
                if (modelTries > 500) {
                    throw new Exception("Model missing");
                }
                return ready;
            }
        }
        modelTries = 0;
        return true;
    }

    public boolean ready(int type) {
        if (modelTypes == null) {
            if (getModelIds() == null || type != 10) {
                return true;
            }

            boolean ready = true;
            for (int id : getModelIds()) {
                ready &= MeshLoader.getSingleton().loaded(id);
            }
            if (ready) {
                modelTries = 0;
            } else {
                modelTries++;
            }
            return ready || modelTries > 500;
        }

        for (int index = 0; index < modelTypes.length; index++) {
            if (modelTypes[index] == type) {
                boolean ready = MeshLoader.getSingleton().loaded(getModelIds()[index]);
                if (ready) {
                    modelTries = 0;
                } else {
                    modelTries++;
                }
                return ready || modelTries > 500;
            }
        }
        modelTries = 0;
        return true;
    }

    public void reset() {
        modelIds = null;
        modelTypes = null;
        name = null;
        description = null;
        originalColours = null;
        replacementColours = null;
        textureToFind = null;
        textureToReplace = null;
        width = 1;
        length = 1;
        solid = true;
        impenetrable = true;
        interactive = false;
        contouredGround = false;
        delayShading = false;
        occludes = false;
        animation = -1;
        decorDisplacement = 16;
        ambientLighting = 0;
        lightDiffusion = 0;
        interactions = null;
        minimapFunction = -1;
        mapscene = -1;
        inverted = false;
        castsShadow = true;
        scaleX = 128;
        scaleY = 128;
        scaleZ = 128;
        surroundings = 0;
        translateX = 0;
        translateY = 0;
        translateZ = 0;
        obstructsGround = false;
        hollow = false;
        supportItems = -1;
        varbit = -1;
        varp = -1;
        morphisms = null;
    }

    public int getHash() {
        final int prime = 337;
        int result = 1;
        result = prime * result + ambientLighting;
        result = prime * result + animation;
        result = prime * result + (castsShadow ? 1231 : 1237);
        result = prime * result + (contouredGround ? 1231 : 1237);
        result = prime * result + decorDisplacement;
        result = prime * result + (delayShading ? 1231 : 1237);
        result = prime * result + (hollow ? 1231 : 1237);
        result = prime * result + (impenetrable ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(interactions);
        result = prime * result + (interactive ? 1231 : 1237);
        result = prime * result + (inverted ? 1231 : 1237);
        result = prime * result + length;
        result = prime * result + lightDiffusion;
        result = prime * result + mapscene;
        result = prime * result + minimapFunction;
        result = prime * result + Arrays.hashCode(modelIds);
        result = prime * result + Arrays.hashCode(modelTypes);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (obstructsGround ? 1231 : 1237);
        result = prime * result + (occludes ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(originalColours);
        result = prime * result + Arrays.hashCode(replacementColours);
        result = prime * result + Arrays.hashCode(textureToFind);
        result = prime * result + scaleX;
        result = prime * result + scaleY;
        result = prime * result + scaleZ;
        result = prime * result + (solid ? 1231 : 1237);
        result = prime * result + supportItems;
        result = prime * result + surroundings;
        result = prime * result + Arrays.hashCode(textureToReplace);
        result = prime * result + translateX;
        result = prime * result + translateY;
        result = prime * result + translateZ;
        result = prime * result + varbit;
        result = prime * result + varp;
        result = prime * result + width;
        return result;
    }
}
