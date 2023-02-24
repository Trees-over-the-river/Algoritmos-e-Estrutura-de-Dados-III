package crud;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvValidationException;

import components.Show;
import components.interfaces.Register;
import utils.csv.CSVManager;

public class CRUD<T extends Register> {
    private final String filePath;
    private DataBase<T> archive;
    private Constructor<T> constructor;

    public CRUD(String path, Constructor<T> constructor) throws IOException {
        this.filePath = path;
        this.archive = new DataBase<T>("Show DataBase", path, constructor);
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

    public List<T> read(int startId, int lastId) throws IOException {
        List<T> list = new ArrayList<>();

        int range = lastId - startId;
        for(int i = 0; i < range; i++) {
            list.add(this.read(startId + i));
        }

        return list;
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

    public Boolean update(int id, T obj) throws IOException {
        return this.archive.update(id, obj);
    }

    public Boolean delete(int id) throws IOException {
        return this.archive.delete(id);
    }

    public void clear() throws IOException {
        this.archive.clear();
    }

    public static void main(String[] args) throws Exception {
        CRUD<Show> crud = new CRUD<Show>("src/main/java/data/arc.db", Show.class.getConstructor());
        crud.populateAll("src/main/java/data/netflix_titles.csv");
        crud.toJsonFile("src/main/java/data/out.json");

        System.out.println(crud.delete(56));
        System.out.println(crud.delete(58));

        SortedFile<Show> sorted = new SortedFile<>("src/main/java/data/arc.db", 500, Show.properties.get("title"), Show.class.getConstructor());
        sorted.sort();
        crud.toJsonFile("src/main/java/data/out1.json");
    }

}
