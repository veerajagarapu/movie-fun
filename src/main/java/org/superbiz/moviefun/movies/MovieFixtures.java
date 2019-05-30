package org.superbiz.moviefun.movies;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType.NUMBER;
import static org.superbiz.moviefun.CsvUtils.readFromCsv;

@Component
public class MovieFixtures {
    Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectReader objectReader;

    public MovieFixtures() {
        CsvSchema schema = CsvSchema.builder()
                .addColumn("title")
                .addColumn("director")
                .addColumn("genre")
                .addColumn("rating", NUMBER)
                .addColumn("year", NUMBER)
                .build();

        objectReader = new CsvMapper().readerFor(Movie.class).with(schema);
    }

    public List<Movie> load() {
        logger.debug("Entered to Movie Fixtures Load");
        return readFromCsv(objectReader, "movie-fixtures.csv");
    }
}
