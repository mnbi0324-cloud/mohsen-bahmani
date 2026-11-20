package com.mineload.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import android.database.Cursor;
import java.util.*;

public class MainActivity extends Activity {
 DB db; String user=""; boolean admin=false; LinearLayout root; int green=Color.rgb(20,105,76);
 final String[] categories={"کارکرد ماشین‌آلات","کارکنان","سوخت","هزینه‌ها","نمونه‌های معدنی","پرداخت‌ها"};
 final String[][] fields={
  {"تاریخ","ماشین/کد","راننده","شروع کار","پایان کار","استراحت (دقیقه)","توقف/خرابی (دقیقه)","نرخ توافقی هر ساعت (تومان)","شرح"},
  {"نام و نام خانوادگی","سمت","شماره تماس","حقوق ماهانه (تومان)","تاریخ شروع","وضعیت"},
  {"تاریخ","ماشین","راننده","فروشنده","لیتر","قیمت هر لیتر (تومان)","کیلومتر/ساعت کارکرد","شرح"},
  {"تاریخ","عنوان هزینه","دسته","مبلغ (تومان)","پرداخت‌کننده/گیرنده","شرح"},
  {"کد نمونه","تاریخ","نوع نمونه","مختصات GPS","عمق/ارتفاع","نمونه‌بردار","نتیجه آزمایش","شرح"},
  {"تاریخ","نام شخص/راننده","بابت","مبلغ (تومان)","روش پرداخت","توضیح"}
 };
 int dp(float n){return (int)(n*getResources().getDisplayMetrics().density);}
 TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(Color.rgb(35,48,45));t.setPadding(dp(10),dp(8),dp(10),dp(8));return t;}
 Button btn(String s){Button b=new Button(this);b.setText(s);return b;}
 void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
 public void onCreate(Bundle b){super.onCreate(b);db=new DB(this);login();}
 void page(String title){root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(14),dp(12),dp(14),dp(12));root.setBackgroundColor(Color.rgb(247,249,248));root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);TextView h=label(title,22);h.setGravity(Gravity.CENTER);h.setTextColor(green);h.setTypeface(null,1);root.addView(h);ScrollView sc=new ScrollView(this);sc.setFillViewport(true);LinearLayout content=new LinearLayout(this);content.setOrientation(1);content.setPadding(dp(14),dp(12),dp(14),dp(12));content.setBackgroundColor(Color.rgb(247,249,248));content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);content.addView(root);sc.addView(content);setContentView(sc);}
 EditText edit(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextSize(16);e.setSingleLine(false);root.addView(e);return e;}
 void login(){page("مدیریت معدن | مهندس بهمنی");root.addView(label("ورود به برنامه",18));EditText u=edit("نام کاربری");EditText p=edit("رمز عبور");p.setInputType(129);Button b=btn("ورود");root.addView(b);root.addView(label("ورود اولیه: admin / admin123 — لطفاً رمز مدیر را تغییر دهید.",13));b.setOnClickListener(v->{if(db.login(u.getText().toString().trim(),p.getText().toString())){user=u.getText().toString().trim();admin=db.isAdmin(user);home();}else toast("نام کاربری یا رمز اشتباه است");});}
 void home(){page("داشبورد معدن");root.addView(label("کاربر: "+user,15));for(int i=0;i<categories.length;i++){final int ix=i;Button b=btn(categories[i]);root.addView(b);b.setOnClickListener(v->form(ix));}
 Button report=btn("گزارش و خروجی");root.addView(report);report.setOnClickListener(v->reports());
 if(admin){Button users=btn("مدیریت کاربران");root.addView(users);users.setOnClickListener(v->users());}
 Button backup=btn("پشتیبان / اشتراک اطلاعات");root.addView(backup);backup.setOnClickListener(v->exportCsv());
 root.addView(label("سازنده: مهندس بهمنی • 09192426409",13));Button logout=btn("خروج");root.addView(logout);logout.setOnClickListener(v->login());}
 void form(int cat){page(categories[cat]+" • ثبت اطلاعات");EditText[] f=new EditText[fields[cat].length];for(int i=0;i<f.length;i++)f[i]=edit(fields[cat][i]);Button save=btn("ثبت");root.addView(save);save.setOnClickListener(v->{String[] vals=new String[f.length];for(int i=0;i<f.length;i++)vals[i]=f[i].getText().toString().trim();if(vals[0].isEmpty()){toast("فیلد اول (معمولاً تاریخ یا نام) را وارد کنید");return;}String extra="";double amount=0;
 if(cat==0){double hours=calcHours(vals[3],vals[4]);double rest=num(vals[5])/60.0;double downtime=num(vals[6])/60.0;double net=Math.max(0,hours-rest-downtime);amount=net*num(vals[7]);extra="\nساعت خام: "+fmt(hours)+"\nساعت خالص: "+fmt(net)+"\nمبلغ کارکرد: "+money(amount);}
 if(cat==2)amount=num(vals[4])*num(vals[5]);if(cat==3)amount=num(vals[3]);if(cat==5)amount=num(vals[3]);
 StringBuilder data=new StringBuilder();for(int i=0;i<vals.length;i++){if(i>0)data.append(" | ");data.append(fields[cat][i]).append(": ").append(vals[i]);}
 db.add(categories[cat],user,vals[0],data.toString()+extra,amount);
 toast("ثبت شد"+(extra.isEmpty()?"":"\n"+extra));form(cat);});
 root.addView(label("ثبت‌های قبلی",18));Cursor c=db.list(categories[cat]);while(c.moveToNext()){root.addView(label(c.getString(1)+" • "+c.getString(2)+"\n"+c.getString(3)+(c.getDouble(4)>0?"\nمبلغ: "+money(c.getDouble(4)):""),14));}c.close();Button back=btn("بازگشت");root.addView(back);back.setOnClickListener(v->home());}
 double calcHours(String start,String end){try{String[] a=start.split(":");String[] b=end.split(":");int s=Integer.parseInt(a[0])*60+Integer.parseInt(a[1]);int e=Integer.parseInt(b[0])*60+Integer.parseInt(b[1]);if(e<s)e+=1440;return(e-s)/60.0;}catch(Exception e){return 0;}}
 double num(String s){try{return Double.parseDouble(s.replace(",","").replace("٬","").trim());}catch(Exception e){return 0;}}
 String fmt(double n){return String.format(Locale.US,"%.2f",n);}
 String money(double n){return String.format(Locale.US,"%,.0f تومان",n);}
 void reports(){page("گزارش کلی");double sumWork=db.sum(categories[0]),sumFuel=db.sum(categories[2]),sumExpense=db.sum(categories[3]),sumPayments=db.sum(categories[5]);root.addView(label("مجموع کارکرد ماشین‌آلات: "+money(sumWork),16));root.addView(label("مجموع هزینه سوخت: "+money(sumFuel),16));root.addView(label("مجموع هزینه‌ها: "+money(sumExpense),16));root.addView(label("مجموع پرداخت‌ها: "+money(sumPayments),16));for(String cat:categories){Cursor c=db.list(cat);root.addView(label(cat+" • "+c.getCount()+" رکورد",14));c.close();}Button ex=btn("خروجی CSV / اشتراک");root.addView(ex);ex.setOnClickListener(v->exportCsv());Button back=btn("بازگشت");root.addView(back);back.setOnClickListener(v->home());}
 void exportCsv(){String csv=db.exportCsv();Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_SUBJECT,"گزارش معدن");i.putExtra(Intent.EXTRA_TEXT,csv);try{startActivity(Intent.createChooser(i,"ارسال / ذخیره گزارش"));}catch(Exception e){toast("امکان اشتراک وجود ندارد");}}
 void users(){page("مدیریت کاربران");EditText name=edit("نام کاربری");EditText pass=edit("رمز عبور (حداقل ۶ کاراکتر)");pass.setInputType(129);Button add=btn("ایجاد کاربر");root.addView(add);add.setOnClickListener(v->{String n=name.getText().toString().trim(),p=pass.getText().toString();if(n.isEmpty()||p.length()<6){toast("نام و رمز حداقل ۶ کاراکتر");return;}if(db.addUser(n,p,false)){toast("کاربر اضافه شد");users();}else toast("نام کاربری تکراری است");});Cursor c=db.listUsers();while(c.moveToNext())root.addView(label(c.getString(1)+" • "+(c.getInt(2)==1?"مدیر":"کاربر"),15));c.close();Button back=btn("بازگشت");root.addView(back);back.setOnClickListener(v->home());}
}
