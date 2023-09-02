package com.tub;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ReadSourceCode {
	public static List<String> nonJavaFiles;
	public static List<File> effectedJavaFiles;

	/**
	 * code analysis - CHA, CFG, CDG
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
			analyzePOMFile();

			// compare gitDiff with relevant sourceCode
			// if equal main source code found - else non-main-java-code
			// on file level (important: there can be more tests per file)
			for (DiffEntry de : gitDiffList) {
				boolean found = false;
				Path diffPath = Paths.get(de.getNewPath());
				// System.out.println("PATHS-DIFF: " + Paths.get(de.getNewPath()));
				for (int i = 0; i < javaFilesMain.size(); i++) {
					Path entryPath = Paths.get(javaFilesMain.get(i).getPath());
					if (entryPath.toString().contains(diffPath.toString())) {
						found = true;
						effectedJavaFiles.add(javaFilesMain.get(i));
						break;
					}
				}
				if (!found) {
					nonJavaFiles.add(de.getNewPath());
				}
			}
			// filter all relevant objects from testSet
			for (File file : effectedJavaFiles) {
				for (TestWrapper tw : testSet) {
					if (!tw.isUnableToMatchSourceCodePath()) {
						if (file.getPath().toString().equals(tw.getSourceCodePath().toString())) {
							result.add(tw);
						}
					}
				}
			}
			analyzeNonMainJavaFiles(nonJavaFiles);

			return result;

		} else if (mode.equals("sideeffect")) {
			analyzePOMFile();

			// externe Imports

			// TODO CHA

			// TODO CFG

			// TODO CDG

			// TODO
			analyzeNonMainJavaFiles(null);

			return null;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static void analyzeNonMainJavaFiles(List<String> nonJavaFiles) {
		for (String s : nonJavaFiles) {
			if (s.endsWith(".java")) {
				System.out.println("tesf#lle wurden ge#ndert :"+s);
			} else {
				// TODO: was mit anderen Dateien. Ignorieren? prio: low
			}
		}
	}

	public static void analyzePOMFile() {
		String pomPath = CBTSPrototypePlugin.srcFolder.replace("src", "pom.xml");
		System.out.println(pomPath);
		// XML Parser
		// genauen Änderungen -- welche Dependency
	}

	public static void analyzeExternalImports() {
		// CompilationUnit cu = StaticJavaParser.parse(file);
		// TODO: IMPORTANT BEFORE TAG !
		// QUELLE FUER EXTERNE MODULE
		// cu.getImports();
		// System.out.println(cu.getImports());
	}
}