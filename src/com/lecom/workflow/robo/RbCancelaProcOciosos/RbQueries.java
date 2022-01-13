package com.lecom.workflow.robo.RbCancelaProcOciosos;

public class RbQueries {
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO NAS TABELAS FORMULARIO, PROCESSO E PROCESSO_ETAPA
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	
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
	 * RETORNA QUERY DE CONSULTA POR PROCESSOS OCIOSOS HA N DIAS
	 * @param tipoExecucao -> 'n' = NOTIFICA GESTORES APENAS / 'c' = CANCELAR PROCESSOS E NOTIFICAR GESTORES 
	 * @param nDiasNotificaProcOciosos
	 * @param nDiasCancelaProcOciosos
	 * @param lstExcludedCodForm
	 * @return
	 */
	public static String consultaProcOciosos(char tipoExecucao, int nDiasNotificaProcOciosos, int nDiasCancelaProcOciosos, String lstExcludedCodForm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  ");
		sql.append(" 	 PRC.COD_FORM "); 		  // CODIGO DO FORMULARIO BPM
		sql.append(" 	,PRC.COD_PROCESSO "); 	  // CODIGO DO PROCESSO BPM
		sql.append(" 	,FRM.DES_TITULO "); 	  // TITULO DO FORMULARIO DEFINIDO NO MODELO
		sql.append(" 	,ETP.DES_ETAPA "); 		  // TITULO DA ETAPA DEFINIDO NO MODELO
		sql.append(" 	,PE.COD_USUARIO_ETAPA "); // CODIGO DO USUARIO RESPONSAVEL PELA ETAPA
		sql.append(" 	,USU.NOM_USUARIO "); 	  // NOME DO USUARIO RESPONSAVEL PELA ETAPA
		sql.append(" FROM PROCESSO AS PRC ");
		sql.append(" INNER JOIN PROCESSO_ETAPA AS PE ");
		sql.append(" ON PRC.COD_PROCESSO = PE.COD_PROCESSO ");
		sql.append(" AND PRC.COD_ETAPA_ATUAL = PE.COD_ETAPA ");
		sql.append(" AND PRC.COD_CICLO_ATUAL = PE.COD_CICLO ");
		sql.append(" INNER JOIN FORMULARIO AS FRM ");
		sql.append(" ON PRC.COD_FORM = FRM.COD_FORM ");
		sql.append(" AND PRC.COD_VERSAO = FRM.COD_VERSAO ");
		sql.append(" INNER JOIN ETAPA AS ETP ");
		sql.append(" ON PRC.COD_FORM = ETP.COD_FORM ");
		sql.append(" AND PRC.COD_VERSAO = ETP.COD_VERSAO ");
		sql.append(" AND PRC.COD_ETAPA_ATUAL = ETP.COD_ETAPA ");
		sql.append(" INNER JOIN USUARIO USU ");
		sql.append(" ON USU.COD_USUARIO = PE.COD_USUARIO_ETAPA ");
		sql.append(" WHERE PRC.IDE_FINALIZADO = 'A' ");
		if (tipoExecucao == 'n') {
			sql.append(" AND CONVERT(DATETIME, DAT_DATA) B ETWEEN GETDATE()-" + nDiasCancelaProcOciosos + " AND GETDATE()-" + nDiasNotificaProcOciosos);
		} else {
			sql.append(" AND CONVERT(DATETIME, DAT_DATA) < GETDATE()-" + nDiasCancelaProcOciosos);
		}
		if (lstExcludedCodForm.length() > 0)
			sql.append(" AND COD_FORM NOT IN (" + lstExcludedCodForm + ") ");
		sql.append(" ORDER BY COD_FORM, COD_PROCESSO ");
		return sql.toString();
	}
	
	/**
	 * RETORNA QUERY PARA CANCELAR UMA LISTA DE PROCESSOS NA TABELA PROCESSO_ETAPA
	 * @param lstCodProc
	 * @return
	 */
	public static String cancelaProcessoEtapaAndamento(String lstCodProc) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE PROCESSO_ETAPA ");
		sql.append(" SET IDE_STATUS = 'C' ");
		sql.append(" 	,DAT_FINALIZACAO = CONVERT(DATETIME,GETDATE()) ");
		sql.append(" WHERE IDE_STATUS = 'A' ");
		sql.append(" AND COD_PROCESSO IN (" + lstCodProc + ") ");
		return sql.toString();
	}
	
	/**
	 * RETORNA QUERY PARA CANCELAR UMA LISTA DE PROCESSOS NA TABELA PROCESSO
	 * @param lstCodProc
	 * @return
	 */
	public static String cancelaProcessoAndamento(String lstCodProc) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE PROCESSO ");
		sql.append(" SET IDE_FINALIZADO = 'C' ");
		sql.append(" WHERE IDE_FINALIZADO = 'A' ");
		sql.append(" AND COD_PROCESSO IN (" + lstCodProc + ") ");
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
