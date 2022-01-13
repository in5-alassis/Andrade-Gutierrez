package com.lecom.workflow.common.util;

import static br.com.lecom.api.factory.ECMFactory.documento;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

public class Funcoes {
	
	private static Writer writer = new StringWriter();

	// ---------------------------------------------------------------------
	// MANIPULACAO DE ACESSOS A ARQUIVOS
	/**
	 * RETORNA O DIRETORIO BASE DO LECOM (TOMCAT)
	 * @return
	 */
	public static String getWFRootDir() {
		String rootDir = Funcoes.class.getClassLoader().getResource("").getPath();
		rootDir += "../"; // WEB-INF
		rootDir += "../"; // CLASSES
		return rootDir;
	}
	//. RETORNA O DIRETORIO BASE DO LECOM (TOMCAT)
	
	/**
	 * RETORNA CANONICAL PATH
	 * @param LOGGER
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getCanonicalPath(Logger LOGGER, String path) throws IOException {
		try {
			File f = new File(path);
			path = f.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace(new PrintWriter(writer));
			LOGGER.error("ERROR AO GERAR PATH CANONICO: " + e.getMessage());
			LOGGER.error(writer.toString());
		}
		return path;
	}
	
	/**
	 * METODO QUE LE ARQUIVO PROPERTIES COM OS PARAMETROS SENDO RETORNADOS EM UM MAPA
	 * @param LOGGER
	 * @param nomeArquivoParam
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getParametrosLecom(Logger LOGGER, String nomeArquivoParam) throws Exception {

		Map<String, String> parametroMap = new HashMap<String, String>();
		try {
	    	if(!nomeArquivoParam.endsWith(".properties")) nomeArquivoParam += ".properties";
	
	    	FileInputStream arquivo = new FileInputStream(nomeArquivoParam);
		    Properties properties = new Properties();
		    properties.load(arquivo);
	
		    Set<Object> keySet = properties.keySet();
		    Iterator<Object> iterator = keySet.iterator();
	
		    while (iterator.hasNext()) {
				String key = (String) iterator.next();
				parametroMap.put(key, properties.getProperty(key));
			}
		    
		    //LOGGER.debug("MAPA DE PARAMETROS: " + parametroMap);
		    
		} catch (Exception e) {
			e.printStackTrace(new PrintWriter(writer));
			LOGGER.error("ERRO NA LEITURA DO ARQUIVO: " + nomeArquivoParam);
			LOGGER.error(writer.toString());
			throw e;
		}
		
		return parametroMap;
		
    }
	
	/**
	 * 
	 * @param fileDir
	 * @param fileContent
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static File generateFile(String fileDir, InputStream fileContent, String fileName) throws Exception {
		File dir = new File("/opt/lecom/anexos/" + fileDir);
		dir.mkdirs();
		File filePath = new File(dir.getPath() + "/" + fileName);
		Files.copy(fileContent, filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return filePath;
	}

	/**
	 * 
	 * @param form
	 * @param campoId
	 * @return
	 * @throws Exception
	 */
	public static InputStream getAnexo(Map<String, Object> form, String campoId) throws Exception {
		return documento().lerArquivo(Funcoes.obj2String(form.get("$" + campoId)).split(":")[1]);
	}
	
	/**
	 * 
	 * @param form
	 * @param campoId
	 * @return
	 */
	public static String getAnexoNome(Map<String, Object> form, String campoId) {
		return Funcoes.obj2String(form.get("$" + campoId)).split(":")[0];
	}
	
	/**
	 * 
	 * @param readFromClasspath
	 * @param filePath
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getContentProperties(Boolean readFromClasspath, String filePath, String key) throws Exception {
		Properties prop = new Properties();
		prop.load(readFromClasspath ? Funcoes.class.getResourceAsStream(filePath) : new FileInputStream(filePath));
		return prop.getProperty(key);
	}
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static String getDbValueString(String arg) {
		return "'" + (arg != null ? arg.trim().replaceAll("\\\"|\\'", "") : "") + "'";
	}
	
	/**
	 * 
	 * @param error
	 * @param response
	 * @return
	 */
	public static Map<String, Object> getResponseMap(Boolean error, Object response) {
		Map<String, Object> map = new HashMap<>();
		map.put("error", error);
		map.put("response", response);
		return map;
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO DE DATA E HORA
	
	/**
	 * COLETA A DATA DO SISTEMA NO FORMATO PARA BANCO DE DADOS E COLUNAS DATETIME
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getDatetime() {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dtSys = new Date();
		String dateServer = sdf.format(dtSys);
		return dateServer;
	}
	
	/**
	 * COLETA A DATA DO SERVIDOR NO FORMATO String dd/MM/yyyy HH:mm
	 * @return
	 */
	public static String getDateServer() {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date dtSystem = new Date();
		String dateServer = sdf.format(dtSystem);
		return dateServer;
	}
	
	/**
	 * COMPARA SE A HORA DE EXECUCAO INFORMADA NOS PARAMETROS EH IGUAL A DO SERVIDOR 
	 * @param LOGGER
	 * @param hrExec
	 * @return
	 */
	public static boolean executaRobo(Logger LOGGER, int hrExec) {
		boolean ret = false;
		Calendar dtAtual = Calendar.getInstance();
		int hrAtual = dtAtual.get(Calendar.HOUR_OF_DAY);
		ret = (hrAtual == hrExec) ? true : false;
		return ret;
	}
	
	/**
	 * FORMATA A DATA NO PADRAO BPM PARA ABERTURA DE PROCESSOS 'AAAA-MM-DD T00:00:00Z'
	 * @param data
	 * @return
	 */
	public static String formataDataPadraoBPM(String data) {
		String dataBPMFormat = data.split("/")[2] + "-" + data.split("/")[1] + "-" + data.split("/")[0];
		dataBPMFormat = dataBPMFormat + "T00:00:00Z";
		return dataBPMFormat;
	}
	
	/**
	 * CONVERTE DATA PARA O FORMATO dd/MM/yyyy
	 * @param data
	 * @return
	 */
	public static String convertData(String data) {
		if ("".equals(data) || data.length() == 0 || data == null) return "";
		DateTimeFormatter formatador = 	DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate  local = LocalDate.parse(data.substring(0, 10));
		return local.format(formatador).toString();
	}
	
	/**
	 * INVERTE O VALOR DATA DE yyyy-MM-dd PARA dd-MM-yyyy
	 * @param arg
	 * @param inverted
	 * @return
	 */
	public static String obj2StringDate(Date arg, Boolean inverted) {
		return new SimpleDateFormat(inverted ? "yyyy-MM-dd" : "dd-MM-yyyy").format(arg);
	}
	
	/**
	 * COMPARA DATA DO DIA COM UMA DATA DE ENTRADA NO FORMATO yyyy-MM-dd
	 * @param LOGGER
	 * @param inDtAdmissao
	 * @return
	 * @throws ParseException
	 */
	public static long dateDiff(Logger LOGGER, String inDtAdmissao) throws ParseException {
		
		LOGGER.debug("-----------------------------------");
		LOGGER.debug("COMPARA DIFF ENTRE DATAS");
		
		long diff = 0;
		long diffDays = 0;
		
		try {
			// CRIA OS FORMATOS DE DATA
			DateFormat sdf1 = new SimpleDateFormat("yyyyMMdd 00:00:00");
			DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			
			// COLETA O DIA DE HJ E O CONVERTE EM STRING NO FORMATO sdf1
			Date   today 	= new Date();
			String strToday = sdf1.format(today);
			
			// COLETA A DATA QUE DESEJA COMPARAR, A CONVERTE COMO DATA VALIDA NO FORMATO sdf2
			// E ENTAO, CONVERTE PARA O FORMATO sdf1
			Date   dtAdmissao     = sdf2.parse(inDtAdmissao);
			String strDtAdmissao  = sdf1.format(dtAdmissao);
			
			// CONVERTE AMBAS DE STRING PARA DATE
			Date dtToday     = sdf1.parse(strToday);
			Date nDtAdmissao = sdf1.parse(strDtAdmissao);
			
			LOGGER.debug("DATA DE HOJE: " + strToday);
			LOGGER.debug("NOVA DATA DE ENTREGA: " + strDtAdmissao);
			
			// CALCULA A DIFERENCA EM MILISEGUNDOS PELO METODO getTime()
			diff = dtToday.getTime() - nDtAdmissao.getTime();
			
			// CALCULA A DIFERENCA EM DIAS BASEADO NOS MILISEGUNDOS
			diffDays = diff / (24 * 60 * 60 * 1000);

			LOGGER.debug(diffDays + " DIAS DE DIFERENCA ");
			LOGGER.debug("-----------------------------------");
			
		} catch (ParseException e) {
			e.printStackTrace(new PrintWriter(writer));
			LOGGER.error("ERRO AO COMPARAR DIFERENCA ENTRE DATAS");
			LOGGER.error(writer.toString());
			throw e;
		}
		return diffDays;
		
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// MANIPULACAO DE TIPOS
	
	/** 
	 * METODO QUE RETORNA A STRING RECEBIDA COMO PARAMETRO CASO O OBJETO SEJA NULO
	 * @param objeto
	 * @param retorno
	 * @return
	 */
	public static String nulo(Object objeto, String retorno) {
		String aux = "";
		if (objeto == null) {
			return retorno;
		} else {
			aux = objeto.toString();
			aux = (aux.equalsIgnoreCase("null") || aux.equalsIgnoreCase("")) ? retorno : aux;
			return aux;
		}
	}
	
	/**
	 * REMOVE ESPACOS INDESEJADOS, ACENTUACAO E CARACTERES ESPECIAIS PARA COMPARACAO DE STRINGS
	 * @param str
	 * @return
	 */
	public static String normalizarString(String str) {
		String retorno = Normalizer.normalize(str.replaceAll("[\\p{Punct}&&[^&/,]]+", ""), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").trim();
		return retorno;
	}
	
	/**
	 * VERIFICA SE STRING EH NUMERICO
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		return str.matches(("-?\\d+(\\.\\d+)?"));
	}
	
	/**
	 * CONVERSAO MAP PARA STRING
	 * @param str
	 * @return
	 */
	public static Map<String, String> convert(String str) {
	    String[] tokens = str.split(" |=");
	    Map<String, String> map = new HashMap<>();
	    for (int i=0; i<tokens.length-1; ) map.put(tokens[i++], tokens[i++]);
	    return map;
	}
	//. CONVERSAO MAP PARA STRING
	
	/**
	 * VERIFICAR SE OS ELEMENTOS INTEIROS DE UMA LISTA SAO TODOS IGUAIS
	 * @param LOGGER
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static boolean checkIntegersAreEquals(Logger LOGGER, List<Integer> list) throws Exception {
		boolean areEqual = true;
		for (Integer i : list)
			if ( i != list.get(0) )
				areEqual = false;
		return areEqual;
	}
	//. VERIFICAR SE OS ELEMENTOS INTEIROS DE UMA LISTA SAO TODOS IGUAIS
	
	/**
	 * VERIFICAR SE OS ELEMENTOS STRINGS DE UMA LISTA SAO TODOS IGUAIS
	 * @param LOGGER
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static boolean checkStringsAreEquals(Logger LOGGER, List<String> list) throws Exception {
		boolean areEqual = true;
		for (String s : list) 
			if ( !s.trim().equalsIgnoreCase(list.get(0).trim() ) ) 
				areEqual = false;
		return areEqual;
	}
	//. VERIFICAR SE OS ELEMENTOS STRINGS DE UMA LISTA SAO TODOS IGUAIS
	
	/**
	 * LIMPAR ESPACOS EM BRANCO DAS CHAVES E VALORES DE UM HASHMAP
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> limpaEspacosBrancosMapas(Map<String, String> map) throws Exception {
		for (Entry<String, String> entry : new HashSet<>(map.entrySet())) {
		    String keyTrimmed = entry.getKey().trim();
		    String valTrimmed = entry.getValue().trim();
	        map.remove(entry.getKey());
	        map.put(keyTrimmed, valTrimmed);
		}
		return map;
	}
	
	/**
	 * LIMPA REGISTROS DE UM HASHMAP - JAVA7-
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> cleanHashMap(Map<String, Object> map) throws Exception {
		for (Entry<String, Object> entry : new HashSet<>(map.entrySet())) map.remove(entry.getKey());
		return map;
	}
	
	/**
	 * TORNA AS PRIMEIRAS LETRAS DAS PALAVRAS MAIUSCULAS, EXCETO QDO STRING EH NUMERICO, EMAIL E LOGIN
	 * UTILIZADO EM PROJETO QUE FAZ SYNC COM AD
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> capitalizeString(Map<String, String> map) throws Exception {
		for (Entry<String, String> entry : new HashSet<>(map.entrySet())) {
			if ( !entry.getValue().contains("@parker.com") && 
				 !isNumeric(entry.getValue()) &&
				 !entry.getKey().contains("LOGIN")) {
				String keyOrig = entry.getKey();
				String valNorm = WordUtils.capitalizeFully(entry.getValue());
				map.remove(entry.getKey());
				map.put(keyOrig, valNorm);
			}
		}
		return map;
	}
	
	/**
	 * VERIFICA SE TODOS KEYS DO MAP POSSUEM VALOR PREENCHIDO
	 * @param LOGGER
	 * @param map
	 * @return
	 */
	public static Boolean checkValuesOfMap(Logger LOGGER, Map<String, String> map) {
		boolean exec = false;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			exec = (entry.getValue().length() > 0) ? true : false;
			if (!exec) break;
		}
		return exec;
	}
	
	/**
	 * CONVERSAO Object TO Boolean
	 * @param arg
	 * @return
	 */
	public static Boolean obj2Boolean(Object arg) {
		return arg != null ? Boolean.valueOf(obj2String(arg)) : null;
	}
	
	/**
	 * CONVERSAO Object TO BigDecimal
	 * @param arg
	 * @return
	 */
	public static BigDecimal obj2Decimal(Object arg) {
		return arg != null ? new BigDecimal(obj2String(arg).replace(",", ".")) : null;
	}
	
	/**
	 * CONVERSAO Object TO Integer
	 * @param arg
	 * @return
	 */
	public static Integer obj2Integer(Object arg) {
		return arg != null ? Integer.valueOf(obj2String(arg)) : null;
	}
	
	/**
	 * CONVERSAO Object TO String
	 * @param arg
	 * @return
	 */
	public static String obj2String(Object arg) {
		return arg != null ? String.valueOf(arg).replaceAll("\\null", "") : "";
	}
	
	/**
	 * MONTA O MAPA DE DADOS QUE SERA TRANSMITIDO PARA A TABELA IN5_REC_EXECUCAO
	 * E ASSIM ARMAZENAR HISTORICO DE EXECUCAO DAS AUTOMACOES
	 * @param codProc
	 * @param codEtapa
	 * @param codCiclo
	 * @param statusExec
	 * @param origem
	 * @param msgRet
	 * @return
	 */
	public static Map<String, Object> in5RecExecDadosProc(int codProc, int codEtapa, int codCiclo, int statusExec, String origem, String msgRet) {
		Map<String, Object> dadosProc = new HashMap<String, Object>();
		String tipoExec = null;
		if (origem.startsWith("Rb")) {
			tipoExec = "Robô";
		} else if (origem.startsWith("Int")) {
			tipoExec = "Integração";
		} else if (origem.startsWith("Erp")) {
			tipoExec = "InterfaceERP";
		} else {
			tipoExec = "Outra";
		}
		dadosProc.put("COD_PROCESSO", codProc);
		dadosProc.put("COD_ETAPA", codEtapa);
		dadosProc.put("COD_CICLO", codCiclo);
		dadosProc.put("STATUS_EXECUCAO", statusExec);
		dadosProc.put("DATA_EXECUCAO", getDatetime());
		dadosProc.put("TIPO_EXECUCAO", tipoExec);
		dadosProc.put("ORIGEM", origem);
		dadosProc.put("MSG_RETORNO", msgRet);
		return dadosProc;
	}
	
	public static String getClassName(Object className) {
		return obj2String(className).substring(obj2String(className).lastIndexOf(".") + 1).trim();
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// FUNCOES DA CLASSE EnviaNotificacao
	/**
	 * MONTA LISTA toDev
	 * @param paramGerais
	 * @return
	 */
	public static List<String> getEmailsDev(Map<String, String> paramGerais) {
		List<String> toDev = new ArrayList<String>();
		String emailsDev = paramGerais.get("toDev");
		String[] emailsDevParts = emailsDev.split(";");
		int emailsDevPartsLen = emailsDevParts.length;
		for (int i=0; i<emailsDevPartsLen; i++) {
			toDev.add(emailsDevParts[i]);
		}
		return toDev;
	}
	
	/**
	 * MONTA MENSAGENS DE EMAIL
	 * @param message
	 * @return
	 */
	public static String montaMensagem(String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        return stringBuilder.toString();
    }
	//. MONTA MENSAGENS DE EMAIL
	
	/**
	 * LIMPA E-MAILS INVALIDOS DA LISTA
	 * @param LOGGER
	 * @param to
	 * @return
	 * @throws Exception
	 */
	public static List<String> limpaListaEmails(Logger LOGGER, List<String> to) throws Exception {
		List<String> lstEmail = new ArrayList<String>();
		for (String email : to)
			if (email.contains("@")) lstEmail.add(email);
		return lstEmail;
		
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// FUNCOES DA CLASSE RbExecutaEtapasGenerico
	/**
	 * RETORNA LISTA COM codForm INFORMADOS NO .properties
	 * @param forms
	 * @return
	 */
	public static List<String> retornaLstForms(String forms) {
		String[] spltForms = forms.split(";");
		List<String> lstForms = new ArrayList<String>();
		for (String form : spltForms) lstForms.add(form);
		return lstForms;
	}
	
	// ---------------------------------------------------------------------
	// ---------------------------------------------------------------------
	// ...
	
}
