package com.project.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="fileTable")
public class FileModel {
	@Id
	private String id;
	
	private String docName;
	private String docType;
	private int version;
	
	@Lob
	private byte[] data;
	
	private String fileUri;
	private String timestamp;
	private boolean bookmarked = false;
	
	private String content;

	public FileModel(String id, String docName, String docType, byte[] data, int version, String fileUri, String timestamp, boolean bookmarked, String content) {
		super();
		this.id = id;
		this.docName = docName;
		this.docType = docType;
		this.data = data;
		this.version = version;
		this.fileUri = fileUri;
		this.timestamp = timestamp;
		this.bookmarked = bookmarked;
	}
	
	
	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public boolean isBookmarked() {
		return bookmarked;
	}

	public void setBookmarked(boolean bookmarked) {
		this.bookmarked = bookmarked;
	}


	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getFileUri() {
		return fileUri;
	}

	public void setFileUri(String fileUri) {
		this.fileUri = fileUri;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public FileModel() {
		super();
		// TODO Auto-generated constructor stub
	}	

}
