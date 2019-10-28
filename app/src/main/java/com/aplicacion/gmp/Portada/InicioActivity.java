package com.aplicacion.gmp.Portada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aplicacion.gmp.MainActivity;
import com.aplicacion.gmp.R;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
    }

    public void iniciar(View view){
        Intent sig = new Intent(this, MainActivity.class);
        startActivity(sig);
    }
}
