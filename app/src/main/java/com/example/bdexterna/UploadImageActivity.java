package com.example.bdexterna;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

public class UploadImageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button upload;
    private FloatingActionButton elegirFoto;
    private EditText nombreImagen;
    private ImageView imagen;

    private static final int STORAGE_PERMISSION_CODE = 2342;
    private static final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        requestStoragePermission();

        upload = findViewById(R.id.btn_upload_image);
        elegirFoto = findViewById(R.id.btn_choose_image);
        nombreImagen = findViewById(R.id.txt_nombre_imagen);
        imagen = findViewById(R.id.imagen_articulo);

        elegirFoto.setOnClickListener(this);
        upload.setOnClickListener(this);

    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permiso concebido", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagen.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);

        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ?",
                new String[]{document_id}, null
        );

        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void cargarImagen(){
        String nombre = nombreImagen.getText().toString().trim();
        String path = getPath(filePath);

        try{
            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, uploadId, MainActivity.url
                    + "subirFoto.php")
                    .addFileToUpload(path, "foto")
                    .addParameter("nombre", nombre)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

        } catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        if(v == upload){
            cargarImagen();
        }

        if(v == elegirFoto){
            showFileChooser();
        }
    }
}
