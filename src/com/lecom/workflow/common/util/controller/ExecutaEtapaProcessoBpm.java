package com.lecom.workflow.common.util.controller;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.workflow.cadastros.rotas.AprovaProcesso;
import com.lecom.workflow.cadastros.rotas.LoginAutenticacao;
import com.lecom.workflow.cadastros.rotas.exception.AprovaProcessoException;
import com.lecom.workflow.cadastros.rotas.exception.LoginAuthenticationException;
import com.lecom.workflow.cadastros.rotas.util.DadosLogin;
import com.lecom.workflow.cadastros.rotas.util.DadosProcesso;
import com.lecom.workflow.cadastros.rotas.util.DadosProcessoAbertura;
import com.lecom.workflow.common.util.ActionsDb;
import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.HandleErrors;

public class ExecutaEtapaProcessoBpm {
	
	private static String retExecAcaoEtp;
	
	// INSTANCIA O NOME DA CLASSE 
	private static final String className = Funcoes.getClassName(ExecutaEtapaProcessoBpm.class.getCanonicalName());

	public String execAcaoEtapa(Logger LOGGER, Connection cnBpm, HandleErrors handleErrors, ExecutaNotificacaoEmailBpm notifica, ActionsDb actDb,
								Map<String, String> paramGerais, boolean isTestando, int codProc, int codEtapa, int codCiclo, 
							    String modoTeste, String acao, Map<String, String> camposValores) throws Exception, AprovaProcessoException, LoginAuthenticationException {
		try {
			LOGGER.debug(new String(new char[50]).replace("\0", "-"));
			LOGGER.debug("EXECUTA ETAPA: " + codEtapa + " DO PROCESSO " + codProc);
			
			// VARIAVEIS DO AMBIENTE
			String usrRobo = paramGerais.get("usrRobo");
			String pwdRobo = paramGerais.get("pwdRobo");
			String codRobo = paramGerais.get("codRobo");
			String urlBpm  = paramGerais.get("urlBpm"); 
			
			// -----------------------------------
			// INSTANCIA OS OBJETOS PARA APROVACAO DA ETAPA DO PROCESSO
			// LOGA SESSAO COM USUARIO ROBO
			DadosLogin dadosLogin = new DadosLogin(usrRobo, pwdRobo, false);
			LoginAutenticacao loginAuthentication = new LoginAutenticacao(urlBpm + "sso", dadosLogin);
			String token = loginAuthentication.getToken();
			
			LOGGER.debug("URL_BPM: " 	+ urlBpm);
			LOGGER.debug("TOKEN: " 		+ token);
			LOGGER.debug("MODO_TESTE: " + modoTeste);
			LOGGER.debug("COD_ROBO: "   + codRobo);
			LOGGER.debug("COD_PROC: "   + codProc);
			LOGGER.debug("COD_ETAPA: "  + codEtapa);
			LOGGER.debug("COD_CICLO: "  + codCiclo);
			LOGGER.debug("ACAO: "       + acao);
			
			// ALOCA OS PARAMETROS DA ETAPA DO PROCESSO QUE SERA APROVADA
			DadosProcessoAbertura dadosProcUtil = new DadosProcessoAbertura();
			dadosProcUtil.setProcessInstanceId(String.valueOf(codProc));
			dadosProcUtil.setCurrentActivityInstanceId(String.valueOf(codEtapa));
			dadosProcUtil.setCurrentCycle(String.valueOf(codCiclo));
			dadosProcUtil.setModoTeste(modoTeste.equalsIgnoreCase("S") ? "true" : "false");
			
			// ATRIBUI A ACAO QUE SERA TOMADA CONFORME O CAMPO chkAllComprador
			DadosProcesso dadosProcesso = new DadosProcesso(acao);
			
			// QDO HOUVER OBS A SER PREENCHIDA SETA NO OBJ dadosProcesso
			if (camposValores.size() > 0) {
				dadosProcesso.geraPadroes(camposValores);
			}
			// APROVA PROCESSO
			AprovaProcesso aprovaProcesso = new AprovaProcesso(urlBpm + "bpm", token, dadosProcUtil, dadosProcesso, modoTeste.equalsIgnoreCase("S") ? "true" : "false", String.valueOf(codRobo));
			setRetExecAcaoEtp(aprovaProcesso.aprovaProcesso());
			
			LOGGER.debug("SUCESSO AO EXECUTAR ETAPA " + codEtapa + " DO PROCESSO " + codProc + "!");
			LOGGER.debug("retornoAprovacao: " + getRetExecAcaoEtp());
			
		} catch (LoginAuthenticationException e) {
			handleErrors.HandleLoginAuthenticationException(LOGGER, notifica, e, ExecutaEtapaProcessoBpm.class.getName(), paramGerais, isTestando, codProc);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(codProc, codEtapa, codCiclo, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (AprovaProcessoException e) {
			handleErrors.HandleAprovaProcessoException(LOGGER, notifica, e, ExecutaEtapaProcessoBpm.class.getName(), paramGerais, isTestando, codProc);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(codProc, codEtapa, codCiclo, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (Exception e) {
			handleErrors.HandleException(LOGGER, notifica, e, ExecutaEtapaProcessoBpm.class.getName(), "ERRO AO EXECUTAR ETAPA DO PROCESSO", paramGerais, isTestando, codProc);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(codProc, codEtapa, codCiclo, 99, className, e.getCause() + " - " + e.getMessage()));
		}
		
		LOGGER.debug(new String(new char[50]).replace("\0", "-"));
		return getRetExecAcaoEtp();
		
	}
	

	public static String getRetExecAcaoEtp() {
		return retExecAcaoEtp;
	}

	public static void setRetExecAcaoEtp(String retExecAcaoEtp) {
		ExecutaEtapaProcessoBpm.retExecAcaoEtp = retExecAcaoEtp;
	}

}
