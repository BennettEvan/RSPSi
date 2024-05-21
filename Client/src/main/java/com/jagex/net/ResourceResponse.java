package com.jagex.net;

import org.displee.util.GZIPUtils;

public record ResourceResponse(ResourceRequest request, byte[] data) {

	public byte[] decompress() {
		byte[] unzipped = GZIPUtils.unzip(data);
		return unzipped == null ? data : unzipped;
	}
}
