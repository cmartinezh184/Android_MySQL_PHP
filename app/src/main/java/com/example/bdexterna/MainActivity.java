package com.example.bdexterna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText codigo, descripcion;
    private Button agregar, modificar, eliminar, subirImagen, enlistar;
    private ListView listaProductos;
    private final String url = "http://192.168.100.44/inventarios/";

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
        subirImagen = findViewById(R.id.btn_adjuntar_foto);
        listaProductos = findViewById(R.id.listaProductos);

        cargarLista();

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("insertar.php?codigo=" + codigo.getText().toString() +
                        "&descripcion=" + descripcion.getText().toString());
                codigo.setText("");
                descripcion.setText("");
                cargarLista();
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("modificar.php?codigo=" + codigo.getText().toString() +
                        "&descripcion=" + descripcion.getText().toString());
                codigo.setText("");
                descripcion.setText("");
                cargarLista();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("eliminar.php?codigo=" + codigo.getText().toString());
                codigo.setText("");
                descripcion.setText("");
                cargarLista();
            }
        });

        enlistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConectarServicio("enlistar.php");
            }
        });

        subirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadImageActivity.class));
            }
        });
    }

    private void ConectarServicio(String q){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + q, new Response.Listener<String>() {
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

    private boolean cargarLista(){
        RequestQueue queue = Volley.newRequestQueue(this);
        final ArrayList<String> lista = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url + "enlistar.php", null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    JSONObject object = null;
                    try {
                        object = response.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String objectString = object.toString();
                    lista.add(objectString);
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error response: ", error);
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listaProductos.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        queue.add(jsonArrayRequest);

        return true;
    }

}
