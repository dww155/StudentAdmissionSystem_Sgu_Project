# data_quick_insert

JavaFX desktop app to read `.xlsx` files and insert rows directly into a database using JDBC batch inserts.

## Features
- Local desktop app (no backend server, no REST API, no ORM).
- Multi-tab import profiles (same idea as `admission_desktop` import):
  - Thi sinh
  - Nganh
  - To hop mon
  - Nganh - To hop
  - Diem thi
  - Diem cong
  - Nguyen vong
  - Bang quy doi
- Select Excel file and optionally choose sheet.
- Preview first 200 rows before importing.
- JDBC connection config from UI (`URL`, `username`, `password`), with profile-based target table.
- Batch insert with chunked commits (`PreparedStatement.addBatch()` + `commit()` every chunk).
- Uses `ExcelImportUtil.ColumnDefinition` alias mapping (like `admission_desktop`) for header matching.
- Streaming Excel parsing via Apache POI SAX API.

## Project structure
```text
data_quick_insert/
  pom.xml
  README.md
  src/main/java/com/sgu/data_quick_insert/
    Launcher.java
    QuickInsertApplication.java
    controller/MainController.java
    model/ImportProfile.java
    model/PreviewData.java
    model/InsertResult.java
    util/ExcelImportUtil.java
    util/ExcelStreamingReader.java
    util/JdbcBatchInsertService.java
    util/SqlIdentifierValidator.java
  src/main/resources/com/sgu/data_quick_insert/
    main-view.fxml
```

## Expected Excel format
- `.xlsx` only.
- First non-empty row is treated as header.
- Header names are matched by aliases defined in `ExcelImportUtil.ColumnDefinition`.
- Each tab/profile expects specific columns (shown in the UI field `Template columns`).

## Batch insert example
```java
rowBinder.bind(connection, statement, rowData, rowNumber);
statement.addBatch();
state.pendingBatchRows++;

if (state.pendingBatchRows >= batchSize) {
    flushBatch(connection, statement, state, logger);
}
```

## Run
1. Make sure JDK 21+ and Maven are installed.
2. Open terminal at `data_quick_insert`.
3. Run:
   ```bash
   mvn clean javafx:run
   ```
4. Do not run `target/*.jar` directly with `java -jar` for JavaFX apps.

## Build runnable local image (no Maven needed at runtime)
1. From `data_quick_insert`, run:
   ```bash
   mvn clean javafx:jlink
   ```
2. Launch:
   - Windows: `target\app\bin\app.bat`
   - Linux/macOS: `target/app/bin/app`

## Notes
- Supported JDBC drivers included: PostgreSQL and MySQL.
- Default DB values in UI:
  - URL: `jdbc:mysql://localhost:3306/xt_bangquydoi?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
  - Username: `root`
  - Password: `root`
- Rows are inserted directly to MySQL using raw SQL + JDBC batch.
