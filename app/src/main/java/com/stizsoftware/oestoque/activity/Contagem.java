package com.stizsoftware.oestoque.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.stizsoftware.oestoque.R;
import com.stizsoftware.oestoque.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class Contagem extends AppCompatActivity {

    ImageButton btCamera;
    Button btQuantidade, btVoltar, btFinalizar, btEliminarLinha;
    ImageButton btSalvar, btLimpar;
    EditText tbCodigoItem, tbQtdItem;
    TextView tvDescricao, tvTitulo;
    ConstraintLayout loContagem;
    ProgressBar pbContagem;
    Switch switchAutoCamera;

    String linkPlanilha;
    String filial;
    String tipoContagem;

    ArrayList<Item> listaEstoqueProtheus = new ArrayList<>();
    ArrayList<Item> listaEstoqueProtheusDescricoes = new ArrayList<>();

    int parametroAutoCamera = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contagem);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("filial");
            String tipo = extras.getString("tipo");
            filial = value;
            tipoContagem = tipo;
        }

        parametroAutoCamera = 0;

        if(filial.equals("0112"))
            linkPlanilha = getResources().getString(R.string.link_canoinhas);
        if(filial.equals("0117"))
            linkPlanilha = getResources().getString(R.string.link_rionegro);
        if(filial.equals("0126"))
            linkPlanilha = getResources().getString(R.string.link_sbs);
        if(filial.equals("0147"))
            linkPlanilha = getResources().getString(R.string.link_uniao);

        tvTitulo = findViewById(R.id.textView);
        loContagem = findViewById(R.id.loTelaContagem);
        pbContagem = findViewById(R.id.pbContagem);
        btCamera = findViewById(R.id.btCamera);
        btQuantidade = findViewById(R.id.btQuantidade);
        btSalvar = findViewById(R.id.btSalvar);
        btLimpar = findViewById(R.id.btLimpar);
        btVoltar = findViewById(R.id.btVoltar);
        btEliminarLinha = findViewById(R.id.btEliminarUltimaLinha);
        btFinalizar = findViewById(R.id.btFinalizar);
        tbCodigoItem = findViewById(R.id.tbCodigoItem);
        tbQtdItem = findViewById(R.id.tbQtdItem);
        tvDescricao = findViewById(R.id.tvDescricao);
        switchAutoCamera = findViewById(R.id.switchAbrirCameraAuto);

        if(tipoContagem.equals("t"))
            tvTitulo.setText("Contagem Total do Estoque");
        if(tipoContagem.equals("p"))
            tvTitulo.setText("Contagem Parcial do Estoque");

        pbContagem.setVisibility(View.VISIBLE);
        loContagem.setVisibility(View.GONE);

        recuperaItem();


        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
                /*
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                intent.putExtra("SCAN_ORIENTATION", "PORTRAIT");
                startActivityForResult(intent, 0);*/
            }
        });
        btQuantidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtdSugerida;
                qtdSugerida = btQuantidade.getText().toString();
                tbQtdItem.setText(qtdSugerida);
            }
        });
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    int qtdInserida = Integer.parseInt(tbCodigoItem.getText().toString());
                }
                catch(Exception e)
                {
                    Toast.makeText(Contagem.this, "Existem campos obrigatórios não preenchidos", Toast.LENGTH_SHORT).show();
                }
                if(!tbQtdItem.getText().toString().equals("") && !tbCodigoItem.getText().toString().equals(""))
                {
                    salvarItem();
                }
                else
                {
                    Toast.makeText(Contagem.this, "Existem campos obrigatórios não preenchidos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tbCodigoItem.setText("");
                tvDescricao.setText("-");
                tbQtdItem.setText("");
                btQuantidade.setText("0");
                tbCodigoItem.requestFocus();
            }
        });
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value=filial;
                Intent i = new Intent(Contagem.this, Modulos.class);
                i.putExtra("filial",value);
                startActivity(i);
            }
        });

        btFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(Contagem.this).create();
                alertDialog.setTitle("Finalizar Contagem");
                alertDialog.setMessage("Ao finalizar esta contagem, serão processadas as quantidades informadas até o momento. Após isto, não será possível continuar com a contagem atual. Deseja realmente finalizar a contagem?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finalizarContagem();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        btEliminarLinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Contagem.this).create();
                alertDialog.setTitle("Eliminar Última Inclusão");
                alertDialog.setMessage("Ao confirmar, o último registro inserido nesta contagem será eliminado. Deseja realmente finalizar a contagem?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                excluirUltimaLinha();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        tbCodigoItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    String codigoDigitado = tbCodigoItem.getText().toString();
                    int parametro = 0;
                    for(Item item: listaEstoqueProtheus)
                    {
                        if(item.getCodigo().equals(codigoDigitado))
                        {
                            tvDescricao.setText(item.getDescricao());
                            btQuantidade.setText(String.valueOf(item.getQtd()));
                            tbQtdItem.setText("");
                            tbQtdItem.requestFocus();
                            parametro = 1;
                        }
                    }
                    if(parametro == 0)
                    {
                        for(Item item: listaEstoqueProtheusDescricoes)
                        {
                            if(item.getCodigo().equals(codigoDigitado))
                            {
                                tvDescricao.setText(item.getDescricao());
                                btQuantidade.setText("0");
                                tbQtdItem.setText("");
                                tbQtdItem.requestFocus();
                                parametro = 1;
                            }
                        }
                        if(parametro == 0)
                        {
                            tvDescricao.setText("-");
                            btQuantidade.setText("0");
                            tbQtdItem.requestFocus();
                        }
                    }
                }
            }
        });
        switchAutoCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    parametroAutoCamera = 1;
                }
                else
                {
                    parametroAutoCamera = 0;
                }
            }
        });
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                tbCodigoItem.setText(contents);
                int parametro = 0;
                for(Item item: listaEstoqueProtheus)
                {
                    if(item.getCodigo().equals(contents))
                    {
                        tvDescricao.setText(item.getDescricao());
                        btQuantidade.setText(String.valueOf(item.getQtd()));
                        tbQtdItem.setText("");
                        parametro = 1;
                    }
                }
                if(parametro == 0)
                {
                    tvDescricao.setText("-");
                    btQuantidade.setText("0");
                }
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }*/

    public void recuperaItem()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, linkPlanilha + "?action=getItem",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Contagem.this, "Ocorreu um erro ao efetuar a operação", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void parseResponse(String jsonResponse)
    {
        try
        {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");
            for(int i = 0; i < jarray.length(); i++)
            {
                JSONObject jo = jarray.getJSONObject(i);
                Item itemEstoque = new Item();

                String itemCodigo = jo.getString("itemCodigo");
                String itemNome = jo.getString("itemNome");
                String itemQtd = jo.getString("itemQtd");

                itemEstoque.setCodigo(itemCodigo);
                itemEstoque.setDescricao(itemNome);
                int qtdEstoque = 0;
                try{
                    qtdEstoque = Integer.parseInt(itemQtd);
                }
                catch (Exception e)
                {
                    qtdEstoque = 0;
                }
                itemEstoque.setQtd(qtdEstoque);

                listaEstoqueProtheus.add(itemEstoque);
            }
            recuperaItemProtheus();
            //pbContagem.setVisibility(View.GONE);
            //loContagem.setVisibility(View.VISIBLE);
        }
        catch(JSONException e)
        {

        }
    }

    public void salvarItem()
    {
        pbContagem.setVisibility(View.VISIBLE);

        String codItem = tbCodigoItem.getText().toString();
        String descItem = tvDescricao.getText().toString();
        String qtdItem = tbQtdItem.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, linkPlanilha,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Contagem.this, response, Toast.LENGTH_SHORT).show();
                        pbContagem.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                        btQuantidade.setText("0");

                        if(parametroAutoCamera == 1)
                        {
                            scanCode();
                        }
                        tbCodigoItem.requestFocus();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Contagem.this, "Ocorreu um erro ao registrar o item", Toast.LENGTH_SHORT).show();
                        pbContagem.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                        btQuantidade.setText("0");
                        tbCodigoItem.requestFocus();
                    }
                }
        ) {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("action", "addItem");
                params.put("itemCod", codItem);
                params.put("itemDesc", descItem);
                params.put("itemQtd", qtdItem);

                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void finalizarContagem()
    {
        pbContagem.setVisibility(View.VISIBLE);
        loContagem.setEnabled(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, linkPlanilha,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Nenhum item foi registrado na contagem"))
                        {
                            Toast.makeText(Contagem.this, response, Toast.LENGTH_SHORT).show();
                            pbContagem.setVisibility(View.GONE);
                            tbCodigoItem.setText("");
                            tvDescricao.setText("-");
                            tbQtdItem.setText("");
                            btQuantidade.setText("0");
                            tbCodigoItem.requestFocus();
                        }
                        else
                        {
                            Toast.makeText(Contagem.this, response, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Modulos.class));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Contagem.this, "Ocorreu um erro ao finalizar a contagem:" + error.toString(), Toast.LENGTH_SHORT).show();
                        pbContagem.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                        btQuantidade.setText("0");
                    }
                }
        ) {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                if(tipoContagem.equals("t"))
                    params.put("action", "finalizar");
                if(tipoContagem.equals("p"))
                    params.put("action", "finalizar-parcial");
                return params;
            }
        };
        int socketTimeOut = 240000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void excluirUltimaLinha()
    {
        pbContagem.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, linkPlanilha,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Contagem.this, response, Toast.LENGTH_SHORT).show();
                        pbContagem.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                        btQuantidade.setText("0");
                        tbCodigoItem.requestFocus();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Contagem.this, "Ocorreu um erro ao excluir o item", Toast.LENGTH_SHORT).show();
                        pbContagem.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                        btQuantidade.setText("0");
                        tbCodigoItem.requestFocus();
                    }
                }
        ) {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("action", "excluirLinha");

                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Se necessário, aperte o botão de aumentar o volume para ligar o flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(Escanear.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        if(result.getContents() !=null)
        {
            String retorno = result.getContents();
            tbCodigoItem.setText(retorno);
            int parametro = 0;
            for(Item item: listaEstoqueProtheus)
            {
                if(item.getCodigo().equals(retorno))
                {
                    tvDescricao.setText(item.getDescricao());
                    btQuantidade.setText(String.valueOf(item.getQtd()));
                    tbQtdItem.setText("");
                    tbQtdItem.requestFocus();
                    parametro = 1;
                }
            }
            if(parametro == 0)
            {
                for(Item item: listaEstoqueProtheusDescricoes)
                {
                    if(item.getCodigo().equals(retorno))
                    {
                        tvDescricao.setText(item.getDescricao());
                        btQuantidade.setText("0");
                        tbQtdItem.setText("");
                        tbQtdItem.requestFocus();
                        parametro = 1;
                    }
                }
                if(parametro == 0)
                {
                    tvDescricao.setText("-");
                    btQuantidade.setText("0");
                    tbQtdItem.requestFocus();
                }
            }
        }
    });

    public void recuperaItemProtheus()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, linkPlanilha + "?action=getItemPedido",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseResponseProtheus(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Contagem.this, "Ocorreu um erro ao efetuar a operação", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void parseResponseProtheus(String jsonResponse)
    {
        try
        {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");
            for(int i = 0; i < jarray.length(); i++)
            {
                JSONObject jo = jarray.getJSONObject(i);
                Item itemEstoque = new Item();

                String itemCodigo = jo.getString("itemCodigo");
                String itemNome = jo.getString("itemNome");
                String itemQtd = jo.getString("itemQtd");

                itemEstoque.setCodigo(itemCodigo);
                itemEstoque.setDescricao(itemNome);
                int qtdEstoque = 0;
                try{
                    qtdEstoque = Integer.parseInt(itemQtd);
                }
                catch (Exception e)
                {
                    qtdEstoque = 0;
                }
                itemEstoque.setQtd(qtdEstoque);

                listaEstoqueProtheusDescricoes.add(itemEstoque);
            }
            pbContagem.setVisibility(View.GONE);
            loContagem.setVisibility(View.VISIBLE);
        }
        catch(JSONException e)
        {

        }
    }
}