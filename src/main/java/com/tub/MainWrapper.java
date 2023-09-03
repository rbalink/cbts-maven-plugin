package com.tub;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;

public class MainWrapper {

	private SimpleName name;
	private Optional<PackageDeclaration> path;
	private String fileNameTestCode;
	private Optional<BlockStmt> content;
	private NodeList<ImportDeclaration> importList;
	private List<Name> importListeName;
	private CompilationUnit cu;

	public MainWrapper(SimpleName simpleName, Optional<PackageDeclaration> packageDeclaration,
			Optional<BlockStmt> optional, String fileName, NodeList<ImportDeclaration> importList, CompilationUnit cu) {
		this.name = simpleName;
		this.path = packageDeclaration;
		this.content = optional;
		this.fileNameTestCode = fileName;
		this.importList = importList;
		this.cu = cu;
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

	public String getFileNameTestCode() {
		return fileNameTestCode;
	}

	public void setFileNameTestCode(String fileNameTestCode) {
		this.fileNameTestCode = fileNameTestCode;
	}

	public Optional<BlockStmt> getContent() {
		return content;
	}

	public void setContent(Optional<BlockStmt> content) {
		this.content = content;
	}

	public NodeList<ImportDeclaration> getImportList() {
		return importList;
	}

	public void setImportList(NodeList<ImportDeclaration> importList) {
		this.importList = importList;
	}

	public CompilationUnit getCu() {
		return cu;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	public List<Name> getImportListeName() {
		return importListeName;
	}

	public void setImportListeName(List<Name> importListeName) {
		this.importListeName = importListeName;
	}
	
	

}
