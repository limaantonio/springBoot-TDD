package com.example.demo.repository.helper;

import com.example.demo.domain.Pessoa;
import com.example.demo.repository.filtro.PessoaFiltro;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PessoaRepositoryImpl implements PessoaRepositoryQueries{

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<Pessoa> filtrar(PessoaFiltro filtro) {
        final StringBuilder sb = new StringBuilder();
        final Map<String, Object> params = new HashMap<>();
        sb.append(" SELECT bean FROM Pessoa bean JOIN bean.telefones tele WHERE 1=1  ");

        preencherNomeSeNecessario(sb, params, filtro.getNome(), " AND bean.nome LIKE :nome ", "nome");

        preencherCpfSeNecessario(filtro, sb, params);

        preencherDddSeNecessario(filtro, sb, params);

        preencherNumeroTelefoneSeNecessario(filtro, sb, params);

        Query query = manager.createQuery(sb.toString(), Pessoa.class);
        preencherParamentrosDaQuery(params, query);
        return query.getResultList();
    }

    private void preencherNumeroTelefoneSeNecessario(PessoaFiltro filtro, StringBuilder sb, Map<String, Object> params) {
        if(StringUtils.hasText(filtro.getTelefone())){
            sb.append(" AND tele.numero = :numero");
            params.put("numero", filtro.getTelefone());
        }
    }

    private void preencherDddSeNecessario(PessoaFiltro filtro, StringBuilder sb, Map<String, Object> params) {
        if(StringUtils.hasText(filtro.getDdd())){
            sb.append(" AND tele.ddd = :ddd ");
            params.put("ddd", filtro.getDdd());
        }
    }

    private void preencherCpfSeNecessario(PessoaFiltro filtro, StringBuilder sb, Map<String, Object> params) {
        if(StringUtils.hasText(filtro.getCpf())){
            sb.append(" AND bean.cpf LIKE :cpf ");
            params.put("cpf", "%" + filtro.getCpf() + "%");
        }
    }

    private void preencherNomeSeNecessario(StringBuilder sb, Map<String, Object> params, String nome, String s, String nome2) {
        if (StringUtils.hasText(nome)) {
            sb.append(s);
            params.put(nome2, "%" + nome + "%");
        }
    }

    private void preencherParamentrosDaQuery(Map<String, Object> params, Query query) {
        for(Map.Entry<String, Object> param : params.entrySet()){
            query.setParameter(param.getKey(), param.getValue());
        }
    }
}
