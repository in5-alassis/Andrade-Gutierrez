package com.lecom.workflow.robo.RbCancelaProcOciosos;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lecom.workflow.common.util.Funcoes;

public class RbEfetuaCancelamentosProcOciosos extends RbCancelaProcOciososMain {

	// INSTANCIA O NOME DA CLASSE 
	private static final String className = Funcoes.getClassName(RbEfetuaCancelamentosProcOciosos.class);
	
	protected void cancelaProcOciosos(Map<String, String> paramGerais, Map<String, String> paramExec,
									  Connection cnBpm, boolean isTestando) throws Exception {
		
		LOGGER.info(new String(new char[50]).replace("\0", "-"));
		LOGGER.info("INICIA A VERIFICACAO POR PROCESSOS OCIOSOS QUE SERAO CANCELADOS.");
		
		List<Map<String, Object>> procOciososCancelar = rbActDb.consultaProcOciosos(LOGGER, cnBpm, className, 'c',
			   																	   Integer.parseInt(paramExec.get("nDiasNotificaProcOciosos")),
			   																	   Integer.parseInt(paramExec.get("nDiasCancelaProcOciosos")), 
			   																	   paramExec.get("lstExcludedCodForm"));
		
		if (procOciososCancelar.size() > 0) {
			
			LOGGER.info("PROCESSOS OCIOSOS IDENTIFICADOS - PREPARANDO DADOS PARA ENVIO DE ALERTA AOS GESTORES DE MODELOS.");
			// FILTRANDO OS COD_FORM
			List<Integer> codForms = new ArrayList<Integer>();
			procOciososCancelar.stream()
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
					dadosProcOcioso = procOciososCancelar.stream()
												 		 .filter(map -> map.get("COD_FORM").equals(codForm))
												 		 .collect(Collectors.toList());
					LOGGER.debug("CANCELAR OS PROCESSOS RELACIONADOS AOS GESTORES " + lstCodUsrGestores + " E COD_FORM " + codForm + ": " + dadosProcOcioso);
					
					
					// CANCELAR PROCESSOS
					List<String> codProcCancelar = new ArrayList<>();
					dadosProcOcioso.stream()
								   .map(dadosProc -> {
									   String codProc = (String) dadosProc.get("COD_PROCESSO");
									   if (!codProcCancelar.contains(codProc)) {
										   codProcCancelar.add(codProc);
									   }
									   return codProcCancelar;
								   })
								   .collect(Collectors.toList());
					String lstCodProcCancelar = String.join(",", codProcCancelar);
					LOGGER.debug("LISTA DE PROCESSOS PARA CANCELAR RELACIONADOS AO COD_FORM " + codForm + ": " + lstCodProcCancelar);
					
					int[] cancelaProcessos = rbActDb.cancelaProcessos(LOGGER, cnBpm, lstCodProcCancelar);
					
					boolean isCancelados = false;
					for (int ret : cancelaProcessos) {
						LOGGER.debug("PROCESSOS CANCELADOS RETORNO: " + ret);
						isCancelados = (ret > 0) ? true : false;
						if (!isCancelados) break;
					}
					
					if (isCancelados) {
						// ENVIA O EMAIL DE NOTIFICACAO SOBRE O CANCELAMENTO DOS PROCESSOS
						RbNotificaGestoresModelo notificaGestor = new RbNotificaGestoresModelo();
						notificaGestor.notificaGestoresCancelamentosEfetuados(paramGerais, isTestando, emailsGestores, dadosProcOcioso);
					}
					
				} else {
					// FORMULARIO NAO POSSUI GESTORES
					handleErrors.HandleGenError(LOGGER, notifica, className, "NAO HA GESTORES DEFINIDOS PARA O FORMULARIO " + codForm, paramGerais, isTestando, 0);
					actDb.insertIn5RecExecucao(cnBpm, paramGerais, Funcoes.in5RecExecDadosProc(0, 0, 0, 99, className, "NAO HA GESTORES DEFINIDOS PARA O FORMULARIO " + codForm));
				}
			}
			
		} else {
			LOGGER.info("NAO HA PROCESSOS OCIOSOS PARA SEREM CANCELADOS.");
		}
		LOGGER.info(new String(new char[50]).replace("\0", "-"));
	}
	
}
