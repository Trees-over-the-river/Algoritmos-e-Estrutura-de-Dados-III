package crud;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvValidationException;

import components.interfaces.Register;
import utils.csv.CSVManager;

public class CRUD<T extends Register<T>> {
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

    public boolean contains(String key, Object value) throws IOException {
        return this.archive.search(key, value) != -1;
    }

    public List<T> read(int startId, int lastId) throws IOException {
        List<T> list = new ArrayList<>();

        int range = lastId - startId + 1;
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

    public boolean update(int id, T obj) throws IOException {
        return this.archive.update(id, obj);
    }

    public boolean update(int id, String key, Object value) throws IOException {
        return this.archive.update(id, key, value);
    }

    public boolean delete(int id) throws IOException {
        return this.archive.delete(id);
    }

    public boolean isEmpty() throws IOException {
        return this.archive.isEmpty();
    }

    public boolean isEOF() throws IOException {
        return this.archive.isEOF();
    }

    public void reset() throws IOException {
        this.archive.reset();
    }

    public void clear() throws IOException {
        this.archive.clear();
    }
}
