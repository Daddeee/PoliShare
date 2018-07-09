# PoliShare

## Installazione

All'interno della directory 'artifacts' è possibile trovare i JAR per l'avvio dell'applicazione, con i relativi file di configurazione già pronti per l'utilizzo dell'app su una sola macchina. Per poter utilizzare l'applicazione è necessario Java 8.

Prima di avviare un peer è necessario attivare il server con il comando 'java -jar server.jar', dopodichè con lo stesso comando si può procedere all'avvio dei peer.

E' possibile testare l'app all'interno di una rete locale, cambiando i parametri nei file config.xml : 'my_ip' rappresenta l'interfaccia di rete dell'eseguibile utilizzato, 'server_ip' quella del server.

L'avvio di un peer genera un database che andrà a tenere traccia degli appunti caricati/scaricati. Nel caso di un utilizzo con più peer sulla stessa macchina è consigliabile creare una cartella per ogni peer, ciascuna contenente l'eseguibile e il file di configurazione. Nel caso in cui due peer operino con lo stesso DB, risulteranno possessori degli stessi appunti.
