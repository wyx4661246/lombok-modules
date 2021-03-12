package com.marzaha.lombok.modules.processor;

import com.google.auto.service.AutoService;
import com.marzaha.lombok.modules.annotation.ToString;
import com.marzaha.lombok.modules.common.LombokProcess;
import com.marzaha.lombok.modules.enums.BinaryTagEnum;
import com.marzaha.lombok.modules.enums.JavaVersionEnum;
import com.marzaha.lombok.modules.utils.JavacAstUtil;
import com.marzaha.lombok.modules.utils.JavacCompilerUtil;
import com.marzaha.lombok.modules.utils.JavacConvertUtil;
import com.marzaha.lombok.modules.utils.MaskUtil;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

@SupportedAnnotationTypes({"com.marzaha.lombok.modules.annotation.ToString"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ToStringProcessor extends AbstractProcessor {

    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Object names;

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        LombokProcess.MESSAGER = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment)processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = JavacAstUtil.getNamesByJdk(context);
        LombokProcess.printMessage(Diagnostic.Kind.NOTE, "names-----------:" + this.names);
        LombokProcess.loadMaskFields();
        String javaVersion = System.getProperty("java.version");
        LombokProcess.printMessage(Diagnostic.Kind.NOTE, "start-----------" + javaVersion);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Iterator var3 = roundEnv.getElementsAnnotatedWith(ToString.class).iterator();

        while(var3.hasNext()) {
            Element element = (Element)var3.next();

            try {
                ToString annotation = (ToString)element.getAnnotation(ToString.class);
                boolean callSuper = annotation.callSuper();
                boolean override = annotation.override();
                boolean toJson = annotation.toJson();
                boolean formatDate = annotation.formatDate();
                LombokProcess.printMessage(Diagnostic.Kind.NOTE, "process-------");
                if (element.getKind() == ElementKind.CLASS) {
                    TypeElement typeElement = (TypeElement)element;
                    LombokProcess.printMessage(Diagnostic.Kind.NOTE, typeElement.getSimpleName().toString());
                    if (toJson) {
                        this.handleToJson(element, callSuper, override, formatDate);
                    } else {
                        this.handleToString(element, callSuper, override);
                    }
                }
            } catch (Exception var11) {
                LombokProcess.printMessage(Diagnostic.Kind.ERROR, "ToStringProcessor process error");
            }
        }
        return false;
    }
    public SourceVersion getSupportedSourceVersion() {
        int javaCompilerVersion = JavacCompilerUtil.getJavaCompilerVersion();

        try {
            if (javaCompilerVersion <= 6) {
                return SourceVersion.RELEASE_6;
            } else {
                return javaCompilerVersion == 7 ? JavacConvertUtil.convertSourceVersion(JavaVersionEnum.VERSION_7) : JavacConvertUtil.convertSourceVersion(JavaVersionEnum.VERSION_8);
            }
        } catch (Exception var3) {
            return SourceVersion.RELEASE_6;
        }
    }

    private void handleToString(Element element, boolean callSuper, boolean override) {
        JCTree jcTree = this.trees.getTree(element);
        if (jcTree instanceof JCTree.JCClassDecl) {
            boolean hasMethod = JavacAstUtil.hasMethod(jcTree, "toString", 0);
            if (!hasMethod || override) {
                JCTree.JCStatement statement = this.toStringV1(element, jcTree, callSuper);
                ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer();
                jcStatements.append(statement);
                JCTree.JCBlock methodBody = this.treeMaker.Block(0L, jcStatements.toList());
                List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
                List<JCTree.JCVariableDecl> parameters = List.nil();
                List<JCTree.JCExpression> throwsClauses = List.nil();
                JCTree.JCAnnotation overrideAnnotation = this.treeMaker.Annotation(this.treeMaker.Ident(JavacAstUtil.nameFromString(this.names, "Override")), List.nil());
                JCTree.JCModifiers mods = this.treeMaker.Modifiers(1L, List.of(overrideAnnotation));
                JCTree.JCExpression returnType = this.treeMaker.Ident(JavacAstUtil.nameFromString(this.names, "String"));
                JCTree.JCMethodDecl methodDecl = this.treeMaker.MethodDef(mods, JavacAstUtil.nameFromString(this.names, "toString"), returnType, methodGenericParams, parameters, throwsClauses, methodBody, (JCTree.JCExpression)null);
                JavacAstUtil.injectMethod(jcTree, methodDecl);
            }
        }
    }

    private void handleToJson(Element element, boolean callSuper, boolean override, boolean formatDate) {
        JCTree jcTree = this.trees.getTree(element);
        if (jcTree instanceof JCTree.JCClassDecl) {
            boolean hasMethod = JavacAstUtil.hasMethod(jcTree, "toString", 0);
            if (!hasMethod || override) {
                JCTree.JCBlock methodBody = this.generateToStringMethod(element, jcTree, callSuper, formatDate);
                List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
                List<JCTree.JCVariableDecl> parameters = List.nil();
                List<JCTree.JCExpression> throwsClauses = List.nil();
                JCTree.JCAnnotation overrideAnnotation = this.treeMaker.Annotation(this.treeMaker.Ident(JavacAstUtil.nameFromString(this.names, "Override")), List.nil());
                JCTree.JCModifiers mods = this.treeMaker.Modifiers(1L, List.of(overrideAnnotation));
                JCTree.JCExpression returnType = this.treeMaker.Ident(JavacAstUtil.nameFromString(this.names, "String"));
                JCTree.JCMethodDecl methodDecl = this.treeMaker.MethodDef(mods, JavacAstUtil.nameFromString(this.names, "toString"), returnType, methodGenericParams, parameters, throwsClauses, methodBody, (JCTree.JCExpression)null);
                JavacAstUtil.injectMethod(jcTree, methodDecl);
            }
        }
    }

    private JCTree.JCBlock generateToStringMethod(Element element, JCTree jcTree, boolean callSuper, boolean formatDate) {
        try {
            JCTree.JCExpression forNameMethod = JavacAstUtil.memberAccess(this.treeMaker, this.names, "java.lang.Class.forName");
            JCTree.JCExpression getPropertyMethod = JavacAstUtil.memberAccess(this.treeMaker, this.names, "java.lang.System.getProperty");
            JCTree.JCExpression setPropertyMethod = JavacAstUtil.memberAccess(this.treeMaker, this.names, "java.lang.System.setProperty");
            JCTree.JCExpression lombokVersionParam = this.treeMaker.Literal("com.marzaha.lombok.modules.version");
            JCTree.JCExpression toStringUtilForNameParam = this.treeMaker.Literal("com.marzaha.lombok.modules.utils.ToStringUtil");
            JCTree.JCMethodInvocation getPropertyApply = this.treeMaker.Apply(List.nil(), getPropertyMethod, List.of(lombokVersionParam));
            JCTree.JCMethodInvocation toStringUtilClass = this.treeMaker.Apply(List.nil(), forNameMethod, List.of(toStringUtilForNameParam));
            JCTree.JCStatement toStringV1 = this.toStringV1(element, jcTree, callSuper);
            JCTree.JCStatement toStringV2 = this.toStringV2(element, jcTree, callSuper, formatDate);

            List append = List.nil().append(getPropertyApply);
            JCTree.JCExpression isV2 = this.treeMaker.Apply(List.nil(), this.treeMaker.Select(this.treeMaker.Literal("2"), JavacAstUtil.nameFromString(this.names, "equals")), append);
            JCTree.JCExpression isV1 = this.treeMaker.Apply(List.nil(), this.treeMaker.Select(this.treeMaker.Literal("1"), JavacAstUtil.nameFromString(this.names, "equals")), append);
            JCTree.JCIf returnV2 = this.treeMaker.If(isV2, toStringV2, (JCTree.JCStatement)null);
            JCTree.JCIf returnV1 = this.treeMaker.If(isV1, toStringV1, (JCTree.JCStatement)null);
            JCTree.JCStatement setV1 = this.treeMaker.Exec(this.treeMaker.Apply(List.nil(), setPropertyMethod, List.of(lombokVersionParam, this.treeMaker.Literal("1"))));
            JCTree.JCStatement setV2 = this.treeMaker.Exec(this.treeMaker.Apply(List.nil(), setPropertyMethod, List.of(lombokVersionParam, this.treeMaker.Literal("2"))));
            JCTree.JCBinary toStringUtilClassNotEmpty = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.NE, JavacAstUtil.makeNull(this.treeMaker), toStringUtilClass);
            JCTree.JCStatement setVersion = this.treeMaker.If(this.treeMaker.Parens(toStringUtilClassNotEmpty), setV2, setV1);
            JCTree.JCBlock setVersionBody = this.treeMaker.Block(0L, (new ListBuffer()).append(setVersion).toList());
            JCTree.JCVariableDecl exceptionVariableDecl = this.createVarDef(this.treeMaker.Modifiers(0L), "e", JavacAstUtil.memberAccess(this.treeMaker, this.names, "java.lang.ClassNotFoundException"), (JCTree.JCExpression)null);
            exceptionVariableDecl.pos = jcTree.pos;
            JCTree.JCCatch setVersionCatch = this.treeMaker.Catch(exceptionVariableDecl, this.treeMaker.Block(0L, (new ListBuffer()).append(setV1).toList()));
            JCTree.JCTry setVersionTry = this.treeMaker.Try(setVersionBody, List.of(setVersionCatch), (JCTree.JCBlock)null);
            JCTree.JCExpression currToString = JavacAstUtil.memberAccess(this.treeMaker, this.names, "toString");
            JCTree.JCExpression toStringRecall = this.treeMaker.Apply(List.nil(), currToString, List.nil());
            JCTree.JCReturn toStringReturn = this.treeMaker.Return(toStringRecall);
            ListBuffer<JCTree.JCStatement> statements = new ListBuffer();
            statements.append(returnV2);
            statements.append(returnV1);
            statements.append(setVersionTry);
            statements.append(toStringReturn);
            return this.treeMaker.Block(0L, statements.toList());
        } catch (Exception var30) {
            LombokProcess.printMessage(Diagnostic.Kind.ERROR, "generateToStringMethodError:" + var30.getMessage());
            return null;
        }
    }

    public static Object getNamesByJdk(Context context) {
        int javaCompilerVersion = JavacCompilerUtil.getJavaCompilerVersion();

        try {
            Class c;
            Method method;
            Object invoke;
            if (javaCompilerVersion <= 8) {
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

    private JCTree.JCStatement toStringV1(Element element, JCTree jcTree, boolean callSuper) {
        JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl)jcTree;
        java.util.List<Element> innerFields = JavacAstUtil.getInnerFields(element);
        boolean first = true;
        String typeName = JavacAstUtil.getTypeName(jcClassDecl);
        String prefix;
        if (callSuper) {
            prefix = typeName + "(super=";
        } else if (innerFields.isEmpty()) {
            prefix = typeName + "()";
        } else {
            prefix = typeName + "(" + ((Element)innerFields.iterator().next()).getSimpleName().toString() + "=";
        }

        JCTree.JCExpression current = this.treeMaker.Literal(prefix);
        if (callSuper) {
            JCTree.JCMethodInvocation callToSuper = this.treeMaker.Apply(List.nil(), this.treeMaker.Select(this.treeMaker.Ident(JavacAstUtil.nameFromString(this.names, "super")), JavacAstUtil.nameFromString(this.names, "toString")), List.nil());
            current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, callToSuper);
            first = false;
        }

        JCTree.JCStatement statement = this.createdJCStatement((JCTree.JCExpression)current, innerFields, jcClassDecl, first);
        return (JCTree.JCStatement)(statement == null ? this.treeMaker.Return(this.treeMaker.Literal("")) : statement);
    }


    private JCTree.JCStatement toStringV2(Element element, JCTree jcTree, boolean callSuper, boolean formatDate) {
        JCTree.JCExpression current = this.createdJsonStatement(JavacAstUtil.getInnerFields(element), (JCTree.JCClassDecl)jcTree, formatDate);
        if (callSuper) {
            JCTree.JCMethodInvocation callToSuper = this.treeMaker.Apply(List.nil(), this.treeMaker.Select(this.treeMaker.Ident(JavacAstUtil.nameFromString(this.names, "super")), JavacAstUtil.nameFromString(this.names, "toString")), List.nil());
            current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, callToSuper);
        }

        return current == null ? this.treeMaker.Return(this.treeMaker.Literal("{}")) : this.treeMaker.Return((JCTree.JCExpression)current);
    }

    private JCTree.JCStatement createdJCStatement(JCTree.JCExpression current, java.util.List<Element> innerFields, JCTree.JCClassDecl jcClassDecl, boolean first) {
        try {
            if (innerFields != null && !innerFields.isEmpty()) {
                String infix = ", ";
                String suffix = ")";
                Iterator var7 = innerFields.iterator();

                while(var7.hasNext()) {
                    Element element = (Element)var7.next();
                    JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl)this.trees.getTree(element);
                    JCTree.JCExpression fieldAccessor = JavacAstUtil.createFieldAccessor(this.treeMaker, jcClassDecl, this.names, variableDecl);
                    JCTree.JCExpression vartype = variableDecl.vartype;
                    boolean fieldIsPrimitiveArray = vartype instanceof JCTree.JCArrayTypeTree && ((JCTree.JCArrayTypeTree)vartype).elemtype instanceof JCTree.JCPrimitiveTypeTree;
                    boolean fieldIsObjectArray = !fieldIsPrimitiveArray && vartype instanceof JCTree.JCArrayTypeTree;
                    Object expr;
                    JCTree.JCExpression maskjcExpression;
                    if (!fieldIsPrimitiveArray && !fieldIsObjectArray) {
                        expr = fieldAccessor;
                    } else {
                        maskjcExpression = JavacAstUtil.memberAccess(this.treeMaker, this.names, "java.util.Arrays." + (fieldIsObjectArray ? "deepToString" : "toString"));
                        expr = this.treeMaker.Apply(List.nil(), maskjcExpression, List.of(fieldAccessor));
                    }

                    maskjcExpression = this.maskHandle(variableDecl, (JCTree.JCExpression)expr);
                    if (maskjcExpression != null) {
                        expr = maskjcExpression;
                    }

                    if (first) {
                        current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, (JCTree.JCExpression)expr);
                        first = false;
                    } else {
                        //JCTree.JCExpression
                        current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, this.treeMaker.Literal(infix + variableDecl.name.toString() + "="));
                        current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, current, (JCTree.JCExpression)expr);
                    }
                }

                if (!first) {
                    current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, this.treeMaker.Literal(suffix));
                }

                JCTree.JCReturn aReturn = this.treeMaker.Return((JCTree.JCExpression)current);
                LombokProcess.printMessage(Diagnostic.Kind.NOTE, "createdJCStatement return result:" + aReturn.toString());
                return aReturn;
            } else {
                return null;
            }
        } catch (Exception var16) {
            LombokProcess.printMessage(Diagnostic.Kind.WARNING, "bulid toString JCStatement Error" + var16.getMessage());
            LombokProcess.printMessage(Diagnostic.Kind.WARNING, "createdJCStatement return result null");
            return null;
        }
    }

    private JCTree.JCExpression createdJsonStatement(java.util.List<Element> innerFields, JCTree.JCClassDecl jcClassDecl, boolean formatDate) {
        try {
            if (innerFields != null && !innerFields.isEmpty()) {
                JCTree.JCExpression current = null;
                String prevVarType = null;

                for(int i = 0; i < innerFields.size(); ++i) {
                    JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl)this.trees.getTree((Element)innerFields.get(i));
                    JCTree.JCExpression fieldAccessor = JavacAstUtil.createFieldAccessor(this.treeMaker, jcClassDecl, this.names, variableDecl);
                    JCTree.JCExpression vartype = variableDecl.vartype;
                    String variableName = variableDecl.name.toString();
                    if (!"serialVersionUID".equals(variableName)) {
                        String infix = current == null ? "{\"" : (!"String".equals(prevVarType) && (!"Date".equals(prevVarType) || formatDate) ? ",\"" : "\",\"");
                        String suffix = !"String".equals(variableDecl.vartype.toString()) && (!"Date".equals(variableDecl.vartype.toString()) || formatDate) ? "\":" : "\":\"";
                        //Object current;
                        if (current == null) {
                            current = this.treeMaker.Literal(infix + variableName + suffix);
                        } else {
                            current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, current, this.treeMaker.Literal(infix + variableName + suffix));
                        }

                        boolean fieldIsPrimitiveArray = vartype instanceof JCTree.JCArrayTypeTree && ((JCTree.JCArrayTypeTree)vartype).elemtype instanceof JCTree.JCPrimitiveTypeTree;
                        boolean fieldIsObjectArray = !fieldIsPrimitiveArray && vartype instanceof JCTree.JCArrayTypeTree;
                        JCTree.JCExpression tsMethod;
                        if ("String".equals(vartype.toString())) {
                            tsMethod = this.maskHandle(variableDecl, fieldAccessor);
                            if (tsMethod != null) {
                                current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, tsMethod);
                            } else {
                                current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, fieldAccessor);
                            }
                        } else {
                            JCTree.JCMethodInvocation apply;
                            if ("Date".equals(vartype.toString())) {
                                if (formatDate) {
                                    tsMethod = JavacAstUtil.memberAccess(this.treeMaker, this.names, "com.marzaha.lombok.modules.utils.ToStringUtil.toJson");
                                    apply = this.treeMaker.Apply(List.nil(), tsMethod, List.of(fieldAccessor));
                                    current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, apply);
                                } else {
                                    current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, fieldAccessor);
                                }
                            } else if (!fieldIsObjectArray && !vartype.toString().contains("List") && !vartype.toString().contains("Map") && !vartype.toString().contains("Set")) {
                                current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, fieldAccessor);
                            } else {
                                tsMethod = JavacAstUtil.memberAccess(this.treeMaker, this.names, "com.marzaha.lombok.modules.utils.ToStringUtil.toJson");
                                apply = this.treeMaker.Apply(List.nil(), tsMethod, List.of(fieldAccessor));
                                current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, (JCTree.JCExpression)current, apply);
                            }
                        }

                        prevVarType = vartype.toString();
                        if (i + 1 == innerFields.size()) {
                            current = JavacAstUtil.binary(this.treeMaker, BinaryTagEnum.PLUS, current, this.treeMaker.Literal(!"String".equals(prevVarType) && (!"Date".equals(prevVarType) || formatDate) ? "}" : "\"}"));
                        }
                    }
                }

                LombokProcess.printMessage(Diagnostic.Kind.NOTE, "createdJCExpression return result:" + (current == null ? null : current.toString()));
                return current;
            } else {
                return this.treeMaker.Literal("{}");
            }
        } catch (Exception var17) {
            LombokProcess.printMessage(Diagnostic.Kind.WARNING, "bulid toString JCExpression Error" + var17.getMessage());
            return null;
        }
    }

    private JCTree.JCExpression maskHandle(JCTree.JCVariableDecl variableDecl, JCTree.JCExpression expr) {
        try {
            JCTree.JCExpression vartype = variableDecl.vartype;
            boolean fieldIsPrimitive = vartype instanceof JCTree.JCPrimitiveTypeTree;
            boolean fieldIsPrimitiveArray = vartype instanceof JCTree.JCArrayTypeTree && ((JCTree.JCArrayTypeTree)vartype).elemtype instanceof JCTree.JCPrimitiveTypeTree;
            boolean fieldIsObjectArray = !fieldIsPrimitiveArray && vartype instanceof JCTree.JCArrayTypeTree;
            boolean fieldIsObject = !fieldIsPrimitive && !fieldIsPrimitiveArray && !fieldIsObjectArray;
            if (!fieldIsObject && !"String".equals(variableDecl.vartype.toString())) {
                return expr;
            } else if (!LombokProcess.MASK_FIELD_MAP.containsKey(variableDecl.name.toString())) {
                return expr;
            } else {
                String expression = (String)LombokProcess.MASK_FIELD_MAP.get(variableDecl.name.toString());
                if (MaskUtil.isBlank(expression)) {
                    return expr;
                } else {
                    List<JCTree.JCExpression> maskParams = List.of(expr, this.treeMaker.Literal(expression));
                    JCTree.JCExpression tsMethod = JavacAstUtil.memberAccess(this.treeMaker, this.names, "com.marzaha.lombok.modules.utils.MaskUtil.mask");
                    //JCTree.JCExpression
                    expr = this.treeMaker.Apply(List.nil(), tsMethod, maskParams);
                    LombokProcess.printMessage(Diagnostic.Kind.NOTE, "maskHandle variableDecl " + variableDecl.name.toString() + " expr:" + expr.toString());
                    return expr;
                }
            }
        } catch (Exception var11) {
            LombokProcess.printMessage(Diagnostic.Kind.WARNING, "maskHandle variableDecl error:" + var11.getMessage());
            return null;
        }
    }

    private JCTree.JCVariableDecl createVarDef(JCTree.JCModifiers modifiers, String name, JCTree.JCExpression varType, JCTree.JCExpression init) {
        return this.treeMaker.VarDef(modifiers, JavacAstUtil.nameFromString(this.names, name), varType, init);
    }
}
