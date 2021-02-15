package com.github.hexffff0.incubator.other.stackoverflow;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
public class SearchStackOverflow {

    public void main(Map<String, Object> context) {
        String stackOverFlow = "https://stackoverflow.com/search?q=";
        AnActionEvent e = (AnActionEvent) context.get("AnActionEvent");
        Editor editor = e.getData(LangDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        CaretModel caretModel = editor.getCaretModel();
        Caret currentCaret = caretModel.getCurrentCaret();
        String selectedText = currentCaret.getSelectedText();
        if (StringUtils.isNotBlank(selectedText)) {
            selectedText = selectedText.trim();
            BrowserUtil.browse(stackOverFlow + selectedText);
        } else {
            BrowserUtil.browse(stackOverFlow);
        }
    }

}
