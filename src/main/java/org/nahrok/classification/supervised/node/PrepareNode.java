package org.nahrok.classification.supervised.node;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nahrok.classification.supervised.INormalization;
import org.nahrok.classification.supervised.SimpleNormalization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Data
public class PrepareNode {

    private Sheet sheet;
    private INormalization normalization = new SimpleNormalization();

    public List<Node> createNodes(FileInputStream file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        this.sheet = sheet;
        LinkedHashMap<String, List<String>> stringValues = new LinkedHashMap<>();
        LinkedHashMap<String, List<Double>> numberValues = new LinkedHashMap<>();

        LinkedHashMap<Integer, List<String>> columns = new LinkedHashMap<>();

        Set<String> willBeCalculatedKeys = new HashSet<>();
        int i = 0;
        int defaultLastCellIndex = -1;
        for (Row row : sheet) {
            int j = 0;
            String key = "";
            for (Cell cell : row) {
                if (i == 0) {
                    defaultLastCellIndex = cell.getRow().getLastCellNum();
                    break;
                }
                if (cell.getRow().getLastCellNum() != defaultLastCellIndex) {
                    willBeCalculatedKeys.add(key);
                }
                if (j == 0) {
                    key = cell.getStringCellValue();
                    if (stringValues.containsKey(key)) {
                        key = key + i;
                    }
                    stringValues.put(key, new ArrayList<>());
                    numberValues.put(key, new ArrayList<>());
                    j++;
                    continue;
                }
                switch (cell.getCellType()) {
                    case STRING:
                        String valAsStr = cell.getStringCellValue();
                        columns.computeIfAbsent(j, k -> new ArrayList<>());
                        columns.get(j).add(valAsStr);
                        stringValues.get(key).add(valAsStr);
                        break;
                    case NUMERIC:
                        numberValues.get(key).add(cell.getNumericCellValue());
                        break;
                }
                j++;
            }
            i++;
        }

        List<Node> nodes = new ArrayList<>();
        if (stringValues.size() > 1) {
            LinkedHashMap<String, List<Double>> convertedValues = convertToNumber(stringValues, columns);
            for (String key : convertedValues.keySet()) {
                List<Double> values = convertedValues.get(key);
                values.addAll(numberValues.get(key));
                if (willBeCalculatedKeys.contains(key)) {
                    values.add(null);
                }
                nodes.add(new Node(key, values, true));
            }
            return nodes;
        } else {
            for (String key : numberValues.keySet()) {
                List<Double> values = numberValues.get(key);
                if (willBeCalculatedKeys.contains(key)) {
                    values.add(null);
                }
                nodes.add(new Node(key, values, true));
            }
        }
        return nodes;
    }

    public void write(List<Integer> status) throws IOException {
        int i = 0;
        int defaultLastCellIndex = -1;
        for (Row row : sheet) {
            defaultLastCellIndex = row.getLastCellNum();
            if (defaultLastCellIndex - 1 == row.getLastCellNum()) {
                CellType cellType = CellType.NUMERIC;
                Cell cell = row.createCell(defaultLastCellIndex, cellType);
                cell.setCellValue(status.get(i));
                i++;
            }
        }
        OutputStream outputStream = new FileOutputStream("C:\\Users\\mekrh\\Documents\\workspace\\data\\test.xlsx");
        sheet.getWorkbook().write(outputStream);
    }

    private LinkedHashMap<String, List<Double>> convertToNumber(LinkedHashMap<String, List<String>> stringValues,
                                                                LinkedHashMap<Integer, List<String>> columns) {
        LinkedHashMap<String, List<Double>> output = new LinkedHashMap<>();

        LinkedHashMap<String, Double> keyValue = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<String>> e : columns.entrySet()) {
            List<String> values = e.getValue();
            double i = 0;
            for (String s : values.stream().distinct().toList()) {
                keyValue.put(s, i);
                i++;
            }
        }

        for (Map.Entry<String, List<String>> e : stringValues.entrySet()) {
            List<String> values = e.getValue();
            List<Double> numberValues = new ArrayList<>();
            for (String s : values) {
                Double val = keyValue.get(s);
                numberValues.add(val);
            }
            output.put(e.getKey(), numberValues);
        }
        return output;
    }
}
