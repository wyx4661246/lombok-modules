package com.marzaha.lombok.modules.utils;

import com.marzaha.lombok.modules.enums.BinaryTagEnum;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class JavacAstUtil {
    public static boolean hasMethod(JCTree jcTree, String methodName, int params) {
        boolean exist = false;
        if (jcTree.getKind() != Tree.Kind.CLASS) {
            return exist;
        } else {
            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl)jcTree;
            List<JCTree> defs = jcClassDecl.defs;
            Iterator var6 = defs.iterator();

            while(var6.hasNext()) {
                JCTree def = (JCTree)var6.next();
                if (def instanceof JCTree.JCMethodDecl) {
                    JCTree.JCMethodDecl md = (JCTree.JCMethodDecl)def;
                    String name = md.name.toString();
                    if (name.equals(methodName) && params > -1) {
                        List<JCTree.JCVariableDecl> ps = md.params;
                        int length = ps.length();
                        if (length == params) {
                            exist = true;
                            break;
                        }
                    }
                }
            }

            return exist;
        }
    }

    public static JCTree.JCMethodDecl getMethod(JCTree jcTree, String methodName, int params) {
        if (jcTree.getKind() != Tree.Kind.CLASS) {
            return null;
        } else {
            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl)jcTree;
            List<JCTree> defs = jcClassDecl.defs;
            Iterator var5 = defs.iterator();

            while(var5.hasNext()) {
                JCTree def = (JCTree)var5.next();
                if (def instanceof JCTree.JCMethodDecl) {
                    JCTree.JCMethodDecl md = (JCTree.JCMethodDecl)def;
                    String name = md.name.toString();
                    if (name.equals(methodName) && params > -1) {
                        List<JCTree.JCVariableDecl> ps = md.params;
                        int length = ps.length();
                        if (length == params) {
                            return md;
                        }
                    }
                }
            }

            return null;
        }
    }

    public static void injectMethod(JCTree jcTree, JCTree.JCMethodDecl method) {
        if (jcTree.getKind() == Tree.Kind.CLASS) {
            JCTree.JCMethodDecl jcMethodDecl = getMethod(jcTree, method.name.toString(), method.params == null ? 0 : method.params.length());
            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl)jcTree;
            if (jcMethodDecl != null) {
                jcClassDecl.defs = removeDef(jcClassDecl.defs, jcMethodDecl);
            }

            jcClassDecl.defs = jcClassDecl.defs.append(method);
        }
    }

    public static List<JCTree> removeDef(List<JCTree> defs, JCTree jcTree) {
        if (defs != null && !defs.isEmpty()) {
            if (!defs.contains(jcTree)) {
                return defs;
            } else {
                List<JCTree> newDefs = List.nil();
                Iterator var3 = defs.iterator();

                while(var3.hasNext()) {
                    JCTree def = (JCTree)var3.next();
                    if (def != jcTree) {
                        newDefs = newDefs.append(def);
                    }
                }

                return newDefs;
            }
        } else {
            return defs;
        }
    }

    public static String getTypeName(JCTree jcTree) {
        return jcTree.getKind() != Tree.Kind.CLASS ? "" : ((JCTree.JCClassDecl)jcTree).name.toString();
    }

    public static java.util.List<Element> getInnerFields(Element element) {
        java.util.List<Element> innerFields = new ArrayList();
        if (element.getKind() != ElementKind.CLASS) {
            return innerFields;
        } else {
            java.util.List<? extends Element> enclosedElements = element.getEnclosedElements();
            if (enclosedElements != null && !enclosedElements.isEmpty()) {
                Iterator var3 = enclosedElements.iterator();

                while(var3.hasNext()) {
                    Element enclosedElement = (Element)var3.next();
                    if (enclosedElement.getKind() == ElementKind.FIELD) {
                        innerFields.add(enclosedElement);
                    }
                }

                return innerFields;
            } else {
                return innerFields;
            }
        }
    }

    public static JCTree.JCExpression createFieldAccessor(TreeMaker treeMaker, JCTree.JCClassDecl jcClassDecl, Object names, JCTree.JCVariableDecl variableDecl) {
        JCTree.JCExpression vartype = variableDecl.vartype;
        boolean isBoolean = false;
        if (vartype instanceof JCTree.JCPrimitiveTypeTree) {
            JCTree.JCPrimitiveTypeTree varTypeTree = (JCTree.JCPrimitiveTypeTree)vartype;
            if (TypeKind.BOOLEAN == varTypeTree.getPrimitiveTypeKind()) {
                isBoolean = true;
            }
        }

        String getter = buildAccessorName(isBoolean, variableDecl.name.toString());
        boolean hasGetter = false;
        List<JCTree> defs = jcClassDecl.defs;
        if (defs != null && !defs.isEmpty()) {
            Iterator var9 = defs.iterator();

            List params;
            do {
                JCTree.JCMethodDecl jcMethodDecl;
                do {
                    JCTree def;
                    Tree.Kind kind;
                    do {
                        if (!var9.hasNext()) {
                            return (JCTree.JCExpression)(hasGetter ? treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(nameFromString(names, "this")), nameFromString(names, getter)), List.nil()) : treeMaker.Select(treeMaker.Ident(nameFromString(names, "this")), variableDecl.name));
                        }

                        def = (JCTree)var9.next();
                        kind = def.getKind();
                    } while(kind != Tree.Kind.METHOD);

                    jcMethodDecl = (JCTree.JCMethodDecl)def;
                } while(!getter.equals(jcMethodDecl.name.toString()));

                params = jcMethodDecl.params;
            } while(params != null && !params.isEmpty());

            hasGetter = true;
        }

        return (JCTree.JCExpression)(hasGetter ? treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(nameFromString(names, "this")), nameFromString(names, getter)), List.nil()) : treeMaker.Select(treeMaker.Ident(nameFromString(names, "this")), variableDecl.name));
    }

    public static JCTree.JCExpression memberAccess(TreeMaker treeMaker, Object names, String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(nameFromString(names, componentArray[0]));

        for(int i = 1; i < componentArray.length; ++i) {
            expr = treeMaker.Select((JCTree.JCExpression)expr, nameFromString(names, componentArray[i]));
        }

        return (JCTree.JCExpression)expr;
    }

    public static String buildAccessorName(boolean isBoolean, String field) {
        if (field.length() == 0) {
            return field;
        } else {
            String prefix = isBoolean ? "is" : "get";
            char first = field.charAt(0);
            if (Character.isLowerCase(first)) {
                boolean useUpperCase = field.length() > 2 && (Character.isTitleCase(field.charAt(1)) || Character.isUpperCase(field.charAt(1)));
                field = String.format("%s%s", useUpperCase ? Character.toUpperCase(first) : Character.toTitleCase(first), field.subSequence(1, field.length()));
            }

            return String.format("%s%s", prefix, field);
        }
    }

    public static Object getNamesByJdk(Context context) {
        int javaCompilerVersion = JavacCompilerUtil.getJavaCompilerVersion();

        try {
            Class c;
            Method method;
            Object invoke;
            if (javaCompilerVersion <= 6) {
                c = Class.forName("com.sun.tools.javac.util.Name$Table");
                method = c.getMethod("instance", Context.class);
                invoke = method.invoke((Object)null, context);
                return invoke;
            } else {
                c = Class.forName("com.sun.tools.javac.util.Names");
                method = c.getMethod("instance", Context.class);
                invoke = method.invoke((Object)null, context);
                return invoke;
            }
        } catch (Exception var5) {
            return null;
        }
    }

    public static Name nameFromString(Object names, String str) {
        int javaCompilerVersion = JavacCompilerUtil.getJavaCompilerVersion();

        try {
            Method fromString;
            if (javaCompilerVersion <= 6) {
                fromString = names.getClass().getMethod("fromString", CharSequence.class);
                return (Name)fromString.invoke(names, str);
            } else {
                fromString = names.getClass().getMethod("fromString", String.class);
                return (Name)fromString.invoke(names, str);
            }
        } catch (Exception var4) {
            return null;
        }
    }

    public static JCTree.JCBinary binary(TreeMaker treeMaker, BinaryTagEnum binaryTagEnum, JCTree.JCExpression exp1, JCTree.JCExpression exp2) {
        int javaCompilerVersion = JavacCompilerUtil.getJavaCompilerVersion();

        try {
            if (javaCompilerVersion < 8) {
                Method binary = treeMaker.getClass().getMethod("Binary", Integer.TYPE, JCTree.JCExpression.class, JCTree.JCExpression.class);
                return (JCTree.JCBinary)binary.invoke(treeMaker, JavacConvertUtil.convertBinary(binaryTagEnum), exp1, exp2);
            } else {
                Class c = Class.forName("com.sun.tools.javac.tree.JCTree$Tag");
                Method binary = treeMaker.getClass().getMethod("Binary", c, JCTree.JCExpression.class, JCTree.JCExpression.class);
                return (JCTree.JCBinary)binary.invoke(treeMaker, JavacConvertUtil.convertBinaryTag(binaryTagEnum), exp1, exp2);
            }
        } catch (Exception var7) {
            return null;
        }
    }

    public static JCTree.JCLiteral literalByJdk(TreeMaker treeMaker, BinaryTagEnum binaryTagEnum, Object var2) {
        try {
            int javaCompilerVersion = JavacCompilerUtil.getJavaCompilerVersion();
            Method literal;
            if (javaCompilerVersion < 8) {
                literal = treeMaker.getClass().getMethod("Literal", Integer.TYPE, Object.class);
                return (JCTree.JCLiteral)literal.invoke(treeMaker, JavacConvertUtil.convertBinary(binaryTagEnum), var2);
            } else {
                literal = treeMaker.getClass().getMethod("Literal", Class.forName("com.sun.tools.javac.code.TypeTag"), Object.class);
                return (JCTree.JCLiteral)literal.invoke(treeMaker, JavacConvertUtil.convertBinaryTag(binaryTagEnum), var2);
            }
        } catch (Exception var5) {
            return null;
        }
    }

    public static JCTree.JCLiteral makeNull(TreeMaker treeMaker) {
        return literalByJdk(treeMaker, BinaryTagEnum.BOT, (Object)null);
    }
}
