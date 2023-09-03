package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.YamlPrinter;

public class ReadMainFiles {
	public static List<MethodDeclaration> methodDeclarations = null;

	public static void readMainFiles(HashSet<TestWrapper> result) {
		for (TestWrapper tw : result) {
			try {
				// collect all data
				readMainFile(tw);
				// do analysis
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
		NodeList nodel = cu.getTypes();

		System.out.println(cu.getTypes());
		// test
		tw.setMainSourceCode(new MainWrapper(simpleName, packageDeclaration, optional, fileName, cu.getImports(), cu));
	}

	public static void analyzeMainCode(TestWrapper tw) {
		// class dependency analysis (CDA)
		dependencyAnalysisInternal(tw);

		// CHECK IF Java inheritance
		javaInheritanceCheck(tw);

		// static CFG
		cfg(tw);

		// class hierarchy analysis
		cha(tw);

		// abstract syntax tree
		ast(tw);

	}

	// Quellcode durchlesen
	// Import und Klassen betrachten
	private static void dependencyAnalysisInternal(TestWrapper tw) {
		// klassen deklarationen suchen

		// IMPORT ANALYSIS
		importAnalysis(tw);

		usedClasses(tw);

		// POM ANALYSIS
		checkIfFileEffectedWithPOM(tw);
	}

	private static void javaInheritanceCheck(TestWrapper tw) {
		NodeList<TypeDeclaration<?>> nl = tw.getMainSourceCode().getCu().getTypes();
		System.out.println(nl);
		// check structure +2
	}

	private static void importAnalysis(TestWrapper tw) {
		List<Name> importierteBezeichner = new ArrayList<Name>();
		NodeList<ImportDeclaration> id = tw.getMainSourceCode().getImportList();
		for (ImportDeclaration dec : id) {
			importierteBezeichner.add(dec.getName());
		}
		tw.getMainSourceCode().setImportListeName(importierteBezeichner);
		System.out.println(id);

		// TODO check from DIFF ob es betroffen wurde
		// schleife durch die Liste, check übereinstimmung mit Path
		// if yes: +10 score
	}

	private static void usedClasses(TestWrapper tw) {
		// TODO: check for other classes inside File (eigene Datenstruktur)
		System.out.println(tw);
		// if yes: +2
	}

	private static void checkIfFileEffectedWithPOM(TestWrapper tw) {
		// check if POM was changed at all
		// check if tw.getMainSourceCode().getImportListeName() equals pomDependencies
		// if yes: +10 score
		System.out.println(ReadSourceCode.pomDependencies);
		System.out.println("TEST");

	}

	private static void cfg(TestWrapper tw) {

	}

	/**
	 * class hierarchy analysis
	 * 
	 * @param tw
	 */
	private static void cha(TestWrapper tw) {
		tw.getMainSourceCode().getCu().accept(new ClassHierarchyVisitor(), null);
		System.out.println("experimental");
		// check if path from outside code of DIFF
		// if yes: +10
	}

	/**
	 * builds abstract syntax tree (graphic)
	 * 
	 * @param tw
	 */
	private static void ast(TestWrapper tw) {
		YamlPrinter printer = new YamlPrinter(true);
		System.out.println(printer.output(tw.getMainSourceCode().getCu()));
	}

	/**
	 * Class hierarchy analysis
	 */
	private static class ClassHierarchyVisitor extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(ClassOrInterfaceDeclaration classDeclaration, Void arg) {
			// Klassennamen
			SimpleName className = classDeclaration.getName();

			// Superklasse, wenn vorhanden
			if (classDeclaration.getExtendedTypes().size() > 0) {
				String superClassName = classDeclaration.getExtendedTypes().get(0).getNameAsString();
				System.out.println(className + " erbt von " + superClassName);
			}

			// Schnittstellen, wenn vorhanden
			if (!classDeclaration.getImplementedTypes().isEmpty()) {
				for (var implementedType : classDeclaration.getImplementedTypes()) {
					String interfaceName = implementedType.getNameAsString();
					System.out.println(className + " implementiert " + interfaceName);
				}
			}

			super.visit(classDeclaration, arg);
		}
	}

}