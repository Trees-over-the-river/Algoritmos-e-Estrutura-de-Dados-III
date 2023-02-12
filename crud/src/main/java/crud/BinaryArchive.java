package crud;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

import err.EmptyFileException;
import err.JsonValidationException;

public class BinaryArchive<T extends Register> implements Closeable {
    
    private final String label;
    private final String filePath;
    private RandomAccessFile file;
    private Constructor<T> constructor;
    private long position = Integer.BYTES;

    public BinaryArchive(String path, Constructor<T> constructor) throws IOException {
        this(null, path, constructor);
    }

    public BinaryArchive(String label, String path, Constructor<T> constructor) throws IOException {
        this.label = label;
        this.filePath = path;
        this.constructor = constructor;

        this.file = new RandomAccessFile(new File(path), "rw");
    }

    public String getLabel() {
        return this.label;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Boolean isEmpty() throws IOException {
        return this.file.length() == 0;
    }

    public void reset() {
        this.position = Integer.BYTES;
    }

    public void clear() throws IOException {
        this.file.setLength(0);
        this.file.seek(0);
        this.file.writeInt(0);
    }

    private int __checkDefaultId() throws IOException {
        this.file.seek(0);
        int id = this.file.readInt();

        if(id == 0)
            throw new EmptyFileException("The file at " + this.filePath + " has no objects.");

        return id;
    }

    private int __foundLastKey() throws IOException {
        int lastKey = this.__checkDefaultId();
        
        long pos = Integer.BYTES;
        this.file.seek(pos);

        byte lapide;

        int len,
            id = 0;

        do {
            lapide = this.file.readByte();
            len = this.file.readInt();
            
            if(lapide == 1 && id != lastKey) {
                id = this.file.readInt();
                this.file.skipBytes(len - Integer.BYTES);
            } else {
                this.file.skipBytes(len);
            }

        } while(this.file.getFilePointer() < this.file.length());

        return id;
    }

    private long __searchGarbage(int id) throws IOException {
        long pos = Integer.BYTES;
        this.file.seek(pos);

        int key,
            len;

        do {
            pos = this.file.getFilePointer();
            this.file.skipBytes(1);
            len = this.file.readInt();
            key = this.file.readInt();

            this.file.skipBytes(len - Integer.BYTES);
        } while(key != id && this.file.getFilePointer() < this.file.length());

        if(key != id) 
            pos = -1;

        return pos;
    }

    public long search(int id) throws IOException {
        int lastKey = this.__checkDefaultId();
        
        long pos = Integer.BYTES;
        this.file.seek(pos);

        byte lapide;

        int key,
            len;

        do {
            pos = this.file.getFilePointer();
            lapide = this.file.readByte();
            len = this.file.readInt();
            key = this.file.readInt();

            this.file.skipBytes(len - Integer.BYTES);
        } while(key != id && key != lastKey);

        if(key != id || lapide == 0) 
            pos = -1;

        return pos;
    }

    public T readObj() throws IOException {
        if(this.position == this.file.length()) 
            return null;
    
        this.__checkDefaultId();

        this.file.seek(this.position);
        byte lapide = this.file.readByte();
        int len = this.file.readInt();

        while(lapide == 0) {
            this.file.skipBytes(len);
            lapide = this.file.readByte();
            len = this.file.readInt(); 
        } 

        byte[] b = new byte[len];
        this.file.read(b);

        T obj = null;
        try {
            obj = this.constructor.newInstance();
            obj.fromByteArray(b);
        } catch(Exception e) {
            System.err.println("Could not make a new instanse of " + this.constructor.getName());
            e.printStackTrace();
        }
        
        this.position = this.file.getFilePointer();
        return obj;
    }

    public T readObj(int id) throws IOException {
        long pos = this.search(id);

        if(pos == -1)
            return null;

        this.file.seek(pos);

        this.file.skipBytes(1);
        int len = this.file.readInt();

        byte[] b = new byte[len];

        this.file.read(b);

        T obj = null;
        try {
            obj = this.constructor.newInstance();
            obj.fromByteArray(b);
        } catch(Exception e) {
            System.err.println("Could not make a new instanse of " + this.constructor.getName());
            e.printStackTrace();
        }

        return obj;
    }

    public void delete(int id) throws IOException {
        int lastKey = this.__checkDefaultId();
        long pos = this.__searchGarbage(id);

        this.file.seek(pos);

        byte lapide = this.file.readByte();
        this.file.skipBytes(Integer.BYTES);
        int key = this.file.readInt();
        
        if(lapide == 1) {
            this.file.seek(pos);
            this.file.writeByte(0);
        }

        if(key == lastKey) {
            int newKey = this.__foundLastKey();

            this.file.seek(0);
            this.file.writeInt(newKey);
        }
    }

    public void write(T obj) throws IOException {
        if(this.isEmpty()) 
            this.clear();
        
        byte[] bytes = obj.toByteArray();

        long pos = this.file.length();
        this.file.seek(pos);

        this.file.write(0x01);
        this.file.writeInt(bytes.length);
        this.file.write(bytes);

        this.file.seek(0);
        this.file.writeInt(obj.getId());
    }

    public void toJsonFile(String path) throws IOException {
        if(!path.endsWith(".json"))
            throw new JsonValidationException("The file at " + path + "is not a JSON file.");

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));

        bw.write("[\n");
        T obj = this.readObj();
        while(obj != null) {
            bw.write(obj.toString());
            obj = this.readObj();
            
            if(obj != null)
                bw.write(",\n");
        }
        bw.write("\n]\n");

        bw.close();
    }

    @Override
    public void close() throws IOException {
        this.file.close();
    }

}
