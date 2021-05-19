package br.com.caelum.leilao.servico;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.email.Carteiro;

public class EncerradorDeLeilao {

	private int total = 0;
	private final LeilaoDao dao;
	private final Carteiro carteiro;

	public EncerradorDeLeilao(LeilaoDao dao, Carteiro carteiro) {
		this.dao = dao;
		this.carteiro = carteiro;
	}

	public void encerra() {
		List<Leilao> todosLeiloesCorrentes = dao.correntes();

		for (Leilao leilao : todosLeiloesCorrentes) {
			try {
				if (comecouSemanaPassada(leilao)) {
					leilao.encerra();
					total++;
					dao.atualiza(leilao);
					carteiro.envia(leilao);
				}
			} catch (Exception e) {

			}
		}
	}

	private boolean comecouSemanaPassada(Leilao leilao) {
		return diasEntre(leilao.getData(), LocalDate.now()) >= 7;
	}

	private int diasEntre(LocalDate inicio, LocalDate fim) {

		long daysBetween = ChronoUnit.DAYS.between(inicio, fim);
		int diasNoIntervalo = (int) daysBetween;
		return diasNoIntervalo;
	}

	public int getTotalEncerrados() {
		return total;
	}
}
