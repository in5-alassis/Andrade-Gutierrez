package com.lecom.workflow.robo.RbCancelaProcOciosos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.lecom.atos.servicos.annotation.Execution;
import br.com.lecom.atos.servicos.annotation.RobotModule;
import br.com.lecom.atos.servicos.annotation.Version;

import com.lecom.tecnologia.db.DBUtils;
import com.lecom.workflow.common.util.ActionsDb;
import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.HandleErrors;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;
import com.lecom.workflow.robo.RbExecutaEtapasGenerico.GetParametros;
import com.lecom.workflow.robo.face.WebServices;
import com.lecom.workflow.robo.face.WebServicesVO;

/**
 * ROBO PARA ALERTA E CANCELAMENTO DE PROCESSOS BPM OCIOSOS
 * 
 * @author Rafael Marquini
 * @since 04/08/2020
 * @see ../upload/cadastros/config/RbCancelaProcOciosos.properties
 *
 */
@RobotModule("RbCancelaProcOciosos")
@Version({1,0,1})
public class RbCancelaProcOciososMain implements WebServices {
	
	// INSTANCIA O OBJETO PARA RETORNAR LOGS
	protected final static Logger LOGGER = Logger.getLogger(RbCancelaProcOciososMain.class);
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
	private static final String className = Funcoes.getClassName(RbCancelaProcOciososMain.class.getCanonicalName());
	
	/**
	 * METODO MAIN
	 * 
	 * ESTE ROBO EH RESPONSAVEL POR CANCELAR PROCESSOS EM ANDAMENTO NO AMBIENTE
	 * QUE ESTAO OCIOSOS, OU SEJA, NAO POSSUEM ATIVIDADE A PARTIR DE N_DIAS_CANCELA
	 * PARA TRAS, CONTADOS A PARTIR DO DIA ATUAL
	 * 
	 * POREM, 30 DIAS ANTES DE REALIZAR O CANCELAMENTO DOS PROCESSOS ENVIA UMA NOTIFICACAO
	 * PARA OS GESTORES DOS MODELOS COMUNICANDO QUE FUTURAMENTE OS PROCESSOS SERAO 
	 * CANCELADOS POR FALTA DE ATIVIDADE. O VALOR DESTA CONDICAO EH ATRIBUIDO NA
	 * VARIAVEL DE AMBIENTE nDiasNotificaProcOciosos NO ARQUIVO RbCancelaProcOciosos.properties
	 * 
	 * O nDiasCancelaProcOciosos EH DEFINIDO NO ARQUIVO RbCancelaProcOciosos.properties E EH
	 * ATRIBUIDO NA QUERIE QUE REALIZA A CONSULTA POR TAIS PROCESSOS NA CLASSE
	 * Queries.java
	 * 
	 * ASSIM QUE ENCONTRADOS OS PROCESSOS EH COLETADO SEUS GESTORES DE MODELO
	 * PARA SEREM NOTIFICADOS SOBRE O ALERTA DE CANCELAMENTO E O PROPRIO CANCELAMENTO EM SI
	 * 
	 * NAS NOTIFICACOES PARA GESTORES SAO ENCAMINHADAS AS INFORMACOES:
	 * COD_FORM, COD_PROCESSO, COD_USUARIO_ETAPA, NOM_USUARIO
	 * 
	 * @throws Exception
	 * @throws SQLException
	 */
	@Execution
	public void executeMain() throws Exception, SQLException {
		LOGGER.warn("----- RbCancelaProcOciosos - INICIO -----");
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
			
			LOGGER.warn(new String(new char[50]).replace("\0", "-"));
			LOGGER.warn("----- EXECUTANDO NOTIFICACAO DE FUTUROS CANCELAMENTOS DE PROCESSOS OCIOSOS -----");
			RbFuturosCancelamentos fc = new RbFuturosCancelamentos();
			fc.verificaFuturosCancelamentos(paramGerais, paramExec, cnBpm, isTestando);
			LOGGER.warn(new String(new char[50]).replace("\0", "-"));
			
			LOGGER.warn(new String(new char[50]).replace("\0", "-"));
			LOGGER.warn("----- EXECUTANDO CANCELAMENTOS DE PROCESSOS OCIOSOS -----");
			RbEfetuaCancelamentosProcOciosos ecpo = new RbEfetuaCancelamentosProcOciosos();
			ecpo.cancelaProcOciosos(paramGerais, paramExec, cnBpm, isTestando);
			LOGGER.warn(new String(new char[50]).replace("\0", "-"));
			
		} catch (SQLException e) {
			handleErrors.HandleSQLException(LOGGER, notifica, e, className, e.getCause() + " - " + e.getMessage() , paramGerais, isTestando, 0);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(0, 0, 0, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (Exception e) {
			handleErrors.HandleException(LOGGER, notifica, e, className, e.getMessage(), paramGerais, isTestando, 0);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(0, 0, 0, 99, className, e.getCause() + " - " + e.getMessage()));
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
		
		LOGGER.warn("----- RbCancelaProcOciosos - FIM -----");
		LOGGER.warn(new String(new char[50]).replace("\0", "-"));
		
	}
	
	@Override
	public List<WebServicesVO> getWebServices() {
		return null;
	}

	@Override
	public void setWebServices(WebServicesVO arg0) {
	}

}
