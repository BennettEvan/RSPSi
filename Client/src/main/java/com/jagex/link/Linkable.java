package com.jagex.link;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Linkable {
	private long key;
	private Linkable next;
	private Linkable previous;

    public void unlink() {
		if (previous == null || next == null) {
			return;
		}
		previous.next = next;
		next.previous = previous;
		next = null;
		previous = null;
	}
}