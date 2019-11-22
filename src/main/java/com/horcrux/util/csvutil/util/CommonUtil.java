package com.horcrux.util.csvutil.util;

import com.google.common.collect.Lists;
import com.horcrux.util.csvutil.config.Constants;
import com.horcrux.util.csvutil.enums.DocHeader;
import com.horcrux.util.csvutil.exception.CSVValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by midhun on 21/11/19.
 */
public final class CommonUtil {

    Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * Read CSV file after validating the document
     *
     * @param multipartFile
     * @param header
     * @param cellProcessors
     * @return
     * @throws IOException
     */
    public static List<CSVRecord> readCSVFile(File multipartFile, Class header, CellProcessor[] cellProcessors) throws IOException, CSVValidationException {
        Reader in = new InputStreamReader(new FileInputStream(multipartFile));
        validateCSVFields(multipartFile, cellProcessors);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(header)
                .withFirstRecordAsHeader()
                .parse(in);
        validateHeaderNames(header, ((CSVParser) records).getHeaderMap().keySet());
        return Lists.newArrayList(records);
    }

    /**
     * Validation helper for csv reader to unnecessarily accessing not existing field
     *
     * @param headers
     * @param headersRead
     */
    public static void validateHeaderNames(Class headers, Set headersRead) throws CSVValidationException {
        if (!Arrays.stream((DocHeader[]) headers.getEnumConstants()).map(co -> co.getName()).collect(Collectors.toSet()).containsAll(headersRead))
            throw new CSVValidationException(Constants.COLUMN_NAMES_NOT_MATCHING, "CSV", Constants.COLUMN_NAMES_NOT_MATCHING);
    }

    /**
     * Validate Whole CSV before read
     *
     * @param multipartFile
     * @param cellProcessors
     * @throws SuperCsvCellProcessorException
     * @throws IOException
     */
    public static void validateCSVFields(File multipartFile, CellProcessor[] cellProcessors) throws SuperCsvCellProcessorException, CSVValidationException, IOException {
        Reader in = new InputStreamReader(new FileInputStream(multipartFile));
        ICsvListReader listReader = new CsvListReader(in, CsvPreference.STANDARD_PREFERENCE);
        String[] headers = listReader.getHeader(true);
        List<Map<String, Object>> validationErrors = new ArrayList<>();
        while (listReader.read() != null) {
            try {
                listReader.executeProcessors(cellProcessors);
            } catch (SuperCsvCellProcessorException e) {
                validationErrors.add(resolveValidationCriteria(e, headers));
            } catch (SuperCsvException e) {
                throw new CSVValidationException(Constants.COLUMN_COUNT_NOT_MATCHING, "CSV", Constants.COLUMN_COUNT_NOT_MATCHING);
            }
        }
        if (!validationErrors.isEmpty()) {
            throw new CSVValidationException(Constants.CSV_ERROR, validationErrors);
        }
    }

    /**
     * Resolve CSV validation Exception to readable
     *
     * @param e
     * @return
     */
    private static Map<String, Object> resolveValidationCriteria(SuperCsvCellProcessorException e, String[] headers) {
        Map<String, Object> validationResponse = new HashMap<>();
        validationResponse.put("responseType", Constants.ERROR_IN_RECORD);
        validationResponse.put("index", e.getCsvContext().getLineNumber() - 1);
        validationResponse.put("columnName", headers[e.getCsvContext().getColumnNumber() - 1]);
        validationResponse.put("value", e.getCsvContext().getRowSource().get(e.getCsvContext().getColumnNumber() - 1));
        switch (e.getProcessor().getClass().getCanonicalName()) {
            case "org.supercsv.cellprocessor.ParseDouble":
                validationResponse.put("reason", Constants.NUMBER_VALIDATION);
                break;
            case "org.supercsv.cellprocessor.ParseLong":
                validationResponse.put("reason", Constants.ID_VALIDATION);
                break;
            case "org.supercsv.cellprocessor.ParseDate":
                validationResponse.put("reason", Constants.DATE_VALIDATION);
                break;
            case "org.supercsv.cellprocessor.constraint.NotNull":
                validationResponse.put("reason", Constants.REQUIRED_FIELD_VALIDATION);
                break;
            default:
                validationResponse.put("reason", Constants.DEFAULT_VALIDATION);
        }
        return validationResponse;
    }

}
