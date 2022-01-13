package com.lecom.workflow.robo.RbCancelaProcOciosos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.HandleErrors;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;

public class RbActionsDb extends RbQueries {
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NAS TABELAS FORMULARIO, PROCESSO E PROCESSO_ETAPA
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * CONSULTA O COD_USUARIO DE GESTORES DO FORMULARIO (MODELO)
	 * @param LOGGER
	 * @param cnBpm
	 * @param handleErrors
	 * @param notifica
	 * @param paramGerais
	 * @param isTestando
	 * @param classeOrigem
	 * @param codForm
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public List<String> consultaUsrGestModelo(Logger LOGGER, Connection cnBpm, HandleErrors handleErrors, ExecutaNotificacaoEmailBpm notifica, 
												Map<String, String> paramGerais, boolean isTestando, String classeOrigem, int codForm, int codProc) throws Exception, SQLException {
		List<String> usrGestoresModelo = new ArrayList<String>();
		String sql = consultaUsrGestModelo(codForm);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (!rs.isBeforeFirst()) {
			LOGGER.warn(classeOrigem + ": O FORMULARIO " + codForm + " NAO POSSUI GESTORES DEFINIDOS");
			handleErrors.HandleGenError(LOGGER, notifica, classeOrigem, "O FORMULARIO " + codForm + " NAO POSSUI GESTORES DEFINIDOS", paramGerais, isTestando, codProc);
		} else {
			if (rs.next()) {
				// O RETORNO DA CONSULTA TERA O VALOR "54/489/624/1494/2731", POR EXEMPLO
				// POR ISSO SERA PRECISO ARRUMAR COM split
				String dirtyCodUsrGest = rs.getString(1);
				String[] splittedCodUsrGest = dirtyCodUsrGest.split("/");
				for (String codUsr : splittedCodUsrGest) {
					usrGestoresModelo.add(codUsr);
				}
			}
		}
		rs.close();
		pst.close();
		return usrGestoresModelo;
	}
	
	/**
	 * CONSULTA POR PROCESSOS OCIOSOS POR PERIODOS E TIPO DE EXECUCAO
	 * @param LOGGER
	 * @param cnBpm
	 * @param classeOrigem
	 * @param tipoExecucao -> 'n' = NOTIFICA GESTORES APENAS / 'c' = CANCELAR PROCESSOS E NOTIFICAR GESTORES 
	 * @param nDiasNotificaProcOciosos
	 * @param nDiasCancelaProcOciosos
	 * @param lstExcludedCodForm
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> consultaProcOciosos(Logger LOGGER, Connection cnBpm, String classeOrigem, char tipoExecucao, int nDiasNotificaProcOciosos, int nDiasCancelaProcOciosos, String lstExcludedCodForm) throws SQLException {
		List<Map<String, Object>> lstProcOciosos = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapProcOcioso = new HashMap<String, Object>();
		String sql = consultaProcOciosos(tipoExecucao, nDiasNotificaProcOciosos, nDiasCancelaProcOciosos, lstExcludedCodForm);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (!rs.isBeforeFirst()) {
			LOGGER.warn(classeOrigem + ": NAO HA PROCESSOS OCIOSOS PARA SEREM CANCELADOS.");
		} else {
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while(rs.next()) {
				for (int columnIndex = 1; columnIndex < (columnCount+1); columnIndex++) {
					mapProcOcioso.put(metaData.getColumnName(columnIndex), rs.getObject(columnIndex));
				}
				lstProcOciosos.add(mapProcOcioso);
			}
		}
		return lstProcOciosos;
	}
	
	/**
	 * CANCELAR PROCESSOS ATRAVES DE UM List DE COD_PROCESSO
	 * @param LOGGER
	 * @param cnBpm
	 * @param lstCodProc
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public int[] cancelaProcessos(Logger LOGGER, Connection cnBpm, String lstCodProc) throws Exception, SQLException {
		// RECUPERA AS QUERIES
		String sql1 = cancelaProcessoEtapaAndamento(lstCodProc);
		String sql2 = cancelaProcessoAndamento(lstCodProc);
		// CRIA A DECLARACAO DAS QUERIES 
		Statement st = cnBpm.createStatement();
		// PRIMEIRO ATUALIZA A TABELA PROCESSO_ETAPA
		st.addBatch(sql1);
		// EM SEGUIDA, ATUALIZA A TABELA PROCESSO
		st.addBatch(sql2);
		// COLETA O RETORNO
		int[] ret = st.executeBatch();
		return ret;
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NA TABELA USUARIO
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * COLETA INFOS DO USUARIO ATIVO E Q TEM AUTENTICACAO PELO LDAP ATRAVES DO COD_USUARIO
	 * @param LOGGER
	 * @param cnBpm
	 * @param handleErrors
	 * @param notifica
	 * @param paramGerais
	 * @param isTestando
	 * @param codForm
	 * @param lstCodUsuarios
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public List<Map<String, Object>> consultaUsuariosPeloCodUsuario(Logger LOGGER, Connection cnBpm, HandleErrors handleErrors, ExecutaNotificacaoEmailBpm notifica, 
																	Map<String, String> paramGerais, boolean isTestando, String classeOrigem, String lstCodUsuarios, int codProc) throws Exception, SQLException {
		List<Map<String, Object>> lstMapUsuarios = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapUsuarios = new HashMap<String, Object>();
		String sql = consultaUsuariosPeloCodUsuario(lstCodUsuarios);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (!rs.isBeforeFirst()) {
			handleErrors.HandleGenError(LOGGER, notifica, classeOrigem, "CONSULTA POR USUARIOS NAO RETORNOU RESULTADOS.", paramGerais, isTestando, codProc);
		} else {
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (rs.next()) {
				for (int columnIndex = 1; columnIndex < (columnCount+1); columnIndex++) {
					mapUsuarios.put(metaData.getColumnName(columnIndex), Funcoes.nulo(rs.getString(columnIndex), ""));
				}
				lstMapUsuarios.add(mapUsuarios);
			}
		}
		rs.close();
		pst.close();
		return lstMapUsuarios;
	}
	
}
