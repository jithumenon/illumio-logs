package com.jmenon.illumio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class InputConfig {

    public static final String LOOKUP_TABLE_ARG = "lookup-table";
    public static final String LOG_FILE_ARG = "log-file";
    public static final String OUTPUT_FILE_ARG = "output-file";


    private final Environment environment;

    public InputConfig(Environment environment) {
        this.environment = environment;
    }

    public String getPathToLookupTable() {
        return environment.getProperty(LOOKUP_TABLE_ARG, "");
    }

    public String getPathToFlowLogs() {
        return environment.getProperty(LOG_FILE_ARG, "");
    }

    public String getPathToOutputFile() {
        return environment.getProperty(OUTPUT_FILE_ARG, "analysis_report");
    }
}
