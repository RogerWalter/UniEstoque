package com.stizsoftware.oestoque.activity;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stizsoftware.oestoque.R;

import java.util.HashMap;
import java.util.Map;

public class Modulos extends AppCompatActivity {

    ImageButton btContagem, btPedido;
    String filial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulos);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("filial");
            filial = value;
        }

        btContagem = findViewById(R.id.btContagem);
        btPedido = findViewById(R.id.btPedido);

        btContagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value=filial;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Modulos.this);
                LayoutInflater inflater = Modulos.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialogo_tipo_contagem, null);
                dialogBuilder.setView(dialogView);
                Button btParcial = (Button) dialogView.findViewById(R.id.btParcial);
                Button btTotal = (Button) dialogView.findViewById(R.id.btTotal);
                btParcial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tipo = "p";
                        Intent i = new Intent(Modulos.this, Contagem.class);
                        i.putExtra("filial",value);
                        i.putExtra("tipo", tipo);
                        startActivity(i);
                    }
                });
                btTotal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tipo = "t";
                        Intent i = new Intent(Modulos.this, Contagem.class);
                        i.putExtra("filial",value);
                        i.putExtra("tipo", tipo);
                        startActivity(i);
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        btPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value=filial;
                Intent i = new Intent(Modulos.this, Pedido.class);
                i.putExtra("filial",value);
                startActivity(i);
            }
        });
        /*StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwEUdBIyh9JYr9qEwd-Ctw8lYliy6W5xB5oOhEC-ORfYneA2TLDwwWEKECHcDWHy9qOzA/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Modulos.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Modulos.this, "Ocorreu um erro ao registrar o item", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("action", "addItem");
                params.put("itemCod", "103000014");
                params.put("itemQtd", "2400");

                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}