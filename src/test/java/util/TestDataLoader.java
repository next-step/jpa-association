package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jdbc.JdbcTemplate;

public class TestDataLoader {
    private final JdbcTemplate jdbcTemplate;

    public TestDataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void load(String filePath) {
        final List<String> list = toList(filePath);
        list.forEach(jdbcTemplate::execute);
    }

    private List<String> toList(String filePath) {
        List<String> lines = new ArrayList<>();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
             lines = reader.lines().collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return lines;
        }
        return lines;
    }


}
