package com.github.afarentino.getfit;

import com.github.afarentino.getfit.core.RecordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.stream.Stream;
import java.util.List;

/**
 * Using Java NIO and Java 8 Streams, lazily read a text file and parse lines into
 * Fitness Records
 *
 * Constraints -> File read will not have more than 1 year of records (365)
 */
public class TextFileService {
    private static final Logger logger = LoggerFactory.getLogger(TextFileService.class);

    private String fileName;
    private List<Record> recordList;

    public TextFileService(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Convert list of Records representing the current file
     */
    public File convertToCSV() {
        if (this.fileName == null) {
            throw new IllegalStateException("fileName cannot be null");
        }
        Path path = Path.of(this.fileName);
        try (Stream<String> lines = Files.lines(path)) {
            this.recordList = RecordFactory.processLines(lines);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Convert the RecordList into a CSV
        return null;
    }
}
