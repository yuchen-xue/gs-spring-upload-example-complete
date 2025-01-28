package com.example.uploadingfiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationContext;

import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.op.Ops;

import com.example.uploadingfiles.storage.StorageProperties;
import com.example.uploadingfiles.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class UploadingFilesApplication {

    @Autowired
    private ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(UploadingFilesApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	@Bean(name = "model")
	SavedModelBundle loadDetectionModel() {
		// get path to model folder
		String modelPath = ClassLoader.getSystemResource("detection-models/ssd_mobilenet_v2_fpnlite_320x320-saved_model").getPath();
		// load saved model
		return SavedModelBundle.load(modelPath, "serve");
	}

	// TF computing things
	@Bean(name = "graph")
	Graph getGraph() {
		return new Graph();
	}

	@Bean(name = "tf")
	Ops getOps() {
		return Ops.create(context.getBean("graph", Graph.class));
	}

}
