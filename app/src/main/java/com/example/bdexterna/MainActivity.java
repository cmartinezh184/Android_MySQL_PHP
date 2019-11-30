package com.example.bdexterna;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText codigo, descripcion;
    private Button agregar, modificar, eliminar, enlistar;
    private ListView listaProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codigo = findViewById(R.id.txt_codigo);
        descripcion = findViewById(R.id.txt_descripcion);
        agregar = findViewById(R.id.btn_agregar);
        modificar = findViewById(R.id.btn_modificar);
        eliminar = findViewById(R.id.btn_eliminar);
        enlistar = findViewById(R.id.btn_enlistar);
        listaProductos = findViewById(R.id.listaProductos);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("http://192.168.100.44/inventarios/insertar.php?codigo=" +
                        codigo.getText().toString() + "&descripcion=" + descripcion.getText().toString());
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("http://192.168.100.44/inventarios/modificar.php");
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("http://192.168.100.44/inventarios/eliminar.php");
            }
        });

        enlistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("http://192.168.100.44/inventarios/enlistar.php");
            }
        });
    }

    private void ConectarServicio(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();
                parametros.put("codigo", codigo.getText().toString());
                parametros.put("descripcion", descripcion.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.start();
        requestQueue.add(stringRequest);
    }
}
