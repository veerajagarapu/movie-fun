package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvUtils {
    private static Logger logger = LoggerFactory.getLogger(CsvUtils.class);
    public static String readFile(String path) {
        try {
            ClassLoader classLoader = CsvUtils.class.getClassLoader();
            InputStream csvInputStream = classLoader.getResourceAsStream(path);
            if (null != csvInputStream) {
                Scanner scanner = new Scanner(csvInputStream).useDelimiter("\\A");
                if (scanner.hasNext()) {
                    return scanner.next();
                } else {
                    logger.debug("In CsvUtils null 1");
                    return "";
                }
            }
            logger.debug("In CsvUtils null 2");
            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> readFromCsv(ObjectReader objectReader, String path) {
        try {
            List<T> results = new ArrayList<>();
            MappingIterator<T> iterator = objectReader.readValues(readFile(path));
            while (iterator.hasNext()) {
                results.add(iterator.nextValue());
            }
            logger.debug("In CsvUtils results :"+results);
            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
