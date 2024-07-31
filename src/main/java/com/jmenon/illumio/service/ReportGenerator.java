package com.jmenon.illumio.service;

import com.jmenon.illumio.config.InputConfig;
import org.springframework.stereotype.Service;

@Service
public class ReportGenerator {

    private final InputConfig inputConfig;
    private final LookupTable lookupTable;
    private final LogParser logParser;

    public ReportGenerator(InputConfig inputConfig,
                           LookupTable lookupTable,
                           LogParser logParser) {
        this.inputConfig = inputConfig;
        this.lookupTable = lookupTable;
        this.logParser = logParser;
    }

    public void generateReport() {
        lookupTable.loadTableFrom(inputConfig.getPathToLookupTable());
        logParser.generateLogAnalysisReport(inputConfig.getPathToFlowLogs(), inputConfig.getPathToOutputFile());
    }
}
