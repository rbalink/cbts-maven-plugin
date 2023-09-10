package com.tub;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ReadSourceCode {
	public static List<String> nonJavaFiles;
	public static List<File> effectedJavaFiles;
	public static List<File> unchangedJavaFiles;
	public static List<Dependency> pomDependencies;
	public static boolean wasPomChanged;

	/**
	 * code analysis
	 * 
	 * @param javaFilesMain
	 * @param testSet
	 */
	public static HashSet<TestWrapper> analyzeCode(String mode, List<File> javaFilesMain, HashSet<TestWrapper> testSet,
			List<DiffEntry> gitDiffList) {
		nonJavaFiles = new ArrayList<String>();
		effectedJavaFiles = new ArrayList<File>();
		unchangedJavaFiles = new ArrayList<File>();
		wasPomChanged = false;
		HashSet<TestWrapper> result = new HashSet<TestWrapper>();

		analyzePOMFile();

		if (mode.equals("classic")) {

			// compare gitDiff with relevant sourceCode
			// if equal main source code found - else non-main-java-code
			// on file level (important: there can be more tests per file)
			for (DiffEntry de : gitDiffList) {
				boolean found = false;
				Path diffPath = Paths.get(de.getNewPath());
				if (diffPath.toString().contains("pom.xml")) {
					wasPomChanged = true;
				}
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

			for (DiffEntry de : gitDiffList) {
				boolean found = false;
				Path diffPath = Paths.get(de.getNewPath());
				if (diffPath.toString().contains("pom.xml")) {
					wasPomChanged = true;
				}
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
			// filter out all changed objects from effectedJavaFiles
			for (File file : javaFilesMain) {
				boolean unchanged = true;
				for (File file2 : effectedJavaFiles) {
					if (file2.getPath().equals(file.getPath())) {
						unchanged = false;
						break;
					}
				}
				if (unchanged) {
					unchangedJavaFiles.add(file);
				}
			}

			// collect tests of unchangedJavaFiles
			for (File file : unchangedJavaFiles) {
				for (TestWrapper tw : testSet) {
					if (!tw.isUnableToMatchSourceCodePath()) {
						if (file.getPath().toString().equals(tw.getSourceCodePath().toString())) {
							result.add(tw);
						}
					}
				}
			}

			try {
				result = ReadMainFiles.readMainFiles(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			analyzeNonMainJavaFiles(nonJavaFiles);

			return result;
		} else {
			throw new IllegalArgumentException();
		}
	}

	// if POM ignore. check path of file
	public static void analyzeNonMainJavaFiles(List<String> nonJavaFiles) {
		if (nonJavaFiles != null) {
			for (String s : nonJavaFiles) {
				if (s.endsWith(".java")) {
					System.out.println("Voraussichtliche Testfälle wurden geändert :" + s);
				} else if (s.contains("config")) {
					System.out.println("Konfigurationsdatei wurde geändert: " + s);
				} else {
					// TODO: andere Dateien. prio: low
				}
			}
		}
	}

	/**
	 * get all POM dependencies
	 */
	public static void analyzePOMFile() {
		try {
			String pomPath = CBTSPrototypePlugin.srcFolder.replace("src", "pom.xml");
			System.out.println(pomPath);
			MavenXpp3Reader reader = new MavenXpp3Reader();
			File pomFile = new File(pomPath);
			Model model = reader.read(new FileReader(pomFile));

			pomDependencies = model.getDependencies();
			/*
			 * for (Dependency dependency : pomDependencies) {
			 * System.out.println("Group ID: " + dependency.getGroupId());
			 * System.out.println("Artifact ID: " + dependency.getArtifactId());
			 * System.out.println("Version: " + dependency.getVersion());
			 * System.out.println("-----------------------"); }
			 */
		} catch (IOException | XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}