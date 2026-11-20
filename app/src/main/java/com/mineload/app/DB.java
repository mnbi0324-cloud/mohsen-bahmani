package com.mineload.app;
import android.content.*;import android.database.*;import android.database.sqlite.*;import java.security.*;import java.nio.charset.StandardCharsets;
class DB extends SQLiteOpenHelper{
 DB(Context c){super(c,"mine.db",null,2);}
 public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY, username TEXT UNIQUE, hash TEXT, admin INTEGER)");d.execSQL("CREATE TABLE records(id INTEGER PRIMARY KEY, category TEXT, username TEXT, date TEXT, fields TEXT, amount REAL DEFAULT 0, created TEXT DEFAULT CURRENT_TIMESTAMP)");seed(d);}
 public void onUpgrade(SQLiteDatabase d,int old,int now){if(old<2){d.execSQL("CREATE TABLE IF NOT EXISTS records(id INTEGER PRIMARY KEY, category TEXT, username TEXT, date TEXT, fields TEXT, amount REAL DEFAULT 0, created TEXT DEFAULT CURRENT_TIMESTAMP)");}}
 void seed(SQLiteDatabase d){ContentValues v=new ContentValues();v.put("username","admin");v.put("hash",hash("admin123"));v.put("admin",1);d.insert("users",null,v);}
 String hash(String s){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));StringBuilder x=new StringBuilder();for(byte q:b)x.append(String.format("%02x",q));return x.toString();}catch(Exception e){return s;}}
 boolean login(String u,String p){Cursor c=getReadableDatabase().rawQuery("SELECT hash FROM users WHERE username=?",new String[]{u});boolean ok=c.moveToFirst()&&c.getString(0).equals(hash(p));c.close();return ok;}
 boolean isAdmin(String u){Cursor c=getReadableDatabase().rawQuery("SELECT admin FROM users WHERE username=?",new String[]{u});boolean a=c.moveToFirst()&&c.getInt(0)==1;c.close();return a;}
 boolean changePassword(String u,String oldPass,String newPass){if(newPass==null||newPass.length()<8||!login(u,oldPass))return false;ContentValues v=new ContentValues();v.put("hash",hash(newPass));return getWritableDatabase().update("users",v,"username=?",new String[]{u})==1;}
 boolean addUser(String u,String p,boolean a){ContentValues v=new ContentValues();v.put("username",u);v.put("hash",hash(p));v.put("admin",a?1:0);return getWritableDatabase().insert("users",null,v)!=-1;}
 void add(String cat,String u,String date,String fields,double amount){ContentValues v=new ContentValues();v.put("category",cat);v.put("username",u);v.put("date",date);v.put("fields",fields);v.put("amount",amount);getWritableDatabase().insert("records",null,v);}
 Cursor list(String cat){return getReadableDatabase().rawQuery("SELECT id,date,fields,amount,created FROM records WHERE category=? ORDER BY id DESC",new String[]{cat});}
 double sum(String cat){Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(amount),0) FROM records WHERE category=?",new String[]{cat});double n=c.moveToFirst()?c.getDouble(0):0;c.close();return n;}
 Cursor listUsers(){return getReadableDatabase().rawQuery("SELECT id,username,admin FROM users ORDER BY id",null);}
 String exportCsv(){StringBuilder s=new StringBuilder("category,date,fields,amount,username\n");Cursor c=getReadableDatabase().rawQuery("SELECT category,date,fields,amount,username FROM records ORDER BY id",null);while(c.moveToNext()){for(int i=0;i<5;i++){if(i>0)s.append(',');String v=c.getString(i)==null?"":c.getString(i).replace("\"","'").replace("\n"," ");s.append('\"').append(v).append('\"');}s.append('\n');}c.close();return s.toString();}
}