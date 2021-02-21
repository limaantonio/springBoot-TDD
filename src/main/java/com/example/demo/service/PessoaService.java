package com.example.demo.service;

import com.example.demo.domain.Pessoa;
import com.example.demo.domain.Telefone;
import com.example.demo.service.exception.TelefoneNaoEncontradoException;
import com.example.demo.service.exception.UnicidadeCpfException;
import com.example.demo.service.exception.UnidadeTelefoneExecption;

public interface PessoaService {

    Pessoa salvar(Pessoa pessoa) throws UnicidadeCpfException, UnidadeTelefoneExecption;

    Pessoa buscarPorTelefone(Telefone telefone) throws TelefoneNaoEncontradoException;
}
