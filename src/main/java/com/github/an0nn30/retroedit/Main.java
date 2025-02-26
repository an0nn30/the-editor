package com.github.an0nn30.retroedit;

import com.github.an0nn30.retroedit.settings.Settings;
import com.github.an0nn30.retroedit.ui.EditorFrame;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) {
        Settings.initialize();
        System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua");

        // Detect and apply Linux scaling if running on Linux
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            double scaleFactor = getScaleFactor();
            System.setProperty("sun.java2d.uiScale", String.valueOf(scaleFactor));
            System.out.println("Detected Linux scale factor: " + scaleFactor);
        }
        SwingUtilities.invokeLater(() -> {
        EditorFrame frame;
        if (args.length >= 1) {
            frame = new EditorFrame(new File(args[0]));
        } else {
            frame = new EditorFrame();
        }
        frame.setVisible(true);

        });
    }

    private static double getScaleFactor() {
        double xrdbScale = getScaleFactorFromXrdb();
        double gdkScale = getScaleFactorFromGDK();
        return Math.max(xrdbScale, gdkScale);
    }

    private static double getScaleFactorFromXrdb() {
        try {
            Process process = Runtime.getRuntime().exec("xrdb -query");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Xft.dpi:")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        double dpi = Double.parseDouble(parts[1]);
                        return dpi / 96.0;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore exceptions and return default scale
        }
        return 1.0;
    }

    private static double getScaleFactorFromGDK() {
        String gdkScale = System.getenv("GDK_SCALE");
        if (gdkScale != null) {
            try {
                return Double.parseDouble(gdkScale);
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }
        return 1.0;
    }

    private static void loadAndSetInterfaceFont() {
        try (InputStream is = Main.class.getResourceAsStream("/fonts/Consolas.ttf")) {
            if (is == null) {
                System.err.println("Font resource not found: /fonts/Consolas.ttf");
                return;
            }
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
            setGlobalUIFont(customFont);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setGlobalUIFont(Font font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
}