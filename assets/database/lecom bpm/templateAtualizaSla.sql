
UPDATE processo_etapa SET
DAT_ALERTA      = '?',
DAT_LIMITE      = '?',
DAT_FINALIZACAO = '?'
WHERE
cod_processo    = 999
AND cod_etapa   = 999
AND cod_ciclo   = 999;
