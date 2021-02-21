package com.example.demo.service;

import com.example.demo.domain.Pessoa;
import com.example.demo.domain.Telefone;
import com.example.demo.repository.PessoaRepository;
import com.example.demo.service.exception.TelefoneNaoEncontradoException;
import com.example.demo.service.exception.UnicidadeCpfException;
import com.example.demo.service.exception.UnidadeTelefoneExecption;
import com.example.demo.service.impl.PessoaServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class PessoaServiceTest {

    private static final String NOME = "Antonio";
    private static final String CPF = "898949458";
    private static final String DDD = "55" ;
    private static final String NUMBER = "22211455" ;


    @MockBean
    private PessoaRepository pessoaRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public PessoaService sut;

    private Pessoa pessoa;

    private Telefone telefone;

    @Before
    public void setUp() throws Exception {
        sut = new PessoaServiceImpl(pessoaRepository);

        pessoa = new Pessoa();
        pessoa.setNome(NOME);
        pessoa.setCpf(CPF);

        telefone = new Telefone();
        telefone.setDdd(DDD);
        telefone.setNumero(NUMBER);

        pessoa.setTelefones(Arrays.asList(telefone));

        when(pessoaRepository.findByCpf(CPF)).thenReturn(Optional.empty());
        when(pessoaRepository.findByTelefoneDddAndTelefoneNumero(DDD, NUMBER)).thenReturn(Optional.empty());

    }

    @Test
    public void deve_salvar_pessoa_no_repositorio() throws  Exception{
       sut.salvar(pessoa);
        verify(pessoaRepository).save(pessoa);
    }


    @Test
    public void nao_deve_salvar_duas_pessoas_com_o_mesmo_cpf() throws Exception {
        when(pessoaRepository.findByCpf(CPF)).thenReturn(Optional.of(pessoa));

        expectedException.expect(UnicidadeCpfException.class);
        expectedException.expectMessage("Já existe pessoa cadastrada com o CPF '"+ CPF +"'");

        sut.salvar(pessoa);
    }

    @Test(expected =  UnidadeTelefoneExecption.class)
    public void nao_deve_salvar_duas_pessoas_com_o_mesmo_telefone() throws Exception {
        when(pessoaRepository.findByTelefoneDddAndTelefoneNumero(DDD, NUMBER)).thenReturn(Optional.of(pessoa));

        sut.salvar(pessoa);
    }

    @Test(expected = TelefoneNaoEncontradoException.class)
    public void deve_retornar_execao_de_nao_encontrado_quando_nao_existe_pessoa_com_o_ddd_e_numero() throws Exception {
        sut.buscarPorTelefone(telefone);

    }

    @Test public void deve_retornar_dados_do_telefone_dentro_da_exececao_de_telefone_nao_encontrado_exception() throws Exception{
        expectedException.expect(TelefoneNaoEncontradoException.class);
        expectedException.expectMessage("Não existe pessoa com o telefone (" + DDD +")" + NUMBER);
        sut.buscarPorTelefone(telefone);
    }

    @Test
    public void deve_procurar_pessoa_pelo_dd_e_numero_do_telefone() throws Exception {
        when(pessoaRepository.findByTelefoneDddAndTelefoneNumero(DDD, NUMBER)).thenReturn(Optional.of(pessoa));
      Pessoa pessoaTeste =  sut.buscarPorTelefone(telefone);

      verify(pessoaRepository).findByTelefoneDddAndTelefoneNumero(DDD, NUMBER);

      assertThat(pessoaTeste).isNotNull();
      assertThat(pessoaTeste.getNome()).isEqualTo(NOME);
      assertThat(pessoaTeste.getCpf()).isEqualTo(CPF);
    }


}
