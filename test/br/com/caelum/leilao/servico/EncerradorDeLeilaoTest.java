package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.email.Carteiro;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		LocalDate dataAnterior = LocalDate.of(1999, 1, 20);

//		LocalDate agora = LocalDate.now();
//		LocalDate dataFutura = LocalDate.of(2099, Month.JANUARY, 25);
//		Period periodo = Period.between(agora, dataFutura);		
		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//		String dataFormatada = formatter.format(agora);
		
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV de LED").naData(dataAnterior).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Lavadoura de Louca").naData(dataAnterior).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);	
		
		LeilaoDao daoFalso = mock(LeilaoDao.class);
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);
		
		Carteiro carteiroFalso = mock(Carteiro.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
		
	}
	
	@Test
	public void deveAtualizarLeiloesEncerrados() {
		LocalDate antiga = LocalDate.of(1999, Month.JANUARY, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("Tv").naData(antiga).constroi();
		
		LeilaoDao daoFalso = mock(LeilaoDao.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));
		
		Carteiro carteiroFalso = mock(Carteiro.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();		
		
		verify(daoFalso, times(1)).atualiza(leilao1);
		
//		verify(daoFalso, never()).atualiza(leilao1);  para garantir que o método nunca foi executado
//		verify(daoFalso, atLeastOnce()).atualiza(leilao1);  para garantir que o método foi invocado no mínimo uma vez
//		verify(daoFalso, atLeast(2)).atualiza(leilao1);  para garantir que o método foi invocado no mínimo 2 vezes		
//		verify(daoFalso, atMost(5)).atualiza(leilao1);  para garantir que o método foi invocado no máximo 5 vezes		
		
	}
	
	@Test
	public void deveContinuarAExecucaoMesmoQuandoDaoFalha() {
		LocalDate antiga = LocalDate.of(1999, Month.JANUARY, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("Tv").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("GEladeira").naData(antiga).constroi();
		
		LeilaoDao daoFalso = mock(LeilaoDao.class);
		Carteiro carteiroFalso = mock(Carteiro.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		verify(daoFalso).atualiza(leilao2);
		verify(carteiroFalso).envia(leilao2);
		
		verify(carteiroFalso, times(0)).envia(leilao1);
		
	}
	
	
}
