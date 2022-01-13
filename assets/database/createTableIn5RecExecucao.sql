/**
 * IN5_REC_EXECUCAO - Tabela Infive Record Execução
 * COD_PROCESSO - código do processo em andamento
 * COD_ETAPA - código da etapa do processo
 * COD_CICLO - código do ciclo da etapa
 * STATUS_EXECUCAO - 0: erro | 1: sucesso
 * DATA_EXECUCAO
 * TIPO_EXECUCAO - ERP: qdo há interface com ERP | LECINT: qdo é uma integração | LECROB: qdo é um robô
 * ORIGEM - classe ou script de origem
 * MSG_RETORNO - Grava os erros, msg de sucesso ou, no caso de ERP, os dados transmitidos (JSON)
 */

----------------------------------------------------------------
-- MySQL
CREATE TABLE IN5_REC_EXECUCAO (
   ID INT AUTO_INCREMENT PRIMARY KEY,
   COD_PROCESSO INT,
   COD_ETAPA INT,
   COD_CICLO INT,
   STATUS_EXECUCAO INT,
   DATA_EXECUCAO DATETIME,
   TIPO_EXECUCAO VARCHAR(10),
   ORIGEM VARCHAR(100),
   MSG_RETORNO TEXT
);

----------------------------------------------------------------
-- MS SQL
IF OBJECT_ID ('dbo.IN5_REC_EXECUCAO') IS NOT NULL
    DROP TABLE dbo.IN5_REC_EXECUCAO
GO

CREATE TABLE dbo.IN5_REC_EXECUCAO (
   ID INT NOT NULL,
   COD_PROCESSO INT,
   COD_ETAPA INT,
   COD_CICLO INT,
   STATUS_EXECUCAO INT,
   DATA_EXECUCAO DATETIME,
   TIPO_EXECUCAO VARCHAR(10),
   ORIGEM VARCHAR(100),
   MSG_RETORNO TEXT
   
   CONSTRAINT PK_IN5_REC_EXECUCAO PRIMARY KEY NONCLUSTERED (ID)
)
GO

----------------------------------------------------------------
-- PostgreSQL
CREATE TABLE IN5_REC_EXECUCAO (
   ID SERIAL,
   COD_PROCESSO INT,
   COD_ETAPA INT,
   COD_CICLO INT,
   STATUS_EXECUCAO INT,
   DATA_EXECUCAO TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   TIPO_EXECUCAO VARCHAR(10),
   ORIGEM VARCHAR(100),
   MSG_RETORNO TEXT
   
   PRIMARY KEY (ID)
)