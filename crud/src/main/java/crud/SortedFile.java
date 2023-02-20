package crud;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import components.Show;

import components.interfaces.Register;

public class SortedFile<T extends Register> extends BinaryArchive<T> {

    private static final int BLOCK_SIZE = 4096;
    private static final int NUMBER_OF_BRANCHES = 4;

    private DataBase<T> database;
    private final int registerSize;
    private final int blocksCeil;

    private int position = 0;

    public SortedFile(String path, int registerSize, Constructor<T> constructor) throws IOException {
        super(path, constructor);

        this.registerSize = registerSize;
        this.blocksCeil = (int)Math.floor(BLOCK_SIZE/(double)this.registerSize);
    }

    private void writeAtFile(T[] arr, int archiveNumber) throws IOException {
        this.file = new RandomAccessFile(new File("src/main/java/data/tmp/tmp" + archiveNumber + ".dat"), "rw");
        System.out.println("FILE: tmp" + archiveNumber + ".dat:\n");

        this.file.writeInt(arr.length);
        for(int i = 0; i < arr.length; i++) {
            this._writeObj((T)arr[i]);
            System.out.println(arr[i]);
        }

        this.file.close();
    }

    @SuppressWarnings("unchecked")
    private T[] readFromFile(int archiveNumber) throws IOException {
        this.file = new RandomAccessFile("tmp" + archiveNumber + ".dat", "rw");
        int len = this.file.readInt();
        
        T[] arr = (T[])new Register[len];
        for(int i = 0; i < len; i++)
            arr[i] = this._readObj();

        this.file.close();
        return arr;
    }

    @SuppressWarnings("unchecked")
    public void sort() throws IOException {
        this.database = new DataBase<T>(filePath, constructor);

        int i = 0,
            j = 1;

        T[] arr = (T[])new Register[blocksCeil];
        
        int nextId = this.database.getNextId(this.database.getPosition());
        while(nextId != -1) {
            arr[i++] = this.database.readObj();

            if(i == arr.length) {
                this.quickSort(arr, 0, arr.length - 1);
                this.writeAtFile(arr, j);
                System.out.println();
                i = 0;

                j++;
                if(j > NUMBER_OF_BRANCHES)
                    j = 1;
            }

            nextId = this.database.getNextId(this.database.getPosition());
        }

        if(i != 0) {
            this.quickSort(arr, 0, arr.length - 1);
            this.writeAtFile(arr, j);
            System.out.println();
        }

        this.interpolate();
    }

    private void interpolate() {
        
    }

    private void quickSort(Register[] arr, int first, int last) {
        if(first < last) {
            int p = partition(arr, first, last);

            quickSort(arr, p, last);
            quickSort(arr, first, p - 1);
        }
    }

    private int partition(Register[] arr, int first, int last) {
        Register p = arr[(first + last) / 2];
            
        int i = first,
            j = last;

        while(i <= j) {
            while(((Show)arr[i]).getListedIn().compareTo(((Show)p).getListedIn()) < 0) i++;
            while(((Show)arr[j]).getListedIn().compareTo(((Show)p).getListedIn()) > 0) j--;
            if(i <= j) {
                swap(arr, i, j);
                i++;
                j--;
            }
        }

        return i;
    }

    private void swap(Register[] arr, int a, int b) {
        Register tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
    }

}
