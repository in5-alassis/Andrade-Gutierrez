package com.lecom.workflow.integracoes.IntTemplate;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.workflow.common.util.Funcoes;

public class GetParametros {
	
	// CRIA OS OBJETOS QUE TERAO OS VALORES ARMAZENADOS
	private Map<String, String> paramGerais = new HashMap<String, String>();
	private Map<String, String> paramExecEtpGen = new HashMap<String, String>();
	
	/**
	 *  RETORNA O CAMINHO PARA O DIRETORIO config NO SERVIDOR
	 * @return
	 */
	String path() {
		return Funcoes.getWFRootDir() + "upload/cadastros/config/";
	}
	
	/**
	 * DEFINI O MAPA DE PARAMETROS GENERICOS PARA EXECUCAO DE ROTINAS NO SERVIDOR
	 * @param LOGGER
	 * @param paramGerais
	 */
	public void setParamGerais(Logger LOGGER, Map<String, String> paramGerais) {
		try {
			String pathGerais = Funcoes.getCanonicalPath(LOGGER, this.path());
			paramGerais = new HashMap<String, String>();
			this.paramGerais  = Funcoes.getParametrosLecom(LOGGER, pathGerais + "/ParametrosGerais.properties");
		} catch (Exception e) {
			LOGGER.error("ERRO AO DEFINIR OS PARAMETROS GERAIS");
		}
	}
	
	/**
	 * DEFINI O MAPA DE PARAMETROS PARA EXECUCAO DO ROBO RbExecutaEtapasGenerico
	 * @param LOGGER
	 * @param paramExecEtpGen
	 */
	public void setParamExecEtpGen(Logger LOGGER, Map<String, String> paramExecEtpGen) {
		try {
			String pathGen = Funcoes.getCanonicalPath(LOGGER, this.path());
			paramExecEtpGen = new HashMap<String, String>();
			this.paramExecEtpGen  = Funcoes.getParametrosLecom(LOGGER, pathGen + "/IntTemplate.properties");
		} catch (Exception e) {
			LOGGER.error("ERRO AO DEFINIR OS PARAMETROS MODELO");
		}
	}

	public Map<String, String> getParamGerais(Logger LOGGER) {
		setParamGerais(LOGGER, paramGerais);
		return paramGerais;
	}
	
	public Map<String, String> getParamExecEtpGen(Logger LOGGER) {
		setParamExecEtpGen(LOGGER, paramExecEtpGen);
		return paramExecEtpGen;
	}
	
}
