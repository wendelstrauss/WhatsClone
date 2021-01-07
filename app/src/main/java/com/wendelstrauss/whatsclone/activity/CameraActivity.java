package com.wendelstrauss.whatsclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.CameraControlador;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, Camera.ShutterCallback, Camera.PictureCallback{

    private SurfaceView cameraView;
    private ImageView flash, botao, trocar;
    private CameraControlador cameraControlador;
    private Bitmap imagem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //permissao
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        //inicializar componentes
        inicializarComponentes();

        botao.setOnClickListener(this);

    }

    private void inicializarComponentes(){

        cameraView = findViewById(R.id.viewCameraView);
        flash = findViewById(R.id.flashCameraView);
        botao = findViewById(R.id.botaoCameraView);
        trocar = findViewById(R.id.switchCameraView);

        //configuracoes iniciais
        cameraControlador = new CameraControlador( this, cameraView );


    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        imagem = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        abrirTratarImagem();
        cameraControlador.pararVisualizacao();
    }

    @Override
    public void onShutter() {

    }

    @Override
    public void onClick(View view) {
        cameraControlador.tirarFoto(this, null, this);
    }

    private void abrirTratarImagem(){
        Intent tratarImg = new Intent(CameraActivity.this, TratarImagemActivity.class);
        startActivity(tratarImg);
    }
}
