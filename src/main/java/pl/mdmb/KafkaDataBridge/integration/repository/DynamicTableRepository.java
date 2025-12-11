package pl.mdmb.KafkaDataBridge.integration.repository;

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
public class DynamicTableRepository {
    private final String insertTableName;
    private final String readTableName;
    private final DataSource dataSource;

    public DynamicTableRepository(@Value("${insert.table.name}") String insertTableName,
                                  @Value("${read.table.name}") String readTableName,
                                  DataSource dataSource) {
        this.insertTableName = insertTableName;
        this.readTableName = readTableName;
        this.dataSource = dataSource;
    }

    public List<HashMap<String, String>> findAll() {
        List<HashMap<String, String>> out = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            ResultSet resultSet = connection
                    .createStatement()
                    .executeQuery("SELECT * FROM " + readTableName);

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

    public void insertData(List<HashMap<String, String>> rows) {

        if (rows.isEmpty()) {
            log.severe("Rows are empty.");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            List<String> keySet = rows.get(0).keySet().stream().toList();
            String dropTable = "DROP TABLE " +
                    insertTableName;

            try (Statement statement = connection.createStatement()) {
                statement.execute(dropTable);
            }
            String createTableSql = "CREATE TABLE " +
                    insertTableName +
                    " (" +
                    String.join(", ", keySet.stream()
                            .map(i -> i + (i.length() < 255 ? "VARCHAR(255)" : "CLOB"))
                            .toList()) +
                    ")";
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSql);
            }
            String sql = "INSERT INTO " +
                    insertTableName +
                    " (" +
                    String.join(", ", keySet) +
                    ") VALUES (" +
                    String.join(", ", keySet.stream().map(i -> "?").toList()) +
                    ")";


            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                for (HashMap<String, String> row : rows) {
                    for (int iColumn = 0; iColumn < keySet.size(); iColumn++) {
                        preparedStatement.setObject(iColumn + 1, row.get(keySet.get(iColumn)));
                    }
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                log.severe(ex.getMessage());
            }

        } catch (SQLException e) {
            log.severe(e.getMessage());
        }
    }
}
