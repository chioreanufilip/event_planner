package com.event_planner.event_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health/db")
    public String testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return "✓ Database connection successful!\nDatabase: " + connection.getCatalog() + 
                   "\nURL: " + connection.getMetaData().getURL();
        } catch (Exception e) {
            return "✗ Database connection failed: " + e.getMessage();
        }
    }

    @GetMapping("/health/db/structure")
    public Map<String, Object> getDatabaseStructure() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("database", connection.getCatalog());
            result.put("url", metaData.getURL());
            result.put("databaseProductName", metaData.getDatabaseProductName());
            result.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            
            List<Map<String, Object>> tables = new ArrayList<>();
            
            // Get all tables
            ResultSet rs = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                Map<String, Object> table = new HashMap<>();
                String tableName = rs.getString("TABLE_NAME");
                table.put("name", tableName);
                
                // Get columns for each table
                List<Map<String, String>> columns = new ArrayList<>();
                ResultSet columnsRs = metaData.getColumns(connection.getCatalog(), null, tableName, "%");
                while (columnsRs.next()) {
                    Map<String, String> column = new HashMap<>();
                    column.put("name", columnsRs.getString("COLUMN_NAME"));
                    column.put("type", columnsRs.getString("TYPE_NAME"));
                    column.put("size", columnsRs.getString("COLUMN_SIZE"));
                    column.put("nullable", columnsRs.getString("IS_NULLABLE"));
                    columns.add(column);
                }
                columnsRs.close();
                table.put("columns", columns);
                
                // Get primary keys
                List<String> primaryKeys = new ArrayList<>();
                ResultSet pkRs = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName);
                while (pkRs.next()) {
                    primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                }
                pkRs.close();
                table.put("primaryKeys", primaryKeys);
                
                tables.add(table);
            }
            rs.close();
            
            result.put("tables", tables);
            result.put("tableCount", tables.size());
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}