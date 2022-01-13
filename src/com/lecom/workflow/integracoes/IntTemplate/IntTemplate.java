package com.lecom.workflow.integracoes.IntTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.tecnologia.db.DBUtils;
import com.lecom.workflow.common.util.ActionsDb;
import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.HandleErrors;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;
import com.lecom.workflow.integracoes.IntTemplate.GetParametros;
import com.lecom.workflow.vo.IntegracaoVO;

import br.com.lecom.atos.servicos.annotation.Execution;
import br.com.lecom.atos.servicos.annotation.IntegrationModule;
import br.com.lecom.atos.servicos.annotation.Version;

/**
 * ESTRUTURA TEMPLATE PARA INICIAR UMA INTEGRACAO
 * 
 * @author Rafael Marquini
 * @since 20/07/2020
 * @see ../upload/cadastros/config/IntTemplate.properties
 *
 */
@IntegrationModule("IntTemplate")
@Version({1,0,1})
public class IntTemplate {
	
	// INSTANCIA O OBJETO PARA RETORNAR LOGS
	private static final Logger LOGGER = Logger.getLogger(IntTemplate.class);
	// INSTANCIA O OBJETO PARA TRATAR EXCECOES
	public static HandleErrors handleErrors = new HandleErrors();
	// INSTANCIA O OBJETO PARA ENVIO DE NOTIFICACOES POR E-MAIL
	public static ExecutaNotificacaoEmailBpm notifica = new ExecutaNotificacaoEmailBpm();
	// INSTANCIA O OBJETO PARA RECUPERAR OS PARAMETROS
	public static GetParametros getParam = new GetParametros();
	// INSTANCIA O OBJETO PARA INTERACOES GENERICAS COM BANCO DE DADOS
	public static ActionsDb actDb = new ActionsDb();
	// INSTANCIA O OBJETO PARA INTERACOES EXCLUSIVAS DESTA INTEGRACAO COM BANCO DE DADOS
	public static IntActionsDb intActDb = new IntActionsDb();
	// INSTANCIA A VARIAVEL PARA FOLLOW-UP DA EXECUCAO
	private static boolean success;
	// INSTANCIA A VARIAVEL PARA ARMAZENAR OS RETORNOS
	private static String retorno;
	// INSTANCIA O NOME DA CLASSE 
	private static final String className = Funcoes.getClassName(IntTemplate.class.getCanonicalName());
	
	/**
	 * METODO MAIN
	 * 
	 * TODO: DESCREVA OQ SUA INTEGRACAO IRA FAZER
	 * 
	 * NESTE TEMPLATE JA ESTA DEFINIDO A RECUPERACAO DE PARAMETROS ORIGINADOS DE ARQUIVOS QUE CONTEM
	 * VARIAVEIS DE AMBIENTE (*.properties), ASSIM COMO A CONEXAO COM BANCO DE DADOS DO LECOM BPM.
	 * 
	 * ESTA DEFINIDO, TAMBEM, A INTERFACE COM O SERVICO IntegracaoVO.
	 * 
	 * PARA ENVIO DE E-MAILS UTILIZE A CLASSE com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm
	 * O BIN DESTA CLASSE ESTARA DISPONIVEL NA ESTRUTURA DE PACOTES MOSTRADA E, PORTANTO, SUA CLASSE JAVA
	 * SO PODE SER MODIFICADA POR MANTENEDORES DO REPOSITORIO.
	 * QUANDO NECESSARIO CRIAR ALGUMA MENSAGEM DE EMAIL PERSONALIZADA (QUASE SEMPRE EH NECESSARIO), CRIE UMA CLASSE
	 * OU METODO LOCAL ONDE MONTARA A MENSAGEM E INVOQUE O ENVIO ATRAVES DO METODO enviaEmail LOCALIZADO NA CLASSE
	 * com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm
	 * 
	 * @param integracaoVO
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Execution
	public String executeMain(IntegracaoVO integracaoVO) throws Exception, SQLException {
		
		LOGGER.warn("----- IntTemplate - INICIO -----");
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
		
		// OBJETO PARA ARMAZENAR OS DADOS DO FORMULARIO DO PROCESSO BPM
		Map<String, Object> camposForm = new HashMap<String, Object>();
		// DADOS DO FORMULARIO
		Integer codForm = Funcoes.obj2Integer(integracaoVO.getCodForm());
		Integer codProc = Funcoes.obj2Integer(integracaoVO.getCodProcesso());
		Integer codEtapa = Funcoes.obj2Integer(integracaoVO.getCodEtapa());
		Integer codCiclo = Funcoes.obj2Integer(integracaoVO.getCodCiclo());
		Integer codUsuarioIniciador = Funcoes.obj2Integer(integracaoVO.getCodUsuarioIniciador());
		Integer codUsuarioEtapa = Funcoes.obj2Integer(integracaoVO.getCodUsuarioEtapa());
		String acao = integracaoVO.getAcao();
		
		// CAMPOS DO FORMULARIO
		camposForm = integracaoVO.getMapCamposFormulario();
		String campoExemplo = Funcoes.obj2String(camposForm.get("$NOME_DO_CAMPO"));
		
		try {
			
			// PREVINE AUTOCOMMITS
			cnBpm.setAutoCommit(false);
			
			// SEU CODIGO AQUI (...) 
			
			if (!isSuccess()) {
				setRetorno("99|Alguma mensagem de erro direcionada ao usuário do BPM. Seja objetivo e claro para não deixá-lo confuso.");
				actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(codProc, codEtapa, codCiclo, 99, className, getRetorno()));
			} else {
				setRetorno("0|Uma mensagem de sucesso direcionada ao usuário do BPM.");
				actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(codProc, codEtapa, codCiclo, 0, className, getRetorno()));
			}
			
		} catch (SQLException e) {
			handleErrors.HandleSQLException(LOGGER, notifica, e, className, e.getCause() + " - " + e.getMessage() , paramGerais, isTestando, codProc);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(codProc, codEtapa, codCiclo, 0, className, e.getCause() + " - " + e.getMessage()));
		} catch (Exception e) {
			handleErrors.HandleException(LOGGER, notifica, e, className, e.getMessage(), paramGerais, isTestando, codProc);
			actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(codProc, codEtapa, codCiclo, 0, className, e.getCause() + " - " + e.getMessage()));
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
		
		LOGGER.warn("----- IntTemplate - FIM -----");
		LOGGER.warn(new String(new char[50]).replace("\0", "-"));
		return getRetorno();
		
	}
	
	/**
	 * GETTERS AND SETTERS 
	 */
	
	public static boolean isSuccess() {
		return success;
	}

	public static void setSuccess(boolean success) {
		IntTemplate.success = success;
	}

	public static String getRetorno() {
		return retorno;
	}

	public static void setRetorno(String retorno) {
		IntTemplate.retorno = retorno;
	}
	
	
	

}
