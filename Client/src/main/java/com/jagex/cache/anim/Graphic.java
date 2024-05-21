package com.jagex.cache.anim;

import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.MeshLoader;
import com.rspsi.core.misc.FixedHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Graphic {

    public static FixedHashMap<Integer, Mesh> modelCache = new FixedHashMap<>(30);

    private Animation animation;
	private int animationId = -1;
    private int breadthScale = 128;
    private int depthScale = 128;
    private int id;
    private int model;
    private int ambience;
	private int modelShadow;
    private int orientation;
    private int[] originalColours = new int[6];
    private int[] replacementColours = new int[6];

    public Mesh getModel() {
        Mesh model = modelCache.get(id);
        if (model != null) {
			return model;
		}
        model = MeshLoader.getSingleton().lookup(this.model);
        if (model == null) {
			return null;
		}

        model = model.copy();
        for (int part = 0; part < 6; part++) {
            if (originalColours[0] != 0) {
                model.recolour(originalColours[part], replacementColours[part]);
            }
        }
        modelCache.put(id, model);
        return model;
    }
}