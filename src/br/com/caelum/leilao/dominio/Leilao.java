package br.com.caelum.leilao.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leilao {

	private String descricao;
	private LocalDate data;
	private List<Lance> lances;
	private boolean encerrado;
	private int id;
	
	public Leilao(String descricao) {
		this(descricao, LocalDate.now());
	}
	
	public Leilao(String descricao, LocalDate data2) {
		this.descricao = descricao;
		this.data = data2;
		this.lances = new ArrayList<Lance>();
	}
	
	public void propoe(Lance lance) {
		if(lances.isEmpty() || podeDarLance(lance.getUsuario())) {
			lances.add(lance);
		}
	}

	private boolean podeDarLance(Usuario usuario) {
		return !ultimoLanceDado().getUsuario().equals(usuario) && qtdDeLancesDo(usuario) <5;
	}

	private int qtdDeLancesDo(Usuario usuario) {
		int total = 0;
		for(Lance l : lances) {
			if(l.getUsuario().equals(usuario)) total++;
		}
		return total;
	}

	private Lance ultimoLanceDado() {
		return lances.get(lances.size()-1);
	}

	public String getDescricao() {
		return descricao;
	}

	public List<Lance> getLances() {
		return Collections.unmodifiableList(lances);
	}

	public LocalDate getData() {
		return data;
	}

	public void encerra() {
		this.encerrado = true;
	}
	
	public boolean isEncerrado() {
		return encerrado;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
