package com.project.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.project.model.IndexxModel;

@Repository
public interface IndexxRepository extends ElasticsearchRepository<IndexxModel, String> {

}
