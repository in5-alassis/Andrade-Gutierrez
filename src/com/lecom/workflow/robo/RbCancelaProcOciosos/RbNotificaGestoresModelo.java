package com.lecom.workflow.robo.RbCancelaProcOciosos;

import java.util.List;
import java.util.Map;

import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.common.util.controller.ExecutaNotificacaoEmailBpm;

/**
 * ESTA CLASSE POSSUI OS METODOS QUE FORMATA AS MENSAGENS DE NOTIFICACAO AOS
 * GESTORES DE MODELOS E QUE SAO RELACIONADAS AO AVISO DE CANCELAMENTO DE
 * PROCESSO E DO PROPRIO CANCELAMENTO DOS PROCESSOS
 * 
 * @author Rafael Marquini
 * @since 07/08/2020
 *
 */
public class RbNotificaGestoresModelo extends RbCancelaProcOciososMain {

	private static final ExecutaNotificacaoEmailBpm notifica = new ExecutaNotificacaoEmailBpm();

	/**
	 * METODO PARA NOTIFICAR FUTUROS CANCELAMENTOS
	 * 
	 * @param paramGerais
	 * @param isTestando
	 * @param emailsGestores
	 * @param dadosProc
	 * @throws Exception
	 */
	protected void notificaGestoresFuturosCancelamentos(Map<String, String> paramGerais, boolean isTestando,
														List<String> emailsGestores, 
														List<Map<String, Object>> dadosProc) throws Exception {

		LOGGER.info(new String(new char[50]).replace("\0", "-"));
		LOGGER.info("ENVIANDO NOTIFICACAO AOS GESTORES SOBRE CANCELAMENTO FUTURO DE PROCESSOS OCIOSOS");
		// DECLARA A VARIAVEL Q RECEBERA O ASSUNTO
		String subject = "Alerta de cancelamento de processos ociosos";
		// INVOCA METODO PARA MONTAR UMA STRING DE MENSAGEM
		StringBuffer message = new StringBuffer();
		// CORPO DA MENSAGEM
		message.append("<table class='main-msg'><tbody>");
		message.append("<tr><td>");
		message.append("<div class='subject'>"+ subject +"</div>");
		message.append("</td></tr>");
		message.append("<tr><td>&nbsp;</td></tr>");
		message.append("<tr><td>");
		message.append("Data: " + Funcoes.getDateServer() + ".");
		message.append("<br />Os seguintes processos serão cancelados dentro de 30 dias. <br />");
		message.append("Caso necessária alguma ação comunique os usuários responsáveis pelas etapas pendentes. <br /><br />");
		message.append("<table class='ov'><tbody>");
		message.append("<tr>");
		message.append("<th>Processo</th>");
		message.append("<th>Formulário</th>");
		message.append("<th>Etapa</th>");
		message.append("<th>Usuário responsável</th>");
		message.append("</tr>");

		for (Map<String, Object> proc : dadosProc) {
			message.append("<tr>");
			message.append("<td>" + proc.get("COD_PROCESSO") + "</td>");
			message.append("<td>" + proc.get("DES_TITULO") + "</td>");
			message.append("<td>" + proc.get("DES_ETAPA") + "</td>");
			message.append("<td>" + proc.get("NOM_USUARIO") + "</td>");
			message.append("</tr>");
		}

		message.append("</tbody></table>");
		message.append("</tr></td>");
		message.append("</tbody></table>");
		

		notifica.enviaEmail(LOGGER, emailsGestores, subject, message.toString(), paramGerais, isTestando);

	}

	// CLASSE PARA NOTIFICAR OS CANCELAMENTOS EFETUADOS
	protected void notificaGestoresCancelamentosEfetuados(Map<String, String> paramGerais, boolean isTestando,
														  List<String> emailsGestores, 
														  List<Map<String, Object>> dadosProc) throws Exception {
		
		LOGGER.info(new String(new char[50]).replace("\0", "-"));
		LOGGER.info("ENVIANDO NOTIFICACAO AOS GESTORES SOBRE CANCELAMENTO DE PROCESSOS OCIOSOS");
		// DECLARA A VARIAVEL Q RECEBERA O ASSUNTO
		String subject = "Cancelamento de processos ociosos";
		// INVOCA METODO PARA MONTAR UMA STRING DE MENSAGEM
		StringBuffer message = new StringBuffer();
		// CORPO DA MENSAGEM
		message.append("<table class='main-msg'><tbody>");
		message.append("<tr><td>");
		message.append("<div class='subject'>"+ subject +"</div>");
		message.append("</td></tr>");
		message.append("<tr><td>&nbsp;</td></tr>");
		message.append("<tr><td>");
		message.append("Data: " + Funcoes.getDateServer() + ".");
		message.append("<br />Os seguintes processos foram cancelados por falta de atividade. <br /><br />");
		message.append("<table class='ov'><tbody>");
		message.append("<tr>");
		message.append("<th>Processo</th>");
		message.append("<th>Formulário</th>");
		message.append("<th>Etapa</th>");
		message.append("<th>Usuário responsável</th>");
		message.append("</tr>");

		for (Map<String, Object> proc : dadosProc) {
			message.append("<tr>");
			message.append("<td>" + proc.get("COD_PROCESSO") + "</td>");
			message.append("<td>" + proc.get("DES_TITULO") + "</td>");
			message.append("<td>" + proc.get("DES_ETAPA") + "</td>");
			message.append("<td>" + proc.get("NOM_USUARIO") + "</td>");
			message.append("</tr>");
		}

		message.append("</tbody></table>");
		message.append("</tr></td>");
		message.append("</tbody></table>");
		
		notifica.enviaEmail(LOGGER, emailsGestores, subject, message.toString(), paramGerais, isTestando);

	}

}
