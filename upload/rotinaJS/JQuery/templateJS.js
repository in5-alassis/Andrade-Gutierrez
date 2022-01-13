/**
 * @author Rafael Marquini
 * @since 20/11/2020
 * @version 1.0.2
 * @abstract TEMPLATE PADRAO PARA INICIAR O DESENVOLVIMENTO DE SCRIPT PARA MODELOS BPM
 * @example ype-plm.js -> ESTE EH UM BOM SCRIPT PARA SEGUIR DE EXEMPLO E TIRAR DUVIDAS SOBRE IMPLEMENTACOES
 */

/**
 * VARIAVEIS DE AMBIENTE / API
 **/
const tituloEtapa = ProcessData.activityTitle;
const codProcesso = ProcessData.processInstanceId;
const codEtapa    = ProcessData.activityInstanceId;
const codCiclo    = ProcessData.cycle;
const DOMAIN	  = getDomain();

// TODO: SE SEU MODELO POSSUI GRID E FOR REALIZAR OPERACOES NELA, INSTANCIE O OBJETO COMO CONSTANTE
// const gridMODELO  = getForm("NOME_DA_GRID");

/**
 * ETAPAS DO PROCESSO
 **/
const NOME_ETAPA_INICIAL = 1;
const NOME_ETAPA_INTERMEDIARIA = 2;
const NOME_ETAPA_FINAL = 3;

/**
 * EXECUTA QUANDO OS OBJETOS DA DOM ESTIVEREM TOTLAMENTE CARREGADOS
 */
$(document).ready(function () {
	
	switch(codEtapa) {
		case NOME_ETAPA_INICIAL:
			// SUAS REGRAS VAO AQUI
			break;
			
		case NOME_ETAPA_INTERMEDIARIA:
			// SUAS REGRAS VAO AQUI
			break;
		
		case NOME_ETAPA_FINAL:
			// SUAS REGRAS VAO AQUI
			break;
		
		default: 
			break;
	}

	if ( [
        NOME_ETAPA_INICIAL
      ].indexOf(codEtapa) !== -1) {
          // CASO DESEJA OuTRA FORMA DE IMPLEMENTACAO
    }
	
	validaForm();
	
});

/**
 * FUNCAO PARA EXECUTAR VALIDACOES NAS ACOES DE SUBMIT DO FORMULARIO DO PROCESSO
 * @param {int} codEtapa 
 */
function validateForm() {
    let actions = Form.actions().map( (a) => {return a.id});
    for (let action of actions) {
        if (action === "aprovar") {
            validateFormChildAprovacao(action);
        } else {
            // USAR PARA REJEICAO OU CANCELAR
        }
    }
}
/**
 * FUNCAO CHILD PARA VALIDAR APOS CLIQUE NO BOTAO DE APROVACAO DA ETAPA
 * @param {String} action - "aprovar"
 */
function validateFormChildAprovacao(action) {
    Form.actions(action).subscribe('SUBMIT', function (formId, actionId, reject) {
        let isValid = true;
        if ([
            NOME_ETAPA
        ].indexOf(codEtapa) !== -1) {

            // TODO: SUAS FUNCOES AQUI...
            // GERALMENTE, FACO O RETORNO SER UM boolean
			// PARA PODER VALIDAR O TERMO isValid

            isValid = (algumaFuncao && outraFuncao) ? true : false;

        }

        /**
         * SE HOUVEREM MOTIVOS PARA NAO AVANCAR A ETAPA, REJEITA E INFORMA O USUARIO
         */
        if (!isValid) reject();
    });
}
