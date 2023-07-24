package com.tub;

import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;

public class TestWrapper {

	public SimpleName name;
	public Optional<PackageDeclaration> path;
	public String absolutPath;
	public Optional<BlockStmt> content;
	public int probability;
	public String cfg;
	public boolean sideeffectedCode;

	public TestWrapper(SimpleName simpleName, Optional<PackageDeclaration> packageDeclaration,
			Optional<BlockStmt> optional) {
		this.name = simpleName;
		this.path = packageDeclaration;
		this.content = optional;
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
	
	private void buildPath() {
		try {
		String currentPath = CBTSPrototypePlugin.srcFolder+"\\main\\java\\";
		String subfolder = this.path.toString().split("\\s+")[1].replace(".", "\\").replace(";", "");
		System.out.println(subfolder);
		System.out.println(currentPath+subfolder);
		this.absolutPath = currentPath+subfolder;
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("BUILD PATH ERROR");
		}
		
	}

}
