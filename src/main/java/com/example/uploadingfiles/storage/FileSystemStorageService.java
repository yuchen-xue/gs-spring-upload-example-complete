package com.example.uploadingfiles.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.uploadingfiles.detection.DetectionBackend;

@Service
public class FileSystemStorageService implements StorageService {

	// Name of the directory that stores the original images.
	private final Path uploadLocation;
	// Name of the directory that stores the images with detection bounding boxes.
	private final Path resultLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
        
        if(properties.getUploadLocation().trim().length() == 0){
            throw new StorageException("File upload location can not be Empty."); 
        }
        
        if(properties.getResultLocation().trim().length() == 0){
            throw new StorageException("Result location can not be Empty."); 
        }

		this.uploadLocation = Paths.get(properties.getUploadLocation());
		this.resultLocation = Paths.get(properties.getResultLocation());
	}

	@Override
	public void store(MultipartFile file) {
		Path destinationFile = this.uploadLocation.resolve(
			Paths.get(file.getOriginalFilename()))
			.normalize().toAbsolutePath();
		Path resultFile = this.resultLocation.resolve(
			Paths.get(file.getOriginalFilename()))
			.normalize().toAbsolutePath();
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			if (!destinationFile.getParent().equals(this.uploadLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
		// Perform obejct detection on the the uploaded image
		DetectionBackend.main(destinationFile.toString(), resultFile.toString());
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.resultLocation, 1)
				.filter(path -> !path.equals(this.resultLocation))
				.map(this.resultLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return resultLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(uploadLocation.toFile());
		FileSystemUtils.deleteRecursively(resultLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(uploadLocation);
			Files.createDirectories(resultLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
