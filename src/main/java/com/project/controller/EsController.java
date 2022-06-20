package com.project.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.Service.EsService;
import com.project.model.FileModel;
import com.project.repository.ResponseDTO;

@RestController
@CrossOrigin("*")
public class EsController {

	@Autowired
	private EsService esService;

	@Value("${project.file}")
	private String path;

	// sending a file
	@PostMapping(value = "/upload/{id}")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("id") int id)
			throws FileNotFoundException, IOException {
		this.esService.UploadDocument(file, id);
		return ResponseEntity.ok("file uploaded");
	}

	// Get All Documents working
	@GetMapping("/getByRepo")
	public SearchResponse getAllByRepo() throws IOException {
		return esService.getAllDocs();
	}

	// working searching a value to get highlighted snippets using dto *working and
	// currently in use*
	@GetMapping("/getHighlightedValue/{value}")
	public List<ResponseDTO> searchDto(@PathVariable String value) throws IOException {
		return esService.searchValueHighlightsDto(value);
	}

	@GetMapping("/getDocByName/{name}")
	public List<FileModel> getDocByName(@PathVariable String name) {
		System.out.println("called view");
		return esService.getDocByName(name);
	}
	
	
//	@GetMapping("/getDocById/{id}")
//	public List<FileModel> getDocById(@PathVariable String id) {
//		System.out.println("called view");
//		return esService.getFileById(id);
//	}

	// call for downloading a file
	@GetMapping("/downloadFile/{fileId}")
	public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileId) {
		System.out.println("got the file id in backend " + fileId);
		FileModel fileModel = esService.getFileById(fileId).get();
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileModel.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attatchment:filename-\"" + fileModel.getDocName() + "\"")
				.body(new ByteArrayResource(fileModel.getData()));
	}

	// call to upload a new file
	@PostMapping("/saveFile")
	public ResponseEntity<FileModel> saveFile(@RequestParam("file") MultipartFile file)
			throws FileNotFoundException, IOException {
		int status = esService.storeDocuments(file);
		if (status > 1) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// call for updating an existing file
	@PostMapping("/updateDoc/{id}")
	public ResponseEntity<FileModel> saveFile(@RequestParam("file") MultipartFile file, @PathVariable String id)
			throws IOException {
		int status = esService.updateFile(file, id);
		System.out.println("it is status "+status);
		if (status > 1) {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
	}
	
	
	@PostMapping("/setbookmark/{id}")
	public String setBookmarkOfDoc(@PathVariable String id) {
		return esService.setDocBookmark(id);
	}

	@GetMapping("/getbookmark")
	public List<FileModel> getBookmarkDocs() {
		return esService.getAllBookmarkedDoc();
	}
}
