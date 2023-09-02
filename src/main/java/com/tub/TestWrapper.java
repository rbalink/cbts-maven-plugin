package com.tub;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;

public class TestWrapper {

	private SimpleName name;
	private Optional<PackageDeclaration> path;
	private Path sourceCodePath;
	private Path testCodePath;
	private String fileNameTestCode;
	private Optional<BlockStmt> content;
	private int probability;
	private String cfg;
	private boolean sideeffectedCode;
	private boolean unableToMatchSourceCodePath;

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

	public Path getSourceCodePath() {
		return sourceCodePath;
	}

	public void setSourceCodePath(Path sourceCodePath) {
		this.sourceCodePath = sourceCodePath;
	}

	public Path getTestCodePath() {
		return testCodePath;
	}

	public void setTestCodePath(Path testCodePath) {
		this.testCodePath = testCodePath;
	}

	public String getFileNameTestCode() {
		return fileNameTestCode;
	}

	public void setFileNameTestCode(String fileNameTestCode) {
		this.fileNameTestCode = fileNameTestCode;
	}

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}

	public String getCfg() {
		return cfg;
	}

	public void setCfg(String cfg) {
		this.cfg = cfg;
	}

	public boolean isSideeffectedCode() {
		return sideeffectedCode;
	}

	public void setSideeffectedCode(boolean sideeffectedCode) {
		this.sideeffectedCode = sideeffectedCode;
	}

	public boolean isUnableToMatchSourceCodePath() {
		return unableToMatchSourceCodePath;
	}

	public void setUnableToMatchSourceCodePath(boolean unableToMatchSourceCodePath) {
		this.unableToMatchSourceCodePath = unableToMatchSourceCodePath;
	}

	/**
	 * find corresponding sourceCode to TestCode (MATCHING) Gedanken: sourceCode ist
	 * verändert aber TestCode nicht Aber jeder TestCode muss ein SourceCode haben -
	 * Ein "Source Code" kann mehrere "TestCode" haben
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
				newRawName = "ErrorX1";
			}

			sourceCodePath = Paths.get(CBTSPrototypePlugin.srcFolder, "main", "java", subfolder, newRawName);
			if (!Files.exists(sourceCodePath)) {
				sourceCodePath = null;
				this.unableToMatchSourceCodePath = true;
			} else {
				this.unableToMatchSourceCodePath = false;
			}

			testCodePath = Paths.get(CBTSPrototypePlugin.srcFolder, "test", "java", subfolder, this.fileNameTestCode);
			if (!Files.exists(testCodePath)) {
				throw new IllegalArgumentException();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("BUILD PATH ERROR");
		}

	}
	
	public String getInfoText() {
		return "Test: "+this.name.asString()+" --- filename: "+this.fileNameTestCode+" source code path: "+ this.getSourceCodePath();
	}

}
