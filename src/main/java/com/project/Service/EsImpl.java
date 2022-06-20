package com.project.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchGenerationException;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.FileModel;
import com.project.model.IndexxModel;
import com.project.myjparepository.MyPostgresRepo;
import com.project.repository.IndexxRepository;
import com.project.repository.ResponseDTO;

@Service
public class EsImpl implements EsService {

	static RestHighLevelClient client = new RestHighLevelClient(
			RestClient.builder(new HttpHost("localhost", 9200, "http")));

	Date date = new Date();
	Timestamp nowTime = new Timestamp(date.getTime());

	@Autowired
	SessionFactory session;
	@Autowired
	IndexxRepository indexxRepo;
	@Autowired
	MyPostgresRepo postgresRepo;
	@Autowired
	IndexxModel indexxModel;

	@Override
	public void UploadDocument(MultipartFile file, int id) throws FileNotFoundException, IOException {

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		System.out.println(fileName);

		FileInputStream fileInputStreamReader = new FileInputStream(fileName);
		byte[] bytes = new byte[(int) ((File) file).length()];

		fileInputStreamReader.read(bytes);

		String s = new String(bytes);

		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("id", id);
		jsonMap.put("Name", fileName);
		jsonMap.put("Content", s);
		try {
			IndexRequest request3 = new IndexRequest("uploadedfile");
			request3.source(jsonMap, XContentType.JSON);
			request3.opType(OpType.INDEX);
			request3.id("12");

			client.index(request3, RequestOptions.DEFAULT);

		} catch (ElasticsearchException | IOException e) {
		}

		fileInputStreamReader.close();
	}

	// getting all documents to display on home page
	@Override
	public SearchResponse getAllDocs() throws IOException {
		SearchRequest searchRequest = new SearchRequest("versionindex");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchRequest.source(searchSourceBuilder.size(100));
		return client.search(searchRequest, RequestOptions.DEFAULT);
//		OR
//		return indexxRepo.findAll();
	}

	/// searching a value to get highlighted snippets using dto *working and
	/// currently in use*
	@Override
	public List<ResponseDTO> searchValueHighlightsDto(String value) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<ResponseDTO> responseDTO = new ArrayList<>();

		SearchRequest searchRequest = new SearchRequest();// 1
		searchRequest.indices("versionindex");

		// creating match query
		MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder = new MatchPhrasePrefixQueryBuilder("Content",
				value);
		// creating searchSourceBuilder
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// creating HighlightBuilder
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// Create a field javaapi.client.highlighter for the Content field
		HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("Content");

		// set field javaapi.client.highlighter type
		highlightContent.highlighterType("unified");
		highlightContent.preTags("<b><em>");
		highlightContent.postTags("</em></b>");
		highlightBuilder.field(highlightContent);

		searchSourceBuilder.query(matchPhrasePrefixQueryBuilder);
		searchSourceBuilder.highlighter(highlightBuilder);
		searchRequest.source(searchSourceBuilder);

		SearchResponse result = client.search(searchRequest, RequestOptions.DEFAULT);

		for (SearchHit hit : result.getHits()) {
			ResponseDTO valueDTO = objectMapper.readValue(hit.getSourceAsString(), ResponseDTO.class);
			Map<String, HighlightField> highlightFieldsMap = hit.getHighlightFields();
			if (!highlightFieldsMap.isEmpty()) {
				for (Map.Entry<String, HighlightField> entry : highlightFieldsMap.entrySet()) {
					valueDTO.getHighlightsMap().put(entry.getKey(), Arrays.stream(entry.getValue().getFragments())
							.map(Text::toString).collect(Collectors.toList()));
				}
			}
			responseDTO.add(valueDTO);
		}
		return responseDTO;
	}

	// get document from db by name when clicking on view
	@Override
	public List<FileModel> getDocByName(String name) {
//		@SuppressWarnings("deprecation")
//		Criteria crit = session.openSession().createCriteria(FileModel.class);
//		crit.add(Restrictions.eq("docName", name));
//		return crit.list().iterator();
		List<FileModel> alldocs = postgresRepo.findAll();
		List<FileModel> sameNamedocs = new ArrayList<FileModel>();

		for (FileModel i : alldocs) {
			String dbDocName = i.getDocName();
			if (dbDocName.equals(name)) {
				sameNamedocs.add(i);
			} else {
				continue;
			}
		}
		return sameNamedocs;

	}

	@Override
	public Optional<FileModel> getFileById(String fileId) {
		return postgresRepo.findById(fileId);
	}

	// saving a new file *in use*
	@SuppressWarnings("resource")
	@Override
	public int storeDocuments(MultipartFile file) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		// to get the file name and remove the .extension and path from file name
//		 String extension = Files. getFileExtension(fileName);
//		String extPattern = "(?<!^)[.]" + (".*");
//		String noExtensionFileName = name.replaceAll(extPattern, "");
		UUID uuid = UUID.randomUUID();
		int count = 1;
		Date date = new Date();
		Timestamp nowTime = new Timestamp(date.getTime());

		Iterable<FileModel> allDoc = postgresRepo.findAll();
		for (FileModel i : allDoc) {
			String indexedFileName = i.getDocName();
			if (fileName.equals(indexedFileName)) {
				count++;
				System.out.println(count);
			} else {
				continue;
			}
		}
		if (count > 1) {
			System.out.println("File already exists");
			return count;
		} else {
//			System.out.println(extPattern+" This is ext pattern");
			File convFile = new File(file.getOriginalFilename());
//			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			FileInputStream fis = new FileInputStream(convFile.getAbsolutePath());
			XWPFDocument docx = new XWPFDocument(fis);
			System.out.println("convFile.getAbsolutePath() = " + convFile.getAbsolutePath());
			
			XWPFWordExtractor extractedFromDoc = new XWPFWordExtractor(docx);
				String s = extractedFromDoc.getText();
				
				try {
					FileModel fileModel = new FileModel();
					fileModel.setData(file.getBytes());

					fileModel.setFileUri(ServletUriComponentsBuilder.fromCurrentContextPath()
							.path(fileName).toUriString());

					fileModel.setId(uuid.toString());
					fileModel.setDocName(fileName);
					fileModel.setDocType(file.getContentType());
					fileModel.setVersion(1);
					fileModel.setTimestamp(nowTime.toString());
					fileModel.setContent(s);
					postgresRepo.save(fileModel);

					indexxModel.setId(uuid.toString());
					indexxModel.setContent(s);
					indexxModel.setName(fileName);
					indexxModel.setTimestamp(nowTime.toString());
					indexxModel.setVersion(1);
					indexxRepo.save(indexxModel);

				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return count;
	}

	// update existing file,, adding a new version of file
	@Override
	public int updateFile(MultipartFile file, String id) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		int count = 1;
		Date date = new Date();
		Timestamp nowTime = new Timestamp(date.getTime());
		String indexedFileName2 = "";

	
		FileModel fileById = postgresRepo.findById(id).get();
		String fileByIdName = fileById.getDocName();

		Iterable<FileModel> allDoc = postgresRepo.findAll();
		for (FileModel i : allDoc) {
			String indexedFileName = i.getDocName();
			if (fileName.equals(indexedFileName) && fileName.equals(fileByIdName)) {
				count++;
				indexedFileName2 = indexedFileName;
				System.out.println(count);
			} else {
				continue;
			}
		}

		System.out.println(indexedFileName2 + " This is indexed file name 2");
		if (count >= 1 && fileName.equals(indexedFileName2)) {
			File convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			FileInputStream fis = new FileInputStream(convFile.getAbsolutePath());
			XWPFDocument docx = new XWPFDocument(fis);
			System.out.println("convFile.getAbsolutePath() = " + convFile.getAbsolutePath());

			try (XWPFWordExtractor extractedFromDoc = new XWPFWordExtractor(docx)) {
				String s = extractedFromDoc.getText();
				FileModel fileModel = new FileModel();
				try {
					UUID uuid = UUID.randomUUID();
					fileModel.setData(file.getBytes());

					fileModel.setFileUri(ServletUriComponentsBuilder.fromCurrentContextPath()
							.path(fileName).toUriString());

					fileModel.setId(uuid.toString());
					fileModel.setDocName(fileName);
					fileModel.setDocType(file.getContentType());
					fileModel.setVersion(count);
					fileModel.setTimestamp(nowTime.toString());
					fileModel.setContent(s);
					postgresRepo.save(fileModel);

					indexxModel.setId(uuid.toString());
					indexxModel.setContent(s);
					indexxModel.setName(fileName);
					indexxModel.setTimestamp(nowTime.toString());
					indexxModel.setVersion(count);
					indexxRepo.save(indexxModel);
					indexxRepo.deleteById(id);

				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (ElasticsearchGenerationException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("cannot update a file with different name" + count);
			return count;
		}
		return count;
	}
	
	@Override
	public String bookmarkDoc(String id) {
		FileModel doc = postgresRepo.findById(id).get();
		doc.setBookmarked(true);
		return "done";
	}

	@Override
	public List<FileModel> getAllBookmarkedDoc() {
		List<FileModel> alldocs = postgresRepo.findAll();
		List<FileModel> bookmarkedDocs = new ArrayList<FileModel>();

		for (FileModel i : alldocs) {
			if(i.isBookmarked()) {
				bookmarkedDocs.add(i);
				System.out.println(i.getDocName()+" "+i.getVersion());
			}
		}
		return bookmarkedDocs;
	}
	
	@Override
	public String setDocBookmark(String id) {
		FileModel doc = postgresRepo.findById(id).get();
		doc.setBookmarked(!doc.isBookmarked());
		postgresRepo.save(doc);
		return "Bookmark status changed";
	}

}
