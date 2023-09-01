package com.tub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

//lifecycle phase in maven
@Mojo(name="cbts_prototype", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CBTSPrototypePlugin extends AbstractMojo {	
	public final String mainRepo = "https://github.com/rbalink/CBTS_Test";
	public final static String localPathMainRepo = "C:\\Users\\rob80186\\Documents\\GitHub\\flow";
	public List<File> javaFilesMain;
	public List<File> javaFilesTest;
	public static HashSet<TestWrapper> testSet;
	public static String srcFolder;
	public static List<DiffEntry> gitDiffList;
	
	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("*** cbts prototype BEGIN ***");
		long start = System.currentTimeMillis();
		long start2 = System.nanoTime();
		// + + + Step 1 - setting up paths and initiate objects
		String currentPath = System.getProperty("user.dir");
		File directoryPath = new File(currentPath);
		//TODO: FLAG
		gitDiffList = new ArrayList<DiffEntry>();
		testSet = new HashSet<TestWrapper>();
		
		// + + + Step 2 - get delta between current and main git branch
		getGitDiff();
		
	    
		
	    // + + + Step 3 - find src dir in current branch
		srcFolder = findFolder(directoryPath);
		if(srcFolder == null) {
			getLog().error("Kein Source Ordner!");
		}
		findJavaFilesInTestAndMain(srcFolder);
		
		
		// + + + Step 4 - reads all .java files for test and source code
		ReadTestFiles.collectTestFiles(javaFilesTest, testSet);
		// + + + Step 5 - analyze source code

		//TODO: if else flag
		ReadSourceCode.analyzeCode(javaFilesMain, testSet);
		
	    //gedanken: für die Klassen einen Graphen
	    // in den tests die Tests zählen und kategorisieren = vergleich zu klassen
	    // ermitteln was neue änderungen sind mit ALTEM stand !

		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;

		long finish2 = System.nanoTime();
		long timeElapsed2 = finish2 - start2;
		getLog().info("*** cbts prototype ENDS *** Time X Elapsed: "+timeElapsed+" ms --- NanoTime:"+timeElapsed2);
		
	}
	
	
	/**
	 * find all .java files in main and test folder
	 * @param srcFolder
	 */
	private void findJavaFilesInTestAndMain(String srcFolder) {
		String mainFolder = srcFolder + "\\main";
		String testFolder = srcFolder + "\\test";
		if(new File(mainFolder).exists()) {
			System.out.println("+++ MAIN FOLDER "+mainFolder);
			javaFilesMain = findJavaFiles(new File(mainFolder));
		}
	    // test für die Tests
		if(new File(testFolder).exists()) {
			System.out.println("+++ TEST FOLDER "+testFolder);
			javaFilesTest = findJavaFiles(new File(testFolder));
		}
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
	public static List<File> findJavaFiles(File directory){
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
	
	/**
	 * calculates the delta between current and main branch, saves diffentries in gitDiffList
	 */
	private static void getGitDiff() {
		//localPathMainRepo muss hinterlegt werden von Beginn an
		try {
			Git git;
			//git = Git.init().call();
			File f = new File(localPathMainRepo);
			git = Git.init().setDirectory(f).call();
			System.out.println("+++ CURRENT BRANCH DIR: "+git.getRepository()+ " +++ BRANCH: "+git.getRepository().getBranch()+" +++ STATE: "+ git.getRepository().getRepositoryState());
			//System.out.println(git.getRepository().getFullBranch()); //full branch name
			//System.out.println(head.getName());
			//System.out.println(previousHead.getName());
			
			Repository repo = git.getRepository();
			
			ObjectId head = repo.resolve("HEAD^{tree}");
			ObjectId previousHead = repo.resolve("HEAD~^{tree}");
			ObjectReader reader = repo.newObjectReader();
			
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, previousHead);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, head);
			
			List<DiffEntry> listDiffs = git.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();
			// Simply display the diff between the two commits
			System.out.println("+++ GIT DIFF START +++");
			for (DiffEntry diff : listDiffs) {
			        System.out.println(diff);
			        System.out.println(diff.getNewPath());
			        gitDiffList.add(diff);
			}
			System.out.println("+++ GIT DIFF END +++");
			
			//PRINTS OUT FILE PER FILE DIFFERENCE
			//getGitDiffContent(listDiffs, repo);
			
			
			
		} catch (IOException | GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * prints out file differences (content of file)
	 * @param listDiffs
	 * @param repo
	 * @throws IOException
	 */
	private static void getGitDiffContent(List<DiffEntry> listDiffs, Repository repo) throws IOException {
		// Display a formatted diff between the two commits
					for (DiffEntry diff : listDiffs) {
						System.out.println(diff);
						DiffFormatter formatter = new DiffFormatter(System.out);
						formatter.setRepository(repo);
						formatter.format(diff);
					}
					
	}
}