package com.gunay.photonoteapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.gunay.photonoteapp.databinding.ActivityAddBinding;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    //galireye gitmek için
    ActivityResultLauncher<Intent> activityResultLauncher;
    //izin istemek için
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityAddBinding binding;
    SQLiteDatabase database;

    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.image.setImageResource(R.drawable.selectok);
        // sistem tarih bilgisini alıp textview ile ekrana verme
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(new Date());
        binding.dateTextView.setText(currentDate);
        // database oluşturulması
        database = this.openOrCreateDatabase("Photos",MODE_PRIVATE,null);
        // launcher'in tanımlanması (yapması zorunlu)
        registerLauncher();

        int x = 1;

        // info bilgsisinin intentle alınması
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        // alinan info bilgisine gore ne yapılacagının belirlenmesi
        if(info.equals("add")){
            binding.nameTextText.setText("");
            binding.noteEditText.setText("");
            binding.image.setImageResource(R.drawable.selectok);
        }else {
            // bilgilerini alacagımızım id'nin alınması
            int photoId = intent.getIntExtra("photoId", 1);

            try {
                //Bu kod satırı, photoNote tablosundan id değeri photoId değişkenine eşit olan satırları seçen bir sorguyu çalıştırır.
                // Sorgu sonuçları, Cursor nesnesi cursor üzerinden erişilebilir.
                Cursor cursor = database.rawQuery("SELECT * FROM photoNote WHERE id = ?", new String[]{String.valueOf(photoId)});
                // id bilgisi alınan raw züerindeki başlıkları columun Index'leri alınır
                int nameIx = cursor.getColumnIndex("photoName");
                int noteIx = cursor.getColumnIndex("note");
                int dateIx = cursor.getColumnIndex("date");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()) {
                    // alınan indexler üzerinden veri getirilerek goruntulenmesi saglanır
                    binding.nameTextText.setText(cursor.getString(nameIx));
                    binding.noteEditText.setText(cursor.getString(noteIx));
                    binding.dateTextView.setText(cursor.getString(dateIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.image.setImageBitmap(bitmap);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_acti_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.equals("go")){
            if (item.getItemId() == R.id.save_photoNote){
                item.setVisible(false);
            }
            if (item.getItemId() == R.id.back_menu){
                Intent intent3 = new Intent(this, MainActivity.class);
                startActivity(intent3);
            }
        } else if ((item.getItemId() == R.id.save_photoNote)) {

                String name = binding.nameTextText.getText().toString();
                String note = binding.noteEditText.getText().toString();
                String date = binding.dateTextView.getText().toString();

                Bitmap bitmap = makeSmallerImage(selectedImage,300);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
                byte[] byteArray = outputStream.toByteArray();


                try {

                    // tablo oluşturulması
                    database.execSQL("CREATE TABLE IF NOT EXISTS photoNote(id INTEGER PRIMARY KEY, photoName VARCHAR, note VARCHAR, date VARCHAR, image BLOB)");
                    // sql komutlarıyla degiskenlerin birlikte kullanılması VALUES(?,?,?) soru isareti olan yerlere ekledigimiz degiskenler gelir
                    String sqlString = "INSERT INTO photoNote (photoName, note, date , image) VALUES(?,?,?,?)";
                    SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                    // degiskenlerin sqLiteStatement'a eklenmesi
                    sqLiteStatement.bindString(1,name);
                    sqLiteStatement.bindString(2,note);
                    sqLiteStatement.bindString(3,date);
                    sqLiteStatement.bindBlob(4,byteArray);
                    sqLiteStatement.execute();

                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent1 = new Intent(this, MainActivity.class);
                // icinde bulunanda dahi bütün activiteler kapanir.
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);

            }
            if (item.getItemId() == R.id.back_menu){
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
            }


        return super.onOptionsItemSelected(item);
    }

    // gorselin boyutunu daha kucuk yapar
    public Bitmap makeSmallerImage(Bitmap image , int maximumSize){
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1){
            //görsel yatay / landscape image
            width = maximumSize;
            height = (int) (width / bitmapRatio);

        }else{
            //görsel dikey / portrait image
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return image.createScaledBitmap(image,width,height,true);
    }

    public void imageAdd(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //Android 33+ -> READ_MEDIA_IMAGES
            //request permission - izin kontrolu - eğer izin verilmediyse
            // basa ContextCompat koymamızın sebebi app'in calıstıgı telefonun android versionu hangisiyse o versiona gore gerekmeyen izinleri sormasını engellemek yoksa sadece checkSelfPermission yeterli olur
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                // kullanıcıya açıklama göstermeye gerek varmı yokmu kontrolü - izini daha açıklamalı göstermelimiyim diye androide sorulur
                // buradaki compat ise acıklama göstermenin hangi versionda gerekip gerekmedigine göre sormasını engellemek
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){
                    //kullanıcı basana kadar eğer izini istemen gerekiyorsa snackbar üzerinde gösterme
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin isteme - yukarıda verilmediyse yaptık ya ondan
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();

                }else{
                    //izin isteme
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }

            }else{
                //gallery - izin verilmiş
                //yapacağımız işlem ve galeri dosyasının android konumu
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);

            }

        }else {
            //Android 32- -> READ_EXTERNAL_STORAGE
            //request permission - izin kontrolu - eğer izin verilmediyse
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                // kullanıcıya açıklama göstermeye gerek varmı yokmu kontrolü - izini daha açıklamalı göstermelimiyim diye androide sorulur
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //kullanıcı basana kadar eğer izini istemen gerekiyorsa snackbar üzerinde gösterme
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin isteme - yukarıda verilmediyse yaptık ya ondan
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();

                }else{
                    //izin isteme
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }else{
                //gallery - izin verilmiş
                //ACTION_PICK gittimiz yerden(gallery) bişey alma(görsel) aksiyonu gibi düşünülebilir
                //URI gideceğimiz konum (galeri yani)
                // burası ben galeriye gidicem bir görsel alıcam geri gelicem anlamına geliyor
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //activityResultLauncher.launch(intent); kodu, belirli bir intenti başlatarak belirli bir aktiviteyi çağırır ve bu aktivitenin sonuçlarını yakalamak için kullanılır.
                activityResultLauncher.launch(intent);

            }
        }
    }


    private void registerLauncher(){

        //registerForActivityResult() isin sonunda bir cevap alınacak olan bir islem yapıcagımızı belirtir
        //new ActivityResultContracts.StartActivityForResult() adındanda anlaşılacağı sonuç(cevap) için activite başlatır - buradaki sonuç görselin nerede kayıtlı olduğu bilgisi
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            // kullanıcının gittiği activity'den aldıgı sonuc - result icinde olur - burada galeriden seçilen görselin nerede kayıtlı olduğu bilgisi result icindedir.
            @Override
            public void onActivityResult(ActivityResult result) {
                //RESULT_OK result degiskeni icinde kullanıcı tarafından secilmis secilmis bir sonuc olup olmadığını kontrol eder. RESULT_OK(kullanıcı bisey secti) anlamına gelir
                if (result.getResultCode() == RESULT_OK){
                    // resulttan alınan veri intent seklindedir
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        // yukarıdaki getData() intent verirken buradaki ise uri(kullanıcının seçtigi göreslin nerede kayıtlı oldugu) verir
                        Uri imageData = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);

                        // görsel verisinin veri tabanına kaydedilebilmesi için bitmap'e çevrilmesi
                        // try - catch icinde hata olusursa hata cıktı olarak alınabilir ve programın cokmesini engeller
                        try {
                            // ana kodun yazıldığı kod blogu
                            //bitmap cevrimi API 28 ve üzeri icin
                            if (Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(AddActivity.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.image.setImageBitmap(selectedImage);

                                // bitmet cevrimi API 28 altı versionlar  icin
                            }else{
                                selectedImage = MediaStore.Images.Media.getBitmap(AddActivity.this.getContentResolver(),imageData);
                                binding.image.setImageBitmap(selectedImage);
                            }


                        }catch (Exception e){
                            // hata olduğunda calısacak kod blogu
                            //hata mesajı log da gösterilir
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        // izinin alındıgı yer , izini isteyen kısım
        //registerForActivityResult() isin sonunda bir cevap alınacak olan bir islem yapıcagımızı belirtir
        //new ActivityResultContracts.RequestPermission() izin istenecegini belirtir - izin isteme işlemi yapmamın sonucunda bir cevap alınacağını belirtir
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {

            // alınan true yada false sonucuna göre (istenen izin sonucunda) yapacağımızın işlemi new ActivityResultCallback<Boolean>() ile belirleriz
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //request granted - true
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //activityResultLauncher.launch(intent); kodu, belirli bir intenti başlatarak belirli bir aktiviteyi çağırır ve bu aktivitenin sonuçlarını yakalamak için kullanılır.
                    // izin verilirse diger laucher üzerinden galeriye gitme ve resmin alınma islemi yapılır
                    activityResultLauncher.launch(intent);

                }else{
                    //request denied - false
                    Toast.makeText(AddActivity.this,"Permission needed !",Toast.LENGTH_LONG).show();
                }
            }


        });
    }

}