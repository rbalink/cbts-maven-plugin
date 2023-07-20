package com.tub;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

//lifecycle phase in maven
@Mojo(name="cbts_prototype", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CBTSPrototypePlugin extends AbstractMojo {	
	public final String mainRepo = "https://github.com/rbalink/CBTS_Test";
	public final String localPathMainRepo = "C:\\Users\\rob80186\\Desktop\\gittest";
	public List<File> javaFilesMain;
	public List<File> javaFilesTest;
	
	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		getLog().info("*** cbts prototype BEGIN ***");
		
		System.out.println("TEST");
		String currentPath = System.getProperty("user.dir");
		File directoryPath = new File(currentPath);
		
		
//		String test = "test";
//		byte[] bytes = test.getBytes();
//		System.out.println(bytes);
		
	    //List of all files and directories
	   
	    
	    
	    // Reihenfolge: 
	    // src / main 
	    // src / test
		
	    // gehe Baumstruktur ab --- finde src Ordner
		String srcFolder = findFolder(directoryPath);
		if(srcFolder == null) {
			getLog().error("Kein Source Ordner!");
		}
	    // main für die Klassen
		String mainFolder = srcFolder + "\\main";
		String testFolder = srcFolder + "\\test";
		if(new File(mainFolder).exists()) {
			System.out.println("MAIN FOLDER "+mainFolder);
			javaFilesMain = findJavaFiles(new File(mainFolder));
		}
	    // test für die Tests
		if(new File(testFolder).exists()) {
			System.out.println("TEST FOLDER "+testFolder);
			javaFilesTest = findJavaFiles(new File(testFolder));
		}
		
		
		ReadTestFiles.collectFiles(javaFilesTest);
		
	    //gedanken: für die Klassen einen Graphen
	    // in den tests die Tests zählen und kategorisieren = vergleich zu klassen
	    // ermitteln was neue änderungen sind mit ALTEM stand !
		
		
		//TODO DELTA ERMITTELN VON ALTEN UND NEU
		getLog().info("*** cbts prototype ENDS ***");
		
	}
	
	/**
	 * check if src folder is in folder structure
	 * breadth-first search algorithm
	 * @param directoryPath
	 * @return
	 */
	private static String findFolder(File directoryPath) {
		Queue<File> queue = new LinkedList<File>();
		queue.offer(directoryPath);

		while (!queue.isEmpty()) {
			File currentDir = queue.poll();
			File[] files = currentDir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						if (file.getName().equals("src")) {
							if (containsPomXml(files)) {
								System.out.println(file.getAbsolutePath());
								return file.getAbsolutePath();
							}
						}
						queue.offer(file);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * check if pom.xml file is in current directory
	 * @param files
	 * @return
	 */
	private static boolean containsPomXml(File[] files) {
		if(files!=null) {
			for(File file : files) {
				if(file.getName().equals("pom.xml")) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * find all .java files in directory
	 * @param directory
	 * @return
	 */
	private static List<File> findJavaFiles(File directory){
		List<File> javaFiles = new ArrayList<File>();
		File[] files = directory.listFiles();
		if(files != null) {
			for (File file : files) {
				if(file.isDirectory()) {
					javaFiles.addAll(findJavaFiles(file));
				}else if(file.getName().endsWith(".java")) {
					javaFiles.add(file);
				}
			}
		}
		return javaFiles;
	}
}