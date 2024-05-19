package com.jagex.draw.raster;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class GameRaster {

	protected int maxRight;
	protected int height;
	protected int[] raster;
	protected int width;
	protected int centreX;
	protected int centreY;
	protected int clipBottom;
	protected int clipLeft;
	protected int clipRight;
	protected int clipTop;

    public void init(int height, int width, int[] pixels) {
		this.raster = pixels;
		this.width = width;
		this.height = height;
		setBounds(height, 0, width, 0);
	}

	public void reset() {
		Arrays.fill(raster, 0);
	}

	public void setBounds(int clipTop, int clipLeft, int clipRight, int clipBottom) {
		if (clipLeft < 0) {
			clipLeft = 0;
		}

		if (clipBottom < 0) {
			clipBottom = 0;
		}

		if (clipRight > this.width) {
			clipRight = this.width;
		}

		if (clipTop > this.height) {
			clipTop = this.height;
		}

		this.clipLeft = clipLeft;
		this.clipBottom = clipBottom;
		this.clipRight = clipRight;
		this.clipTop = clipTop;

		maxRight = this.clipRight - 1;
		centreX = this.clipRight / 2;
		centreY = this.clipTop / 2;
	}
}