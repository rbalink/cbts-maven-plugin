package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.YamlPrinter;

public class ReadMainFiles{
	public static List<MethodDeclaration> methodDeclarations = null;
	
	public static void readMainFiles(HashSet<TestWrapper> result) {
		for(TestWrapper tw : result) {
			try {
				//collect all data
				readMainFile(tw);
				//do analysis
				analyzeMainCode(tw);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void readMainFile(TestWrapper tw) throws IOException {
		SimpleName simpleName = null;
		Optional<PackageDeclaration> packageDeclaration = null;
		Optional<BlockStmt> optional = null;
		String fileName = null;

		CompilationUnit cu = StaticJavaParser.parse(tw.getSourceCodePath());
		packageDeclaration = cu.getPackageDeclaration();
		if (packageDeclaration.isPresent()) {
			System.out.println(packageDeclaration.get().toString());
		} else {
			throw new FileNotFoundException();
		}

		for (TypeDeclaration<?> td : cu.getTypes()) {
			fileName = td.getNameAsString();
			methodDeclarations = td.findAll(MethodDeclaration.class);
		}

		System.out.println(cu.getImports());
		for (MethodDeclaration md : methodDeclarations) {
			if (md.getAnnotations().isNonEmpty()) {
				optional = md.getBody();
				simpleName = md.getName();
				System.out.println(md.getBody());
				System.out.println(md.getName());
			}
		}
		for (TypeDeclaration<?> td : cu.getTypes()) {
			System.out.println(td.getNameAsString());
			System.out.println(td.findAll(MethodDeclaration.class));
		}

		System.out.println(cu.getTypes());
		System.out.println(cu.getClass());
		System.out.println(cu.getModule());
		//test
		YamlPrinter printer = new YamlPrinter(true);
		System.out.println(printer.output(cu));
		tw.setMainSourceCode(new MainWrapper(simpleName, packageDeclaration, optional, fileName, cu.getImports(), cu));
	}
	
	public static void analyzeMainCode(TestWrapper tw) {
		//CHECK IF Java inheritance
		javaInheritanceCheck(tw);
		
		//IMPORT ANALYSIS 
		importAnalysis(tw);
		
		//POM ANALYSIS
		checkIfFileEffectedWithPOM(tw);
		
		//static CFG
		cfg(tw);
		
		//class hierarchy analysis
		cha(tw);
		
		//DEPENDENCY ANALYSIS
	}
	
	
	private static void javaInheritanceCheck(TestWrapper tw) {
		NodeList<TypeDeclaration<?>> nl = tw.getMainSourceCode().getCu().getTypes();
		System.out.println(nl);
	}
	
	private static void importAnalysis(TestWrapper tw) {
		NodeList<ImportDeclaration> id = tw.getMainSourceCode().getImportList();
		System.out.println(id);
	}
	
	private static void checkIfFileEffectedWithPOM(TestWrapper tw) {
		
	}
	
	private static void cfg(TestWrapper tw) {
		
	}
	
	private static void cha(TestWrapper tw) {
		
	}
}