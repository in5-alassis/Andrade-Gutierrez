package com.lecom.workflow.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.HandleErrors;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;

public class ActionsDb extends Queries {
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NA TABELA IN5_REC_EXECUCAO
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * INSERE REGISTRO DE EXECUCAO DE ROBOS, INTEGRACOES OU INTERFACES COM ERP
	 * NA TABELA QUE ARMAZENA HISTORICO E EH UTILIZADA PARA MONITORAMENTO DE 
	 * DESTE TIPO DE PROCESSO
	 * @param cnBpm
	 * @param paramGerais
	 * @param dadosExec
	 * @return
	 * @throws SQLException
	 */
	public int insertIn5RecExecucao(Connection cnBpm, Map<String, String> paramGerais, Map<String, Object> dadosExec) throws SQLException {
		String sql = insertIn5RecExecucao(paramGerais.get("tabelaRec"));
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		pst.setInt(1, Funcoes.obj2Integer(dadosExec.get("COD_PROCESSO")));
		pst.setInt(2, Funcoes.obj2Integer(dadosExec.get("COD_ETAPA")));
		pst.setInt(3, Funcoes.obj2Integer(dadosExec.get("COD_CICLO")));
		pst.setInt(4, Funcoes.obj2Integer(dadosExec.get("STATUS_EXECUCAO")));
		pst.setString(5, Funcoes.obj2String(dadosExec.get("DATA_EXECUCAO")));
		pst.setString(6, Funcoes.obj2String(dadosExec.get("TIPO_EXECUCAO")));
		pst.setString(7, Funcoes.obj2String(dadosExec.get("ORIGEM")));
		pst.setString(8, Funcoes.obj2String(dadosExec.get("MSG_RETORNO")));
		int row = pst.executeUpdate();
		pst.close();
		return row;
	}
	
	/**
	 * RETORNA SE O COD_PROCESSO EXISTE NA TABELA IN5_REC_EXECUCAO
	 * @param cnBpm
	 * @param paramGerais
	 * @param codProc
	 * @return
	 * @throws SQLException
	 */
	public int consultaProcIn5RecExecucao(Connection cnBpm, Map<String, String> paramGerais, int codProc) throws SQLException {
		int countProc = 0;
		String sql = consultaProcIn5RecExecucao(paramGerais.get("tabelaRec"), codProc);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) countProc = rs.getInt(1);
		rs.close();
		pst.close();
		return countProc;
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// CONSULTAS NAS TABELAS MODELO E GRID GENERICAS
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * CONSULTA DADOS DO MODELO OU DA GRID, BASTA PASSAR NO PARAMETRO TabelaSQL
	 * @param LOGGER
	 * @param conn
	 * @param handleErrors
	 * @param notifica
	 * @param paramGerais
	 * @param isTestando
	 * @param tabelaSQL
	 * @param codForm
	 * @param codProc
	 * @param codEtapa
	 * @param codCiclo
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public Map<String, String> consultaGenericaProcesso(Logger LOGGER, Connection cnBpm, HandleErrors handleErrors, ExecutaNotificacaoEmailBpm notifica, 
														Map<String, String> paramGerais, boolean isTestando, String classeOrigem, String tabelaSQL, 
														int codForm, int codProc, int codEtapa, int codCiclo) throws Exception, SQLException {
		Map<String, String> resConsulta = new HashMap<String, String>();
		cons: {
			String sql = null;
			// PREFIXO f_ REFERE-SE A TABELA DO MODELO
			// PREFICO g_ REFERE-SE A TABELA DE GRID
			if (tabelaSQL.startsWith("f_")) {
				sql = consultaTbModelo(tabelaSQL, codProc, codEtapa, codCiclo);
			} else if (tabelaSQL.startsWith("g_")) {
				sql = consultaTbGrid(tabelaSQL, codProc, codEtapa, codCiclo);
			} else {
				handleErrors.HandleGenError(LOGGER, notifica, classeOrigem, "ERRO AO DETERMINAR A TABELA PARA CONSULTA DO PROCESSO: " + codProc + " (PREFIXO INVALIDO).", paramGerais, isTestando, codProc);
				break cons;
			}
			PreparedStatement pst = cnBpm.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			if (!rs.isBeforeFirst()) {
				handleErrors.HandleGenError(LOGGER, notifica, classeOrigem, "CONSULTA DOS DADOS DO PROCESSO: " + codProc + " NAO RETORNOU RESULTADOS.", paramGerais, isTestando, codProc);
			} else {
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (rs.next()) {
					for (int columnIndex = 1; columnIndex < (columnCount+1); columnIndex++) {
						resConsulta.put(metaData.getColumnName(columnIndex), Funcoes.nulo(rs.getString(columnIndex), ""));
					}
				}
			}
			rs.close();
			pst.close();
		}
		return resConsulta;
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NAS TABELAS FORMULARIO, PROCESSO E PROCESSO_ETAPA
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * RETORNA VALOR DE MODO TESTE DO FORMULARIO
	 * @param LOGGER
	 * @param cnBpm
	 * @param handleErrors
	 * @param notifica
	 * @param paramGerais
	 * @param isTestando
	 * @param codForm
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public String formModoTeste(Logger LOGGER, Connection cnBpm, HandleErrors handleErrors, ExecutaNotificacaoEmailBpm notifica, 
								Map<String, String> paramGerais, boolean isTestando, String classeOrigem, int codForm, int codProc) throws Exception, SQLException {
		String modoTeste = null;
		String sql = formModoTeste(codForm);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (!rs.isBeforeFirst()) {
			handleErrors.HandleGenError(LOGGER, notifica, classeOrigem, "NAO FOI POSSIVEL DETERMINAR SE O FORMULARIO " + codForm + " ESTA EM MODO TESTE. CONFIRME O CODIGO INFORMADO.", paramGerais, isTestando, codProc);
		} else {
			if (rs.next()) {
				modoTeste = rs.getString(1);
			}
		}
		rs.close();
		pst.close();
		return modoTeste;
	}
	
	/**
	 * RETORNA VALOR DE MODO TESTE DO PROCESSO
	 * @param LOGGER
	 * @param cnBpm
	 * @param handleErrors
	 * @param notifica
	 * @param paramGerais
	 * @param isTestando
	 * @param classeOrigem
	 * @param codProc
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public String procModoTeste(Logger LOGGER, Connection cnBpm, HandleErrors handleErrors, ExecutaNotificacaoEmailBpm notifica, 
								Map<String, String> paramGerais, boolean isTestando, String classeOrigem, int codProc) throws Exception, SQLException {
		String modoTeste = null;
		String sql = procModoTeste(codProc);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (!rs.isBeforeFirst()) {
			handleErrors.HandleGenError(LOGGER, notifica, classeOrigem, "NAO FOI POSSIVEL DETERMINAR SE O PROCESSO " + codProc + " ESTA EM MODO TESTE. CONFIRME O CODIGO INFORMADO.", paramGerais, isTestando, codProc);
		} else {
			if (rs.next()) {
				modoTeste = rs.getString(1);
			}
		}
		rs.close();
		pst.close();
		return modoTeste;
	}
	
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
	 *  RETORNA LISTA DE PROCESSOS PARADOS COM O USUARIO in5.robo
	 * @param LOGGER
	 * @param cnBpm
	 * @param codRobo
	 * @param codForm
	 * @param codEtapa
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public List<Map<String, Integer>> consultaProcessosRobo(Logger LOGGER, Connection cnBpm, int codRobo, int codForm, int codEtapa) throws Exception, SQLException {
		List<Map<String, Integer>> lstProcRobo = new ArrayList<Map<String, Integer>>();
		String sql = consultaProcessosRobo(codRobo, codForm, codEtapa);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (!rs.isBeforeFirst()) {
			LOGGER.warn("CONSULTA POR PROCESSOS PARADOS COM O ROBO NAO RETORNOU RESULTADOS.");
		} else {
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (rs.next()) {
				Map<String, Integer> procRobo = new HashMap<String, Integer>();
				for (int columnIndex = 1; columnIndex < (columnCount+1); columnIndex++) {
					procRobo.put(metaData.getColumnName(columnIndex), rs.getInt(columnIndex));
				}
				lstProcRobo.add(procRobo);
			}
		}
		rs.close();
		pst.close();
		return lstProcRobo;
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
