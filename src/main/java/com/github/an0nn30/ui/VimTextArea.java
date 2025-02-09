package com.github.an0nn30.ui;

import com.github.an0nn30.vim.VimModes;
import com.github.an0nn30.vim.handlers.VimShortcutManager;
import com.github.an0nn30.vim.ui.BlockCaret;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import java.awt.Font;

public class VimTextArea extends RSyntaxTextArea {
    private VimModes mode;
    private VimShortcutManager shortcutManager;

    public VimTextArea() {
        super();
        // Start in READY (normal) mode by default.
        mode = VimModes.NORMAL;
        setSyntaxEditingStyle("text/x-java");
        setCodeFoldingEnabled(true);
        // In READY mode the text area is read-only.
        setEditable(false);

        // Set the initial caret to our custom block caret.
        setCaret(new BlockCaret());

        // Create and register the shortcut manager.
        shortcutManager = new VimShortcutManager(this);
        addKeyListener(shortcutManager);

        // (Optional) Set a default font.
        setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    public VimModes getCurrentMode() {
        return mode;
    }

    /**
     * Switches the editor to the specified mode, updating its editability
     * and caret style accordingly.
     *
     * @param newMode the new Vim mode
     */
    public void setMode(VimModes newMode) {
        // Save the current caret position.
        int pos = getCaretPosition();

        this.mode = newMode;
        // Only allow editing in INSERT mode.
        setEditable(newMode == VimModes.INSERT);

        if (newMode == VimModes.INSERT) {
            // In INSERT mode, use the standard I-beam caret.
            setCaret(new DefaultCaret());
        } else {
            // In NORMAL (READY) or VISUAL mode, use our custom block caret.
            setCaret(new BlockCaret());
        }

        // Restore the caret position after switching the caret.
        setCaretPosition(pos);

        System.out.println("Switched to " + newMode + " mode.");
    }

    // --- Caret Movement Methods ---
    public void moveLeft() {
        int pos = getCaretPosition();
        if (pos > 0) {
            setCaretPosition(pos - 1);
        }
    }

    public void moveRight() {
        int pos = getCaretPosition();
        if (pos < getDocument().getLength()) {
            setCaretPosition(pos + 1);
        }
    }

    public void moveUp() {
        try {
            int pos = getCaretPosition();
            int currentLine = getLineOfOffset(pos);
            if (currentLine > 0) {
                int startOfCurrentLine = getLineStartOffset(currentLine);
                int column = pos - startOfCurrentLine;
                int prevLineStart = getLineStartOffset(currentLine - 1);
                int prevLineEnd = getLineEndOffset(currentLine - 1);
                int prevLineLength = prevLineEnd - prevLineStart;
                int newColumn = Math.min(column, Math.max(0, prevLineLength - 1));
                setCaretPosition(prevLineStart + newColumn);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    public void moveDown() {
        try {
            int pos = getCaretPosition();
            int currentLine = getLineOfOffset(pos);
            if (currentLine < getLineCount() - 1) {
                int startOfCurrentLine = getLineStartOffset(currentLine);
                int column = pos - startOfCurrentLine;
                int nextLineStart = getLineStartOffset(currentLine + 1);
                int nextLineEnd = getLineEndOffset(currentLine + 1);
                int nextLineLength = nextLineEnd - nextLineStart;
                int newColumn = Math.min(column, Math.max(0, nextLineLength - 1));
                setCaretPosition(nextLineStart + newColumn);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}