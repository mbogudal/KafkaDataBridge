package pl.mdmb.KafkaDataBridge.integration.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
@RequiredArgsConstructor
public class DynamicTableRepository {
    @Value("${table.name}")
    private String tableName;
    private final DataSource dataSource;

    public List<HashMap<String, String>> findAll() {
        List<HashMap<String, String>> out = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            ResultSet resultSet = connection
                    .createStatement()
                    .executeQuery("SELECT * FROM " + tableName);

            ResultSetMetaData metaData = resultSet.getMetaData();
            HashMap<String, String> row = null;


            while (resultSet.next()) {
                row = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(columnName);
                    String valueStr = "";
                    if (value != null) {
                        if (value instanceof Clob) {
                            Clob valueClob = (Clob) value;
                            try (Reader reader = valueClob.getCharacterStream()) {
                                try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                                    valueStr = bufferedReader
                                            .lines()
                                            .collect(Collectors.joining("\n"));
                                }
                            } catch (IOException e) {
                                log.info(e.getMessage());
                            }
                        } else {
                            valueStr = value.toString();
                        }
                    }
                    row.put(columnName, valueStr);
                }
                out.add(row);
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
        }
        return out;
    }
}
