package com.example.uploadingfiles.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

import com.example.uploadingfiles.db.SingleResult;

public interface StorageService {

	void init();

	void store(MultipartFile file);

	Stream<Path> loadAll();

	Path load(String filename);

	Iterable<SingleResult> getAllResults();

	Resource loadAsResource(String filename);

	void deleteAll();

}
