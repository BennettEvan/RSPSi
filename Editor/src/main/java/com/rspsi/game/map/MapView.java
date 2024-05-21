package com.rspsi.game.map;

import com.google.common.io.Files;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.cache.CacheFileType;
import com.rspsi.ui.MainWindow;
import com.rspsi.util.ChangeListenerUtil;
import com.rspsi.util.FXDialogs;
import com.rspsi.util.FilterMode;
import com.rspsi.util.RetentionFileChooser;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Renders a global map view.
 * Work in progress
 * Currently JavaFX doesn't do this as well as Swing.
 *
 * @author James
 */
public class MapView extends JFrame {

    public static boolean renderHash = true;
    public static boolean renderXY = true;
    public static BooleanProperty showImages = new SimpleBooleanProperty(true);
    public static IntegerProperty heightLevel = new SimpleIntegerProperty(0);
    private final JPanel jPanel;

    public MapView() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        jPanel = new JPanel();
        jPanel.setLayout(null);
		final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jPanel);
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem saveOption = new JMenuItem("Save map_index");
        this.setJMenuBar(menu);
        saveOption.addActionListener(al -> {
            Platform.runLater(() -> {
                File f = RetentionFileChooser.showSaveDialog("Please select a location", null, "map_index", FilterMode.NONE);
                if (f != null) {
                    try {
                        Files.write(MapIndexLoader.instance.encode(), f);
                    } catch (IOException e) {
                        FXDialogs.showError(MainWindow.getSingleton().getStage().getOwner(), "Error while saving map_index", "There was a failure while attempting to save\nthe map_index to the selected file.");
                        e.printStackTrace();
                    }
                }
            });
        });
        file.add(saveOption);
        menu.add(file);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));

        setContentPane(contentPane);

        contentPane.add(jScrollPane, BorderLayout.CENTER);
        ChangeListenerUtil.addListener(this::invalidateChildren, showImages);
        ChangeListenerUtil.addListener(this::invalidateChildren, heightLevel);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResourceResponse(ResourceResponse response) {
        if (response.request().getType() == CacheFileType.MAP) {
            for (Component component : jPanel.getComponents()) {
                if (component instanceof RegionView view) {
                    view.deliverResource(response);
                }
            }
        }
    }

    public void invalidateChildren() {
        for (Component component : jPanel.getComponents()) {
            if (component instanceof RegionView view) {
                view.invalidate();
            }
        }
    }

    public void initTiles() {
        EventBus.getDefault().register(this);
        RegionViewMouseListener listener = new RegionViewMouseListener();

        jPanel.setPreferredSize(new Dimension(150 * 64 + 1, 150 * 64 + 1));
        jPanel.revalidate();
        Thread t = new Thread(() -> {
            for (int y = 150; y >= 0; y--) {
                for (int x = 0; x < 150; x++) {
                    RegionView r = new RegionView(x, y);
                    r.addMouseListener(listener);
                    r.setSize(new Dimension(64, 64));
                    r.setLocation(new Point(x * 64, (64 * 149) - y * 64));
                    r.setVisible(true);
                    jPanel.add(r);
                }
            }
            jPanel.revalidate();
            jPanel.repaint();
        });
        t.start();
    }
}
