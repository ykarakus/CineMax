package cinemax.Helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;


public class CSVReader {
	
	private String delimiter;
    private boolean hasHeader;
    
    /**
     * Constructor with default delimiter (comma) and assumes header exists
     */
    public CSVReader() {
        this(",", true);
    }
    
    /**
     * Constructor with custom delimiter
     * @param delimiter CSV delimiter (e.g., ",", ";", "|")
     * @param hasHeader Whether the CSV file has a header row
     */
    public CSVReader(String delimiter, boolean hasHeader) {
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
    }
    
    /**
     * Generic method to read CSV file and convert to list of models
     * @param filePath Path to CSV file
     * @param mapper Function to convert a row of strings to your model object
     * @return List of converted model objects
     */
    public <T> List<T> readFile(String filePath, Function<List<String>, T> mapper) throws IOException {
        List<T> results = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                // Parse the CSV line, handling quoted values
                List<String> values = parseCSVLine(line);
                
                // Skip header if present
                if (hasHeader && isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;
                
                // Convert row to model object using the provided mapper
                T model = mapper.apply(values);
                results.add(model);
            }
        }
        
        return results;
    }
    
    /**
     * Parse a CSV line, handling quoted values that may contain delimiters
     * @param line The CSV line to parse
     * @return List of values
     */
    private List<String> parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Handle escaped quotes
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++; // Skip the next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter.charAt(0) && !inQuotes) {
                // End of current value
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // Add the last value
        values.add(currentValue.toString().trim());
        
        return values;
    }
    
 
    /**
     * Helper methods for type conversion from string to Integer
     * @param value
     * @return Integer
     */
    public static Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Helper methods for type conversion from String to Double
     * @param value
     * @return Double
     */
    public static Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Helper methods for type conversion from String to Date
     * @param value
     * @return Date
     */
    public static Date parseDate(String value, String format) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(value.trim());
        } catch (ParseException e) {
            return null;
        }
    }

}