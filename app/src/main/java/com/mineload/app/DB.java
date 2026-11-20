package com.mineload.app;
import android.content.*;import android.database.*;import android.database.sqlite.*;import java.security.*;
class DB extends SQLiteOpenHelper{
 DB(Context c){super(c,"mine.db",null,1); }
 public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY, username TEXT UNIQUE, hash TEXT, admin INTEGER)");d.execSQL("CREATE TABLE records(id INTEGER PRIMARY KEY, tbl TEXT, username TEXT, payload TEXT, created TEXT DEFAULT CURRENT_TIMESTAMP)");addSeed(d);}
 void addSeed(SQLiteDatabase d){ContentValues v=new ContentValues();v.put("username","admin");v.put("hash",hash("admin123"));v.put("admin",1);d.insert("users",null,v);}
 public void onUpgrade(SQLiteDatabase d,int o,int n){}
 String hash(String s){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8"));StringBuilder x=new StringBuilder();for(byte q:b)x.append(String.format("%02x",q));return x.toString();}catch(Exception e){return s;}}
 boolean login(String u,String p){Cursor c=getReadableDatabase().rawQuery("SELECT hash FROM users WHERE username=?",new String[]{u});boolean ok=c.moveToFirst()&&c.getString(0).equals(hash(p));c.close();return ok;}
 boolean isAdmin(String u){Cursor c=getReadableDatabase().rawQuery("SELECT admin FROM users WHERE username=?",new String[]{u});boolean a=c.moveToFirst()&&c.getInt(0)==1;c.close();return a;}
 boolean addUser(String u,String p,boolean a){ContentValues v=new ContentValues();v.put("username",u);v.put("hash",hash(p));v.put("admin",a?1:0);return getWritableDatabase().insert("users",null,v)!=-1;}
 void add(String t,String u,String payload){ContentValues v=new ContentValues();v.put("tbl",t);v.put("username",u);v.put("payload",payload);getWritableDatabase().insert("records",null,v);}
 Cursor list(String t){if(t.equals("users"))return getReadableDatabase().rawQuery("SELECT id,username,admin FROM users ORDER BY id",null);return getReadableDatabase().rawQuery("SELECT id,username,payload,created FROM records WHERE tbl=? ORDER BY id DESC",new String[]{t});}
}