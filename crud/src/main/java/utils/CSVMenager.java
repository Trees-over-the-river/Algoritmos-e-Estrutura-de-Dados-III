package utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class CSVMenager implements Closeable {
   private final String CSV_FILE_PATH;
   private final Character CSV_SEPARATOR;
   private final CSVParser parser;
   private final CSVReader reader;
   private final Map<String, Integer> columns;

   public CSVMenager(String path) throws IOException, CsvValidationException {
      this(path, null);
   }

   public CSVMenager(String path, Character separator) throws IOException, CsvValidationException {
      if(path == null) 
         throw new NullPointerException();

      this.CSV_FILE_PATH = path;
      this.CSV_SEPARATOR = (separator == null) ? ',' : separator;

      this.parser = new CSVParserBuilder().withSeparator(this.CSV_SEPARATOR).build(); 
      this.reader = new CSVReaderBuilder(new FileReader(new File(path))).withCSVParser(this.parser).build();
      this.columns = this.readNext();
   }

   public String getFilePath() {
      return this.CSV_FILE_PATH;
   }

   public Character getFileSeparator() {
      return this.CSV_SEPARATOR;
   }

   public String[] getColumns() {
      return this.columns;
   }

   public String[] readNext() throws IOException, CsvValidationException {
      return this.reader.readNext();
   }

   @Override
   public void close() throws IOException {
      this.reader.close();
   }

}
