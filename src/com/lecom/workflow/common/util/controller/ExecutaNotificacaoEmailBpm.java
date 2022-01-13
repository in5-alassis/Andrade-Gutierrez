package com.lecom.workflow.common.util.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lecom.workflow.common.util.Funcoes;
import com.lecom.workflow.robo.satelite.WFMail;

import br.com.lecom.workflow.email.EmailMessage;

public class ExecutaNotificacaoEmailBpm {
	
	/**
	 * ACIONA SUPORTE DEV INDICADO NA VARIAVEL DE AMBIENTE toDev DEFINIDA 
	 * NO ARQUIVO ParametroGerais.properties
	 * @param LOGGER
	 * @param errorParam
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void acionaSuporteDev(Logger LOGGER, Map<String, String> errorParam, Map<String, String> paramGerais, boolean isTestando, int codProc) throws Exception {
		
		// DECLARA A VARIAVEL Q RECEBERA O ASSUNTO
		String subject = "ERRO DE PROCESSAMENTO";
		// INVOCA METODO PARA MONTAR UMA STRING DE MENSAGEM
		StringBuffer message = new StringBuffer();
		// CORPO DA MENSAGEM
		message.append("<table class='main-msg'><tbody>");
		
		message.append("<tr><td>");
		message.append("<div class='subject'>"+ subject +"</div>");
		message.append("<div class='proc'>Processo: "+ codProc +"</div>");
		message.append("</td></tr>");
		
		message.append("<tr><td>&nbsp;</td></tr>");
		
		message.append("<tr><td>");
		message.append("Data: " + errorParam.get("DATA_SISTEMA") + ".");
		message.append("<br />Informações do Erro: <br /><br />");
		
		message.append("<table class='ov'><tbody>");
		message.append("<tr>");
		message.append("<th>Classe</th>");
		message.append("<th>Erro</th>");
		message.append("</tr>");		
		message.append("<tr>");
		message.append("<td>"+ errorParam.get("CLASSE") +"</td>");
		message.append("<td>"+ errorParam.get("ERROR") +"</td>");
		message.append("</tr>");
		message.append("</tbody></table>");
		
		message.append("</tr></td>");
		
		message.append("</tbody></table>");
		
		enviaEmail(LOGGER, Funcoes.getEmailsDev(paramGerais), subject, message.toString(), paramGerais, isTestando);
		
	}
	
	// =====================================================================
	// ========================= CORPO EMAIL FINAL =========================
	/**
	 * DISPARA O ENVIO DE NOTIFICACAO POR EMAIL
	 * @param LOGGER
	 * @param to
	 * @param subject
	 * @param message
	 * @param paramGerais
	 * @param isTestando
	 * @throws Exception
	 */
	public void enviaEmail(Logger LOGGER, List<String> to, String subject, String message, 
								  Map<String, String> paramGerais, boolean isTestando) throws Exception {
		
		StringBuffer bodyMsg = new StringBuffer();
		
		bodyMsg.append("<!DOCTYPE html><html>");
		bodyMsg.append("<head><style type='text/css'>");
		bodyMsg.append("@import url('https://fonts.googleapis.com/css?family=Roboto&display=swap');");
		bodyMsg.append("html, body {margin: 0; padding: 10px 10%;}");
		bodyMsg.append("body {font-family: 'Roboto', Arial, sans-serif; font-size: 14px; color: #333333;}");
		bodyMsg.append("img {vertical-align: middle;}");
		bodyMsg.append("table {width: 100%; border-collapse: collapse;}");
		bodyMsg.append("table.titulo tr {background: #FFF; color: #ffc30b; border-bottom: 3px solid #ffc30b;}");
		bodyMsg.append("table.titulo tr td {padding: 10px 5px;}");
		bodyMsg.append("div.logo {display: block; float: left; padding: 0 0 7px;}");
		bodyMsg.append("img {vertical-align: middle;}");
		bodyMsg.append("table.main-msg tr:first-child {background: #F4F4F4;}");
		bodyMsg.append("div.subject, div.proc {display: block; float: left; font-size: 18px; font-weight: 600; text-align: center; padding: 20px 5px; color: #333;}");
		bodyMsg.append("div.proc {float: right;}");
		bodyMsg.append("div.clear {clear: both;}");
		bodyMsg.append("table.ov {width: 100%; position: relative; left: 0;}");
		bodyMsg.append("table.ov th, table.ov td {border: 1px solid #999999; padding: 3px 7px; text-align: center;}");
		bodyMsg.append("table.ov tr th {background: #ffc30b; color: #333;}");
		bodyMsg.append("table.ov tr:nth-child(2n+1) td {background: #F4F4F4;}");
		bodyMsg.append("table.footer {border-top: solid 1px #CCCCCC; color: #999999;}");
		bodyMsg.append("</style></head>");
		bodyMsg.append("<body><table class='titulo'><thead><tr><td>");
		bodyMsg.append("<div class='logo'><img src='"+ paramGerais.get("urlLogo") +"' width='200px' /></div>");
		bodyMsg.append("<div class='clear'></div>");
		bodyMsg.append("</td></tr></thead></table><br />");
		bodyMsg.append(message);
		bodyMsg.append("<br /><table class='footer'><tbody><tr><td>");
		bodyMsg.append("<small>Mensagem enviada automaticamente. Por favor, não responder.</small><br />");
		bodyMsg.append("<small>"+ paramGerais.get("nomeCliente") +" - "+ paramGerais.get("emailBpm") +"</small><br /><br />");
		bodyMsg.append("<small>Esta mensagem e seus anexos são de uso exclusivo de pessoas e entidades que possuem relacionamento com a "+ paramGerais.get("nomeCliente") +". ");
		bodyMsg.append("é proibido revelar, alterar, copiar, divulgar ou beneficiar-se dessas informações, direta ou indiretamente, sem a ");
		bodyMsg.append("autorização de seus autores. Se você não for o real destinatário deste e-mail, por favor informe o remetente sobre o engano e apague a");
		bodyMsg.append("mensagem imediatamente. A "+ paramGerais.get("nomeCliente") +" se reserva o direito de pleitear ressarcimento por possíveis prejuízos decorrentes do uso indevido dessas ");
		bodyMsg.append("informações e requerer a aplicação das penalidades cabíveis.</small> ");
		bodyMsg.append("</td></tr></tbody></table></body></html>");

		try {
			LOGGER.debug("LISTA DE EMAIL OFICIAL: " + to);
			
			// SE TIVER EM MODO TESTE MANDA EMAIL PARA toDev
			if (isTestando) {
				LOGGER.debug("ENVIANDO EMAIL PARA TESTE");
				to.clear();
				to = Funcoes.limpaListaEmails(LOGGER, Funcoes.getEmailsDev(paramGerais));
			} else {
				// VERIFICA SE OS EMAILS SAO VALIDOS E FORMATA LISTA DE E-MAILS 
				to = Funcoes.limpaListaEmails(LOGGER, to);
			}
			
			// MONTA A MENSAGEM, CONVERTENDO-A EM STRING
			EmailMessage email = new EmailMessage(subject, Funcoes.montaMensagem(bodyMsg.toString()), paramGerais.get("emailBpm"), to, true); // TRUE PARA USAR HTML
			WFMail wfmail = new WFMail();
			wfmail.enviaEmailMessage(email);
			LOGGER.info("NOTIFICACAO DE E-MAIL - ENVIADO COM SUCESSO.");
			
			
		} catch (Exception e) {
			Writer writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			LOGGER.error("FALHA AO ENVIAR E-MAIL: " + e.getMessage());
			LOGGER.error(writer.toString());
		}

	}

}
