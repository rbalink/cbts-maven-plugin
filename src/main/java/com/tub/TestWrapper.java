package com.tub;

import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;

public class TestWrapper {

	public SimpleName name;
	public Optional<PackageDeclaration> path;
	public String absolutPathSourceCode;
	public String absolutePathTestCode;
	public String fileNameTestCode;
	public Optional<BlockStmt> content;
	public int probability;
	public String cfg;
	public boolean sideeffectedCode;

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
	 * IMPORTANT: HARDCODE SECTION
	 */
	private void buildPath() {
		try {
			String currentPath = CBTSPrototypePlugin.srcFolder + "\\main\\java\\";
			String currentPathTestcode = CBTSPrototypePlugin.srcFolder + "\\test\\java\\";
			String subfolder = this.path.toString().split("\\s+")[1].replace(".", "\\").replace(";", "");
			System.out.println(subfolder);
			System.out.println(currentPath + subfolder);
			this.absolutPathSourceCode = currentPath + subfolder;
			this.absolutePathTestCode = currentPathTestcode + subfolder;
			
			String fileNameRaw = this.fileNameTestCode.split(".java")[0];
			if(fileNameRaw.contains("IT")) {
				fileNameRaw.replace("IT", "View.java");
			}else if(fileNameRaw.contains("Test")) {
				fileNameRaw.replace("Test", ".java");
			}else {
				System.err.println("KEIN KORRESPONDIERENDER TEST");
			}
			System.out.println("CORRESPONDING FILENAME: "+fileNameRaw);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("BUILD PATH ERROR");
		}

	}

}
