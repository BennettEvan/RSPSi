package com.jagex.entity.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jagex.chunk.Chunk;
import com.jagex.map.object.DefaultWorldObject;
import lombok.Getter;

@Getter
public class ObjectGroup {

	private final int objectId;
	private final List<DefaultWorldObject> objects;

	public ObjectGroup(int objectId) {
		objects = new ArrayList<>();
		this.objectId = objectId;
	}

	public void addObject(DefaultWorldObject object) {
		if (objects.contains(object)) {
            return;
        }
		objects.add(object);
	}

    public void sort() {
		objects.sort(Comparator.comparingInt(DefaultWorldObject::getLocHash));
	}
}
