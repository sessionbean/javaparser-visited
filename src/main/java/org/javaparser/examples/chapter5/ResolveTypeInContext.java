package org.javaparser.examples.chapter5;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.util.Optional;

public class ResolveTypeInContext {

    private static final String FILE_PATH = "src/main/java/org/javaparser/examples/chapter5/Foo.java";
    private static final String SRC_PATH = "src/main/java";

    public static void main(String[] args) throws Exception {
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(SRC_PATH));
//        reflectionTypeSolver.setParent(reflectionTypeSolver);

        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        combinedSolver.add(javaParserTypeSolver);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        StaticJavaParser
                .getConfiguration()
                .setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

        FieldDeclaration fieldDeclaration = Navigator.findNodeOfGivenClass(cu, FieldDeclaration.class);

        Type type = fieldDeclaration.getVariables().get(0).getType();

        if(type instanceof ClassOrInterfaceType) {
            ClassOrInterfaceType classOrInterfaceType = type.asClassOrInterfaceType();
            Optional<NodeList<Type>> typeArguments = classOrInterfaceType.getTypeArguments();

            //list of arguments might have different length
            typeArguments.ifPresent(types -> System.out.println(types.get(0).resolve().asReferenceType().getQualifiedName()));
        }

//        System.out.println("Field type: " +
//                .resolve().asReferenceType().getQualifiedName());
    }
}
