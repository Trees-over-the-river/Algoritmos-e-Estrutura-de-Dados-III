package crud;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    public Boolean contains(String key, Object value) throws IOException {
        return this.archive.search(key, value) != -1;
    }

    public List<T> read(int startId, int lastId) throws IOException {
        List<T> list = new ArrayList<>();

        int range = lastId - startId;
        for(int i = 0; i < range; i++) {
            list.add(this.read("id", startId + i));
        }

        return list;
    }

    public void create(T obj) throws IOException {
        this.archive.write(obj);
    }

    public T read() throws IOException {
        return this.archive.readObj();
    }

    public T read(String key, Object value) throws IOException {
        return this.archive.readObj(key, value);
    }

    public T[] readAllObj(String key, Object value) throws IOException {
        return this.archive.readAllObj(key, value);
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
        crud.populateAll("src/main/java/data/dat.csv");
        crud.toJsonFile("src/main/java/data/out.json");

        System.out.println(crud.delete(56));
        System.out.println(crud.delete(58));


        crud.create(new Show("Movie", "Fernando Campos Silva Dal Maria", "Fabio Freire, Fernando Campos, Vitoria de Lourdes", new Date(System.currentTimeMillis()), (short)2020, "180 min", "Drama, Horror, Comedy", "HAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        System.out.println(Arrays.toString(crud.readAllObj("releaseYear", (short)2020)));
        crud.toJsonFile("src/main/java/data/out1.json");








        // WatchTime watch = new WatchTime();
        // watch.start();

        // SortedFileSecond<Show> sorted = new SortedFileSecond<>("src/main/java/data/arc.db", 500, Show.properties.get("title"), Show.class.getConstructor());
        // sorted.sort();

        // System.out.println(watch.stop());
        // crud.toJsonFile("src/main/java/data/out1.json");


        // crud.populateAll("src/main/java/data/netflix_titles.csv");

        // watch.reset();
        // watch.start();

        // SortedFileSecond<Show> sortede = new SortedFileSecond<>("src/main/java/data/arc.db", 500, Show.properties.get("title"), Show.class.getConstructor());
        // sortede.sort();

        // System.out.println(watch.stop());
        // crud.toJsonFile("src/main/java/data/out2.json");
    }

}
