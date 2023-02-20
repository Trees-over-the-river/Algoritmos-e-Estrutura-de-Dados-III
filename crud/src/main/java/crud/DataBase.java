package crud;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

import components.interfaces.Register;
import err.EmptyFileException;
import err.JsonValidationException;

public class DataBase<T extends Register> extends BinaryArchive<T> {
    
    private long position = Integer.BYTES;
    private int ID = 0;

    public DataBase(String path, Constructor<T> constructor) throws IOException {
        this(null, path, constructor);
    }

    public DataBase(String label, String path, Constructor<T> constructor) throws IOException {
        super(label, path, constructor);
    }

    public String getLabel() {
        return this.label;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public long getPosition() {
        return this.position;
    }

    public Boolean isEmpty() throws IOException {
        this.file = new RandomAccessFile(this.filePath, "rw");
        Boolean res = this.file.length() == 0;
        this.file.close();
        return res;
    }

    public void reset() {
        this.position = Integer.BYTES;
    }

    private void __initiateDB() throws IOException {
        if(this.file.length() == 0) {
            this.file.setLength(0);
            this.file.seek(0);
            this.file.writeInt(0);
        }
    }

    public int getLastId() throws IOException {
        this.file = new RandomAccessFile(new File(this.filePath), "rw");
        this.file.seek(0);
        int id = this.file.readInt();
    
        this.file.close();
        return id;
    }

    public int getNextId(long pos) throws IOException {
        if(pos < 0)
            throw new IndexOutOfBoundsException();

        this.file = new RandomAccessFile(new File(this.filePath), "rw");
        if(pos == 0)
            pos = Integer.BYTES;

        int len = 0;

        this.file.seek(pos);
        if(this.file.getFilePointer() < this.file.length()) {
            byte lapide = this.file.readByte();
            while(lapide != 1 && this.file.getFilePointer() < this.file.length()) {
                len = this.file.readInt();
                this.file.skipBytes(len);
    
                if(this.file.getFilePointer() < this.file.length()) 
                    lapide = this.file.readByte();
            }
        }

        int id  = -1;
        if(this.file.getFilePointer() < this.file.length()) {
            this.file.skipBytes(Integer.BYTES);
            id = this.file.readInt();
        }
        
        this.file.close();
        return id;
    }

    private int __checkDefaultId() throws IOException {
        this.file.seek(0);
        int id = this.file.readInt();

        if(id == 0)
            throw new EmptyFileException("The file at " + this.filePath + " has no objects.");

        return id;
    }

    public long search(int id) throws IOException {
        this.file = new RandomAccessFile(new File(this.filePath), "rw");
        int lastKey = this.__checkDefaultId();
        
        long pos = Integer.BYTES;
        this.file.seek(pos);

        byte lapide;

        T obj = null;
        do {
            pos = this.file.getFilePointer();
            lapide = this.file.readByte();
            obj = this._readObj();
        } while(obj.getId() != id && obj.getId() != lastKey);

        if(obj == null || obj.getId() != id || lapide == 0) 
            pos = -1;

        this.file.close();
        return pos;
    }

    public T readObj() throws IOException {
        this.file = new RandomAccessFile(new File(this.filePath), "rw");
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
        this.file.close();
        return obj;
    }

    public T readObj(int id) throws IOException {
        long pos = this.search(id);

        this.file = new RandomAccessFile(new File(this.filePath), "rw");
        
        T obj = null;
        if(pos != -1) {
            this.file.seek(pos);
            byte lapide = this.file.readByte();

            if(lapide != 0) 
                obj = this._readObj();
        }

        this.file.close();
        return obj;
    }

    public Boolean update(int id, T obj) throws IOException {
        long pos = this.search(id);
        this.file = new RandomAccessFile(new File(this.filePath), "rw");

        if(pos == -1) 
            return false;

        this.file.seek(pos);
        this.file.skipBytes(1);
        int len = this.file.readInt();

        obj.setId(id);
        byte[] b = obj.toByteArray();
 
        if(b.length <= len) {
            this.file.write(b);
        } else {
            this.file.seek(pos);
            this.file.write(0x00);
            this.write(obj);
        }

        this.file.close();
        return true;
    }

    public Boolean delete(int id) throws IOException {
        long pos = this.search(id);
        this.file = new RandomAccessFile(new File(this.filePath), "rw");
        
        if(pos == -1) 
            return false;

        this.file.seek(pos);
        this.file.writeByte(0);

        this.file.close();
        return true;
    }

    public void write(T obj) throws IOException {
        this.file = new RandomAccessFile(new File(this.filePath), "rw");
        this.__initiateDB();
        
        this.file.seek(0);
        this.ID = this.file.readInt();

        this.ID++;
        obj.setId(this.ID);

        file.seek(this.file.length());
        this.file.write(0x01);

        this._writeObj(obj);
        
        this.file.seek(0);
        this.file.writeInt(obj.getId());
        this.file.close();
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

}
