package com.lecom.workflow.robo.RbExecutaEtapasGenerico;

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

import com.lecom.workflow.common.util.HandleErrors;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;

public class RbActionsDb extends RbQueries {
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NAS TABELAS FORMULARIO, PROCESSO E PROCESSO_ETAPA
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
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
	
	/**
	 * RETORA O COD_ETAPA_ATUAL DO PROCESSO EM ANDAMENTO
	 * UTILIZADO PARA WF QUE POSSUEM PARALELOS ANINHADOS
	 * @param LOGGER
	 * @param cnBpm
	 * @param codForm
	 * @param codProc
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public int consultaCodEtapaAtual(Logger LOGGER, Connection cnBpm, int codForm, int codProc) throws Exception, SQLException {
		int codEtapaAtual = 0;
		String sql = consultaCodEtapaAtual(codForm, codProc);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		if (!rs.isBeforeFirst()) {
			LOGGER.error("CONSULTA PELA COD_ETAPA_ATUAL DO PROCESSO " + codProc + " NAO RETORNOU RESULTADOS.");
		} else {
			if (rs.next()) codEtapaAtual = rs.getInt(1);
		}
		return codEtapaAtual;
	}
	
	/**
	 *  RETORNA QTDE DE ETAPAS EM ANDAMENTO EM UM PROCESSO
	 * @param LOGGER
	 * @param cnBpm
	 * @param handleErrors
	 * @param notifica
	 * @param codForm
	 * @param codProc
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public Integer consultaQtdeEtapasAndamento(Logger LOGGER, Connection cnBpm, int codForm, int codProc, int codEtapaConcentradora, int codEtapaAtual) throws Exception, SQLException {
		int qtdeEtapasAndamento = 0;
		String sql = consultaQtdeEtapasAndamento(codProc, codForm, codEtapaConcentradora, codEtapaAtual);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery(sql);
		if (rs.next()) qtdeEtapasAndamento = rs.getInt(1);
		rs.close();
		pst.close();
		return qtdeEtapasAndamento;
	}
	
	/**
	 * CONSULTA STATUS ETAPAS ANTERIORES A CONCENTRADORA
	 * @param LOGGER
	 * @param cnBpm
	 * @param codProc
	 * @param prevEtapas
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public String consultaStatusPrevEtapas(Logger LOGGER, Connection cnBpm, int codProc, String prevEtapas) throws Exception, SQLException {
		String status = null;
		String sql = consultaStatusAnteriores(codProc, prevEtapas);
		PreparedStatement pst = cnBpm.prepareStatement(sql);
		ResultSet rs = pst.executeQuery(sql);
		while (rs.next()) {
			status = rs.getString(1);
			if (status.equalsIgnoreCase("R")) break;
		}
		return status;
	}
	
}
