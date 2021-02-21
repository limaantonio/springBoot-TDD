package com.example.demo.repository.helper;

import com.example.demo.domain.Pessoa;
import com.example.demo.repository.filtro.PessoaFiltro;

import java.util.List;

public interface PessoaRepositoryQueries {

    List<Pessoa> filtrar(PessoaFiltro filtro);



}
