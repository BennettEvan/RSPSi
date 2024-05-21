package com.rspsi.game.map;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.rspsi.ui.EditRegionsWindow;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RegionViewMouseListener implements MouseListener {

    private final EditRegionsWindow editRegions;

    public RegionViewMouseListener() {
        editRegions = new EditRegionsWindow();
        try {
            editRegions.start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (arg0.getComponent() instanceof RegionView view) {
            view.setHovered(true);
        }
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if (arg0.getComponent() instanceof RegionView view) {
            view.setHovered(false);
        }
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        if (arg0.getComponent() instanceof RegionView view) {
            if (arg0.getButton() == MouseEvent.BUTTON3) {
                for (Component comp : view.getParent().getComponents()) {
                    if (comp instanceof RegionView rv) {
                        rv.setSelected(false);
                    }
                }

                view.setSelected(true);
                JPopupMenu popup = new JPopupMenu();
                JMenuItem editRegion = new JMenuItem("Edit region");
                editRegion.addActionListener(al -> {
                    Platform.runLater(() -> {
                        editRegions.show(view);
                        if (editRegions.valid()) {
                            MapIndexLoader.setRegionData(view.getRegionX(), view.getRegionY(), editRegions.getLandscapeId(), editRegions.getObjectId());
                            view.images = null;
                            view.loadMap();
                            view.invalidate();
                        }
                    });
                });
                popup.add(editRegion);
                popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
            } else {
                if (!arg0.isShiftDown()) {
                    for (Component comp : view.getParent().getComponents()) {
                        if (comp instanceof RegionView rv) {
                            rv.setSelected(false);
                        }
                    }
                }
                view.setSelected(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
}
