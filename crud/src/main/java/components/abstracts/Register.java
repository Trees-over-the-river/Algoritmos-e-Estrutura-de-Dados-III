package components.abstracts;

import java.io.IOException;

public interface Register {
   int getId();
   void setId(int id);
   byte[] toByteArray() throws IOException;
   void fromByteArray(byte[] b) throws IOException;
}
