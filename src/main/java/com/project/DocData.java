package com.project;

import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;

@Document(indexName= "indexx")
@Data
public class DocData {

	String typeOfDoc;
	String autherOfDoc;
	
	public DocData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DocData(String typeOfDoc, String autherOfDoc) {
		super();
		this.typeOfDoc = typeOfDoc;
		this.autherOfDoc = autherOfDoc;
	}
	//getter and setter
	public String getAutherOfDoc() {
		return autherOfDoc;
	}
	public void setAutherOfDoc(String autherOfDoc) {
		this.autherOfDoc = autherOfDoc;
	}
	public String getTypeOfDoc() {
		return typeOfDoc;
	}
	public void setTypeOfDoc(String typeOfDoc) {
		this.typeOfDoc = typeOfDoc;
	}
}
