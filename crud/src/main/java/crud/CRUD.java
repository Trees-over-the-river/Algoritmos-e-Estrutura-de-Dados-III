package crud;

import components.Show;
import components.abstracts.Register;
import utils.CSVMenager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class CRUD<T extends Register> {
    private RandomAccessFile archive;


    public CRUD(String path) throws IOException {
        this.archive = new RandomAccessFile(new File(path), "rw");
        
        if(this.archive.length() == 0)
            this.archive.writeInt(0);
    }

    public void create(T obj) throws IOException {
        this.archive.seek(0);
        int id = this.archive.readInt(); 
    }

    public static void main(String[] args) throws Exception {
        CSVMenager menager = new CSVMenager("src/main/java/Data/netflix_titles.csv");
        String[] arr = menager.readNext();
        System.out.println(arr[6]);
        menager.close();
    }
}
