
UPDATE processo SET
IDE_FINALIZADO = 'C'
WHERE
cod_form IN (999)    -- aceite;
-- cod_form IN (999) -- produção;
AND ide_beta_teste = 'S'
AND ide_finalizado = 'A';

UPDATE processo_etapa SET
IDE_STATUS = 'C'
WHERE
cod_processo IN (
SELECT
pe.cod_processo
FROM processo_etapa pe
LEFT JOIN processo p ON p.cod_processo = pe.cod_processo
WHERE
p.cod_form IN (999)    -- aceite;
-- p.cod_form IN (999) -- produção;
AND ide_beta_teste = 'S'
AND pe.ide_status  = 'A'
);
