package com.lecom.workflow.robo.RbTemplate;

public class RbQueries {
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// CONSULTAS NAS TABELAS MODELO E GRID GENERICAS
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * RETORNA QUERY DE CONSULTA NA TABELA DO MODELO
	 * @param tabelaSQL
	 * @param codProc
	 * @param codEtapa
	 * @param codCiclo
	 * @return
	 */
	public static String consultaTbModelo(String tabelaSQL, int codProc, int codEtapa, int codCiclo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" SELECT * ");
		sqlb.append(" FROM " + tabelaSQL);
		sqlb.append(" WHERE COD_PROCESSO_F = " + codProc);
		sqlb.append(" AND   COD_ETAPA_F = " + codEtapa);
		sqlb.append(" AND   COD_CICLO_F = " + codCiclo);
		return sqlb.toString();
	}
	
	/**
	 * RETORNA QUERY DE CONSULTA NA TABELA GRID
	 * @param tabelaSQL
	 * @param codProc
	 * @param codEtapa
	 * @param codCiclo
	 * @return
	 */
	public static String consultaTbGrid(String tabelaSQL, int codProc, int codEtapa, int codCiclo) {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append(" SELECT * ");
		sqlb.append(" FROM " + tabelaSQL);
		sqlb.append(" WHERE COD_PROCESSO = " + codProc);
		sqlb.append(" AND   COD_ETAPA = " + codEtapa);
		sqlb.append(" AND   COD_CICLO = " + codCiclo);
		return sqlb.toString();
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NAS TABELAS FORMULARIO, PROCESSO E PROCESSO_ETAPA
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * RETORNA QUERY CONSULTA SE O FORMULARIO ESTA EM MODO TESTE
	 * @param codForm
	 * @return
	 */
	public static String formModoTeste(int codForm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT IDE_BETA_TESTE FROM FORMULARIO WHERE COD_FORM = " + codForm);
		return sql.toString();
	}
	
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
	 * RETORNA QUERY DE CONSULTA DO COD_USU_GESTOR DO FORMULARIO
	 * @param codForm
	 * @return
	 */
	public static String consultaUsrGestModelo(int codForm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COD_USU_GESTOR ");
		sql.append(" FROM FORMULARIO ");
		sql.append(" WHERE COD_FORM = " + codForm);
		sql.append(" ORDER BY COD_VERSAO DESC ");
		return sql.toString();
	}
	
}
