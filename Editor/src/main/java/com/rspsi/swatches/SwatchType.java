package com.rspsi.swatches;

import com.jagex.util.StringUtils;
import lombok.Getter;

@Getter
public enum SwatchType {
	OBJECT(0), OVERLAY(1), UNDERLAY(2),;
	
	SwatchType(int id) {
		this.id = id;
	}
	
	private final int id;

    public static SwatchType getById(int id) {
		for(SwatchType type : SwatchType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return OBJECT;
	}
	
	@Override
	public String toString() {
		return StringUtils.format(name().toLowerCase() + "s");
	}
}