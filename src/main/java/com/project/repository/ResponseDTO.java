package com.project.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

  @JsonProperty("Name")
  private String name;
  @JsonProperty("Content")
  private String content;
  @JsonProperty("timestamp")
  private String timestamp;
  @JsonProperty("id")
  private String id;
  @JsonProperty("version")
  private int version;
  
  @Builder.Default
  private Map<String, List<String>> highlightsMap = new HashMap<>();

public Map<String, List<String>> getHighlightsMap() {
	return highlightsMap;
}

public void setHighlightsMap(Map<String, List<String>> highlightsMap) {
	this.highlightsMap = highlightsMap;
}

}