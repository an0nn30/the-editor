package com.github.an0nn30.retroedit.ui.actions;

import com.github.an0nn30.retroedit.ui.Editor;
import com.github.an0nn30.retroedit.ui.search.SearchController;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FindDialogAction extends AbstractAction {

    private final Editor editor;

    public FindDialogAction(Editor editor) {
        super("Find...");
        this.editor = editor;
        int shortcut = editor.getToolkit().getMenuShortcutKeyMaskEx();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, shortcut));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SearchController searchController = editor.getSearchController();
        if (searchController.isReplaceDialogVisible()) {
            searchController.hideReplaceDialog();
        }
        searchController.showFindDialog();
    }
}

