package com.stizsoftware.oestoque.activity;

import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stizsoftware.oestoque.R;

public class TelaLogin extends AppCompatActivity {

    Button bt112, bt117, bt126, bt147;

    String senha112 = "u@112";
    String senha117 = "n@117";
    String senha126 = "i@126";
    String senha147 = "f@147";

    String filialSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);
        getSupportActionBar().hide();

        bt112 = findViewById(R.id.bt0112);
        bt117 = findViewById(R.id.bt0117);
        bt126 = findViewById(R.id.bt0126);
        bt147 = findViewById(R.id.bt0147);

        bt112.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value="0112";
                filialSelecionada=value;
                coletarSenha();
            }
        });

        bt117.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value="0117";
                filialSelecionada=value;
                coletarSenha();
            }
        });

        bt126.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value="0126";
                filialSelecionada=value;
                coletarSenha();
            }
        });

        bt147.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value="0147";
                filialSelecionada=value;
                coletarSenha();
            }
        });
    }
    public void coletarSenha()
    {
       AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_senha, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.tbSenha);
        Button btLogar = (Button) dialogView.findViewById(R.id.btLogar);
        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filialSelecionada.equals("0112") && senha112.equals(editText.getText().toString()))
                {
                    Intent i = new Intent(TelaLogin.this, Modulos.class);
                    i.putExtra("filial",filialSelecionada);
                    startActivity(i);
                }
                else if(filialSelecionada.equals("0117") && senha117.equals(editText.getText().toString()))
                {
                    Intent i = new Intent(TelaLogin.this, Modulos.class);
                    i.putExtra("filial",filialSelecionada);
                    startActivity(i);
                }
                else if(filialSelecionada.equals("0126") && senha126.equals(editText.getText().toString()))
                {
                    Intent i = new Intent(TelaLogin.this, Modulos.class);
                    i.putExtra("filial",filialSelecionada);
                    startActivity(i);
                }
                else if(filialSelecionada.equals("0147") && senha147.equals(editText.getText().toString()))
                {
                    Intent i = new Intent(TelaLogin.this, Modulos.class);
                    i.putExtra("filial",filialSelecionada);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(TelaLogin.this, "A senha informada é inválida. Verifique e tente novamente.", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}