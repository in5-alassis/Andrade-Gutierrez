package com.lecom.workflow.robo.RbExecutaEtapasGenerico;

public class RbQueries {
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NAS TABELAS FORMULARIO, PROCESSO E PROCESSO_ETAPA
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * RETORNA QUERY CONSULTA SE O PROCESSO ESTA EM MODO TESTE
	 * @param codProc
	 * @return
	 */
	public static String procModoTeste(int codProc) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT IDE_BETA_TESTE FROM PROCESSO WHERE COD_PROCESSO = " + codProc);
		return sql.toString();
	}
	
	/**
	 *  RETORNA QUERY DE CONSULTA PROCESSOS PARADOS COM O USUARIO in5.robo
	 * @param codRobo
	 * @param codForm
	 * @param codEtapa
	 * @return
	 */
	public static String consultaProcessosRobo(int codRobo, int codForm, int codEtapa) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT PE.COD_PROCESSO, MAX(PE.COD_CICLO) AS COD_CICLO ");
		sql.append(" FROM PROCESSO AS PRC ");
		sql.append(" INNER JOIN PROCESSO_ETAPA AS PE ");
		sql.append(" ON  PRC.COD_PROCESSO = PE.COD_PROCESSO ");
		sql.append(" WHERE PRC.COD_FORM = " + codForm);
		sql.append(" AND   PE.COD_ETAPA = " + codEtapa);
		sql.append(" AND   PE.COD_USUARIO_ETAPA = " + codRobo);
		sql.append(" AND   PE.IDE_STATUS = 'A' ");
		sql.append(" GROUP BY PE.COD_PROCESSO ");
		sql.append(" ORDER BY PRC.COD_PROCESSO ASC ");
		return sql.toString();
	}
	
	/**
	 * RETORNA QUERY DE CONSULTA DO COD_ETAPA_ATUAL DO PROCESSO EM ANDAMENTO
	 * @param codForm
	 * @param codProc
	 * @return
	 */
	public static String consultaCodEtapaAtual(int codForm, int codProc) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COD_ETAPA_ATUAL FROM PROCESSO WHERE COD_FORM = " + codForm + " AND COD_PROCESSO = " + codProc);
		return sql.toString();
	}
	
	/**
	 * RETORNA QUERY PARA CONTAR ETAPAS EM ANDAMENTO EM UM PROCESSO
	 * @param codProc
	 * @param codForm
	 * @return
	 */
	public static String consultaQtdeEtapasAndamento(int codProc, int codForm, int codEtapaConcentradora, int codEtapaAtual) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COUNT(PE.COD_ETAPA) AS TOTAL_ETAPAS_ANDAMENTO ");
		sql.append(" FROM processo_etapa AS PE ");
		sql.append(" INNER JOIN processo AS PRC ");
		sql.append(" ON PE.COD_PROCESSO = PRC.COD_PROCESSO ");
		sql.append(" WHERE PE.IDE_STATUS = 'A' ");
		sql.append(" AND PRC.COD_FORM = " + codForm);
		sql.append(" AND PRC.COD_PROCESSO = " + codProc);
		sql.append(" AND PE.COD_ETAPA NOT IN (" + codEtapaConcentradora + ")");
		return sql.toString();
	}
	
	/**
	 * RETORNA QUERY PARA CONSULTA DO STATUS DAS ETAPAS ANTERIORES A CONCENTRADORA
	 * @param codProc
	 * @param codForm
	 * @param prevEtapas
	 * @return
	 */
	public static String consultaStatusAnteriores(int codProc, String prevEtapas) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT IDE_STATUS ");
		sql.append(" FROM PROCESSO_ETAPA ");
		sql.append(" WHERE COD_PROCESSO = " + codProc);
		sql.append(" AND COD_ETAPA IN (" + prevEtapas + ") ");
		sql.append(" AND COD_CICLO IN (SELECT MAX(COD_CICLO) FROM PROCESSO_ETAPA WHERE COD_PROCESSO = " + codProc + "  AND COD_ETAPA IN (" + prevEtapas + ") ) ");
		return sql.toString();
	}
	
}
