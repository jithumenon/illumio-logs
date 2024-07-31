package com.jmenon.illumio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class LogParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogParser.class);

    public static final String LOG_DELIMITER = " ";
    public static final int PORT_INDEX = 6;
    public static final int PROTOCOL_INDEX = 7;
    private final LookupTable lookupTable;

    private static final Map<String, Integer> PORT_AND_PROTOCOL_COUNTS = new HashMap<>(10_000);
    private static final Map<String, Integer> TAG_COUNTS = new HashMap<>(5_000);

    public LogParser(LookupTable lookupTable) {
        this.lookupTable = lookupTable;
    }

    public void loadLogsFrom(@NonNull final String filePath) {
        Objects.requireNonNull("filePath", "Please provide a non-null log file path");
        LOGGER.info("Loading log file from {}", filePath);
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.filter(line -> !line.isBlank())
                    .map(line -> line.split(LOG_DELIMITER))
                    .filter(splitLine -> splitLine.length == 14) // default log format has 14 v2 fields
                    .forEach(splitLine -> {
                        final String port = splitLine[PORT_INDEX].trim();
                        final String protocol = splitLine[PROTOCOL_INDEX].trim();
                        final String translatedProtocol = lookupTable.getTranslatedProtocol(protocol);

                        if (LookupTable.UNKNOWN_PROTOCOL.equalsIgnoreCase(translatedProtocol)) {
                            LOGGER.warn("Could not find protocol matching log value {}. Skipping", protocol);
                        } else {
                            final String key = lookupTable.getKeyFrom(port, translatedProtocol);
                            final String tag = lookupTable.getTagFor(key);

                            PORT_AND_PROTOCOL_COUNTS.put(key, PORT_AND_PROTOCOL_COUNTS.getOrDefault(key, 0) + 1);
                            TAG_COUNTS.put(tag, TAG_COUNTS.getOrDefault(tag, 0) + 1);
                        }
                    });

        } catch (Throwable throwable) {
            LOGGER.error("Something went wrong while trying to load lookup table from {}", filePath, throwable);
        }
    }

    public String getTagCountsAsTable() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Tag Counts:\n");
        sb.append("Tag\t\t Count\n");
        TAG_COUNTS.keySet()
                .stream()
                .sorted()
                .forEach(tag -> sb.append(tag).append("\t\t").append(TAG_COUNTS.get(tag)).append("\n"));
        return sb.toString();
    }

    public String getPortAndProtocolCountsAsTable() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Port/Protocol Combination Counts:\n");
        sb.append("Port\t Protocol\t Count\n");
        PORT_AND_PROTOCOL_COUNTS.keySet()
                .stream()
                .sorted()
                .forEach(combinedKey -> {
                    final String[] portAndProtocol = combinedKey.split(LookupTable.KEY_DELIMITER);
                    final String port = portAndProtocol[0];
                    final String protocol = portAndProtocol[1];
                    sb.append(port).append("\t").append(protocol).append("\t\t").append(PORT_AND_PROTOCOL_COUNTS.get(combinedKey)).append("\n");
                });
        return sb.toString();
    }

    public String getLogAnalysisOutput() {
        return getTagCountsAsTable() + getPortAndProtocolCountsAsTable();
    }

    public void generateLogAnalysisReport(@NonNull final String logFilePath,
                                          @NonNull final String outputFilePath) {
        loadLogsFrom(logFilePath);
        try {
            LOGGER.info("Analyzing log file {} and saving generated report to {}", logFilePath, outputFilePath);
            Files.writeString(Paths.get(outputFilePath), getLogAnalysisOutput());
        } catch (Throwable throwable) {
            LOGGER.error("Something went wrong while generating and writing the log file analysis report to {}", outputFilePath, throwable);
        }
    }
}
