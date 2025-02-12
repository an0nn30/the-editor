package com.github.an0nn30;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.IntelliJTheme;
import com.github.an0nn30.ui.EditorFrame;
import com.github.an0nn30.ui.EditorTabManager;

public class Main {

    public static void main(String[] args) {
        IntelliJTheme.setup(Main.class.getResourceAsStream("/DarkPurple.theme.json"));
        SwingUtilities.invokeLater(() -> {
            EditorFrame frame = new EditorFrame();

            // If a file argument is provided, try to open that file.
            if (args.length > 0) {
                File file = new File(args[0]);
                if (file.exists() && file.isFile()) {
                    EditorTabManager tabManager = frame.getTabManager();
                    tabManager.openFile(file);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "File not found: " + args[0],
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            frame.setVisible(true);
        });
    }
}
