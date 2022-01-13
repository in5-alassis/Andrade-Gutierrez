package com.lecom.workflow.robo.RbCancelaProcOciosos;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lecom.workflow.common.util.Funcoes;

/**
 * CLASSE QUE EXTENDE RbCancelaProcOciososMain E BUSCA POR PROCESSOS OCIOSOS
 * DENTRO DO PERIODO DE ALERTA E ENVIA AS NOTIFICACOES AOS GESTORES DOS FORMULARIOS
 * 
 * @author Rafael Marquini
 * @since 06/08/2020
 *
 */
public class RbFuturosCancelamentos extends RbCancelaProcOciososMain {
	
	// INSTANCIA O NOME DA CLASSE 
	private static final String className = Funcoes.getClassName(RbFuturosCancelamentos.class.getCanonicalName());
	
	/**
	 * CLASSE QUE IDENTIFICA OS PROCESSOS OCIOSOS ENTRE O LIMITE DE DIAS PARA CANCELAR 
	 * E 30 DIAS ANTES. QDO OS PROCESSOS SAO IDENTIFICADOS, OS COD_FORM SAO ISOLADOS
	 * PARA RECUPERAR OS USUARIOS GESTORES DOS FORMULARIOS. ENTAO, COM OS USUARIOS
	 * IDENTIFICADOS EH ISOLADOS SEUS E-MAILS E, EM SEGUIDA, PARA CADA COD_FORM IDENTIFICADO
	 * EH MONTADO UMA NOVA LISTA DE MAPAS COM DADOS DOS PROCESSOS RELACIONADOS AQUELES
	 * GESTORES, PARA FINALMENTE, SEREM ENVIADAS A NOTIFICACAO DE FUTURO CANCELAMENTO.
	 * 
	 * @param paramGerais
	 * @param paramExec
	 * @param cnBpm
	 * @param isTestando
	 * @throws Exception
	 */
	protected void verificaFuturosCancelamentos(Map<String, String> paramGerais, Map<String, String> paramExec,
												Connection cnBpm, boolean isTestando) throws Exception {
		
		LOGGER.info(new String(new char[50]).replace("\0", "-"));
		LOGGER.info("INICIA A VERIFICACAO POR PROCESSOS OCIOSOS DENTRO DO PRAZO DE ALERTA.");

		List<Map<String, Object>> procOciososNotificar = rbActDb.consultaProcOciosos(LOGGER, cnBpm, className, 'n',
																				   Integer.parseInt(paramExec.get("nDiasNotificaProcOciosos")),
																				   Integer.parseInt(paramExec.get("nDiasCancelaProcOciosos")), 
																				   paramExec.get("lstExcludedCodForm"));

		if (procOciososNotificar.size() > 0) {
			LOGGER.info("PROCESSOS OCIOSOS IDENTIFICADOS - PREPARANDO DADOS PARA ENVIO DE ALERTA AOS GESTORES DE MODELOS.");
			// FILTRANDO OS COD_FORM
			List<Integer> codForms = new ArrayList<Integer>();
			procOciososNotificar.stream()
								.map(procOcioso -> {
									int codForm = (int) procOcioso.get("COD_FORM");
									if (!codForms.contains(codForm))
										codForms.add(codForm);
									return codForms;
								})
								.collect(Collectors.toList());
			LOGGER.debug("LISTA DE COD_FORM IDENTIFICADOS: " + codForms);
			
			for (int codForm : codForms) {
				// CONSULTA OS COD_USUARIO DOS GESTORES DO FORMULARIO
				List<String> codUsrGestores = rbActDb.consultaUsrGestModelo(LOGGER, cnBpm, handleErrors, notifica, paramGerais, isTestando, className, codForm, 0);
				if (codUsrGestores.size() > 0) {
					// FORMULARIO POSSUI GESTORES
					String lstCodUsrGestores = codUsrGestores.stream()
															 .collect(Collectors.joining(","));
					LOGGER.debug("LISTA COD_USUARIO GESTORES DO COD_FORM " + codForm + ": " + lstCodUsrGestores);
					
					// CONSULTA INFORMACOES DOS USUARIOS GESTORES ATRAVES DO COD_USUARIO
					List<Map<String, Object>> gestoresForm = rbActDb.consultaUsuariosPeloCodUsuario(LOGGER, cnBpm, handleErrors, notifica, paramGerais, isTestando, className, lstCodUsrGestores, 0);
					
					// CONVERTE UMA LISTA APENAS COM OS EMAILS DOS GESTORES 
					// DO List of Maps gestoresForm QUE CONTEM OS DADOS DE USUARIOS
					List<String> emailsGestores = new ArrayList<>();
					gestoresForm.stream()
								.map(gestor -> {
									emailsGestores.add((String) gestor.get("DES_EMAIL"));
									return emailsGestores;
								})
								.collect(Collectors.toList());
					LOGGER.debug("LISTA DE EMAILS DOS GESTORES DO FORM " + codForm + ": " + emailsGestores);
					
					// FILTRA APENAS OS DADOS DOS PROCESSOS COM O COD_FORM ATUAL
					List<Map<String, Object>> dadosProcOcioso = new ArrayList<>();
					dadosProcOcioso = procOciososNotificar.stream()
												 		  .filter(map -> map.get("COD_FORM").equals(codForm))
												 		  .collect(Collectors.toList());
					LOGGER.debug("PROCESSOS RELACIONADOS AOS GESTORES " + lstCodUsrGestores + " E COD_FORM " + codForm + ": " + dadosProcOcioso);
					
					// ENVIA O EMAIL DE NOTIFICACAO SOBRE FUTUROS CANCELAMENTOS
					RbNotificaGestoresModelo notificaGestor = new RbNotificaGestoresModelo();
					notificaGestor.notificaGestoresFuturosCancelamentos(paramGerais, isTestando, emailsGestores, dadosProcOcioso);
					
				} else {
					// FORMULARIO NAO POSSUI GESTORES
					handleErrors.HandleGenError(LOGGER, notifica, className, "NAO HA GESTORES DEFINIDOS PARA O FORMULARIO " + codForm, paramGerais, isTestando, 0);
					actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(0, 0, 0, 99, className, "NAO HA GESTORES DEFINIDOS PARA O FORMULARIO " + codForm));
				}
			}
		} else {
			LOGGER.info("NAO HA PROCESSOS OCIOSOS PARA NOTIFICACAO DE CANCELAMENTO FUTURO.");
		}
		LOGGER.info(new String(new char[50]).replace("\0", "-"));

	}

}
