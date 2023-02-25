package Sort;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import components.Show;
import crud.CRUD;
import crud.SortedFile;

public class SortedFileTest {
   
   @Test
   public void testSort1() throws Exception {
      CRUD<Show> crud = new CRUD<Show>("src/test/java/data/arc.db", Show.class.getConstructor());
      crud.populateAll("src/main/java/data/dat.csv");
      
      SortedFile<Show> sorted = new SortedFile<Show>("src/test/java/data/arc.db", 500, Show.properties.get("title"), Show.class.getConstructor());
      sorted.sort();
      
      crud.toJsonFile("src/test/java/data/out.json");

      Show s = null,
           last = null;

      boolean value = true;
      while(value && !crud.isEOF()) {
         s = crud.read();
         value = Show.properties.get("title").compare(s, ((last == null) ? s : last)) >= 0;
         last = s.clone();
      }

      assertTrue(value);
   }

}
