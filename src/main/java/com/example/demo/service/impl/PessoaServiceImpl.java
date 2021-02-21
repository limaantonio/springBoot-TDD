package com.example.demo.service.impl;

import com.example.demo.domain.Pessoa;
import com.example.demo.domain.Telefone;
import com.example.demo.repository.PessoaRepository;
import com.example.demo.service.PessoaService;
import com.example.demo.service.exception.TelefoneNaoEncontradoException;
import com.example.demo.service.exception.UnicidadeCpfException;
import com.example.demo.service.exception.UnidadeTelefoneExecption;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PessoaServiceImpl implements PessoaService {
    private  final  PessoaRepository pessoaRepository;
    public PessoaServiceImpl(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public Pessoa salvar(Pessoa pessoa) throws UnicidadeCpfException, UnidadeTelefoneExecption {
        Optional<Pessoa> optional = pessoaRepository.findByCpf(pessoa.getCpf());

        if(optional.isPresent()){
            throw new UnicidadeCpfException("Já existe pessoa cadastrada com o CPF '"+ pessoa.getCpf() +"'");
        }

        final String ddd = pessoa.getTelefones().get(0).getDdd();
        final String numero = pessoa.getTelefones().get(0).getNumero();
        optional = pessoaRepository.findByTelefoneDddAndTelefoneNumero(ddd, numero );

        if(optional.isPresent()){
            throw new UnidadeTelefoneExecption();
        }

        return pessoaRepository.save(pessoa);
    }

    @Override
    public Pessoa buscarPorTelefone(Telefone telefone) throws TelefoneNaoEncontradoException {
        Optional<Pessoa> optional= pessoaRepository.findByTelefoneDddAndTelefoneNumero(telefone.getDdd(), telefone.getNumero());
        return optional.orElseThrow(() ->  new TelefoneNaoEncontradoException("Não existe pessoa com o telefone (" +telefone.getDdd() + ")" + telefone.getNumero()));
    }
}
