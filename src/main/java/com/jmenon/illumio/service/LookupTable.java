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

import static java.util.Collections.unmodifiableMap;

/**
 * Responsible for loading and storing the lookup table for the combination of destPort and protocol to the corresponding tag.
 */
@Component
public class LookupTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LookupTable.class);
    public static final String KEY_DELIMITER = "_";
    public static final String COMMA = ",";

    // Key will be combination of dstPort and protocol, like "25_tcp" and value will be a tag like "sv_P1"
    private static final Map<String, String> TAG_MAP = new HashMap<>(15_000, 0.75f);
    public static final String FIRST_LINE_PREFIX = "dstport";

    // See: https://www.iana.org/assignments/protocol-numbers/protocol-numbers.txt
    private static final Map<String, String> PROTOCOL_MAP = Map.of("6", "tcp", "17", "udp");
    public static final String UNKNOWN_PROTOCOL = "Unknown";
    public static final String UNTAGGED = "Untagged";

    public void loadTableFrom(@NonNull final String filePath) {
        Objects.requireNonNull("filePath", "Please provide a non-null lookup table path");
        LOGGER.info("Loading lookup table info from {}", filePath);
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.filter(line -> !line.startsWith(FIRST_LINE_PREFIX))
                    .map(line -> line.split(COMMA))
                    .filter(splitLine -> splitLine.length == 3)
                    .forEach(splitLine -> {
                        final String key = getKeyFrom(splitLine[0].trim(), splitLine[1].trim().toLowerCase());
                        final String tag = splitLine[2].trim();
                        TAG_MAP.put(key, tag);
                    });

        } catch (Throwable throwable) {
            LOGGER.error("Something went wrong while trying to load lookup table from {}", filePath, throwable);
        }
    }

    public String getKeyFrom(@NonNull final String port,
                             @NonNull final String protocol) {
        return String.format("%s%s%s", port, KEY_DELIMITER, protocol);
    }

    public Map<String, String> getTagMap() {
        return unmodifiableMap(TAG_MAP);
    }

    public String getTranslatedProtocol(@NonNull final String logProtocol) {
        return PROTOCOL_MAP.getOrDefault(logProtocol, UNKNOWN_PROTOCOL);
    }

    public String getTagFor(@NonNull final String key) {
        return TAG_MAP.getOrDefault(key, UNTAGGED);
    }
}
