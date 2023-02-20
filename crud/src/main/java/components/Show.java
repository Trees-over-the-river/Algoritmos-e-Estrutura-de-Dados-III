package components;

import java.util.Date;

import components.interfaces.DateFormatter;
import components.interfaces.Register;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Show implements Register, DateFormatter {
   private Integer showId = null;
   private String type;
   private String title;
   private String directors;
   private Date dateAdded;
   private short releaseYear;
   private String duration;
   private String listedIn;
   private String description;

   // Constructors

   public Show() {
      this("", "", "", new Date(), (short)-1, "", "", "");
   }

   public Show(String type, String title, String directors, Date dateAdded, short releaseYear, String duration, String listedIn, String description) {
      this.type = type;
      this.title = title;
      this.directors = directors;
      this.listedIn = listedIn;
      this.dateAdded = dateAdded;
      this.releaseYear = releaseYear;
      this.duration = duration;
      this.description = description;
   }

   // Getters

   @Override
   public int getId() {
      return showId;
   }

   public String getType() {
      return type;
   }

   public String getTitle() {
      return title;
   }
   public String getDirectors() {
      return directors;
   }

   public String getListedIn() {
      return listedIn;
   }

   public Date getDateAdded() {
      return dateAdded;
   }

   public short getReleaseYear() {
      return releaseYear;
   }

   public String getDuration() {
      return duration;
   }

   public String getDescription() {
      return description;
   }

   // Setters

   @Override
   public void setId(int showId) {
      this.showId = showId;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setDirectors(String directors) {
      this.directors = directors;
   }

   public void setListedIn(String listedIn) {
      this.listedIn = listedIn;
   }

   public void setDateAdded(Date dateAdded) {
      this.dateAdded = dateAdded;
   }

   public void setReleaseYear(short releaseYear) {
      this.releaseYear = releaseYear;
   }

   public void setDuration(String duration) {
      this.duration = duration;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   // Methods

   @Override
   public void from(String... arr) {
      this.showId = Integer.parseInt(arr[0].substring(1));
      this.type = arr[1];
      this.title = arr[2];
      this.directors = arr[3];
      this.dateAdded = this.dateParser(arr[4]);
      this.releaseYear = (short)Integer.parseInt(arr[5]);
      this.duration = arr[6];
      this.listedIn = arr[7];
      this.description = arr[8];
   }

   @Override
   public Date dateParser(String originalDate) {
      String str  = null;
      Date date = null;

      originalDate = originalDate.replaceAll(",", "").trim();
      String[] arr = originalDate.split(" ");

      try {
         if(arr.length <= 1)  {
             date = new Date(System.currentTimeMillis());
         } else {
            str = "";
            str += arr[1] + "-";
            str += months.get(arr[0]) + "-";
            str += arr[2];
     
            date = dateFormat.parse(str);
         }
     } catch(Exception e) {            
         System.err.println("The given string (" + str + ") does not match the pattern.");
         e.printStackTrace();
     }

      return date;
   }

   @Override
   public byte[] toByteArray() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      
      dos.writeInt(this.getId());
      dos.writeUTF(this.getType());
      dos.writeUTF(this.getTitle());
      dos.writeUTF(this.getDirectors());
      dos.writeLong(this.getDateAdded().getTime());
      dos.writeShort(this.getReleaseYear());
      dos.writeUTF(this.getDuration());
      dos.writeUTF(this.getListedIn());
      dos.writeUTF(this.getDescription());
      return baos.toByteArray();
   }

   @Override
   public void fromByteArray(byte[] b) throws IOException {
      ByteArrayInputStream bais = new ByteArrayInputStream(b);
      DataInputStream dis = new DataInputStream(bais);

      this.setId(dis.readInt());
      this.setType(dis.readUTF());
      this.setTitle(dis.readUTF());
      this.setDirectors(dis.readUTF());
      this.setDateAdded(new Date(dis.readLong()));
      this.setReleaseYear(dis.readShort());
      this.setDuration(dis.readUTF());
      this.setListedIn(dis.readUTF());
      this.setDescription(dis.readUTF());
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer("{\n");
      sb.append("\t" + "\"showId\": " + "\"" + this.getId() + "\"" + ",\n");
      sb.append("\t" + "\"type\": " + "\"" + this.getType() + "\"" + ",\n");
      sb.append("\t" + "\"title\": " + "\"" + this.getTitle().replaceAll("\"", "\'") + "\"" + ",\n");
      sb.append("\t" + "\"directors\": " + "\"" + this.getDirectors().replaceAll("\"", "\'") + "\"" + ",\n");
      sb.append("\t" + "\"dateAdded\": " + "\"" + this.getDateAdded() + "\"" + ",\n");
      sb.append("\t" + "\"releaseYear\": " + "\"" + this.getReleaseYear() + "\"" + ",\n");
      sb.append("\t" + "\"duration\": " + "\"" + this.getDuration() + "\"" + ",\n");
      sb.append("\t" + "\"listedIn\": " + "\"" + this.getListedIn() + "\"" + ",\n");
      sb.append("\t" + "\"description\": " + "\"" + this.getDescription().replaceAll("\"", "\'") + "\"" + "\n");
      return sb.append("}").toString();
   }

   @Override 
   public boolean equals(Object o) {
      return  this.getId() == ((Show)o).getId();
   }
}
