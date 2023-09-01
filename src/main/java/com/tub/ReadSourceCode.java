package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ReadSourceCode{
	
	/**
	 * all existing files in testset (Datei-Ebene)
	 * @param javaFilesMain
	 * @param testSet
	 */
	public static void analyzeCode(List<File> javaFilesMain, HashSet<TestWrapper> testSet) {
		for (File file : javaFilesMain) {
			try {
				readMainFile(file, testSet);
				
				
				buildCFG(file);
				
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
	
	/**
	 * per testfile unterschiedliche Tests (Quellcode einer Datei - mehrere Tests evtl)
	 * @param file
	 * @param testSet
	 * @throws FileNotFoundException
	 */
	public static void readMainFile(File file, HashSet<TestWrapper> testSet) throws FileNotFoundException {
		for(TestWrapper tw : testSet) {
			//Inhalt für Quellcode pro Test
			System.out.println("CORRESPONDING PATH: "+tw.absolutPathSourceCode);
			System.out.println("TESTCODE PATH: "+tw.fileNameTestCode);
			//System.out.println(file.getAbsolutePath());
			//System.out.println(file.getPath());
		};
		CompilationUnit cu = StaticJavaParser.parse(file);

		// TODO: IMPORTANT BEFORE TAG !
		
		
		// QUELLE FUER EXTERNE MODULE
		cu.getImports();
		System.out.println(cu.getImports());
		
	}
	
	/**
	 * korrespondierender source code for testset - build CFG for java source code
	 */
	public static void buildCFG(File file) {
		
		
	}
	
	public static void analyzeExternalImports() {
		
	}
	
	public static void analyzePOMFile() {
		
	}
}