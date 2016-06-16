package com.mcbfinance.aio.web.rest.resources;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SqlInsertIntoGrouper {

    private static final int BUFFER_SIZE = 512000;

    private static final String PATH = "path to file";
    private static final String FILE_NAME_IN = "not grouped sql file with alot of insert into statemetns.sql";
    private static final String FILE_NAME_OUT = "output file name.sql";


    public static void main(String[] args) {

        final String TABLE_NAME = "table_name";
        final int GROUP_SIZE = 100;

        // Order is important
        String[] columns = {"col1", "col2", "col3", "col4"};
        List<String[]> values = readValuesFromFile();

        writeToFileByGroups(columns, values, TABLE_NAME, GROUP_SIZE);
    }

    static List<String[]> readValuesFromFile() {

        final String VALUES_PREFIX = "VALUES(";
        BufferedReader br = null;

        // LinkedList has to be better for memory allocation
        List<String[]> values = new LinkedList<String[]>();

        try {
            br = new BufferedReader(new FileReader(PATH + FILE_NAME_IN), BUFFER_SIZE);
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (currentLine.contains(VALUES_PREFIX)) {
                    String valueContent = currentLine.substring(currentLine.indexOf(VALUES_PREFIX) + VALUES_PREFIX.length(), currentLine.length() - 1);
                    String[] valueRow = valueContent.split(",");

                    escapeUpperQuotes(valueRow);

                    values.add(valueRow);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return values;
    }

    private static void escapeUpperQuotes(String[] valueRow) {
        for (int index = 0; index < valueRow.length; index++) {
            String value = valueRow[index];
            int occurrenceCount = value.length() - value.replace("'", "").length();
            if (occurrenceCount > 2) {

                System.out.println(value);
                value = value.substring(1, value.length() - 1);
                value = value.replaceAll("'", "''");
                value = "'" + value + "'";
                System.out.println(value);
                valueRow[index] = value;
            }
        }
    }

    private static void writeToFileByGroups(String[] columns, List<String[]> values, String tableName, int groupSize) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            File file = new File(PATH + FILE_NAME_OUT);

            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw, BUFFER_SIZE);

            for (int index = 0; index < values.size(); index++) {
                String[] strings = values.get(index);
                String content = getValuesBetweenParentheses(strings);

                // A 'wider' or 'taller' sql script
                String newLine = "\n";
//                String newLine = "";

                String insert;
                if (index % groupSize == 0) {
                    insert = "INSERT INTO " + tableName + getValuesBetweenParentheses(columns) + "VALUES" + content + "," + newLine;
                } else if (index % groupSize == groupSize - 1 || index == values.size() - 1) {
                    insert = content + ";\n";
                } else {
                    insert = content + "," + newLine;
                }
                bw.write(insert);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getValuesBetweenParentheses(String[] values) {
        String content = "(";
        for (int insertValueIndex = 0; insertValueIndex < values.length; insertValueIndex++) {
            content += values[insertValueIndex];
            if (insertValueIndex < values.length - 1) {
                content += ",";
            }
        }
        content += ")";
        return content;
    }
}
