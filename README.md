# CSV-Util

* [What is this project?](#what-is-this-project)
* [Requirements](#requirements)
* [How to Use](#how-to-use)
 * [Maven](#maven)
* [Documentation and getting help](#documentation-and-getting-help)
* [Building from Source](#building-from-source)
* [Links](#links)
* [Licensing](#licensing)

## What is this project?

The utility is intended to help with read and validation of *csv* files , which is well oriented to provide necessary information got from the validation checks as well as fields read as part of the CSV processing.

this offers the following advantages:

* **Field Validation checks** validation of columns based on type and value
* **CSV Format Validation and compliance**. CSV headers and Columns are validated to confirm the provided file is in accordance with the expected
* **Access Columns in a Row**—can access values of columns in a row using corresponding header field values
* **Highly Configurable** Validation rules/ Header values are configurable to attain the desired result

## How to use
The following are some sample snippets to on board and use utility

```java
public enum StudentList implements DocHeader {
    studentName("Student Name"),
    department("Dept."),
    attendence("Attendence"),
    joiningDate("Joining Date"),
    rollNumber("Roll Number"),
    private final String name;

    private StudentList(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
```
The above file tries to describe the header fields of the expected CSV file. For every CSV file you want to process using the utility you need to create such an Enumerated Type which implements `DocHeader`

Once you are done with the above you can read the file contents as below(achieved with the help of [commons-csv](https://commons.apache.org/proper/commons-csv/))

```java
List<CSVRecord> rows = com.horcrux.util.csvutil.CsvHelper.getCSVLineItems(csvFile, StudentList.class);
rows.forEach(row -> {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setName(csvRecord.get(StudentList.studentName.getName()));
            });
```

To impose any rules to the processing CSV file provided such as validations on fields(*Type*, *Value* of a  field expected), we can define cell processors as below( provided using [super-csv](https://github.com/super-csv/super-csv) ).
```java
public class CSVConstants {  
  public static final CellProcessor[] PROCESSORS_STUDENT_LIST = new CellProcessor[]{  
       new NotNull(),//Student Name  
       null,//Department  
       new ParseDouble(),//Attendence  
       new org.supercsv.cellprocessor.Optional(new ParseDate("yyyyMMdd")),//Joining Date  
       new ParseLong(),//Roll Number  
    };    
  }
```
For the above we need to ensure the column order in which the *CSV* file will be provided.
Sample CSV file may look like below

```csv
Student Name,Department,Attendence,Joining Date,Roll Number
Test,,65.3,20190213,23
```

To specify the processors to validate a file, you can proceed as below
```java
try {  
    com.horcrux.util.csvutil.CsvHelper.validateRows(csvFile, CSVConstants.PROCESSORS_STUDENT_LIST)  
} catch (CSVValidationException e) {  
    List<Map<String, Object>> validationErrors = e.getParamMap();
}
```

`validationErrors` will contain a list of error details, something similar to below for every row encountered with invalid data

```json
 [{
 	"reason": "Field contains invalid data",
 	"responseType": "error.ERROR_IN_RECORD",
 	"index": 1,
 	"value": "05201802", //expecting in yyyymmdd
 	"columnName": "Joining Date"
 }]
```

## Requirements

Requires Java 1.8 or later.


### Maven

For Maven-based projects, add the following to your POM file in order to use (the dependencies are not yet available at Maven Central, please build from source and use from you local maven repository):

```xml
...
    <dependency>
        <groupId>com.horcrux.util.csvutil</groupId>
        <artifactId>csv-util</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
...
```

## Documentation and getting help


## Building from Source

This Project uses Maven for its build. Java 8 is required for building the project from source. To build the complete project, run

    mvn clean install

from the root of the project directory. Now we can use as specified [here](#maven)
    
## Importing into IDE


### IntelliJ 

Make sure that you have at least IntelliJ 2018.2.x (needed since support for `annotationProcessors` from the `maven-compiler-plugin` is from that version).
Enable annotation processing in IntelliJ (Build, Execution, Deployment -> Compiler -> Annotation Processors)

### Eclipse

Make sure that you have the [m2e_apt](https://marketplace.eclipse.org/content/m2e-apt) plugin installed.

## Links

* [Homepage]()
* [Source code]()
* [Downloads]()
* [Issue tracker]()
* [User group]()
* [CI build]()

## Licensing