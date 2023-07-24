package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class ReadTestFiles {
	public static String fileName = null;
	public static List<MethodDeclaration> methodDeclarations = null;
	public static String originSourceCode = null;

	/**
	 * collects all files
	 * 
	 * @param javaFilesTest List of files
	 */
	public static void collectTestFiles(List<File> javaFilesTest, HashSet<TestWrapper> testSet) {
		for (File file : javaFilesTest) {
			try {
				readTestFile(file, testSet);
			} catch (FileNotFoundException e) {
				System.err.println("Datei existiert nicht");
				e.printStackTrace();
			}
		}
	}

	/**
	 * read TEST files - packageDeclaration - filename - amount of tests
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	private static void readTestFile(File file, HashSet<TestWrapper> testSet) throws FileNotFoundException {
		CompilationUnit cu = StaticJavaParser.parse(file);
		Optional<PackageDeclaration> packageDeclaration = cu.getPackageDeclaration();
		if (packageDeclaration.isPresent()) {
			originSourceCode = packageDeclaration.get().toString();
		} else {
			throw new FileNotFoundException();
		}

		// TODO: potentielle fehlerquelle bei mehreren eintraegen
		for (TypeDeclaration<?> td : cu.getTypes()) {
			fileName = td.getNameAsString();
			methodDeclarations = td.findAll(MethodDeclaration.class);
		}

		for (MethodDeclaration md : methodDeclarations) {
			if (md.getAnnotations().isNonEmpty() && md.getAnnotations().toString().equals("[@Test]")) {
				TestWrapper neuerTest = new TestWrapper(md.getName(), packageDeclaration, md.getBody());
				testSet.add(neuerTest);
			}
		}

		for (TestWrapper tw : testSet) {
			System.out.println(tw.toString());
		}
	}
}