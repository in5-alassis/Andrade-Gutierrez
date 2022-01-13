package com.lecom.workflow.robo.RbTemplate;

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
 * ESTRUTURA TEMPLATE PARA INICIAR UM ROBO
 * 
 * @author Rafael Marquini
 * @since 17/07/2020
 * @see ../upload/cadastros/config/RbTemplate.properties
 *
 */
@RobotModule("RbTemplate")
@Version({1,0,1})
public class RbTemplate implements WebServices {
	
	// INSTANCIA O OBJETO PARA RETORNAR LOGS
	private final static Logger LOGGER = Logger.getLogger(RbTemplate.class);
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
	private static final String className = Funcoes.getClassName(RbTemplate.class.getCanonicalName());
	
	/**
	 * METODO MAIN
	 * 
	 * TODO: DESCREVA OQ SEU ROBO IRA FAZER
	 * 
	 * NESTE TEMPLATE JA ESTA DEFINIDO A RECUPERACAO DE PARAMETROS ORIGINADOS DE ARQUIVOS QUE CONTEM
	 * VARIAVEIS DE AMBIENTE (*.properties), ASSIM COMO A CONEXAO COM BANCO DE DADOS DO LECOM BPM.
	 * 
	 * ESTA DEFINIDO, TAMBEM, A CONEXAO COM O WEBSERVICE LECOM PARA UTILIZACAO DE OBJETOS PROVIDOS PELA INTERFACE:
	 * 1. ABERTURA DE PROCESSOS: UTILIZE A CLASSE com.lecom.workflow.common.util.controller.ExecutaAberturaProcessoBpm
	 * 2. EXECUCAO DE ETAPAS: UTILIZE A CLASSE com.lecom.workflow.common.util.controller.ExecutaEtapaProcessoBpm
	 * OS BINS DESTAS CLASSES JA ESTARAO DISPONIVEIS NO AMBIENTE DO CLIENTE NA ESTRUTURA DE PACOTES MOSTRADA.
	 * ELAS NAO DEVEM SER MODIFICADAS, EXCETO POR MANTENEDORES DO REPOSITORIO.
	 * 
	 * PARA ENVIO DE E-MAILS UTILIZE A CLASSE com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm
	 * O BIN DESTA CLASSE, TAMBEM, ESTARA DISPONIVEL NA ESTRUTURA DE PACOTES MOSTRADA E, PORTANTO, SUA CLASSE JAVA
	 * SO PODE SER MODIFICADA POR MANTENEDORES DO REPOSITORIO.
	 * QUANDO NECESSARIO CRIAR ALGUMA MENSAGEM DE EMAIL PERSONALIZADA (QUASE SEMPRE EH NECESSARIO), CRIE UMA CLASSE
	 * OU METODO LOCAL ONDE MONTARA A MENSAGEM E INVOQUE O ENVIO ATRAVES DO METODO enviaEmail LOCALIZADO NA CLASSE
	 * com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm
	 * 
	 * 
	 * @throws Exception
	 * @throws SQLException
	 */
	@Execution
	public void executeMain() throws Exception, SQLException {
		LOGGER.warn("----- RbTemplate - INICIO -----");
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
			
			// SEU CODIGO AQUI (...)			
			
		} catch (SQLException e) {
			handleErrors.HandleSQLException(LOGGER, notifica, e, RbTemplate.class.getCanonicalName(), e.getCause() + " - " + e.getMessage() , paramGerais, isTestando, 0);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(0, 0, 0, 99, className, e.getCause() + " - " + e.getMessage()));
		} catch (Exception e) {
			handleErrors.HandleException(LOGGER, notifica, e, RbTemplate.class.getCanonicalName(), e.getMessage(), paramGerais, isTestando, 0);
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
		
		LOGGER.warn("----- RbTemplate - FIM -----");
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
