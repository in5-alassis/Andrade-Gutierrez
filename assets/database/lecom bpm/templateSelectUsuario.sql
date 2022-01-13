
-- default;
---------------------------------------------------------------------------------------------------
SELECT
u.NOM_USUARIO,
u.DES_LOGIN
FROM usuario u
WHERE
UPPER(u.nom_usuario)      LIKE UPPER('%$ID_CAMPO%')
AND u.des_login           NOT IN ('adm', 'atosmaster')
AND u.ide_usuario_inativo <> 'S'
AND UPPER(u.nom_usuario)  NOT LIKE UPPER('%IN5%')
ORDER BY
1

-- left join depto;
---------------------------------------------------------------------------------------------------
SELECT
u.NOM_USUARIO,
u.DES_LOGIN,
d.DES_DEPTO,
(SELECT nom_usuario FROM usuario WHERE cod_usuario = u.cod_lider) "NOM_LIDER"
FROM usuario u
LEFT JOIN depto d ON d.cod_depto = u.cod_depto
WHERE
UPPER(u.nom_usuario)      LIKE UPPER('%$ID_CAMPO%')
AND u.des_login           NOT IN ('adm', 'atosmaster')
AND u.ide_usuario_inativo <> 'S'
AND UPPER(u.nom_usuario)  NOT LIKE UPPER('%IN5%')
ORDER BY
1

-- left join grupo_usuario;
---------------------------------------------------------------------------------------------------
SELECT
u.NOM_USUARIO,
u.DES_LOGIN
FROM usuario u
LEFT JOIN grupo_usuario gu ON gu.cod_usuario = u.cod_usuario
LEFT JOIN grupo g ON g.cod_grupo = gu.cod_grupo
WHERE
UPPER(u.nom_usuario)      LIKE UPPER('%$ID_CAMPO%')
AND g.des_comando         =  'APELIDO_GRUPO'
AND u.ide_usuario_inativo <> 'S'
ORDER BY
1
