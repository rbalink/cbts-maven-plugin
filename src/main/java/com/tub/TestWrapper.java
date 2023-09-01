package com.tub;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;

public class TestWrapper {

	public SimpleName name;
	public Optional<PackageDeclaration> path;
	public Path sourceCodePath;
	public Path testCodePath;
	public String fileNameTestCode;
	public Optional<BlockStmt> content;
	public int probability;
	public String cfg;
	public boolean sideeffectedCode;
	public boolean unableToMatchSourceCodePath;

	public TestWrapper(SimpleName simpleName, Optional<PackageDeclaration> packageDeclaration,
			Optional<BlockStmt> optional, String fileName) {
		this.name = simpleName;
		this.path = packageDeclaration;
		this.content = optional;
		this.fileNameTestCode = fileName;
		buildPath();
	}

	public SimpleName getName() {
		return name;
	}

	public void setName(SimpleName name) {
		this.name = name;
	}

	public Optional<PackageDeclaration> getPath() {
		return path;
	}

	public void setPath(Optional<PackageDeclaration> path) {
		this.path = path;
	}

	public Optional<BlockStmt> getContent() {
		return content;
	}

	public void setContent(Optional<BlockStmt> content) {
		this.content = content;
	}

	public String toString() {
		return "\n--- TESTNAME: " + this.name.asString() + " \n--- PATH: " + this.path.toString() + " \n--- CODE: "
				+ this.content.toString();
	}

	/**
	 * find corresponding sourceCode to TestCode (MATCHING)
	 * Gedanken: sourceCode ist verändert aber TestCode nicht
	 * Aber jeder TestCode muss ein SourceCode haben - Ein "Source Code" kann mehrere "TestCode" haben
	 */
	private void buildPath() {
		try {
			String subfolder = this.path.toString().split("\\s+")[1].replace(".", "\\").replace(";", "");
			String fileNameRaw = this.fileNameTestCode.split(".java")[0];
			String newRawName = "";
			if (fileNameRaw.contains("IT")) {
				newRawName = fileNameRaw.replace("IT", "View.java");
			} else if (fileNameRaw.contains("Test")) {
				newRawName = fileNameRaw.replace("Test", ".java");
			} else {
				System.err.println("KEIN KORRESPONDIERENDER TEST");
			}

			
			sourceCodePath = Paths.get(CBTSPrototypePlugin.srcFolder, "main", "java", subfolder, newRawName);
			if(!Files.exists(sourceCodePath)) {
				sourceCodePath = null;
				this.unableToMatchSourceCodePath = true;
			}else {
				this.unableToMatchSourceCodePath = false;
			}
			
			testCodePath = Paths.get(CBTSPrototypePlugin.srcFolder, "test", "java", subfolder, this.fileNameTestCode);
			if(!Files.exists(testCodePath)) {
				throw new IllegalArgumentException();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("BUILD PATH ERROR");
		}

	}

}
