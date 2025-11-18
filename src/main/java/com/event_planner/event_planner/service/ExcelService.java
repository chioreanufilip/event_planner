package com.event_planner.event_planner.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for parsing Excel files to extract participant data
 * Expected Excel format:
 * Column 0: Name
 * Column 1: Email
 */
@Service
public class ExcelService {

    /**
     * Parses Excel file and extracts participant information
     * 
     * @param file The uploaded Excel file (.xlsx)
     * @return List of maps containing "name" and "email" keys
     * @throws IOException If file cannot be read
     */
    public List<Map<String, String>> parseParticipantEmails(MultipartFile file) throws IOException {
        List<Map<String, String>> participants = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Map<String, String> participant = new HashMap<>();
                
                // Column 0: Name (optional)
                Cell nameCell = row.getCell(0);
                if (nameCell != null) {
                    String name = getCellValueAsString(nameCell);
                    if (name != null && !name.trim().isEmpty()) {
                        participant.put("name", name.trim());
                    }
                }
                
                // Column 1: Email (required)
                Cell emailCell = row.getCell(1);
                if (emailCell != null) {
                    String email = getCellValueAsString(emailCell);
                    if (email != null && !email.trim().isEmpty()) {
                        participant.put("email", email.trim());
                        participants.add(participant);
                    }
                }
            }
        }
        
        return participants;
    }
    
    /**
     * Extracts cell value as string, handling different cell types
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                // Handle phone numbers stored as numbers
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf((long) cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }
}
