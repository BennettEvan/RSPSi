package com.rspsi.game.save.tile.state;

public class ImportTileState extends TileState {
	
	
	private FlagState flagState;
	private HeightState heightState;
	private OverlayState overlayState;
	private UnderlayState underlayState;
	
	public ImportTileState(int x, int y, int z) {
		super(x, y, z);
	}

	@Override
	public void preserve() {
		this.flagState = new FlagState(this.getX(), this.getY(), this.getZ());
		this.heightState = new HeightState(this.getX(), this.getY(), this.getZ());
		this.overlayState = new OverlayState(this.getX(), this.getY(), this.getZ());
		this.underlayState = new UnderlayState(this.getX(), this.getY(), this.getZ());

		this.flagState.preserve();
		this.heightState.preserve();
		this.overlayState.preserve();
		this.underlayState.preserve();
	}

	@Override
	public int getUniqueId() {
		return 5;
	}

	public FlagState getFlagState() {
		return flagState;
	}

	public HeightState getHeightState() {
		return heightState;
	}

	public OverlayState getOverlayState() {
		return overlayState;
	}

	public UnderlayState getUnderlayState() {
		return underlayState;
	}
	
	

}