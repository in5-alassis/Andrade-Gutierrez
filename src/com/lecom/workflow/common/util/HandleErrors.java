package com.lecom.workflow.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.workflow.cadastros.rotas.exception.AbreProcessoException;
import com.lecom.workflow.cadastros.rotas.exception.AprovaProcessoException;
import com.lecom.workflow.cadastros.rotas.exception.LoginAuthenticationException;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;

public class HandleErrors {
	
	// INSTANCIA O OBJETO writer COMUM PARA TODA CLASSE HandleErrors
	private Writer writer = new StringWriter();
	
	// MAPA QUE SERA UTILIZADO PARA ARMAZENAR OS PARAMETROS DE ERROS
	private Map<String, String> errorParam = new HashMap<String, String>();
	
	/**
	 * HANDLE GENERIC ERROR
	 * @param LOGGER
	 * @param notifica
	 * @param classeOrigem
	 * @param errorMsg
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleGenError(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, String classeOrigem, String errorMsg, 
							   Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", errorMsg);
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": " + errorMsg);
	}
	
	/**
	 * HANDLE Exception
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param errorMsg
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, Exception e, String classeOrigem, String errorMsg, 
							     Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", errorMsg);
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": " + errorMsg + " | " + e.getMessage());
		LOGGER.error(writer.toString());
	}
	
	/**
	 * HANDLE SQLException
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param errorMsg
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleSQLException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, SQLException e, String classeOrigem, String errorMsg, 
									Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", errorMsg + " | " + e.getErrorCode() + " | " + e.getMessage());
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": " + errorMsg + " | " + e.getErrorCode() + " | " + e.getMessage());
		LOGGER.error(writer.toString());
	}
	
	/**
	 * HANDLE ParseException
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param errorMsg
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleParseException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, ParseException e, String classeOrigem, String errorMsg, 
									  Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", errorMsg);
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": " + errorMsg + " | " + e.getMessage());
		LOGGER.error(writer.toString());
	}
	
	/**
	 * HANDLE IOException
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param errorMsg
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleIOException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, IOException e, String classeOrigem, String errorMsg, 
							       Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", errorMsg);
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": " + errorMsg + " | " + e.getMessage());
		LOGGER.error(writer.toString());
	}
	
	/**
	 * HANDLE HandleLoginAuthenticationException
	 * QUANDO ESTIVER EXECUTANDO ALGUM METODO QUE PRECISA 
	 * REALIZAR LOGIN VIA WEB SERVICE NO BPM
	 * 
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleLoginAuthenticationException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, LoginAuthenticationException e, String classeOrigem, 
												    Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", "LoginAuthenticationException: " + e.getMessage());
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": LoginAuthenticationException | " + e.getMessage());
		LOGGER.error(writer.toString());
	}
	
	/**
	 * HANDLE HandleAbreProcessoException
	 * QUANDO ESTIVER EXECUTANDO ALGUM METODO QUE PRECISA 
	 * ABRIR PROCESSO VIA WEB SERVICE NO BPM
	 * 
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleAbreProcessoException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, AbreProcessoException e, String classeOrigem, 
											 Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", "AbreProcessoException: " + e.getMessage() + " | COD_PROCESSO: " + codProc);
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": AbreProcessoException | " + e.getMessage() + " | COD_PROCESSO: " + codProc);
		LOGGER.error(writer.toString());
	}
	
	/**
	 * HANDLE HandleAprovaProcessoException
	 * QUANDO ESTIVER EXECUTANDO ALGUM METODO QUE PRECISA 
	 * EXECUTAR UMA ETAPA DE PROCESSO VIA WEB SERVICE NO BPM
	 * 
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleAprovaProcessoException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, AprovaProcessoException e, String classeOrigem, 
											   Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", "AprovaProcessoException: " + e.getMessage() + " | COD_PROCESSO: " + codProc);
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": AprovaProcessoException | " + e.getMessage() + " | COD_PROCESSO: " + codProc);
		LOGGER.error(writer.toString());
	}
	
	/**
	 * HANDLE InterruptedException
	 * @param LOGGER
	 * @param notifica
	 * @param e
	 * @param classeOrigem
	 * @param errorMsg
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void HandleInterruptedException(Logger LOGGER, ExecutaNotificacaoEmailBpm notifica, InterruptedException e, String classeOrigem, String errorMsg, 
										    Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		e.printStackTrace(new PrintWriter(writer));
		errorParam = new HashMap<String, String>();
		errorParam.put("DATA_SISTEMA", Funcoes.getDateServer());
		errorParam.put("CLASSE", classeOrigem);
		errorParam.put("ERROR", errorMsg);
		notifica.acionaSuporteDev(LOGGER, errorParam, paramGerais, isTestando, codProc);
		LOGGER.error(classeOrigem + ": " + errorMsg + " | " + e.getMessage());
		LOGGER.error(writer.toString());
	}

}
