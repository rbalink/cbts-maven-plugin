package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ReadSourceCode{
	
	public static void analyzeCode(List<File> javaFilesMain, HashSet<TestWrapper> testSet) {
		for (File file : javaFilesMain) {
			try {
				readMainFile(file, testSet);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//source code path ermitteln
		
		//datei auslesen und auf dateiebene cfg erstellen
		
		
		//externe imports analysieren
		
		//pom analysieren
		
	}
	
	public static void readMainFile(File file, HashSet<TestWrapper> testSet) throws FileNotFoundException {
		for(TestWrapper tw : testSet) {
			System.out.println(tw.absolutPath);
			System.out.println(file.getAbsolutePath());
			System.out.println(file.getPath());
		};
		CompilationUnit cu = StaticJavaParser.parse(file);

		// QUELLE FUER EXTERNE MODULE
		cu.getImports();
		System.out.println(cu.getImports());
		
	}
	
	public static void buildCFG() {
		
	}
	
	public static void analyzeExternalImports() {
		
	}
	
	public static void analyzePOMFile() {
		
	}
}