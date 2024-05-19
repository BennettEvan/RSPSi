package com.jagex.entity.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.rspsi.cache.CacheFileType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jagex.net.ResourceProvider;
import com.jagex.net.ResourceResponse;

@Slf4j
public class MeshLoader {

    @Getter
    private static MeshLoader singleton;

    private final Map<Integer, Mesh> loadedMeshes = Collections.synchronizedMap(Maps.newHashMap());
    private final List<Integer> awaitingLoad = Collections.synchronizedList(Lists.newArrayList());
    private final ResourceProvider provider;

    public MeshLoader(ResourceProvider provider) throws Exception {
        if (singleton != null) {
            throw new Exception("MeshLoader.class already loaded!");
        }
        this.provider = provider;
        EventBus.getDefault().register(this);
        singleton = this;
    }

    public void clear(int id) {
        loadedMeshes.remove(id);
    }

    public void dispose() {
        clearAll();
        singleton = null;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResourceResponse(ResourceResponse response) {
        if (response.getRequest().getType() == CacheFileType.MODEL) {
            load(response.decompress(), response.getRequest().getFile());
        }
    }

    public static Mesh load(byte[] data) {
        MeshRevision revision = MeshUtils.getRevision(data);
        if (revision == null) {
            // default old format
            return new MeshOldFormat(data);
        }
        return switch (revision) {
            case OSRS_TYPE_3 -> new MeshOSRSType3(data);
            case OSRS_TYPE_2 -> new MeshOSRSType2(data);
        };
    }

    public Mesh load(byte[] data, int id) {
        MeshRevision revision = MeshUtils.getRevision(data);
        if (revision == null) {
            // default old format
            return new MeshOldFormat(data);
        }
        Mesh mesh = null;
        try {
            mesh = switch (revision) {
                case OSRS_TYPE_3 -> new MeshOSRSType3(data);
                case OSRS_TYPE_2 -> new MeshOSRSType2(data);
            };
            mesh.id = id;
            mesh.revision = revision;

            loadedMeshes.put(id, mesh);
            awaitingLoad.remove(Integer.valueOf(id));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mesh;
    }

    public boolean loaded(int id) {
        if (loadedMeshes.containsKey(id)) {
            return true;
        }
        boolean alreadyLoading = awaitingLoad.contains(id);
        if (!alreadyLoading) {
            awaitingLoad.add(id);
            System.out.println("Requested model " + id);
            provider.requestFile(CacheFileType.MODEL, id);
            return false;
        }
        return false;
    }

    public Mesh lookup(int id) {
        if (loaded(id)) {
            return loadedMeshes.get(id);
        }
        return null;
    }

    public void clearAll() {
        loadedMeshes.clear();
        awaitingLoad.clear();
    }
}
