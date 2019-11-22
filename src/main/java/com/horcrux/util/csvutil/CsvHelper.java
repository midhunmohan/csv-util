package com.horcrux.util.csvutil;

import com.google.common.collect.Lists;
import com.horcrux.util.csvutil.exception.CSVValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.*;
import java.util.List;

import static com.horcrux.util.csvutil.util.CommonUtil.readCSVFile;
import static com.horcrux.util.csvutil.util.CommonUtil.validateCSVFields;

/**
 * Created by midhun on 21/11/19.
 */
public class CsvHelper {

    /**
     * Validate both rows and columns of a provided csv format and return the list of rows if no errors
     *
     * @param multipartFile
     * @param header
     * @param cellProcessors
     * @return
     * @throws IOException
     * @throws CSVValidationException
     */
    public static List<CSVRecord> readCSV(File multipartFile, Class header, CellProcessor[] cellProcessors) throws IOException, CSVValidationException {
        return readCSVFile(multipartFile, header, cellProcessors);
    }


    /**
     * Validate Rows of Uploaded CSV with the Types and values specified via Cell Processors
     *
     * @param multipartFile
     * @param cellProcessors
     * @throws IOException
     * @throws CSVValidationException
     */
    public static void validateRows(File multipartFile, CellProcessor[] cellProcessors) throws IOException, CSVValidationException {
        validateCSVFields(multipartFile, cellProcessors);
    }

    /**
     * Get CSV Data as list of CSV Record type so as to access via column name
     *
     * @param multipartFile
     * @param header
     * @return
     * @throws IOException
     */
    public List<CSVRecord> getCSVLineItems(File multipartFile, Class header) throws IOException {
        Reader in = new InputStreamReader(new FileInputStream(multipartFile));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(header)
                .withFirstRecordAsHeader()
                .parse(in);
        return Lists.newArrayList(records);
    }

}
