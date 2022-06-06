package com.stizsoftware.oestoque.activity;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

public class Pedido extends AppCompatActivity {

    Button btMais1, btMais10, btMais100, btMais1000;
    ImageButton btCamera;
    Button btVoltar, btFinalizar;
    ImageButton btSalvar, btLimpar;
    EditText tbCodigoItem, tbQtdItem;
    TextView tvDescricao;
    ConstraintLayout loContagem;
    ProgressBar pbPedido;

    String linkPlanilha;
    String filial;

    ArrayList<Item> listaEstoqueProtheus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("filial");
            filial = value;
        }

        if(filial.equals("0112"))
            linkPlanilha = getResources().getString(R.string.link_canoinhas);
        if(filial.equals("0117"))
            linkPlanilha = getResources().getString(R.string.link_rionegro);
        if(filial.equals("0126"))
            linkPlanilha = getResources().getString(R.string.link_sbs);
        if(filial.equals("0147"))
            linkPlanilha = getResources().getString(R.string.link_uniao);

        btMais1 = findViewById(R.id.btMais1);
        btMais10 = findViewById(R.id.btMais10);
        btMais100 = findViewById(R.id.btMais100);
        btMais1000 = findViewById(R.id.btMais1000);

        loContagem = findViewById(R.id.loTelaContagem);
        pbPedido = findViewById(R.id.pbPedido);
        btCamera = findViewById(R.id.btCamera);
        btSalvar = findViewById(R.id.btSalvar);
        btLimpar = findViewById(R.id.btLimpar);
        btVoltar = findViewById(R.id.btVoltar);
        btFinalizar = findViewById(R.id.btFinalizar);
        tbCodigoItem = findViewById(R.id.tbCodigoItem);
        tbQtdItem = findViewById(R.id.tbQtdItem);
        tvDescricao = findViewById(R.id.tvDescricao);

        pbPedido.setVisibility(View.VISIBLE);
        loContagem.setVisibility(View.GONE);

        recuperaItem();

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
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
                    Toast.makeText(Pedido.this, "Existem campos obrigatórios não preenchidos", Toast.LENGTH_SHORT).show();
                }
                if(!tbQtdItem.getText().equals("") && !tbCodigoItem.getText().equals(""))
                {
                    salvarItem();
                }
                else
                {
                    Toast.makeText(Pedido.this, "Existem campos obrigatórios não preenchidos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tbCodigoItem.setText("");
                tvDescricao.setText("-");
                tbQtdItem.setText("");
            }
        });
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value=filial;
                Intent i = new Intent(Pedido.this, Modulos.class);
                i.putExtra("filial",value);
                startActivity(i);
            }
        });

        btFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(Pedido.this).create();
                alertDialog.setTitle("Limpar Planilha de Pedido");
                alertDialog.setMessage("Ao confirmar, todos os itens que estão na planilha de Pedido serão excluídos. Essa função é usada para iniciar um novo pedido. Deseja realmente continuar?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                limparPedido();
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
                            parametro = 1;
                        }
                    }
                    if(parametro == 0)
                    {
                        tvDescricao.setText("-");
                    }
                }
            }
        });

        btMais1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tbQtdItem.getText().toString().equals(""))
                    tbQtdItem.setText("0");
                try{
                    int qtdAtual = Integer.parseInt(tbQtdItem.getText().toString());
                    int novaQtd = qtdAtual + 1;
                    tbQtdItem.setText(String.valueOf(novaQtd));
                }
                catch(Exception e)
                {
                    tbQtdItem.setText("0");
                }
            }
        });
        btMais10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tbQtdItem.getText().toString().equals(""))
                    tbQtdItem.setText("0");
                try{
                    int qtdAtual = Integer.parseInt(tbQtdItem.getText().toString());
                    int novaQtd = qtdAtual + 10;
                    tbQtdItem.setText(String.valueOf(novaQtd));
                }
                catch(Exception e)
                {
                    tbQtdItem.setText("0");
                }
            }
        });
        btMais100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tbQtdItem.getText().toString().equals(""))
                    tbQtdItem.setText("0");
                try{
                    int qtdAtual = Integer.parseInt(tbQtdItem.getText().toString());
                    int novaQtd = qtdAtual + 100;
                    tbQtdItem.setText(String.valueOf(novaQtd));
                }
                catch(Exception e)
                {
                    tbQtdItem.setText("0");
                }
            }
        });
        btMais1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tbQtdItem.getText().toString().equals(""))
                    tbQtdItem.setText("0");
                try{
                    int qtdAtual = Integer.parseInt(tbQtdItem.getText().toString());
                    int novaQtd = qtdAtual + 1000;
                    tbQtdItem.setText(String.valueOf(novaQtd));
                }
                catch(Exception e)
                {
                    tbQtdItem.setText("0");
                }
            }
        });
    }


    public void recuperaItem()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, linkPlanilha + "?action=getItemPedido",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Pedido.this, "Ocorreu um erro ao efetuar a operação", Toast.LENGTH_SHORT).show();
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
            pbPedido.setVisibility(View.GONE);
            loContagem.setVisibility(View.VISIBLE);
        }
        catch(JSONException e)
        {

        }
    }
    public void salvarItem()
    {
        pbPedido.setVisibility(View.VISIBLE);

        String codItem = tbCodigoItem.getText().toString();
        String descItem = tvDescricao.getText().toString();
        String qtdItem = tbQtdItem.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, linkPlanilha,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Pedido.this, response, Toast.LENGTH_SHORT).show();
                        pbPedido.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Pedido.this, "Ocorreu um erro ao registrar o item", Toast.LENGTH_SHORT).show();
                        pbPedido.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                    }
                }
        ) {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("action", "addItemPedido");
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

    public void limparPedido()
    {
        pbPedido.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, linkPlanilha,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Pedido.this, response, Toast.LENGTH_SHORT).show();
                        pbPedido.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                        startActivity(new Intent(getApplicationContext(), Modulos.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Pedido.this, "Ocorreu um erro ao registrar o item", Toast.LENGTH_SHORT).show();
                        pbPedido.setVisibility(View.GONE);
                        tbCodigoItem.setText("");
                        tvDescricao.setText("-");
                        tbQtdItem.setText("");
                    }
                }
        ) {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("action", "limparPedido");

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
                    tbQtdItem.setText("");
                    tbQtdItem.requestFocus();
                    parametro = 1;
                }
            }
            if(parametro == 0)
            {
                tvDescricao.setText("-");
            }
        }
    });
}