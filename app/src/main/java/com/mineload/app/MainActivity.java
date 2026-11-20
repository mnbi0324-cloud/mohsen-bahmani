package com.mineload.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.database.Cursor;
import java.util.*;

public class MainActivity extends Activity {
    DB db; String currentUser=""; boolean isAdmin=false;
    final String[] modules={"کارکرد بیل و ماشین‌آلات","کارکنان و حضور و غیاب","سوخت","هزینه‌های معدن","رگه و نمونه سنگ","گزارش‌ها","مدیریت کاربران"};
    final String[] tables={"work","staff","fuel","expenses","samples","reports","users"};
    LinearLayout root;
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density);}
    TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(Color.rgb(30,40,48));t.setPadding(dp(12),dp(10),dp(12),dp(10));return t;}
    Button button(String s){Button b=new Button(this);b.setText(s);return b;}
    @Override public void onCreate(Bundle b){super.onCreate(b);db=new DB(this);login();}
    void base(String title){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);root.setPadding(dp(14),dp(12),dp(14),dp(12));root.setBackgroundColor(Color.rgb(245,247,249));TextView h=text(title,22);h.setGravity(Gravity.CENTER);h.setTextColor(Color.rgb(20,90,70));root.addView(h);ScrollView sc=new ScrollView(this);sc.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);sc.addView(root);setContentView(sc);}
    void login(){base("مدیریت معدن | مهندس بهمنی");root.addView(text("ورود به سامانه",18));EditText u=new EditText(this);u.setHint("نام کاربری");EditText p=new EditText(this);p.setHint("رمز عبور");p.setInputType(129);root.addView(u);root.addView(p);Button go=button("ورود");root.addView(go);root.addView(text("ورود اولیه مدیر: admin / admin123\nپس از ورود، رمز را از مدیریت کاربران تغییر دهید.",14));go.setOnClickListener(v->{String name=u.getText().toString().trim();if(db.login(name,p.getText().toString())){currentUser=name;isAdmin=db.isAdmin(name);dashboard();}else toast("نام کاربری یا رمز اشتباه است");});}
    void dashboard(){base("سلام "+currentUser+" | داشبورد معدن");root.addView(text("نسخه ۱.۰ • ذخیره‌سازی محلی • قابل توسعه",14));for(int i=0;i<modules.length;i++){final int n=i;if(i==6&&!isAdmin)continue;Button b=button(modules[i]);root.addView(b);b.setOnClickListener(v->{if(n==6)users();else if(n==5)reports();else module(n);});}Button out=button("خروج از حساب");root.addView(out);out.setOnClickListener(v->login());}
    void module(int n){base(modules[n]);String table=tables[n];EditText[] fields;String[] labels;
        switch(n){case 0:labels=new String[]{"تاریخ (شمسی)","ماشین / کد","راننده","ساعت شروع","ساعت پایان","استراحت (دقیقه)","خرابی / توقف","مرخصی","نرخ توافقی هر ساعت (تومان)","توضیحات"};break;
        case 1:labels=new String[]{"نام و نام خانوادگی","شماره تماس","سمت","تاریخ شروع","حقوق / دستمزد","وضعیت"};break;
        case 2:labels=new String[]{"تاریخ","فروشنده","مقدار لیتر","قیمت هر لیتر","ماشین","تحویل‌دهنده","توضیحات"};break;
        case 3:labels=new String[]{"تاریخ","دسته هزینه","مبلغ (تومان)","شرح","فروشنده / دریافت‌کننده"};break;
        default:labels=new String[]{"کد نمونه","تاریخ (شمسی)","نوع نمونه","مختصات GPS","عمق / ارتفاع","نام نمونه‌بردار","شرح","نتیجه آزمایش"};}
        fields=new EditText[labels.length];for(int i=0;i<labels.length;i++){fields[i]=new EditText(this);fields[i].setHint(labels[i]);root.addView(fields[i]);}
        Button save=button("ثبت اطلاعات");root.addView(save);save.setOnClickListener(v->{StringBuilder val=new StringBuilder();for(int i=0;i<labels.length;i++){if(i>0)val.append(" | ");val.append(labels[i]).append(": ").append(fields[i].getText().toString());}db.add(table,currentUser,val.toString());toast("ثبت شد");module(n);});
        root.addView(text("ثبت‌های اخیر",18));Cursor c=db.list(table);while(c.moveToNext()){root.addView(text(c.getString(1)+" • "+c.getString(2)+"\n"+c.getString(3),14));}c.close();Button back=button("بازگشت");root.addView(back);back.setOnClickListener(v->dashboard());
    }
    void reports(){base("گزارش‌های معدن");for(String t:tables){Cursor c=db.list(t);root.addView(text(tablesLabel(t)+": "+c.getCount()+" ثبت",16));c.close();}Button back=button("بازگشت");root.addView(back);back.setOnClickListener(v->dashboard());}
    String tablesLabel(String t){switch(t){case "work":return "کارکرد ماشین";case "staff":return "کارکنان";case "fuel":return "سوخت";case "expenses":return "هزینه";case "samples":return "نمونه‌ها";default:return "کاربران";}}
    void users(){base("مدیریت کاربران (مدیر اصلی)");EditText name=new EditText(this);name.setHint("نام کاربری جدید");EditText pass=new EditText(this);pass.setHint("رمز عبور");pass.setInputType(129);root.addView(name);root.addView(pass);Button add=button("ایجاد کاربر");root.addView(add);add.setOnClickListener(v->{if(name.getText().toString().trim().isEmpty()||pass.getText().length()<6){toast("نام کاربری و رمز حداقل ۶ حرف لازم است");return;}if(db.addUser(name.getText().toString().trim(),pass.getText().toString(),false)){toast("کاربر ساخته شد");users();}else toast("نام کاربری تکراری است");});Cursor c=db.list("users");while(c.moveToNext())root.addView(text(c.getString(1)+" • "+c.getString(2),15));c.close();Button back=button("بازگشت");root.addView(back);back.setOnClickListener(v->dashboard());}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
