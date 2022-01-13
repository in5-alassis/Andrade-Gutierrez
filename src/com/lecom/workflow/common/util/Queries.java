package com.lecom.workflow.common.util;

public class Queries {
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NA TABELA IN5_REC_EXECUCAO
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * RETORNA QUERY DE INSERCAO NA TABELA IN5_REC_EXECUCAO
	 * @param tabelaRet
	 * @return
	 */
	public static String insertIn5RecExecucao(String tabelaRec) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO " + tabelaRec + " ( ");
		sql.append(" COD_PROCESSO ");
		sql.append(" ,COD_ETAPA ");
		sql.append(" ,COD_CICLO ");
		sql.append(" ,STATUS_EXECUCAO ");
		sql.append(" ,DATA_EXECUCAO ");
		sql.append(" ,TIPO_EXECUCAO ");
		sql.append(" ,ORIGEM ");
		sql.append(" ,MSG_RETORNO ");
		sql.append(" ) VALUES ( ");
		sql.append(" ? ");  // 1. COD_PROCESSO - INT
		sql.append(" ,? "); // 2. COD_ETAPA - INT
		sql.append(" ,? "); // 3. COD_CICLO - INT
		sql.append(" ,? "); // 4. STATUS_EXECUCAO - INT
		sql.append(" ,? "); // 5. DATA_EXECUCAO - DATETIME, YYYY-MM-DD HH:MM:SS
		sql.append(" ,? "); // 6. TIPO_EXECUCAO - VARCHAR
		sql.append(" ,? "); // 7. ORIGEM - VARCHAR
		sql.append(" ,? "); // 8. MSG_RETORNO - TEXT
		sql.append(" ) ");
		return sql.toString();
	}
	
	/**
	 * RETORNA QUERY DE CONSULTA NA TABELA IN5_REC_EXECUCAO
	 * PARA DETERMINAR SE PROCESSO EXISTE GRAVADO NELA
	 * @param tabelaRec
	 * @param codProc
	 * @return
	 */
	public static String consultaProcIn5RecExecucao(String tabelaRec, int codProc) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COUNT(COD_PROCESSO) ");
		sql.append(" FROM " + tabelaRec);
		sql.append(" WHERE COD_PROCESSO IN (" + codProc + ") ");
		sql.append(" AND STATUS_EXECUCAO = 0 ");
		return sql.toString();
	}
	
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
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NA TABELA USUARIO
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
	/**
	 * RETORNA QUERY PARA CONSULTAR INFOS DO USUARIO ATIVO E Q TEM AUTENTICACAO PELO LDAP ATRAVES DO COD_USUARIO
	 * @param lstCodUsuarios
	 * @return
	 */
	public static String consultaUsuariosPeloCodUsuario(String lstCodUsuarios) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	 COD_USUARIO ");
		sql.append(" 	,NOM_USUARIO ");
		sql.append(" 	,DES_LOGIN ");
		sql.append(" 	,IDE_ACESSO_ADM ");
		sql.append(" 	,IDE_USUARIO_INATIVO ");
		sql.append(" 	,DES_EMAIL ");
		sql.append(" 	,IDE_ACESSO_PESQ ");
		sql.append(" 	,IDE_ACESSO_ESTAT ");
		sql.append(" 	,IDE_ACESSO_MOD ");
		sql.append(" 	,COD_SUBSTITUTO ");
		sql.append(" 	,DATA_INI_FERIAS ");
		sql.append(" 	,DATA_FIM_FERIAS ");
		sql.append(" 	,IDE_ALTERAR_DADOS ");
		sql.append(" 	,COD_DEPTO ");
		sql.append(" 	,COD_LIDER ");
		sql.append(" 	,ACTIVED_AT ");
		sql.append(" 	,INACTIVED_AT ");
		sql.append(" 	,LAST_ACCESS_AT ");
		sql.append(" 	,IDE_AUTENTICA_TIPO ");
		sql.append(" FROM USUARIO ");
		sql.append(" WHERE COD_USUARIO IN (" + lstCodUsuarios + ") ");
		sql.append(" AND IDE_USUARIO_INATIVO = 'N' ");
		sql.append(" AND IDE_AUTENTICA_TIPO = 'LDAP' ");
		return sql.toString();
	}
	
	
}
