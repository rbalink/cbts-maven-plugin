package com.tub;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ReadSourceCode{
	public static List<String> nonJavaFiles;
	public static List<File> effectedJavaFiles;
	
	/**
	 * all existing files in testset (Datei-Ebene)
	 * @param javaFilesMain
	 * @param testSet
	 */
	public static void analyzeCode(String mode, List<File> javaFilesMain, HashSet<TestWrapper> testSet, List<DiffEntry> gitDiffList) {
		nonJavaFiles = new ArrayList<String>();
		effectedJavaFiles = new ArrayList<File>();
		
		if(mode.equals("classic")) {
			
			for(DiffEntry de : gitDiffList) {
				boolean found = false;
				Path diffPath = Paths.get(de.getNewPath());
				System.out.println("PATHS-DIFF: "+Paths.get(de.getNewPath()));
				for(int i = 0; i<javaFilesMain.size();i++) {
					
					Path entryPath = Paths.get(javaFilesMain.get(i).getPath());
					if(entryPath.toString().contains(diffPath.toString())){
						System.out.println("TEST");
						System.out.println("DIFF: "+diffPath.toString());
						System.out.println("ENTR:"+entryPath.toString());
						found = true;
						effectedJavaFiles.add(javaFilesMain.get(i));
						//auflisten DAZUGEHÖRIGER TEST (weil 1 File kann mehrere Tests haben!!)
						break;
						
					}
					
					
					
					
					//System.out.println("FILE :"+Paths.get(javaFilesMain.get(i).getPath()));
				}
				if(!found) {
					nonJavaFiles.add(de.getNewPath());
					// NICHT-JAVA DATEI - ANDERER EINTRAG!
				}
				
				
				
				
				//for(File file : javaFilesMain) {
				//}
				//TODO: if Eintrag ist in javaFilesMain enthalten --> dazugehöriger Test muss MARKIERT WERDEN
				// else - markiere Datei in einer anderen List (eine NICHT java File!!)
			}
			
			//TODO: effectedJavaFiles mappen mit testSet -- danach nonJavaFiles analyse
			effectedJavaFiles.toString();
			
			for(File file : effectedJavaFiles) {
				try {
					readMainFile(file, testSet);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			System.out.println("Ende Loop");
			// filter out relevant javaFilesMain from gitDiffList
			
			// print out all tests which are in relation with javaFilesMain 
			
			// analyze gitDiffList that was not in list
			
			
		}else if(mode.equals("sideeffect")) {
			
		}else {
			throw new IllegalArgumentException();
		}
		
		//TODO: check ob relevant!!!
		
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
			//System.out.println("CORRESPONDING PATH: "+tw.absolutPathSourceCode);
			//System.out.println("TESTCODE PATH: "+tw.fileNameTestCode);
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