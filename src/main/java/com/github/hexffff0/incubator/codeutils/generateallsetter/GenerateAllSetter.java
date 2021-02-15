package com.github.hexffff0.incubator.codeutils.generateallsetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTypesUtil;
import sun.tools.tree.PreIncExpression;
public class GenerateAllSetter {

    private static final Logger logger = Logger.getInstance(GenerateAllSetter.class);

    public void main(Map<String, Object> context) {
        AnActionEvent e = (AnActionEvent) context.get("AnActionEvent");
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile currentFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement == null || project == null || currentFile == null) {
            return;
        }

        if (psiElement instanceof PsiLocalVariable) {
            generateAllSetter(project, currentFile, (PsiLocalVariable) psiElement);
        }
        // todo copy properties
    }

    private void generateAllSetter(Project project, PsiFile currentFile, PsiLocalVariable variable) {
        PsiElement varDeclaration = variable.getParent();
        if (!(varDeclaration instanceof PsiDeclarationStatement)) {
            return;
        }

        PsiClass varType = PsiTypesUtil.getPsiClass(variable.getType());
        List<PsiMethod> setterMethods = extractSetMethods(varType);
        if (setterMethods.size() == 0) {
            return;
        }

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(currentFile);
        if (document == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (PsiMethod setter : setterMethods) {
            // val.setter();
            generateSetter(variable, sb, setter);
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            int writeOffset = varDeclaration.getTextOffset() + varDeclaration.getText().length();
            document.insertString(writeOffset, sb.toString());
            if (currentFile instanceof PsiJavaFile) {
                JavaCodeStyleManager javaCodeStyleManager = JavaCodeStyleManager.getInstance(project);
                javaCodeStyleManager.shortenClassReferences(currentFile);
                new ReformatCodeProcessor(currentFile, false).run();
            }
        });
    }

    private void generateSetter(PsiLocalVariable variable, StringBuilder sb, PsiMethod setter) {
        // todo resolve param type of the setter method and set default value
        sb.append(variable.getName()).append(".").append(setter.getName())
          .append("()").append(";");
    }

    public static List<PsiMethod> extractSetMethods(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isNotSystemClass(psiClass)) {
            addSetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }

    public static List<PsiMethod> extractGetMethod(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isNotSystemClass(psiClass)) {
            addGetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }

    public static boolean isNotSystemClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null || qualifiedName.startsWith("java.")) {
            return false;
        }
        return true;
    }

    public static void addSetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidSetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    public static void addGetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidGetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    public static boolean isValidSetMethod(PsiMethod m) {
        return m.hasModifierProperty(PsiModifier.PUBLIC)
            && !m.hasModifierProperty(PsiModifier.STATIC)
            && (m.getName().startsWith("set") || m.getName().startsWith("with"));
    }

    public static boolean isValidGetMethod(PsiMethod m) {
        return m.hasModifierProperty(PsiModifier.PUBLIC)
            && !m.hasModifierProperty(PsiModifier.STATIC)
            && (m.getName().startsWith("get") || m.getName().startsWith("is"));
    }
}
