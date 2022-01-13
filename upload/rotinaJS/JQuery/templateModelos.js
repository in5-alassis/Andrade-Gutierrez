/**
 * @author Rafael Marquini
 * @since 22/12/2020
 * @version 1.0.3
 * @abstract ESTILO PADRAO PARA MODELOS/FORMS DO CLIENTE
 */

const PATH_LOGO = "";
const PRIMARY_COLOR = '#1D1F50';
const WHITE_COLOR = '#FFFFFF';
const LIGHT_GRAY_COLOR = '#ECECEC';

$(document).ready(function () {
  estilizaFormulario("TITULO_FORMULARIO", "LOGO", PATH_LOGO);
});

/**
 * estiliza formulario;
 *
 * @param idTitulo id do campo titulo;
 * @param idLogo id do campo logo;
 * @param urlLogo url do logo;
 */
function estilizaFormulario(idTitulo, idLogo, urlLogo) {
  try {
    // define logo;
    // --------------------------------------------------------------------------------------------
    if (!isMobile()) {
      // REPOSICIONA A DIV DO TITULO PARA ALINHAR AO LOGO
      $("#input__" + idLogo).css("margin-bottom", "-7rem");
      $("#input__" + idLogo).css("padding", "1rem");
      $("#input__" + idLogo).css("background-color", LIGHT_GRAY_COLOR);
      // CORRIGI BUG QUE ADICIONA UMA LABEL ESCRITO "LOGO" NO TOPO DA LINHA
      document.getElementById('input__' + idLogo).getElementsByClassName('label')[0].setAttribute("style", "display: none !important");
      // CARREGA A LOGO
      $("#" + idLogo).css("background-color", "none");
      $("#" + idLogo).css("display","none");
      $("#" + idLogo).after("<div class='logo'><img src='" + PATH_LOGO + "'></div>");
      // AJUSTA A LARGURA DA DIV DO TITULO
      $("#label__" + idTitulo).css("position", "relative");
      $("#label__" + idTitulo).css("left", "1.5rem");
      $("#label__" + idTitulo).css("padding", "1.5rem");
      $("#label__" + idTitulo).css("z-index", "999");
      $("#label__" + idTitulo).css("color", PRIMARY_COLOR);
      $("#label__" + idTitulo).css("background", "none");
      
    }
    //
    else Form.fields(idLogo).hidden(true).apply();

    // ajusta cor dos agrupadores;
    // --------------------------------------------------------------------------------------------
    let groupBorder = document.querySelectorAll('.group-border');
    groupBorder[0].setAttribute("style", "display: none !important");
    $('.group-border').css('margin', '30px 10px -5px');
    $('.group-border').css('background-color', PRIMARY_COLOR);
    $('.group-name').css('color', PRIMARY_COLOR);
    //

    // ajusta cor dos agrupadores;
    // --------------------------------------------------------------------------------------------
    $('.small.label-text').css('color', WHITE_COLOR);
    $('.small.label-text').css('background-color', PRIMARY_COLOR);
    $('.small.label-text').css('padding', '0.5rem');
    $('.small.label-text').css('font-size', '16px');
    //
    
    // hack MS Edge
    // --------------------------------------------------------------------------------------------
    $(".type-simple").css("display", "none");

  } catch (error) {
    console.log("estilizaFormulario.error:\r\n" + error);
  }
}

/**
 * @returns booleano de url renderizada em dispositivos mobile;
 */
function isMobile() {
  try {
    let check = false;
    (function (a) {
      if (
        /(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(
          a
        ) ||
        /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(
          a.substr(0, 4)
        )
      )
        check = true;
    })(navigator.userAgent || navigator.vendor || window.opera);
    return check;
  } catch (error) {
    console.log("isMobile.error:\r\n" + error);
  }
}
