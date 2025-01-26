package com.example.uploadingfiles.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder uploadLocation for storing files
	 */
	private String uploadLocation = "upload-dir";
	private String resultLocation = "result-dir";

	public String getUploadLocation() {
		return uploadLocation;
	}

	public void setUploadLocation(String uploadLocation) {
		this.uploadLocation = uploadLocation;
	}

	public String getResultLocation() {
		return resultLocation;
	}

	public void setResultLocation(String resultLocation) {
		this.resultLocation = resultLocation;
	}
}
