package com.stizsoftware.oestoque.model;

public class Item {
    private String codigo;
    private String descricao;
    private int qtd;

    public Item() {
    }

    public Item(String codigo, String descricao, int qtd) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.qtd = qtd;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }
}
