package crud;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

import components.interfaces.Register;

public class BinaryArchive<T extends Register> {
    
    protected final String label;
    protected final String filePath;
    protected RandomAccessFile file;
    protected Constructor<T> constructor;

    public BinaryArchive(String path, Constructor<T> constructor) throws IOException {
        this(null, path, constructor);
    }

    public BinaryArchive(String label, String path, Constructor<T> constructor) throws IOException {
        this.label = label;
        this.filePath = path;
        this.constructor = constructor;
    }

    public String getLabel() {
        return this.label;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void clear() throws IOException {
        this.file = new RandomAccessFile(new File(this.filePath), "rw");
        this.file.setLength(0);
        this.file.seek(0);
        this.file.close();
    }

    protected T _readObj() throws IOException {
        T obj = null;

        if(this.file.getFilePointer() < this.file.length()) {
            int len = this.file.readInt();
            byte[] b = new byte[len];
            
            this.file.read(b);
    
            try {
                obj = this.constructor.newInstance();
                obj.fromByteArray(b);
            } catch(Exception e) {
                System.err.println("Could not make a new instanse of " + this.constructor.getName());
                e.printStackTrace();
            }
        }

        return obj;
    }

    protected void _writeObj(T obj) throws IOException {
        byte[] b = obj.toByteArray();

        this.file.writeInt(b.length);
        this.file.write(b);
    }

}
