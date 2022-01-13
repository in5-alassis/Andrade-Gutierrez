package com.lecom.workflow.robo.RbExecutaEtapasGenerico;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.lecom.atos.servicos.annotation.Execution;
import br.com.lecom.atos.servicos.annotation.RobotModule;
import br.com.lecom.atos.servicos.annotation.Version;

import com.lecom.tecnologia.db.DBUtils;
import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.ActionsDb;
import com.lecom.workflow.common.util.HandleErrors;
import com.lecom.workflow.common.util.controller.ExecutaEtapaProcessoBpm;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;
import com.lecom.workflow.robo.RbExecutaEtapasGenerico.GetParametros;
import com.lecom.workflow.robo.face.WebServices;
import com.lecom.workflow.robo.face.WebServicesVO;

/**
 * ROBO PARA EXECUCAO GENERICA DE ETAPAS DOS PROCESSOS BPM
 * REALIZA APROVACOES E REJEICOES DE ETAPAS NORMAIS E CONCENTRADORAS
 *  
 * @author Rafael Marquini
 * @since 17/07/2020
 * @see ../upload/cadastros/config/RbExecutaEtapasGenerico.properties
 *
 */
@RobotModule("RbExecutaEtapasGenerico")
@Version({1,0,1})
public class RbExecutaEtapasGenerico implements WebServices {
	
	// INSTANCIA O OBJETO PARA RETORNAR LOGS
	private final static Logger LOGGER = Logger.getLogger(RbExecutaEtapasGenerico.class);
	// INSTANCIA O OBJETO PARA TRATAR EXCECOES
	public static HandleErrors handleErrors = new HandleErrors();
	// INSTANCIA O OBJETO PARA ENVIO DE NOTIFICACOES POR E-MAIL
	public static ExecutaNotificacaoEmailBpm notifica = new ExecutaNotificacaoEmailBpm();
	// INSTANCIA O OBJETO PARA RECUPERAR OS PARAMETROS
	public static GetParametros getParam = new GetParametros();
	// INSTANCIA O OBJETO PARA INTERACOES GENERICAS COM BANCO DE DADOS
	public static ActionsDb actDb = new ActionsDb();
	// INSTANCIA O OBJETO PARA INTERACOES EXCLUSIVAS DESTE ROBO COM BANCO DE DADOS
	public static RbActionsDb rbActDb = new RbActionsDb();
	// INSTANCIA O NOME DA CLASSE 
	private static final String className = Funcoes.getClassName(RbExecutaEtapasGenerico.class.getCanonicalName());
	// COD_PROCESSO
	private static Integer codProc;
	
	/**
	 * ESTE ROBO EXECUTA APROVACOES E REJEICOES DE ETAPAS NORMAIS E CONCENTRADORAS DOS PROCESSO DE BPM,
	 * QUE ESTEJAM PARADO COM O USUARIO SISTEMICO RESPONSAVEL POR ESSES TIPOS DE EXECUCOES. 
	 * 
	 * 
	 * @throws Exception
	 * @throws SQLException
	 */
	@Execution
	public void executeMain() throws Exception, SQLException {
		LOGGER.warn("----- RbExecutaEtapasGenerico - INICIO -----");
		LOGGER.warn(new String(new char[50]).replace("\0", "-"));
		
		
		Map<String, String> paramGerais = new HashMap<String, String>();
		Map<String, String> paramExec   = new HashMap<String, String>();
		
		paramGerais = getParam.getParamGerais(LOGGER);
		paramExec   = getParam.getParamExecEtpGen(LOGGER);
		
		LOGGER.debug("PARAMETROS GERAIS: " + paramGerais);
		LOGGER.debug("PARAMETROS EXECUCAO: " + paramExec);
		LOGGER.debug(new String(new char[50]).replace("\0", "-"));
		
		// INSTANCIA A CONEXAO COM BANCO DE DADOS
		Connection cnBpm = DBUtils.getConnection(paramGerais.get("cnBpm"));
		LOGGER.debug("CONEXAO COM O BANCO DE DADOS BPM ESTA FECHADA? " + cnBpm.isClosed()); // VALOR TEM QUE SER false
		
		// VERIFICAR SE EH UMA EXECUCAO TESTE
		boolean isTestando = Boolean.parseBoolean(paramExec.get("isTestando"));
		
		try {
			
			// PREVINE AUTOCOMMITS
			cnBpm.setAutoCommit(false);
			
			// OBJETO PARA EXECUCAO DAS ETAPAS
			ExecutaEtapaProcessoBpm execEtp = new ExecutaEtapaProcessoBpm();
			
			// MAPA PARA TRANSMITIR VALORES PARA CAMPOS DOS FORMULARIOS QDO NECESSARIO
			Map<String, String> camposValores = new HashMap<String, String>();
			
			// ----------------------------------------------------------------------
			// APROVACAO DE PROCESSOS PARADOS COM O ROBO
			// ----------------------------------------------------------------------
			LOGGER.debug(new String(new char[50]).replace("\0", "-"));
			LOGGER.debug("APROVACAO DE PROCESSOS PARADOS COM O ROBO");
			List<String> lstFormsAprovacoes = new ArrayList<String>();
			lstFormsAprovacoes = Funcoes.retornaLstForms(paramExec.get("rbAprovacoes"));
			LOGGER.debug("lstFormsAprovacoes: " + lstFormsAprovacoes);
			if (lstFormsAprovacoes.size() > 0 && !(paramExec.get("rbAprovacoes").equals("")) ) {
				for (String form : lstFormsAprovacoes) {
					int codForm  = Integer.parseInt(form.split("@")[0]);
					int codEtapa = Integer.parseInt(form.split("@")[1]);
					LOGGER.debug(new String(new char[25]).replace("\0", "-"));
					LOGGER.debug("COD_FORM: " + codForm);
					LOGGER.debug("COD_ETAPA: " + codEtapa);
					List<Map<String, Integer>> lstProcessos = rbActDb.consultaProcessosRobo(LOGGER, cnBpm, Integer.parseInt(paramGerais.get("codRobo")), codForm, codEtapa);
					LOGGER.debug("LST_PROCESSOS: " + lstProcessos);
					if (lstProcessos.size() > 0) {
						for (Map<String, Integer> mapProc : lstProcessos) {
							// NO mapProc SAO RETORNADOS COLUNAS COD_PROCESSO E COD_CICLO_ATUAL
							LOGGER.debug("PROCESSO: " + mapProc);
							setCodProc(mapProc.get("COD_PROCESSO"));
							String modoTeste = rbActDb.procModoTeste(LOGGER, cnBpm, handleErrors, notifica, paramGerais, isTestando, className, getCodProc());
							LOGGER.debug("MODO_TESTE: " + modoTeste);
							execEtp.execAcaoEtapa(LOGGER, cnBpm, handleErrors, notifica, actDb, paramGerais, isTestando, getCodProc(), codEtapa, mapProc.get("COD_CICLO"), modoTeste, "P", camposValores);
						}
					}
					
				}
			}
			
			// ----------------------------------------------------------------------
			// REJEICAO DE PROCESSOS PARADOS COM O ROBO
			// ----------------------------------------------------------------------
			LOGGER.debug(new String(new char[50]).replace("\0", "-"));
			LOGGER.debug("REJEICAO DE PROCESSOS PARADOS COM O ROBO");
			List<String> lstFormsRejeicoes = new ArrayList<String>();
			lstFormsRejeicoes = Funcoes.retornaLstForms(paramExec.get("rbReijeicoes"));
			LOGGER.debug("lstFormsRejeicoes: " + lstFormsRejeicoes);
			if ( lstFormsRejeicoes.size() > 0 && !(paramExec.get("rbReijeicoes").equals("")) ) {
				for (String form : lstFormsRejeicoes) {
					int codForm  = Integer.parseInt(form.split("@")[0]);
					int codEtapa = Integer.parseInt(form.split("@")[1]);
					LOGGER.debug(new String(new char[25]).replace("\0", "-"));
					LOGGER.debug("COD_FORM: " + codForm);
					LOGGER.debug("COD_ETAPA: " + codEtapa);
					List<Map<String, Integer>> lstProcessos = rbActDb.consultaProcessosRobo(LOGGER, cnBpm, Integer.parseInt(paramGerais.get("codRobo")), codForm, codEtapa);
					LOGGER.debug("LST_PROCESSOS: " + lstProcessos);
					if (lstProcessos.size() > 0) {
						for (Map<String, Integer> mapProc : lstProcessos) {
							// NO mapProc SAO RETORNADOS COLUNAS COD_PROCESSO E COD_CICLO_ATUAL
							LOGGER.debug("PROCESSO: " + mapProc);
							setCodProc(mapProc.get("COD_PROCESSO"));
							String modoTeste = rbActDb.procModoTeste(LOGGER, cnBpm, handleErrors, notifica, paramGerais, isTestando, className, getCodProc());
							LOGGER.debug("MODO_TESTE: " + modoTeste);
							execEtp.execAcaoEtapa(LOGGER, cnBpm, handleErrors, notifica, actDb, paramGerais, isTestando, getCodProc(), codEtapa, mapProc.get("COD_CICLO"), modoTeste, "R", camposValores);
						}
					}
				}
			}
			
			// ----------------------------------------------------------------------
			//  EXECUCAO DE PROCESSOS EM ETAPAS CONCENTRADORAS PARADOS COM O ROBO
			// ----------------------------------------------------------------------
			LOGGER.debug(new String(new char[50]).replace("\0", "-"));
			LOGGER.debug("EXECUCAO DE PROCESSOS EM ETAPAS CONCENTRADORAS PARADOS COM O ROBO");
			List<String> lstFormsConcentradora = new ArrayList<String>();
			lstFormsConcentradora = Funcoes.retornaLstForms(paramExec.get("rbExecucoesParalelas"));
			LOGGER.debug("lstFormsConcentradora: " + lstFormsConcentradora);
			if (lstFormsConcentradora.size() > 0 && !(paramExec.get("rbExecucoesParalelas").equals("")) ) {
				for (String form : lstFormsConcentradora) {
					int codForm 		 	  = Integer.parseInt(form.split("@")[0]);
					String etapas 		 	  = form.split("@")[1];
					int codEtapaConcentradora = Integer.parseInt(etapas.split("-")[0]);
					String prevEtapas 	  	  = etapas.split("-")[1];
					LOGGER.debug(new String(new char[25]).replace("\0", "-"));
					LOGGER.debug("COD_FORM: " + codForm);
					LOGGER.debug("COD_ETAPA_CONCENTRADORA: " + codEtapaConcentradora);
					LOGGER.debug("COD_ETAPAS_PREV: " + prevEtapas);
					List<Map<String, Integer>> lstProcessos = rbActDb.consultaProcessosRobo(LOGGER, cnBpm, Integer.parseInt(paramGerais.get("codRobo")), codForm, codEtapaConcentradora);
					LOGGER.debug("LST_PROCESSOS: " + lstProcessos);
					if (lstProcessos.size() > 0) {
						chkProc: for (Map<String, Integer> mapProc : lstProcessos) {
							LOGGER.debug("PROCESSO: " + mapProc);
							setCodProc(mapProc.get("COD_PROCESSO"));
							String modoTeste = rbActDb.procModoTeste(LOGGER, cnBpm, handleErrors, notifica, paramGerais, isTestando, className, getCodProc());
							LOGGER.debug("MODO_TESTE: " + modoTeste);
							// PARA WF COM PARALELISMO ANINHADOS, ARMAZENA O COD_ETAPA_ATUAL DA TABELA 'PROCESSO' PARA VERIFICACAO
							int codEtapaAtual = rbActDb.consultaCodEtapaAtual(LOGGER, cnBpm, codForm, getCodProc());
							if (codEtapaAtual == 0) {
								LOGGER.error("COD_ETAPA_ATUAL NAO RECUPERADO PARA O PROCESSO: " + getCodProc());
								handleErrors.HandleGenError(LOGGER, notifica, RbExecutaEtapasGenerico.class.getCanonicalName(), "COD_ETAPA_ATUAL NAO RECUPERADO PARA O PROCESSO: " + getCodProc(), paramGerais, isTestando, getCodProc());
								actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(getCodProc(), 0, 0, 99, className, "COD_ETAPA_ATUAL NAO RECUPERADO PARA O PROCESSO: " + getCodProc()));
								continue chkProc;
							} else {
								int qtdeEtapasAndamento = rbActDb.consultaQtdeEtapasAndamento(LOGGER, cnBpm, codForm, getCodProc(), codEtapaConcentradora, codEtapaAtual);
								LOGGER.debug("codEtapaAtual: " + codEtapaAtual);
								LOGGER.debug("qtdeEtapasAndamento: " + qtdeEtapasAndamento);
								if (qtdeEtapasAndamento > 0) {
									LOGGER.warn("HA ETAPAS EM ANDAMENTO DO PROCESSO " + getCodProc() + " E FORMULARIO " + codForm + ". AGUARDANDO APROVACAO PARA EXECUTAR A ETAPA CONCENTRADORA.");
									continue chkProc;
								} else {
									String acao = rbActDb.consultaStatusPrevEtapas(LOGGER, cnBpm, getCodProc(), prevEtapas);
									execEtp.execAcaoEtapa(LOGGER, cnBpm, handleErrors, notifica, actDb, paramGerais, isTestando, getCodProc(), codEtapaConcentradora, mapProc.get("COD_CICLO"), modoTeste, acao, camposValores);
								}
							}
						}
					}
				}
			}
			
		} catch (SQLException e) {
			handleErrors.HandleSQLException(LOGGER, notifica, e, RbExecutaEtapasGenerico.class.getCanonicalName(), e.getCause() + " - " + e.getMessage() , paramGerais, isTestando, 0);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(getCodProc(), 0, 0, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (Exception e) {
			handleErrors.HandleException(LOGGER, notifica, e, RbExecutaEtapasGenerico.class.getCanonicalName(), e.getMessage(), paramGerais, isTestando, 0);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(getCodProc(), 0, 0, 99, className, e.getCause() + " - " + e.getMessage()));
		} finally {
			// COMMITA AS MUDANCAS NO BANCO OU FAZ ROLLBACK SE TESTES
			if (isTestando) cnBpm.rollback();
			else cnBpm.commit();
			
			// VOLTA O ESTADO DO COMMIT
			cnBpm.setAutoCommit(true);
			
			// FECHA CONEXAO COM BANCO
			if (!cnBpm.isClosed()) {
				cnBpm.close();
				LOGGER.warn("CONEXAO COM O BANCO DE DADOS BPM ESTA FECHADA? " + cnBpm.isClosed()); // VALOR TEM QUE SER true
			} else {
				LOGGER.warn("CONEXAO COM O BANCO DE DADOS BPM PERMANCEU ABERTA! " + cnBpm.isClosed()); // VALOR TEM QUE SER false
			}
		}
		
		LOGGER.warn("----- RbExecutaEtapasGenerico - FIM -----");
		LOGGER.warn(new String(new char[50]).replace("\0", "-"));
		
	}
	
	public static Integer getCodProc() {
		return codProc;
	}

	public static void setCodProc(Integer codProc) {
		RbExecutaEtapasGenerico.codProc = codProc;
	}

	@Override
	public List<WebServicesVO> getWebServices() {
		return null;
	}

	@Override
	public void setWebServices(WebServicesVO arg0) {
	}

}
