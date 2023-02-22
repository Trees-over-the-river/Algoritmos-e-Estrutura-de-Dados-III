package crud;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;

import components.interfaces.Register;
import err.InsufficientMemoryException;

public class SortedFile<T extends Register> extends BinaryArchive<T> {

    private static final int BLOCK_SIZE = 4096;
    private static final int NUMBER_OF_BRANCHES = 4;
    private static final String TEMPORARY_FILES_DIRECTORY = "src/main/java/data/tmp";
    private static final String TEMPORARY_FILES_PATH = "src/main/java/data/tmp/tmp";

    private final DataBase<T> database;
    private final int registerSize;
    private final int originalNumberOfRegistersPerBlock;
    
    private int numberOfRegistersPerBlock;
    private Comparator<T> comparator = (T obj1, T obj2) -> obj1.getId() - obj2.getId();
    
    private BinaryArchive<T>[] originalFiles;
    private BinaryArchive<T>[] tmpFiles;

    public SortedFile(String path, int registerSize, Constructor<T> constructor) throws IOException {
        this(path, registerSize, null, constructor);
    }

    public SortedFile(String path, int registerSize, Comparator<T> comparator, Constructor<T> constructor) throws IOException {
        super(path, constructor);
        this.database = new DataBase<T>(path, constructor);
        if(comparator != null) this.comparator = comparator;

        this.registerSize = registerSize;
        this.originalNumberOfRegistersPerBlock = (int)Math.floor(BLOCK_SIZE/(double)this.registerSize);

        this.numberOfRegistersPerBlock = this.originalNumberOfRegistersPerBlock;

        if(this.numberOfRegistersPerBlock < NUMBER_OF_BRANCHES)
            throw new InsufficientMemoryException("The main memory must cache at least " + NUMBER_OF_BRANCHES + " registers.");

        this.__createArchives();
    }

    public int getNumberOfReistersPerBlock() {
        return this.originalNumberOfRegistersPerBlock;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void sort() throws IOException {
        int numberOfBlocks = this.distribute();

        while(numberOfBlocks > 1) 
            numberOfBlocks = this.interpolate();

        this.database.copy(this.originalFiles[0]);
        this.__close();
    }

    private Boolean __blockWasReaded(Boolean[] values) {
        Boolean value = true;

        for(int i = 0; value && i < values.length; i++)
            value = values[i];

        return value;
    }

    private void __resetFilePointers(BinaryArchive<T>[] arr) throws IOException {
        for(int k = 0; k < arr.length; k++)
            arr[k].file.seek(0);
    } 

    @SuppressWarnings("unchecked")
    private void __createArchives() throws IOException {
        this.originalFiles = new BinaryArchive[NUMBER_OF_BRANCHES];
        this.tmpFiles = new BinaryArchive[NUMBER_OF_BRANCHES];

        for(int i = 0; i < this.originalFiles.length; i++) {
            this.originalFiles[i] = new BinaryArchive<T>(TEMPORARY_FILES_PATH + (i + 1) + ".dat", this.constructor);
            this.originalFiles[i].file = new RandomAccessFile(this.originalFiles[i].filePath, "rw");
            
            this.tmpFiles[i] = new BinaryArchive<T>(TEMPORARY_FILES_PATH + (i + 1 + NUMBER_OF_BRANCHES) + ".dat", this.constructor);
            this.tmpFiles[i].file = new RandomAccessFile(this.tmpFiles[i].filePath, "rw");
        }
    }

    @SuppressWarnings("unchecked")
    private void __changeOriginalFiles() throws IOException {
        BinaryArchive<T>[] arr = new BinaryArchive[NUMBER_OF_BRANCHES];
        for(int i = 0; i < this.originalFiles.length; i++) {
            this.originalFiles[i].file.setLength(0);
            arr[i] = this.originalFiles[i];
        }

        for(int i = 0; i < this.originalFiles.length; i++) {
            this.originalFiles[i] = this.tmpFiles[i];
            this.tmpFiles[i] = arr[i];
        }
    }

    private Boolean __haveRegister() throws IOException {
        Boolean value = false;

        for(int i = 0; !value && i < this.originalFiles.length; i++)
            value = !this.originalFiles[i]._isEOF();

        return value;
    }

    private void __close() throws IOException {
        for(int i = 0; i < NUMBER_OF_BRANCHES; i++) {
            this.originalFiles[i].file.close();
            this.tmpFiles[i].file.close();
        }

        File[] list = new File(TEMPORARY_FILES_DIRECTORY).listFiles();
        for(int i = 0; i < list.length; i++)
            list[i].delete();
    }

    @SuppressWarnings("unchecked")
    private int distribute() throws IOException {
        int numberOfBlocks = 0,
            j = 0;

        ArrayList<T> arr = new ArrayList<>();
    
        while(this.database.getPosition() < this.database.length()) {
            arr.add(this.database.readObj());

            if(arr.size() == this.originalNumberOfRegistersPerBlock) {
                arr.sort(this.comparator);
                this.originalFiles[j]._writeObjs(arr.toArray((T[])new Register[arr.size()]));
                
                arr.clear();
                j = ++numberOfBlocks % NUMBER_OF_BRANCHES;
            }
        }

        if(arr.size() != 0) {
            arr.sort(this.comparator);
            this.originalFiles[j]._writeObjs(arr.toArray((T[])new Register[arr.size()]));

            numberOfBlocks++;
        }

        this.__resetFilePointers(this.originalFiles);

        return numberOfBlocks;
    }

    private int interpolate() throws IOException {
        int numberOfBlocks = 0,
            i = 0;

        while(this.__haveRegister()) {
            this.readRegistersAndWriteOrdered(this.tmpFiles[i]);
            i = ++numberOfBlocks % this.tmpFiles.length;
        }

        this.numberOfRegistersPerBlock *= NUMBER_OF_BRANCHES;

        this.__resetFilePointers(this.tmpFiles);
        this.__changeOriginalFiles();
        this.__resetFilePointers(this.originalFiles);

        return numberOfBlocks;
    }

    private void readRegistersAndWriteOrdered(BinaryArchive<T> arc) throws IOException {
        Boolean[] restrictions = new Boolean[this.originalFiles.length];
        for(int i = 0; i < restrictions.length; i++)
            restrictions[i] = false;

        int[] numberOfReadedRegisters = new int[this.originalFiles.length];

        int positionOfMinObj = -1;
        while(!this.__blockWasReaded(restrictions)) {
            T min = null;

            for(int i = 0; i < this.originalFiles.length; i++) {
                if(!this.originalFiles[i]._isEOF() && numberOfReadedRegisters[i] < this.numberOfRegistersPerBlock) {
                    numberOfReadedRegisters[i]++;
                    T obj = this.originalFiles[i]._readObj();

                    if(min == null) {
                        min = obj;
                        positionOfMinObj = i;
                    } else if(this.comparator.compare(obj, min) < 0) {
                            min = obj;
    
                            this.originalFiles[positionOfMinObj]._returnOneRegister();
                            numberOfReadedRegisters[positionOfMinObj]--;  
    
                            positionOfMinObj = i;
                    } else {
                        this.originalFiles[i]._returnOneRegister();
                        numberOfReadedRegisters[i]--;
                    }
                } else restrictions[i] = true;
            }

            if(min != null) arc._writeObj(min);
        }
    }
}
