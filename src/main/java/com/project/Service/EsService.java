package com.project.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.SearchResponse;
import org.springframework.web.multipart.MultipartFile;

import com.project.model.FileModel;
import com.project.repository.ResponseDTO;

public interface EsService {

	public void UploadDocument(MultipartFile file, int id) throws FileNotFoundException, IOException;

	public SearchResponse getAllDocs() throws IOException;

	public List<ResponseDTO> searchValueHighlightsDto(String value) throws IOException;

	List<FileModel> getDocByName(String name);

	Optional<FileModel> getFileById(String fileId);

	int storeDocuments(MultipartFile file) throws FileNotFoundException, IOException;

	int updateFile(MultipartFile file, String id) throws IOException;

	List<FileModel> getAllBookmarkedDoc();

	String bookmarkDoc(String id);

	String setDocBookmark(String id);

}
