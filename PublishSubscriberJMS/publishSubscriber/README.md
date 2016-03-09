Publish/Subscriber com JMS
============
# Repositório referente às atividades da disciplina de Programação Distribuida
## Curso de Especialização em Sistemas Distribuídos e Ubíquos do Instituto Federal da Bahia


Preparando o ambiente:
$ baixar o j2ee SDK 1.3.1
$ baixar o JDK 1.4
$ Setar as variáveis de ambiente no que segue:

| Plataforma        | Variavel de ambiente | Valor                                                                            |
| ------------------|:--------------------:|:--------------------------------------------------------------------------------:|
|                   |  %JAVA_HOME%         | Diretório no qual o JDK 1.4 está instalado                                       |
|                   |:--------------------:|:--------------------------------------------------------------------------------:|
| Microsoft Windows |  %J2EE_HOME%         | Diretório no qual o J2EE SDK 1.3.1 está instalado, geralmente C: \ j2sdkee1.3.1  |
|                   |:--------------------:|:--------------------------------------------------------------------------------:|
|                   |  %CLASSPATH%         |Incluir o seguinte:  .;%J2EE_HOME%\lib\j2ee.jar;%J2EE_HOME%\lib\locale            |
|                   |:--------------------:|:--------------------------------------------------------------------------------:|
|                   |  %PATH%              |Incluir %J2EE_HOME%\bin                                                           |
| ----------------- |:--------------------:|:--------------------------------------------------------------------------------:|
|                   |  %JAVA_HOME%         | Diretório no qual o JDK 1.4 está instalado                                       |
|                   |:--------------------:|:--------------------------------------------------------------------------------:|
| UNIX              |  %J2EE_HOME%         | Diretório no qual o J2EE SDK 1.3.1 está instalado, geralmente $HOME/j2sdkee1.3.1 |
|                   |:--------------------:|:--------------------------------------------------------------------------------:|
|                   |  %CLASSPATH%         |Incluir o seguinte:  .:$J2EE_HOME/lib/j2ee.jar:$J2EE_HOME/lib/locale              |
|                   |:--------------------:|:--------------------------------------------------------------------------------:|
|                   |  %PATH%              |Incluir $J2EE_HOME/bin                                                            |
| ----------------- |:--------------------:|:--------------------------------------------------------------------------------:|


$ iniciar o servidor J2EE como se segue:

j2ee -verbose

$ compilar as classes java
javac *.java

$ Executar Publishers e Subscribers no que segue:

| Plataforma        | Entidade   | Comando                                                                                                         |
| ------------------|:----------:|:---------------------------------------------------------------------------------------------------------------:|
|                   | SUBSCRIBER | java -Djms.properties=%J2EE_HOME%\config\jms_client.properties Subscriber <topic-name>[Opcional]                |
|                   |:----------:|:---------------------------------------------------------------------------------------------------------------:|
| Microsoft Windows | PUBLISHER  | java -Djms.properties=%J2EE_HOME%\config\jms_client.properties Publisher <publisher-name> <topic-name>[Opcional]|
| ----------------- |:----------:|:---------------------------------------------------------------------------------------------------------------:|
|                   | SUBSCRIBER | java -Djms.properties=$J2EE_HOME/config/jms_client.properties Subscriber <topic-name>[Opcional]                 |
|                   |:----------:|:---------------------------------------------------------------------------------------------------------------:|
| UNIX              | PUBLISHER  | java -Djms.properties=$J2EE_HOME/config/jms_client.properties Publisher <publisher-name> <topic-name>[Opcional] |
| ----------------- |:----------:|:---------------------------------------------------------------------------------------------------------------:|











