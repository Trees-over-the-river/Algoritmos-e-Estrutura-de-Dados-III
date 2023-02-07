package components;

import java.util.Date;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;

import components.abstracts.Register;

public class Show implements Register {
   private int showId;
   private String type;
   private String title;
   private String directors;
   private String cast;
   private String listedIn;
   private String country;
   private Date dateAdded;
   private short releaseYear;
   private String rating;
   private String duration;
   private String description;

   // Constructors

   public Show() {
      this(-1, "", "", "", "", "", "", new Date(), (short)-1, "", "", "");
   }

   public Show(int showId, String type, String title, String directors, String cast, String listedIn,
   String country, Date dateAdded, short releaseYear, String rating, String duration, String description) {
      this.showId = showId;
      this.type = type;
      this.title = title;
      this.directors = directors;
      this.cast = cast;
      this.listedIn = listedIn;
      this.country = country;
      this.dateAdded = dateAdded;
      this.releaseYear = releaseYear;
      this.rating = rating;
      this.duration = duration;
      this.description = description;
   }

   public Show(String[] arr) {
      this.showId = Integer.parseInt(arr[0].substring(1));
      this.type = arr[1];
      this.title = arr[2];
      this.directors = arr[3];
      this.cast = arr[4];
      this.listedIn = arr[5];
      this.country = arr[6];
      this.dateAdded =  arr[7];
      this.releaseYear = arr[8];
      this.rating = arr[9];
      this.duration = arr[10];
      this.description = arr[11];
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

   public String getCast() {
      return cast;
   }

   public String getListedIn() {
      return listedIn;
   }

   public String getCountry() {
      return country;
   }

   public Date getDateAdded() {
      return dateAdded;
   }

   public short getReleaseYear() {
      return releaseYear;
   }

   public String getRating() {
      return rating;
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

   public void setCast(String cast) {
      this.cast = cast;
   }

   public void setListedIn(String listedIn) {
      this.listedIn = listedIn;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public void setDateAdded(Date dateAdded) {
      this.dateAdded = dateAdded;
   }

   public void setReleaseYear(short releaseYear) {
      this.releaseYear = releaseYear;
   }

   public void setRating(String rating) {
      this.rating = rating;
   }

   public void setDuration(String duration) {
      this.duration = duration;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   // Methods

   @Override
   public byte[] toByteArray() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      
      dos.writeInt(this.getId());
      dos.writeUTF(this.getType());
      dos.writeUTF(this.getTitle());
      dos.writeUTF(this.getDirectors());
      dos.writeUTF(this.getCast());
      dos.writeUTF(this.getListedIn());
      dos.writeUTF(this.getCountry());
      dos.writeLong(this.getDateAdded().getTime());
      dos.writeShort(this.getReleaseYear());
      dos.writeUTF(this.getRating());
      dos.writeUTF(this.getDuration());
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
      this.setCast(dis.readUTF());
      this.setListedIn(dis.readUTF());
      this.setCountry(dis.readUTF());
      this.setDateAdded(new Date(dis.readLong()));
      this.setReleaseYear(dis.readShort());
      this.setRating(dis.readUTF());
      this.setDuration(dis.readUTF());
      this.setDescription(dis.readUTF());
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();
      
      return sb.toString();
   }

   @Override 
   public boolean equals(Object o) {
      return  this.getId() == ((Show)o).getId();
   }
}
