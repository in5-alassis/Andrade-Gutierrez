package com.lecom.workflow.robo.RbTemplate;

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

public class RbActionsDb extends RbQueries {
	
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
	
}
