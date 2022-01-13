package com.lecom.workflow.common.util.controller;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.workflow.cadastros.rotas.AbreProcesso;
import com.lecom.workflow.cadastros.rotas.AprovaProcesso;
import com.lecom.workflow.cadastros.rotas.LoginAutenticacao;
import com.lecom.workflow.cadastros.rotas.exception.AbreProcessoException;
import com.lecom.workflow.cadastros.rotas.exception.AprovaProcessoException;
import com.lecom.workflow.cadastros.rotas.exception.LoginAuthenticationException;
import com.lecom.workflow.cadastros.rotas.util.DadosLogin;
import com.lecom.workflow.cadastros.rotas.util.DadosProcesso;
import com.lecom.workflow.cadastros.rotas.util.DadosProcessoAbertura;
import com.lecom.workflow.common.util.ActionsDb;
import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.HandleErrors;

public class ExecutaAberturaProcessoBpm {
	
	private String abrProcRet;
	// INSTANCIA O NOME DA CLASSE 
	private static final String className = Funcoes.getClassName(ExecutaAberturaProcessoBpm.class.getCanonicalName());
	
	/**
	 * EXECUTA ABERTURA DE PROCESSO 5.30 UTILIZANDO RotasBPM_v4.jar
	 * @param LOGGER
	 * @param handleErrors
	 * @param notifica
	 * @param paramGerais
	 * @param isTestando
	 * @param codForm
	 * @param codVersao
	 * @param modoTeste
	 * @param campos
	 * @return
	 * @throws Exception
	 */
	public String execAberturaProcesso(Logger LOGGER, Connection cnBpm, HandleErrors handleErrors, ExecutaNotificacaoEmailBpm notifica, ActionsDb actDb, Map<String, String> paramGerais, 
									   boolean isTestando, String codForm, String codVersao, String modoTeste, Map<String, String> campos) throws Exception, LoginAuthenticationException, AbreProcessoException, AprovaProcessoException {
		
		
		try {
			LOGGER.debug(new String(new char[50]).replace("\0", "-"));
			LOGGER.debug("ABRE NOVO PROCESSO");
			
			// VARIAVEIS DO AMBIENTE
			String usrRobo = paramGerais.get("usrRobo");
			String pwdRobo = paramGerais.get("pwdRobo");
			String codRobo = paramGerais.get("codRobo");
			String urlBpm  = paramGerais.get("urlBpm"); 
			
			// REALIZA O LOGIN NO AMBIENTE
			LOGGER.debug("USR ROBO: " + usrRobo);
			LOGGER.debug("PWD ROBO: " + pwdRobo);
			DadosLogin dadosLogin = new DadosLogin(usrRobo, pwdRobo, false);
			
			LOGGER.debug("URL_BPM: " 	+ urlBpm);
			LoginAutenticacao loginAuthentication = new LoginAutenticacao(urlBpm + "sso", dadosLogin);
			String token = loginAuthentication.getToken();
			
			// ABRE O PROCESSO
			LOGGER.debug("TOKEN: " 		+ token);
			LOGGER.debug("COD_FORM: "   + codForm);
			LOGGER.debug("COD_VERSAO: " + codVersao);
			LOGGER.debug("MODO_TESTE: " + modoTeste);
			LOGGER.debug("COD_ROBO: "   + codRobo);
			LOGGER.debug("CAMPOS ABERTURA: " + campos);
			
			DadosProcesso dadosProcesso = null;
			AbreProcesso abreProcesso = null;
			AprovaProcesso aprovaProcesso = null;

			dadosProcesso = new DadosProcesso("P");

			// DADOS DOS CAMPOS QUE NAO SAO DO TIPO GRID
			dadosProcesso.geraPadroes(campos);

			//ABRE O PROCESSO, MAS NAO ENVIA AS INFOS AO FORM
			abreProcesso = new AbreProcesso(urlBpm + "bpm", token, codForm, codVersao, (modoTeste.equalsIgnoreCase("S")) ? "true" : "false", codRobo, null);

			// OBTEM DADOS DO PROCESSO ABERTO
			DadosProcessoAbertura dadosProcessoAbertura = abreProcesso.getAbreProcesso();

			// CRIACAO DO OBJETO QUE POPULAREMOS OS DADOS QUE QUEREMOS QUE SEJA ABERTO/APROVADO NO PROCESSO
			aprovaProcesso = new AprovaProcesso(urlBpm + "bpm", token, dadosProcessoAbertura, dadosProcesso, (modoTeste.equalsIgnoreCase("S")) ? "true" : "false", codRobo);

			// RETORNO DA APROVACAO DA ATIVIDADE
			String retornoAprovacao = aprovaProcesso.aprovaProcesso();
			setAbrProcRet(dadosProcessoAbertura.getProcessInstanceId());
			LOGGER.info("RETORNO APROVACAO: " + retornoAprovacao);
			LOGGER.info("COD_PROCESSO GERADO: " + dadosProcessoAbertura.getProcessInstanceId());
			
		} catch (LoginAuthenticationException e) {
			handleErrors.HandleLoginAuthenticationException(LOGGER, notifica, e, ExecutaAberturaProcessoBpm.class.getName(), paramGerais, isTestando, 0);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(0, 0, 0, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (AbreProcessoException e) {
			handleErrors.HandleAbreProcessoException(LOGGER, notifica, e, ExecutaAberturaProcessoBpm.class.getName(), paramGerais, isTestando, Integer.parseInt(getAbrProcRet()));
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(0, 0, 0, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (AprovaProcessoException e) {
			handleErrors.HandleAprovaProcessoException(LOGGER, notifica, e, ExecutaAberturaProcessoBpm.class.getName(), paramGerais, isTestando, Integer.parseInt(getAbrProcRet()));
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(Integer.parseInt(getAbrProcRet()), 1, 1, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (Exception e) {
			handleErrors.HandleException(LOGGER, notifica, e, ExecutaAberturaProcessoBpm.class.getName(), "ERRO AO EXECUTAR ABERTURA DE PROCESSO", paramGerais, isTestando, Integer.parseInt(getAbrProcRet()));
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(Integer.parseInt(getAbrProcRet()), 1, 1, 99, className, e.getCause() + " - " + e.getMessage()));
		}
		
		LOGGER.debug(new String(new char[50]).replace("\0", "-"));
		return getAbrProcRet();
	}

	public String getAbrProcRet() {
		return abrProcRet;
	}

	public void setAbrProcRet(String abrProcRet) {
		this.abrProcRet = abrProcRet;
	}

}
