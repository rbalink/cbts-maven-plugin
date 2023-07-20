package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

public class ReadTestFiles{
	public static String fileName = null;
	public static List<MethodDeclaration> methodDeclarations = null;
	public static String originSourceCode = null;

	/**
	 * collects all files
	 * @param javaFilesTest List of files
	 */
	public static void collectFiles(List<File> javaFilesTest) {
		
		for(File file : javaFilesTest) {
			try {
				readTestFile(file);
			} catch (FileNotFoundException e) {
				System.err.println("Datei existiert nicht");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * read TEST files - packageDeclaration - filename - amount of tests
	 * @param file
	 * @throws FileNotFoundException
	 */
	private static void readTestFile(File file) throws FileNotFoundException {
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
		
		for(MethodDeclaration md : methodDeclarations) {
			if(md.getAnnotations().isNonEmpty() && md.getAnnotations().toString().equals("[@Test]")) {
				System.out.println("TEST GEFUNDEN");
				String testname = md.getName().toString();
				System.out.println(testname);
				
				//TODO: Code analysieren und in Verbindung bringen mit Programmfluss
				//TODO DATENSTRUKTUR - TEST (name - ursprung - pfad - body)
				Optional<BlockStmt> code = md.getBody();
				System.out.println(code);
				
				readSourceCode();
				
			}
		}
	}
	
	private static void readSourceCode() {
		String pathOfSourceCode = "";
		CompilationUnit cu = StaticJavaParser.parse(pathOfSourceCode);
		
		//QUELLE FUER EXTERNE MODULE
		cu.getImports();
		System.out.println(cu.getImports());
		
	}
}