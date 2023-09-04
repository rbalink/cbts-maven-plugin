package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.maven.model.Dependency;
import org.eclipse.jgit.diff.DiffEntry;

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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.YamlPrinter;

public class ReadMainFiles {
	public static List<MethodDeclaration> methodDeclarations = null;

	public static HashSet<TestWrapper> readMainFiles(HashSet<TestWrapper> result) throws IOException {
		for (TestWrapper tw : result) {
			readMainFile(tw);
			analyzeMainCode(tw);
		}
		return result;
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

		// System.out.println(cu.getImports());
		for (MethodDeclaration md : methodDeclarations) {
			if (md.getAnnotations().isNonEmpty()) {
				optional = md.getBody();
				simpleName = md.getName();
			}
		}
		NodeList nodel = cu.getTypes();

		// System.out.println(cu.getTypes());
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

	// TODO: ACHTUNG: was wenn mehrere coi??
	private static void javaInheritanceCheck(TestWrapper tw) {
		ClassOrInterfaceDeclaration coi = null;
		for (TypeDeclaration<?> typeDeclaration : tw.getMainSourceCode().getCu().getTypes()) {
			if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
				coi = (ClassOrInterfaceDeclaration) typeDeclaration;
			}
			if (!coi.isEmpty()) {
				NodeList<ClassOrInterfaceType> et = coi.getExtendedTypes();
				for (ClassOrInterfaceType type : et) {
					for (DiffEntry de : CBTSPrototypePlugin.gitDiffList) {
						if (de.getNewPath().contains(type.asString())) {
							System.out.println("EXTENDS GEFUNDEN");
							tw.addProbability(2);
							// evtl detaillierteren Codecheck
						}
					}
				}
			}
		}
	}

	/**
	 * analyze all imports from the file, check if imports were part of the difflist
	 * 
	 * @param tw
	 */
	private static void importAnalysis(TestWrapper tw) {
		List<Name> importierteBezeichner = new ArrayList<Name>();
		NodeList<ImportDeclaration> id = tw.getMainSourceCode().getImportList();
		for (ImportDeclaration dec : id) {
			importierteBezeichner.add(dec.getName());
		}
		tw.getMainSourceCode().setImportListeName(importierteBezeichner);

		for (Name n : tw.getMainSourceCode().getImportListeName()) {
			Path importPath = Paths.get(n.asString().replace(".", File.separator));
			for (DiffEntry de : CBTSPrototypePlugin.gitDiffList) {
				if ((Paths.get(de.getNewPath()).toString().contains(importPath.toString()))) {
					System.out.println("IMPORT BEINHALTET AENDERUNGEN");
					tw.addProbability(10);
				}
			}
		}
	}

	private static void usedClasses(TestWrapper tw) {
		// TODO: check for other classes inside File (eigene Datenstruktur)
		// System.out.println(tw);
		// if yes: +2
	}

	/**
	 * check if import is connected to any changes to pom file
	 * 
	 * @param tw
	 */
	private static void checkIfFileEffectedWithPOM(TestWrapper tw) {
		boolean effected = false;
		if (ReadSourceCode.wasPomChanged) {
			for (Dependency de : ReadSourceCode.pomDependencies) {
				if (effected) {
					break;
				}
				for (Name na : tw.getMainSourceCode().getImportListeName()) {
					if (na.toString().contains(de.getGroupId())) {
						System.out.println("POM EFFECTED");
						tw.addProbability(10);
						effected = true;
						// TODO: check if possible to parse POM code to be more specific
						// TODO: better combination of GroupId + artifactId
					}
					if (effected) {
						break;
					}
				}

			}
		}

	}

	private static void cfg(TestWrapper tw) {
		// redundant
	}

	/**
	 * class hierarchy analysis
	 * 
	 * @param tw
	 */
	private static void cha(TestWrapper tw) {
		tw.getMainSourceCode().getCu().accept(new ClassHierarchyVisitor(), null);
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
			SimpleName className = classDeclaration.getName();

			// Superklasse wenn vorhanden
			if (classDeclaration.getExtendedTypes().size() > 0) {
				String superClassName = classDeclaration.getExtendedTypes().get(0).getNameAsString();
				System.out.println(className + " erbt von " + superClassName);
			}

			// Schnittstellen,wenn vorhanden
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