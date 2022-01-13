# Procedimento para atualizar o certificado SSL no BPM de produção Infive

### 1. Parar o Tomcat

```bash
$ bpm -c stop
```

### 2. Ver se as portas indicadas estão ocupadas

```bash
$ ss -tlnp | grep -E ":(80|443)"
```
### 3. Renovar o certificado SSL Let's Encrypt

```bash
$ certbot certonly --standalone -d bpm.infive.com.br
```

### 4. Verificar se os certificados foram gerados

```bash
$ ls -la /etc/letsencrypt/live/bpm.infive.com.br/
```

### 5. Criar diretório temporário para armazenar o PK12 e o JKS

> Antes criamos um diretório temporário na pasta `/tmp`, pois não podemos escrever diretamente no diretório `/opt/lecom/certificados`

```bash
$ mkdir /tmp/tomcat_cert
```
### 6. Gerar o PK12

* ALIAS: YOUR_YOUR_ALIAS
* Password: YOUR_PASSWORD

```bash
$ openssl pkcs12 -export -out /tmp/tomcat_cert/bpm.infive.com.br.pk12 -in /etc/letsencrypt/live/bpm.infive.com.br/fullchain.pem -inkey /etc/letsencrypt/live/bpm.infive.com.br/privkey.pem -name YOUR_ALIAS
```

### 7. Gerar o JSK à partir do PK12

```bash
$ keytool -importkeystore -deststorepass YOUR_PASSWORD -destkeypass YOUR_PASSWORD -destkeystore /tmp/tomcat_cert/bpm.infive.com.br.jks -srckeystore /tmp/tomcat_cert/bpm.infive.com.br.pk12 -srcstoretype PKCS12 -srcstorepass YOUR_PASSWORD -YOUR_ALIAS YOUR_ALIAS
```

### 8. Verificar no diretório temporário se os arquivos foram criados

```bash
$ ls -la /tmp/tomcat_cert
```

### 9. Copie os arquivos .jks E pk12 para o diretório /opt/lecom/certificados

```bash
$ cp /tmp/tomcat_cert/bpm.infive.com.br.* /opt/lecom/certificados/
```

### 10. Verificar se os arquivos foram copiados

```bash
$ ls -la /opt/lecom/certificados/
```

### (Opcional) Definir as configurações no server.xml do Tomcat, caso tenha alterado o YOUR_ALIAS e a PASSWORD

* ALIAS: YOUR_ALIAS
* Password: YOUR_PASSWORD

```bash
$ nano /opt/lecom/app/tomcat_aceite/conf/server.xml
```

### 11. Iniciar o Tomcat

```bash
$ bpm -c start
```