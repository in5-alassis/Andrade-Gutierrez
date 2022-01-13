/**
 * @author Rafael Marquini
 * @since 19/02/2021
 * @version 1.0.1
 * @abstract LIB com funcoes genericas para nossos JS
 */

// --------------------------------------------------------------------------------
// ACOES NO FORM.APP
// --------------------------------------------------------------------------------

/**
 * RECARREGA O FRAME DO FORMULARIO
 */
const eReloadFrame = (mustReload) => {
    if (mustReload)
        window.location.reload();
}

/**
 * RETORNA A POSICAO DA SCROLLBAR PARA O DOM OBJECT INFORMADO
 * 
 * @param {Object} object - OBJETO DA DOM
 */
function setScrollPositionById(object) {
    let el = document.getElementById(object);
    window.scrollTo(el.scrollLeft, el.scrollTop);
}

// --------------------------------------------------------------------------------
// MANIPULACOES NO FORM.APP
// --------------------------------------------------------------------------------

/**
 * RECUPERA O DOMINIO (URL) DO AMBIENTE
 */
const getDomain = function () {
    return location.protocol + '//' + location.hostname;
}

/**
 * SIMPLIFICAÇÃO getForm()/grids()/actions()
 * OBS: PARA PEGAR UM CAMPO DENTRO DE UMA GRID NÃO É NECESSÁRIO O NOME DA GRID,
 * O QUE IMPOSSIBILITA HAVEREM CAMPOS COM MESMO NOME DENTRO E FORA DE UMA GRID
 * @param {String} campo - ACEITA 'CAMPO', 'BOTAO' OU VAZIO
 * @return {*}
 */
const getForm = (campo) => {
    campo = campo ? campo.trim() : '';
    const forms = Form.fields()
        .concat(Form.actions())
        .reduce((accu, curr) => {
            return accu.concat(curr.fields ? curr.fields().concat(curr) : curr);
        }, []);
    const find = campo ? forms.find((form) => {
        return form.id === campo
    }) : forms;
    if (find) return find;

    throw new Error(campo + ' não existe na etapa atual ou no processo.');
}

/**
 * RECUPERA O VALOR DO CAMPO INFORMADO
 * @param campo Object;
 * @returns valor do campo
 */
function getValue(campo) {
    try {
        return campo === null || campo === undefined || campo.toString() === ""
            ? null
            : getForm(campo).value();
    } catch (error) {
        console.log("getValue.error:\r\n" + error);
    }
}

/**
 * DEFINE O VALOR DE UM UNICO CAMPO
 * @param {String} nomeCampo 
 * @param {String} valor 
 */
function setValue(nomeCampo, valor) {
    getForm(nomeCampo).value(valor).apply();
}

/**
 * VERIFICA SE O CAMPO ESTÁ DISPONIVEL NO FORMULÁRIO PARA SER MANIPULADO
 * @requires getForm
 * @param {String} nomeCampo
 * @return {boolean}
 */
const fieldExists = function (nomeCampo) {
    try {
        getForm(nomeCampo);
        return true;
    } catch (e) {
        return false;
    }
}

/**
 * RETORNA EM UM ARRAY OS OBJETOS DOS CAMPOS COM O TIPO INFORMADO
 * @requires getForm
 * @param {String} tipoCampo TEXT, DATE, RADIO, DOCUMENT(anexo), AUTOCOMPLETE(lista), CHECKBOX, TEXTAREA, LABEL
 * @return {Object Array}
 */
const getTypeFields = function (tipoCampo) {
    return getForm().filter(function (campo) {
        return campo.type() === tipoCampo;
    });
}

/**
 * ADICIONA MENSAGEM DE ERRO A UM CAMPO DO FORMULÁRIO
 * @param campo
 * @param mensagem
 */
function addError(campo, mensagem) {
    let errorsForm = Form.errors();
    errorsForm[campo] = ['' + mensagem];
    Form.errors(errorsForm);
    Form.apply();
}

/**
 * REMOVE MENSAGENS DE ERRO DE UM CAMPO DO FORMULÁRIO
 * @param campo
 */
function removeError(campo) {
    let errorsForm = Form.errors();
    delete errorsForm[campo];
    Form.errors(errorsForm);
    Form.apply();
}

/**
 * DEFINE A VISIBILIDADE DOS GRUPOS INFORMADOS
 * @param {Object Array} grupos 
 * @param {boolean} visibility 
 */
function setGroupVisibility(grupos, visibility) {
    for (let grupo of grupos) {
        Form.groups(grupo).visible(visibility);
    }
    Form.apply();
}

/**
 * DEFINE A EXPANSIVIDADE DOS GRUPOS INFORMADOS
 * @param {Object Array} grupos 
 * @param {boolean} expansiveness 
 */
function setGroupExpansiveness(grupos, expansiveness) {
    for (let grupo of grupos) 
        Form.groups(grupo).expanded(expansiveness);
    Form.apply();
}

/**
 * DEFINE A VISIBILIDADE DOS CAMPOS INFORMADOS
 * @requires getForm
 * @param {Object Array} fields 
 * @param {boolean} isVisible 
 */
function setFieldsVisibility(fields, isVisible) {
    for (let field of fields) 
        getForm(field).visible(isVisible);
    Form.apply();
}

/**
 * DEFINE CAMPOS LISTADOS COMO BLOQUEADO OU NAO
 * @requires getForm
 * @param {Object Array} fields 
 * @param {boolean} isDisabled 
 */
function setFieldsDisabled(fields, isDisabled) {
    for (let field of fields) 
        getForm(field).disabled(isDisabled);
    Form.apply();
}

/**
 * DEFINE SE CAMPOS LISTADOS SAO OBRIGATORIOS OU NAO
 * @requires getForm
 * @param {Object Array} fields 
 * @param {String} action
 * @param {boolean} isRequired 
 */
function setFieldsRequired(fields, action, isRequired) {
    for (let field of fields) 
        getForm(field).setRequired(action, isRequired);
    Form.apply();
}

/**
 * DEFINE SE OS CAMPOS LISTADOS SAO SOMENTE LEITURA
 * @requires getForm
 * @param {Object Array} fields 
 * @param {boolean} isReadOnly 
 */
function setFieldsReadOnly(fields, isReadOnly) {
    for (let field of fields) 
        getForm(field).readOnly(isReadOnly);
    Form.apply();
}

/**
 * DEFINE A MASCARA PARA UM CAMPO
 * @param {String} field 
 * @param {String} fmask 
 */
function setFieldMask(field, fmask) {
    getForm(field).mask(fmask).apply();
}

/**
 * REMOVE A MASCARA DE UM CAMPO
 * @requires getForm
 * @param {String} field 
 */
function removeFieldMask(field) {
    getForm(field).removeMask().apply();
}

/**
 * PREENCHE OPCOES DE UMA LISTA
 * @requires getForm
 * @param {String} listField 
 * @param {Object} listOptions 
 */
function addListOptions(listField, listOptions) {
    getForm(listField).addOptions(listOptions).apply();
}

/**
 * LIMPA AS OPCOES DE UMA LISTA
 * @requires getForm
 * @param {Object Array} listFields
 */
function clearListOptions(listFields) {
    for (let listField of listFields) 
        getForm(listField).removeOptions([]).apply();
    Form.apply();
}

/**
 * ALTERA A LARGURA DO CAMPO
 * @requires getForm
 * @param {String} nomeCampo - NOME DO CAMPO A SER ALTERADO O TAMANHO
 * @param {String} novoTamanho - NOVO TAMANHO {m2 - m3 - m4 .... - m10 - m11 - 12}
 */
function setFieldWidth(nomeCampo, novoTamanho) {
    try {
        getForm(nomeCampo).className("col " + novoTamanho).apply();
    } catch (error) {
        console.error("setFieldWidth.error:\r\n" + error);
    }
}

/**
 * ALTERA LABEL DE UM CAMPO PELA API DO PRODUTO
 * 
 * @param {String} campo 
 * @param {String} txt 
 * @requires getForm
 */
function setFieldLabel(campo, txt) {
    getForm(campo).label(txt).apply();
}

/**
 * INVOCAR MODAL PADRAO
 * @param {String} headerTitle
 * @param {String} msg
 * @param {Array} fields
 */
function openSimpleCustomModal(headerTitle, msg, clearFields, fields) {
    Form.addCustomModal({ title: headerTitle, description: msg, buttons: [] });
    if (clearFields)
        cleanFieldsValue(fields);
}

/**
 * INVOCA A EXIBICAO DA MODAL DA API JS DO LECOM
 * 
 * @param {String} title 
 * @param {String} description 
 */
function openComplexCustomModal(title, description) {
    Form.addCustomModal({
        title,
        description,
        showButtonClose: false,
        buttons: [{
            name: 'OK',
            icon: 'done',
            closeOnClick: true,
            action: () => {
                removeModalLayer();
            }
        }]
    });
}

/**
 * REMOVE A LAYER CINZA DA MODAL 
 * CASO AO FECHA-LA, EH MANTIDA A EXIBICAO DA LAYER
 */
function removeModalLayer() {
    let leanOverlay = document.querySelectorAll('.lean-overlay');
    for (let layer of leanOverlay)
        layer.setAttribute('style', 'display: none !important; transition: display ease 0.3s;');
}

/**
 * LIMPA O VALOR DOS CAMPOS INFORMADOS NO ARRAY fields
 * @requires getForm
 * @param {Object Array} fields 
 */
function clearFieldsValue(fields) {
    for (let field of fields) 
        getForm(field).value('')
    Form.apply();
}

/**
 * oculta campos vazios;
 * *executar após as demais tratativas;
 * @param idGrupos ex: ['ID_GRUPO_A', 'ID_GRUPO_B'];
 */
function hideEmptyFields(idGrupos) {
    try {
        for (let index = 0; index < idGrupos.length; index++) {
            let grupoRows = Form.groups(idGrupos[index]).fields();

            for (let index2 = 0; index2 < grupoRows.length; index2++) {
                let element = getForm(grupoRows[index2].id);

                if (
                    element !== null &&
                    element !== undefined &&
                    element !== "" &&
                    (element.type() !== "GRID" ||
                        (element.type() === "GRID" &&
                            Form.grids(element.id).dataRows().length === 0)) &&
                    element.readOnly()
                ) {
                    let elementValue = element.value();

                    if (
                        elementValue === null ||
                        elementValue === undefined ||
                        elementValue.toString().trim() === ""
                    )
                        element.visible(false);
                }
            }
        }

        Form.apply();
        //
    } catch (error) {
        console.log("hideEmptyFields.error:\r\n" + error);
    }
}

// --------------------------------------------------------------------------------
// ESTILIZACOES CAMPOS DO FORM APP
// --------------------------------------------------------------------------------

/**
 * TRANSFORMA O ALINHAMENTO DAS OPCOES DE RADIO BUTTON PARA A HORIZONTAL
 * EH NECESSARIO ADICIONAR UMA QUEBRA DE LINHA SIMPLES NO CAMPO CRIADO 
 * NO FORMULARIO PELO STUDIO
 * 
 * RECOMENDADO PARA ATE QUATRO ITENS DE SELECAO
 * 
 * @param {String} nomeCampo - NOME DO CAMPO RADIO
 */
function setStyleRadioButtonInline(nomeCampo) {
    let divParent = document.querySelectorAll("#input__" + nomeCampo + " .input-checkbox-group");
    $(divParent).css("display", "flex");
    $(divParent).css("justify-content", "space-between");
    $(divParent).css("margin-bottom", "0");
    $(divParent).css("margin-left", "-10px");
    let divChild = document.querySelectorAll("#input__" + nomeCampo + " .input-checkbox-group > p");
    $(divChild).css("width", "100%");
    let divLabel = document.querySelectorAll("#input__" + nomeCampo + " .input-checkbox-group > p > label");
    $(divLabel).css("width", "100%");
    $(divLabel).css("padding-right", "20px");
}

// --------------------------------------------------------------------------------
// DEFINICOES DE VALORES EM CAMPOS PREDEFINIDOS DO FORM APP
// --------------------------------------------------------------------------------

/**
 * MONTA A LISTA DE UFS EM UM CAMPO LISTA INFORMADO
 * @param {String} campo 
 */
function setUFList(campo) {
    let ufField = getForm(campo);
    let arrUf = new Array();
    getLstUf()
        .then(function (response) {
            for (let i = 0; i < response.length; i++) {
                //console.log(response[i].sigla);
                arrUf.push({ name: response[i].sigla, value: response[i].sigla })
            }
            ufField.addOptions(arrUf);
            Form.apply();
        })
        .catch(function (error) {
            console.error('Não foi possível retornar a listagem de UF - ' + error);
        });
}

// --------------------------------------------------------------------------------
// MANIPULACOES DE GRID
// --------------------------------------------------------------------------------

/**
 * FUNCAO PARA ESTILIZAR GRID
 * REMOVE A BORDA E SOMBRA DEFAULT DO PRODUTO
 * @param {Object Array} grids - OBJETO DOM DA GRID
 */
function setStyleNGrids(grids) {
    for (let grid of grids) {
        let idGrid = "#input__" + grid._id;
        let elemGrid = document.querySelector(idGrid);
        elemGrid.setAttribute(
            "style",
            "border: none; box-shadow: none; padding: 0.5rem !important; margin: 0 auto !important;"
        );
    }
}

/**
 * CONTROLA A VISIBILIDADE DA GRID
 * 
 * @param {Object} grids - OBJETO DOM DA GRID
 * @param {boolean} isVisible 
 */
function setGridsVisibility(grids, isVisible) {
    for (let grid of grids) 
        grid.visible(isVisible);
    Form.apply();
}

/**
 * RETORNA AS Array CONTENTO UMA MATRIZ DE LINHAS E COLUNAS COM OS VALORES DA GRID
 * @requires getForm
 * @param {String} idGrid - ID DA GRID
 * @returns {Array}
 */
function gridGetDataRows(idGrid) {
    return getForm(idGrid).dataRows();
}

/**
 * RETORNA O NUMERO TOTAL DE LINHAS DA GRID
 * @requires getForm
 * @param {String} idGrid - ID DA GRID
 * @returns {int} 
 */
function gridGetDataRowsCount(idGrid) {
    return getForm(idGrid).dataRows().length;
}

/**
 * RETORNA O MAIOR ID DE UMA GRID
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @returns {Integer} maxId
 */
const gridGetMaxId = (grid) => {
    return new Promise( (resolve, reject) => {
        let gridRowsCount = getDataRowsCount(grid._id);
        let maxId = (gridRowsCount > 0) ? grid.dataRows(gridRowsCount-1).id : 0;
        resolve(maxId);
    });
}

/**
 * Controla a visibilidade de colunas de Grid
 * 
 * @param {Object} grid - Objeto DOM da Grid
 * @param {Array} columns - Array com as colunas
 * @param {boolean} isVisible - true/false
 */
function gridSetColumnsVisibility(grid, columns, isVisible) {
    for (let column of columns)
        grid.columns(column).visible(isVisible);
    Form.apply();
}

/**
 * SOMA O VALOR DA COLUNA DA GRID E INSERE O RESULTADO EM OUTRO CAMPO
 * @param grid - OBJETO DOM DA GRID
 * @param nomeCampoValor - NOME DO CAMPO QUE IRA SOMAR
 * @param nomeCampoTotal - NOME DO CAMPO QUE RECEBERÁ A SOMA DA GRID
 */
function gridSumValues(grid, nomeCampoValor, nomeCampoTotal) {
    Form.apply().then(function () {
        let total = grid.columns(nomeCampoValor).sum();
        setValor(nomeCampoTotal, total);
    });
}

/**
 * MAIOR VALOR CADASTRO NA GRID
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {String} nomeCampoValor - NOME DO CAMPO QUE REPRESENTA A COLUNA COM OS VALORES
 * @param {String} nomeCampoTotal - NOME DO CAMPO QUE RECEBERÁ O VALOR MÁXIMO DA GRID
 */
function gridGetHighestValue(grid, nomeCampoValor, nomeCampoMaximo) {
    Form.apply().then(function () {
        let maiorValor = grid.columns(nomeCampoValor).max();
        setValor(nomeCampoMaximo, maiorValor);
    });
}

/**
 * MENOR VALOR CADASTRO NA GRID
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {String} nomeCampoValor - NOME DO CAMPO QUE REPRESENTA A COLUNA COM OS VALORES
 * @param {String} nomeCampoMinimo - NOME DO CAMPO QUE RECEBERÁ O VALOR MÍNIMO DA GRID
 */
function gridGetSmallestValue(grid, nomeCampoValor, nomeCampoMinimo) {
    Form.apply().then(function () {
        let menorValor = grid.columns(nomeCampoValor).min();
        setValor(nomeCampoMinimo, menorValor);
    });
}

/**
 * VALIDA O PREENCHIMENTO DE N GRIDS QDO INVOCADA NO SUBMIT DO FORMULARIO
 * @param {Object} idGrids - [{"id": idGrid, "description": "descricao grid"}]
 * @param {Integer} quantidade 
 * @returns {boolean} - false (qdo ha erros) / true (qdo valido)
 */
function gridCheckMinimumInsertion(idGrids) {
    let gridsNPreenchidas = [];
    for (let grid of idGrids) {
        let tamanhoGrid = getDataRowsCount(grid.id);
        if (tamanhoGrid == 0) {
            gridsNPreenchidas.push(grid);
        }
    }

    if (gridsNPreenchidas.length > 0) {
        let message = "As grids (tabelas) a seguir, precisam ser preenchidas: ";
        let gridsDescription = [];
        for (let grid of gridsNPreenchidas) {
            gridsDescription.push(grid.description);
            adicionaErro(grid.id, "Adicionar informações pertinentes.")
        }
        message += gridsDescription.join(', ');
        message += ".";
        Form.addCustomModal({ title: "AVISO", description: message, buttons: [] });
        return false;
    } else {
        return true;
    }

}

/**
 * CONTROLA A EXIBICAO DE TODA GRID
 * @requires getForm
 * @param {Object} grid - OBJETO DOM DA GRID 
 * @param {boolean} ocultar 
 */
function gridSetVisibility(grid, isVisible) {
    grid.visible(isVisible).apply();
}

/**
 * CONTROLA EXIBICAO DE CAMPOS DE UMA GRID
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {Array} campos 
 * @param {boolean} ocultar 
 */
function gridSetFieldsVisibility(grid, campos, isVisible) {
    for (let campo in campos) {
        grid.fields(campo).visible(isVisible);
    }
    Form.apply();
}

/**
 * OCULTA LINHA DA GRID
 * @param {String} nomeGrid
 * @param {String} nomeCampo
 * @param {*} naoOcultarValor
 */
function gridSetRowVisibility(nomeGrid, nomeCampo, naoOcultarValor) {
    var idGrid = $("#input__" + nomeGrid).attr("data-reactid");
    var idGridContainer = $('table tbody[data-reactid^="' + idGrid + '"]').attr("data-reactid");
    var linhas = $('[data-reactid="' + idGridContainer + '"]').find('tr');
    var qtdLinhas = linhas.length;
    for (let i = 0; i < qtdLinhas; i++) {
        let linha = $(linhas)[i];
        let valor = $(linha).find('span[data-reactid*="' + nomeCampo + '"]')[0].textContent;
        if (valor != naoOcultarValor) {
            $(linha).css({ "display": "none" });
        }
    }
}

/**
 * altera a label do btn da grid;
 *
 * @param idGrid id da grid;
 * @param labelAdicao label de adição;
 * @param labelEdicao label de edição;
 */
function gridSetButtonLabel(idGrid, labelAdicao, labelEdicao) {
    gridSetButtonLabelChild(idGrid, "CREATE", labelAdicao, labelEdicao);

    // monitora os btns da grid;
    // --------------------------------------------------------------------------------------------
    Form.grids(idGrid).subscribe("GRID_SUBMIT", function () {
        gridSetButtonLabelChild(idGrid, "CREATE", labelAdicao, labelEdicao);
    });

    Form.grids(idGrid).subscribe("GRID_RESET", function () {
        gridSetButtonLabelChild(idGrid, "CREATE", labelAdicao, labelEdicao);
    });

    Form.grids(idGrid).subscribe("GRID_EDIT", function () {
        gridSetButtonLabelChild(idGrid, "UPDATE", labelAdicao, labelEdicao);
    });
}
function gridSetButtonLabelChild(idGrid, acao, labelAdicao, labelEdicao) {
    setTimeout(function () {
        document.querySelector(
            "#input__" + idGrid + " #" + acao + " span"
        ).innerText = acao === "CREATE" ? labelAdicao : labelEdicao;
    }, 500);
}

/**
 * OCULTA BOTOES DE ACOES DA GRID
 * 
 * @param {Object} idGrid - Objeto DOM da Grid
 * @param {String} acao - CREATE, EDIT, DESTROY
 * @param {boolean} isHidden 
 */
function gridHideActionButton(idGrid, acao, isHidden) {
    setTimeout(() => {
        let btnGrid = document.querySelector("#input__" + idGrid + " #" + acao);
        if (isHidden) btnGrid.setAttribute("style", "display: none !important");
        else btnGrid.setAttribute("style", "display: inline-block !important");
    });
}

/**
 * desabilita edições/ exclusões na grid;
 *
 * @param idGrid id da grid;
 * @param desabilitarEdicoes desabilitar edições;
 * @param desabilitarExclusoes desabilitar exclusões;
 * @param indexList lista de indexes das linhas que devem ser tratadas (*null* para todas | gridGetIndexList());
 */
function gridDisableActionsEditDestroy(
    idGrid,
    desabilitarEdicoes,
    desabilitarExclusoes,
    indexList
) {
    try {
        //
        setTimeout(function () {
            //
            // oculta btns de edição/ exclusão na grid;
            // ----------------------------------------------------------------------------------------
            gridDisableActionsEditDestroyChild(
                idGrid,
                desabilitarEdicoes,
                desabilitarExclusoes,
                indexList
            );

            // monitora btn **salvar**;
            // ----------------------------------------------------------------------------------------
            document
                .querySelectorAll("#root-app .button-group button")[4]
                .addEventListener("click", function () {
                    setTimeout(function () {
                        gridDisableActionsEditDestroyChild(
                            idGrid,
                            desabilitarEdicoes,
                            desabilitarExclusoes,
                            indexList
                        );
                    }, 1500);
                });

            // monitora btns da grid (tratativa preventiva para grids com muitos registros);
            // ----------------------------------------------------------------------------------------
            let grid = Form.grids(idGrid);
            let gridRowsBackup = grid.dataRows();

            // efetua um backup da grid;
            // --------------------------------------
            grid.subscribe("GRID_SUBMIT", function () {
                setTimeout(function () {
                    gridRowsBackup = grid.dataRows();
                }, 500);
            });

            // limpa e restaura a grid;
            // --------------------------------------
            if (desabilitarExclusoes) {
                //
                grid.subscribe("GRID_DESTROY", function () {
                    setTimeout(function () {
                        //
                        let gridRows = grid.dataRows();
                        for (let index = 0; index < gridRows.length; index++)
                            grid.removeDataRow(gridRows[index].id);
                        for (let index = 0; index < gridRowsBackup.length; index++)
                            grid.insertDataRow(gridRowsBackup[index]);

                        setTimeout(function () {
                            gridDisableActionsEditDestroyChild(
                                idGrid,
                                desabilitarEdicoes,
                                desabilitarExclusoes,
                                indexList
                            );
                        }, 1000);
                        alert("Ação não permitida.");
                        //
                    }, 500);
                });
            }
            //
        }, 500);
    } catch (error) {
        console.log("gridDisableActionsEditDestroy.error:\r\n" + error);
    }
}
function gridDisableActionsEditDestroyChild(
    idGrid,
    desabilitarEdicoes,
    desabilitarExclusoes,
    indexList
) {
    try {
        let gridRows;
        let display;
        // desabilitarEdicoes;
        gridRows = document.querySelectorAll("#input__" + idGrid + " #edit");
        display = desabilitarEdicoes ? "none" : "inline-block";
        gridDisableActionsEditDestroyChild2(gridRows, indexList, display);
        // desabilitarExclusoes;
        gridRows = document.querySelectorAll("#input__" + idGrid + " #destroy");
        display = desabilitarExclusoes ? "none" : "inline-block";
        gridDisableActionsEditDestroyChild2(gridRows, indexList, display);
    } catch (error) {
        console.log("gridDisableActionsEditDestroyChild.error:\r\n" + error);
    }
}
function gridDisableActionsEditDestroyChild2(gridRows, indexList, display) {
    try {
        for (let index = 0; index < gridRows.length; index++) {
            let check = true;
            if (indexList !== null && indexList !== undefined)
                check = indexList.indexOf(index) !== -1;
            if (check) gridRows[index].style.display = display;
        }
    } catch (error) {
        console.log("gridDisableActionsEditDestroyChild2.error:\r\n" + error);
    }
}

/**
 * RECUPERA INDEXES DE LINHAS DA GRID;
 *
 * @param gridRows ex: Form.grids('ID_GRID').dataRows();
 * @param idCampo id do campo que sera utilizado como parâmetro de validação;
 * @param parametro parâmetro;
 */
function gridGetIndexList(gridRows, idCampo, parametro) {
    try {
        let indexList = [];
        for (let index = 0; index < gridRows.length; index++)
            if (gridRows[index][idCampo] + "" === parametro + "")
                indexList.push(index);
        return indexList;
    } catch (error) {
        console.log("getIndexList.error:\r\n" + error);
    }
}

/**
 * INIBE NOVAS INSERCOES NA GRID CONFORME O NUMERO MAXIMO DE ITENS PERMITIDOS
 * EH NECESSARIO QUE SEJA INVOCADO DENTRO DO EVENTO GRID_SUBMIT OU GRID_ADD_SUBMIT
 * ABRE UM MODAL EXIBINDO UM ALERTA AO USUÁRIO, E AINDA, POSSUI ACAO PARA REMOVER A
 * LINHA ERRONEAMENTE ADICIONADA
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {Integer} maxItens - NUMERO MAXIMO DE ITENS PERMITIDOS NA GRID
 * @param {String} mensagem - MENSAGEM AO USUÁRIO
 * @returns {boolean} - RETORNA TRUE OU FALSE PARA QUE VC POSSA REALIZAR TRATATIVAS NO SEU CODIGO
 */
function gridPreventNewRowInsertion(grid, maxItens, mensagem) {
    if (getDataRowsCount(grid._id) > maxItens) {
        setTimeout(() => {
            let prevRowAdded = grid.dataRows(grid.dataRows().length - 1);
            Form.addCustomModal({
                title: 'Alerta',
                description: mensagem,
                showButtonClose: false,
                buttons: [{
                    name: 'OK',
                    icon: 'done',
                    closeOnClick: true,
                    action: function () {
                        grid.removeDataRow(prevRowAdded['id']);
                    }
                }],
            });
        }, 300);
        return true;
    }
    return false;
}

/**
 * REMOVE A ULTIMA LINHA INSERIDA NA GRID
 * UTILIZAVEL PARA VALIDAR INSERCOES IDENTICAS DE ITENS, POR EXEMPLO
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 */
function gridRemoveLastRowAdded(grid) {
    let lastRowAdded = gridGetLastRowAdded(grid);
    grid.removeDataRow(lastRowAdded['id']);
}

/**
 * RECUPERA A LINHA QUE ESTA SENDO INSERIDA
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @returns {Object} 
 */
function gridGetLastRowAdded(grid) {
    return grid.dataRows(grid.dataRows().length - 1);
}

/**
 * RECUPERA OS IDs QUE SE ENCONTRAM NA GRID NO MOMENTO DO ONLOAD DO FORM
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @returns {Array} LISTA DE IDs (INDICES) PRESENTES NA GRID
 */
const gridGetCurrentIDs = (grid) => {
    return new Promise( (resolve, reject) => {
        let ids = [];
        let gridRowsCount = getDataRowsCount(grid._id);
        if (gridRowsCount > 0) {
            for (let i=0; i<gridRowsCount; i++) {
                ids.push(grid.dataRows(i).id);
            }
        }
        resolve(ids);
    });
}

/**
 * VERIFICA QUAL A ULTIMA LINHA REMOVIDA DA GRID QDO 
 * O EVENTO GRID_DESTROY EH DISPARADO.
 * 
 * OLHAR O JS ds.js COMO EXEMPLO, POIS HA OBRIGATORIEDADE
 * DO USO DA FUNCAO setTimeout COM TIMEOUTS EM ms PREESTABELECIDOS.
 * 
 * O CLIQUE NO BOTAO DESTROY DEMORA 2500 ms PARA SER
 * RECONHECIDO E ASSIM GRAVAR O VALOR DO ID REMOVIDO ESPERADO.
 * 
 * CADA CLIQUE NO BOTAO DESTROY, PARA CAPTURAR O ID REMOVIDO
 * EXIGE QUE O FORMULARIO SEJA SALVO E O FRAME DO FORM
 * RECARREGADO. PARA ISSO TEMOS A FUNCAO saveFormNReload().
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {Array} curIdsGrid - ARRAY CONTENDO OS IDS ATUAIS CAPTURADOS NO ONLOAD DO FORM
 * @returns {Integer} ID REMOVIDO
 */
const gridGetDestroyedId = (grid, curIdsGrid) => {
    return new Promise( (resolve, reject) => {
        let idsOnClickDestroy = [];
        gridGetCurrentIDs(grid)
            .then(ids => {
                for (let id of ids) {
                    idsOnClickDestroy.push(id);
                }
            })
            .catch(err => {
                console.error(err);
            });
        
        setTimeout( () => {
            
            let destroyedId = curIdsGrid.filter( (id) => {
                if (idsOnClickDestroy.length === 0) {
                    // SE QDO CLICADO NO DESTROY NAO SOBRAR NENHUM ID, 
                    // SIGNIFICA QUE ESTOU REMOVENDO O ULTIMO ID PRESENTE NA GRID
                    return id;
                } else {
                    // DO CONTRARIO, RETORNO O ID Q ESTA SENDO REMOVIDO
                    return idsOnClickDestroy.indexOf(id) === -1;
                }
            });
    
            resolve(destroyedId);

        }, 300);
        
    });
}

/**
 * RECUPERA O VALOR DA COLUNA (idCampo) DA ULTIMA LINHA INSERIDA
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {String} idCampo - IDENTIFICADOR DO CAMPO
 * @returns {String} valor PREENCHIDO NA ULTIMA LINHA INSERIDA
 */
function gridGetFieldValueFromLastRowAdded(grid, idCampo) {
    let gridRows    = grid.dataRows();
    let ultimoIndex = gridRows.length - 1;
    let valor       = gridRows[ultimoIndex][idCampo];
    return (Array.isArray) ? valor[0] : valor;
}

/**
 * RECUPERA OS VALORES JA INSERIDOS NA GRID E ARMAZENA NUM Array
 * 
 * NOTE QUE FOI DISCRIMINADO gridRowsCount-1, POIS NAO DESEJO QUE 
 * O ELEMENTO QUE ESTA SENDO INSERIDO NO MOMENTO DO GRID_SUBMIT
 * NAO SEJA TAMBEM VERIFICADO
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {String} idCampo - IDENTIFICAÇÃO DO CAMPO
 * @returns {Array} CONTENDO OS VALORES INSERIDOS NA COLUNA idCampo DA GRID
 */
function gridGetInsertedValues(grid, idCampo) {
    let valoresJaInseridos = [];
    let gridRowsCount = getDataRowsCount(grid._id);
    if (gridRowsCount > 0) {
        for (let i=0; i<gridRowsCount-1; i++) {
            let row = grid.dataRows(i);
            valoresJaInseridos.push(row[idCampo]);
        }
    }
    return valoresJaInseridos;
}

/**
 * VERIFICA SE O TIPO ALTERACAO SELECIONADO JA FOI INSERIDO
 * 
 * ATRAVES DO METODO filter EH ITERADO TODO O ARRAY valoresJaInseridos
 * COM INTUITO DE IDENTIFICAR SE O valorVerificar JA ESTA PRESENTE NA GRID
 * 
 * @param {String} valorVerificar 
 * @param {Array} valoresJaInseridos 
 * @returns {boolean}
 */
function gridValidateInsertedValues(valorVerificar, valoresJaInseridos) {
    let valorJaInserido = valoresJaInseridos.filter( valIns => {
        return String(valIns) == String(valorVerificar);
    });
    return (valorJaInserido.length > 0) ? true : false;

}

/**
 * VERIFICA SE HA DUPLICIDADE DE VALORES INSERIDOS NA GRID
 * CONFORME A CAMPO INFORMADO
 * 
 * UTILIZE ESSE METODO JUNTO DO GRID_SUBMIT, POIS QDO EH REALIZADA
 * A INSERCAO DO ELEMENTO NA GRID A VERIFICACAO DEVE SER REALIZADA
 * 
 * @param {Object} grid - OBJETO DOM DA GRID
 * @param {String} idCampo - IDENTIFICACAO DO CAMPO
 * @param {String} valorVerificar - COM QUAL VALOR COMPARAR
 * @returns {boolean}
 */
const gridCheckDuplicity = (grid, idCampo, valorVerificar) => {
    return new Promise( (resolve, reject) => {
        let valoresJaInseridos = gridGetValoresJaInseridos(grid, idCampo);
        let valorJaInserido = gridValidarValorJaInserido(valorVerificar, valoresJaInseridos);
        resolve(valorJaInserido);
    });
}

// --------------------------------------------------------------------------------
// MANIPULACOES NOS BOTOES DO FORM
// --------------------------------------------------------------------------------
/**
 * Desabilitar/Habilitar botoes indicados no Array
 * 
 * @param {Array} btn - ['aprovar', 'rejeitar', 'cancelar'] 
 * @param {boolean} isDisabled - true/false (Desabilitar/Habilitar)
 */
function disableButtons(btn, isDisabled) {
    for (let b of btn)
        Form.actions(b).disabled(isDisabled);
    Form.apply();
}

/**
 * Ocultar/Exibir botoes indicados no Array
 * 
 * @param {Array} btn - ['aprovar', 'rejeitar', 'cancelar'] 
 * @param {boolean} isHidden - true/false (Ocultar/Exibir)
 */
function hideButtons(btn, isHidden) {
    for (let b of btn)
        Form.actions(b).hidden(isHidden);
    Form.apply();
}

// --------------------------------------------------------------------------------
// MANIPULACOES DE TIPOS E VALIDACOES
// --------------------------------------------------------------------------------

/**
 * FUNCAO QUE RETORNA VALOR DO CAMPO, CASO SEJA VALIDO.
 * CASO NAO SEJA, RETORNA <retorno>
 * @requires getForm
 * @param campo
 * @returns {*} retorno
 */
function nulo(campo, retorno) {
    let oCampo = getForm(campo);
    if (oCampo) {
        try {
            if (oCampo.value() == undefined) return retorno;

            if (oCampo.value() instanceof Array) {

                if (oCampo.value()[0] == undefined) return retorno;

                return oCampo.value()[0];
            } else {
                return oCampo.value();
            }
        } catch (e) {
            return retorno;
        }
    } else {
        return retorno;
    }
}

/**
 * REMOVE CARACTERES ESPECIAIS
 * @param {String} str
 * @return {String}
 */
function removeSpecialChar(str) {
    return str.replace(/[^\w\s]/gi, '');
}

/**
 * VERIFICA SE VALOR EH NUMERICO
 * @param {*} valor 
 * @return {boolean} 
 */
function isNumeric(valor) {
    return /^(\d+)$/g.test(valor);
}

/**
 * REMOVE ITENS DE UM ARRAY COLLECTION ONDE POSSUO A ESTRUTURA
 * [{name: '', value: ''}, {name: '', value: ''}, ...]
 * UTILIZADO, PRINCIPALMENTE, PARA ACOES EM CAMPOS LISTA DA API LECOM
 * @param {Object Array} obj 
 * @param {String} item
 */
function removeElementFromArrayCollection(obj, items) {
    for (let i = 0; i < obj.length; i++)
        for (let item of items)
            if (obj[i].value == item)
                obj.splice(i, 1);
}

/**
 * VALIDA CPF
 * @requires getForm
 * @param {String} cpf 
 * @param {String} campo 
 * @return {boolean}
 */
function isValidCPF(cpf, campo) {
    let oCampo = getForm(campo);
    let erro = false;
    if (cpf != "") {
        if (
            isNaN(cpf) ||
            cpf.length != 11 ||
            cpf == "00000000000" || cpf == "11111111111" || cpf == "22222222222" || cpf == "33333333333" ||
            cpf == "44444444444" || cpf == "55555555555" || cpf == "66666666666" || cpf == "77777777777" ||
            cpf == "88888888888" || cpf == "99999999999"
        ) {
            erro = true;
        } else {
            let a = [];
            let b = new Number;
            let c = 11;
            for (let i = 0; i < 11; i++) {
                a[i] = cpf.charAt(i);
                if (i < 9) b += (a[i] * --c);
            }
            let x;
            if ((x = b % 11) < 2) a[9] = 0;
            else a[9] = 11 - x;
            b = 0;
            c = 11;
            for (let y = 0; y < 10; y++) b += (a[y] * c--);
            if ((x = b % 11) < 2) a[10] = 0;
            else a[10] = 11 - x;
            if ((cpf.charAt(9) != a[9]) || (cpf.charAt(10) != a[10])) erro = true;
        }
        if (erro) {
            oCampo.value("").apply();
            adicionaErro(campo, 'CPF Inválido.');
            return false;
        } else {
            removeErro(campo);
        }
    }
    return erro;
}

/**
 * VALIDA CNPJ
 * @requires getForm
 * @param {String} cnpj 
 * @param {String} campo
 * @return {boolean}
 */
function isValidCnpj(cnpj, campo) {
    let oCampo = getForm(campo);
    let erro = false;
    if (cnpj != "") {
        if (isNaN(cnpj)) {
            erro = true;
        } else {
            let i;
            let c = cnpj.substr(0, 12);
            let dv = cnpj.substr(12, 2);
            let d1 = 0;
            for (i = 0; i < 12; i++) d1 += c.charAt(11 - i) * (2 + (i % 8));
            if (d1 == 0) {
                erro = true;
            } else {
                d1 = 11 - (d1 % 11);

                if (d1 > 9) d1 = 0;

                if (dv.charAt(0) != d1) {
                    erro = true;
                } else {
                    d1 *= 2;
                    for (i = 0; i < 12; i++) d1 += c.charAt(11 - i) * (2 + ((i + 1) % 8));
                    d1 = 11 - (d1 % 11);
                    if (d1 > 9) d1 = 0;
                    if (dv.charAt(1) != d1) erro = true;
                }
            }
        }
        if (erro) {
            oCampo.value("").apply();
            adicionaErro(campo, 'CNPJ Inválido.');
        } else {
            removeErro(campo);
        }
    }
    return erro;
}

/**
 * VERIFICA SE O EMAIL INFORMADO EH VALIDO
 * @param {String} email 
 * @return {boolean}
 */
function isValidEmail(email) {
    let strEmail = email.toString().replace(/\s+/gim, '');
    return (strEmail === '' || strEmail.length < 3 || !strEmail.includes('@')) ? false : true;
}

// --------------------------------------------------------------------------------
// MANIPULACOES DE DATAS
// --------------------------------------------------------------------------------

/**
 * RETORNA A DATA NO FORMATO dd/MM/yyyy HH:mm:ss
 * @returns {String} systemDate
 */
const getSystemDate = () => {
    return new Date().toLocaleString("pt-BR");
}

/**
 * VALIDA SE A DATA SELECIONADA PELO USUÁRIO É MAIOR QUE A DATA CORRENTE
 * @requires getForm
 * @param {String} campo - NOME DO CAMPO "TIPO DATA" A SER VALIDADO
 * @returns {Boolean}
 */
function isValidDate(campo) {
    Form.apply().then(function () {
        let dataAtual = new Date();
        let data = getForm(campo).value();
        if (data != "" && data != null) {
            let dia = (data.substring(0, 2));
            let mes = (data.substring(3, 5) - 1);
            let ano = (data.substring(6, 10));
            let dataPassada = new Date(ano, mes, dia);
            if (dataAtual > dataPassada) {
                Form.addCustomModal({ title: "AVISO - Data Inválida", description: "A data informada deve ser maior que a data atual.", buttons: [] });
                getForm(campo).value("").apply();
            }
        }
    });
}

/**
 * FORMATA DATA LECOM dd/MM/aaaa PARA MM-dd-aaaa
 * @param {String} data dd/MM/aaaa;
 * @return {String} MM-dd-aaaa
 */
function setDateFormatI(data) {
    let dd = data.substring(0, 2);
    let mm = data.substring(3, 5);
    let aaaa = data.substring(6, 10);
    return [mm, dd, aaaa].join('-');
}

// --------------------------------------------------------------------------------
// CONSULTAS EM APIS DE TERCEIROS
// --------------------------------------------------------------------------------
/**
 * CONSULTAR CNPJ VIA API receitaws.com.br
 * @param {String} cnpj
 * @return {Object JSONP}
 */
const getCnpj = function (cnpj) {
    cnpj = cnpj.replace(/\D/gim, "");
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: 'https://www.receitaws.com.br/v1/cnpj/' + cnpj,
            type: "GET",
            dataType: "jsonp",
            cors: true,
            contentType: "application/json",
            secure: true,
            headers: {
                "Access-Control-Allow-Origin": "*",
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Basic " + btoa(""));
            },
            async: false,
            success: function (content) { resolve(content); },
            error: function (status, message) { reject(message); }
        });
    });
}

/**
 * CONSULTA CEP VIA API viacep.com.br
 * @param {String} cep 
 * @return {Object JSON}
 */
const getCep = function (cep) {
    cep = cep.replace(/\D/gim, "");
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: "https://viacep.com.br/ws/" + cep + "/json/",
            type: "GET",
            dataType: "jsonp",
            cors: true,
            contentType: "application/json",
            secure: true,
            headers: {
                "Access-Control-Allow-Origin": "*",
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Basic " + btoa(""));
            },
            async: false,
            success: function (content) { resolve(content); },
            error: function (status, message) { reject(message); }
        });
    });
}

/**
 * CONSULTA UF VIA API ibge.gov.br
 * @return {Object JSON}
 * {id, sigla, nome, regiao:{id, sigla, nome} }
 */
const getLstUf = function () {
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: "https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome",
            type: "GET",
            dataType: "json",
            cors: true,
            contentType: "application/json",
            secure: true,
            headers: {
                "Access-Control-Allow-Origin": "*",
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Basic " + btoa(""));
            },
            async: false,
            success: function (content) { resolve(content); },
            error: function (status, message) { reject(message); }
        });
    });
}

/**
 * CONSULTA ULTIMA COTACAO DA MOEDA - PTAX COMPRA E VENDA
 * @param {String} moeda - suportados pela API do BC do Brasil (DKK, NOK, SEK, USD, AUD, CAD, EUR, CHF, JPY, GBP)
 * @param {String} data - MM-DD-AAAA
 * @return {Object JSON}
 * {@odata.context, value[{cotacaoCompra, cotacaoVenda, dataHoraCotacao}]}
 */
const getUsdCotation = function (moeda, data) {
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)?@moeda=%27" + moeda + "%27&@dataCotacao=%27" + data + "%27&$top=1&$orderby=dataHoraCotacao%20desc&$format=json&$select=cotacaoCompra,cotacaoVenda,dataHoraCotacao",
            type: "GET",
            dataType: "json",
            cors: "true",
            contentType: "application/json",
            secure: true,
            headers: {
                "Access-Control-Allow-Origin": "*",
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Basic" + btoa(""));
            },
            async: false,
            success: function (content) { resolve(content); },
            error: function (status, message) { reject(message); }
        });
    });
}