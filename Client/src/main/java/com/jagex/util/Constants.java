package com.jagex.util;

public class Constants {

	public static int[] COSINE, SINE, SHADOW_DECAY, LIGHT_DECAY;

	static {
		SHADOW_DECAY = new int[512];
		LIGHT_DECAY = new int[2048];
		SINE = new int[2048];
		COSINE = new int[2048];
	
		for (int i = 1; i < 512; i++) {
			SHADOW_DECAY[i] = 32768 / i;
		}
	
		for (int i = 1; i < 2048; i++) {
			LIGHT_DECAY[i] = 0x10000 / i;
		}
	
		for (int i = 0; i < 2048; i++) {
			SINE[i] = (int) (65536D * Math.sin(i * 0.0030679614999999999D));
			COSINE[i] = (int) (65536D * Math.cos(i * 0.0030679614999999999D));
		}
	}
}
