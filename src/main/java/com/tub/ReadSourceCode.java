package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.diff.DiffEntry;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ReadSourceCode {
	public static List<String> nonJavaFiles;
	public static List<File> effectedJavaFiles;

	/**
	 * all existing files in testset (Datei-Ebene)
	 * 
	 * @param javaFilesMain
	 * @param testSet
	 */
	public static HashSet<TestWrapper> analyzeCode(String mode, List<File> javaFilesMain, HashSet<TestWrapper> testSet,
			List<DiffEntry> gitDiffList) {
		nonJavaFiles = new ArrayList<String>();
		effectedJavaFiles = new ArrayList<File>();
		HashSet<TestWrapper> result = new HashSet<TestWrapper>();

		if (mode.equals("classic")) {

			// compare gitDiff with relevant sourceCode
			// if equal main source code found - else non-main-java-code
			for (DiffEntry de : gitDiffList) {
				boolean found = false;
				Path diffPath = Paths.get(de.getNewPath());
				System.out.println("PATHS-DIFF: " + Paths.get(de.getNewPath()));
				for (int i = 0; i < javaFilesMain.size(); i++) {

					Path entryPath = Paths.get(javaFilesMain.get(i).getPath());
					if (entryPath.toString().contains(diffPath.toString())) {
						//System.out.println("DIFF: " + diffPath.toString());
						//System.out.println("ENTR:" + entryPath.toString());
						found = true;
						effectedJavaFiles.add(javaFilesMain.get(i));
						// auflisten DAZUGEHÖRIGER TEST (weil 1 File kann mehrere Tests haben!!)
						break;

					}

					// System.out.println("FILE :"+Paths.get(javaFilesMain.get(i).getPath()));
				}
				if (!found) {
					nonJavaFiles.add(de.getNewPath());
					// NICHT-JAVA DATEI - ANDERER EINTRAG! ODER TESTDATEI IST VERÄNDERT
				}
			}

			for (File file : effectedJavaFiles) {
				for (TestWrapper tw : testSet) {
					if (!tw.isUnableToMatchSourceCodePath()) {
						if (file.getPath().toString().equals(tw.getSourceCodePath().toString())) {
							result.add(tw);
						}
					}

				}

				/**
				 * TestWrapper tw = testSet .stream() .filter(e ->
				 * e.testCodePath.toString().equals(file.getPath().toString())) .findFirst()
				 * .orElse(null); System.out.println(tw.toString());
				 **/
			}

			// analyze gitDiffList that was not in list

			
			return result;
			
		} else if (mode.equals("sideeffect")) {
			
			//TODO ALGO

			return null;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public static void analyzeNonMainJavaCode() {
		
	}
	
	public static void analyzePOMFile() {
		
	}
	

		// TODO: check ob relevant!!!
/**
		for (File file : javaFilesMain) {
			try {
				readMainFile(file, testSet);

				buildCFG(file);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
		// source code path ermitteln

		// datei auslesen und auf dateiebene cfg erstellen

		// externe imports analysieren

		// pom analysieren

	}
	**/	

	/**
	 * per testfile unterschiedliche Tests (Quellcode einer Datei - mehrere Tests
	 * evtl)
	 * 
	 * @param file
	 * @param testSet
	 * @throws FileNotFoundException
	 */
	public static void readMainFile(File file, HashSet<TestWrapper> testSet) throws FileNotFoundException {

		for (TestWrapper tw : testSet) {
			// Inhalt für Quellcode pro Test
			// System.out.println("CORRESPONDING PATH: "+tw.absolutPathSourceCode);
			// System.out.println("TESTCODE PATH: "+tw.fileNameTestCode);
			// System.out.println(file.getAbsolutePath());
			// System.out.println(file.getPath());
		}
		;
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
}