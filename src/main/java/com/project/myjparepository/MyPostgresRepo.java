package com.project.myjparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.model.FileModel;


@Repository
public interface MyPostgresRepo extends JpaRepository<FileModel, String>{

}
