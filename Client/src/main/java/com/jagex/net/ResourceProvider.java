package com.jagex.net;

import com.google.common.collect.Lists;
import com.jagex.Cache;
import com.rspsi.cache.CacheFileType;
import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

public class ResourceProvider implements Runnable {

    private final List<ResourceRequest> requests = Collections.synchronizedList(Lists.newArrayList());

    private final Cache cache;

    public ResourceProvider(Cache cache) {
        this.cache = cache;
    }

    public final void requestMap(int file, int regionId) {
        if (Lists.newArrayList(requests).stream().anyMatch(node -> node != null && node.getType() == CacheFileType.MAP && node.getFile() == file)) {
            return;
        }
        ResourceRequest node = new MapResourceRequest(regionId, file);
        requests.add(node);
    }

    public final void requestFile(CacheFileType type, int file) {
        if (Lists.newArrayList(requests).stream().anyMatch(node -> node != null && node.getType() == type && node.getFile() == file)) {
            return;
        }

        ResourceRequest node = new ResourceRequest(file, type);
        requests.add(node);
    }

    public final void handleRequests() {
        if (requests.isEmpty()) {
			return;
		}
        List<ResourceRequest> loopedResources = Lists.newArrayList(requests);
        List<ResourceRequest> successfulRequests = Lists.newArrayList();
        for (ResourceRequest request : loopedResources) {
            if (request == null) {
                continue;
            }
            try {
                byte[] data;
                if (request.getType() == CacheFileType.MAP) {
                    MapResourceRequest mapReq = (MapResourceRequest) request;
                    data = cache.readMap(request.getFile(), mapReq.getRegionId());
                } else {
                    data = cache.getFile(request.getType(), request.getFile());
                }
                if (data != null) {
                    successfulRequests.add(request);
                    ResourceResponse response = new ResourceResponse(request, data);
                    EventBus.getDefault().post(response);
                } else {
                    throw new Exception("Fetch Error");
                }
            } catch (Exception ex) {
                if (request.getAge() > 150000) {
                    requests.remove(request);
                    System.out.println("Failed to fetch resource " + request.getFile() + " from index " + request.getType());
                }
            }
        }
        requests.removeAll(successfulRequests);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                handleRequests();
                Thread.sleep(50);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}