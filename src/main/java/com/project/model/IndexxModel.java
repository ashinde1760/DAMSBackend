package com.project.model;

import javax.persistence.Column;
import javax.persistence.Table;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Component;

import lombok.Data;

@Document(indexName="versionindex", shards = 1, createIndex = true)
@Data
@Component
@Table(name="docstable")
public class IndexxModel {
	@Id
	@Column(name="id")  
	private String id;
	@Column(name="content")
	private String Content;
	@Column(name="name")
	private String Name;
	@Column(name="timestamp")
	private String timestamp;
	@Column(name="version")
	private int version;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public IndexxModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public IndexxModel(String id, String content, String name, String timestamp, int version) {
		super();
		this.id = id;
		Content = content;
		Name = name;
		this.timestamp = timestamp;
		this.version = version;
	}

	
}
