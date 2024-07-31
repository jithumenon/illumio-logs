package com.jmenon.illumio;

import com.jmenon.illumio.service.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlowLogsApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlowLogsApplication.class);

	@Autowired
	ReportGenerator reportGenerator;

	public static void main(String[] args) {
		SpringApplication.run(FlowLogsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reportGenerator.generateReport();
	}
}
