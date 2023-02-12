package crud;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;

import com.opencsv.exceptions.CsvValidationException;

import entities.Show;
import utils.csv.CSVManager;

public class CRUD<T extends Register> implements Closeable {
    private final String filePath;
    private BinaryArchive<T> archive;
    private Constructor<T> constructor;

    public CRUD(String path, Constructor<T> constructor) throws IOException {
        this.filePath = path;
        this.archive = new BinaryArchive<T>("Show DataBase", path, constructor);
        this.constructor = constructor;
    }

    public void populateAll(String CSVpath) throws IOException, CsvValidationException {
        CSVManager menager = new CSVManager(CSVpath);
        this.archive.clear();

        try {
            String[] arr = menager.readNext();
            while(arr != null) {
                T obj = this.constructor.newInstance();
                obj.from(arr);
                this.archive.write(obj);
                arr = menager.readNext();
            }
        } catch(Exception e) {
            System.err.println("The file " + this.filePath + " has a register that is not from the given type at line " + menager.getLines() + ".");
            e.printStackTrace();
        }

        this.archive.reset();
        menager.close();
    }

    public void toJsonFile(String path) throws IOException {
        this.archive.toJsonFile(path);
    }

    public Boolean contains(int id) throws IOException {
        return this.archive.search(id) != -1;
    }

    public void create(T obj) throws IOException {
        this.archive.write(obj);
    }

    public T read() throws IOException {
        return this.archive.readObj();
    }

    public T read(int id) throws IOException {
        return this.archive.readObj(id);
    }

    public void update(int id) throws IOException {

    }

    public void delete(int id) throws IOException {
        this.archive.delete(id);
    }

    public void clear() throws IOException {
        this.archive.clear();
    }

    @Override
    public void close() throws IOException {
        this.archive.close();
    }

    public static void main(String[] args) throws Exception {
        CRUD<Show> crud = new CRUD<Show>("src/main/java/Data/arc.db", Show.class.getConstructor());
        // crud.populateAll("src/main/java/Data/netflix_titles.csv"); 
        // crud.toJsonFile("src/main/java/Data/out.json");

        System.out.println(crud.read());
        System.out.println(crud.read(1));
        System.out.println(crud.read());

        crud.close();
    }

}
