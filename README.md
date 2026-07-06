================================================================
 CineMax - Laboratorio Interdisciplinare B - a.a. 2025/2026
================================================================

Autori:
- Karakus Yasemin, matricola 762746, sede VA
- Choudhry Maha Ilyas, matricola 747119, sede VA

Repository: https://github.com/ykarakus/CineMax

Descrizione
-----------
CineMax e' una piattaforma client/server per la gestione di un cinema
monosala da 200 posti. Permette ai clienti di cercare proiezioni e
prenotare posti, ai proiezionisti di gestire il palinsesto e ai
bigliettai di consultare le prenotazioni.

La piattaforma e' composta da due moduli:
- serverCM: back-end che si interfaccia con il database PostgreSQL
  e serve piu' client in parallelo (un thread per client)
- clientCM: applicazione desktop con interfaccia grafica JavaFX

Requisiti
---------
- Java JDK 21 o superiore
- Apache Maven 3.8 o superiore (solo per ricompilare il progetto)
- PostgreSQL (testato con la versione 18)

Struttura del repository
------------------------
- src/   codice sorgente (package cinemax)
- doc/   manuale utente, manuale tecnico, diagrammi ER/UML, javadoc
- bin/   eseguibili serverCM.jar e clientCM.jar
- lib/   (vuota: le dipendenze sono gestite da Maven, vedere pom.xml)
- data/  script SQL e file CSV per la creazione del database
- pom.xml  build Maven del progetto

1) Creazione del database
-------------------------
Creare un database vuoto chiamato "dbcm":

    psql -U postgres -h localhost -c "CREATE DATABASE dbcm;"

Posizionarsi nella cartella data/ ed eseguire nell'ordine:

    cd data
    psql -U postgres -h localhost -d dbcm -f crea_database.sql
    psql -U postgres -h localhost -d dbcm -f migra_dati_laba.sql
    psql -U postgres -h localhost -d dbcm -f vincolo_capienza.sql

Il primo script crea le tabelle (film, proiezione, utenti,
prenotazione), gli utenti predefiniti (2 proiezionisti e 5 bigliettai)
e importa il palinsesto dal file proiezioni.csv (8878 proiezioni,
725 film). Il secondo inserisce due clienti di prova e alcune
prenotazioni di esempio. Il terzo installa il trigger che impedisce,
a livello di database, il superamento della capienza della sala.

NOTA: lo script crea_database.sql usa il comando \copy con percorso
relativo, quindi va eseguito dalla cartella data/ (dove si trova
proiezioni.csv).

2) Compilazione
---------------
Dalla radice del progetto:

    mvn clean package

Il comando compila il progetto e genera in target/ i due eseguibili:
- serverCM.jar
- clientCM.jar
(copie dei JAR sono gia' presenti nella cartella bin/)

Per generare la documentazione javadoc:

    mvn javadoc:javadoc

(output in target/reports/apidocs, copia in doc/javadoc)

3) Esecuzione
-------------
Avviare PRIMA il server, che chiede all'avvio l'host del database e
le credenziali di accesso a PostgreSQL:

    java -jar bin/serverCM.jar

    Host del database [localhost]:  (invio per localhost)
    Username del database:          postgres
    Password del database:          <password scelta all'installazione>

Quando compare "Server in ascolto sulla porta 4444" avviare uno o
piu' client (anche su computer diversi):

    java -jar bin/clientCM.jar

Il client si connette automaticamente a localhost:4444; se il server
si trova su un altro host, l'indirizzo puo' essere indicato nella
schermata di connessione.

Credenziali di prova
--------------------
Proiezionisti:  proiez1 / proiez1123    proiez2 / proiez2123
Bigliettai:     bigl1 / bigl1123  ...  bigl5 / bigl5123
Clienti:        test / test             mch / 1234

E' inoltre possibile registrare nuovi clienti dalla schermata
iniziale dell'applicazione.

Note
----
- Le dipendenze esterne (driver JDBC PostgreSQL, librerie JavaFX)
  sono dichiarate nel pom.xml e vengono scaricate automaticamente
  da Maven; i JAR eseguibili le includono gia' al loro interno e
  sono multipiattaforma (macOS Intel/Apple Silicon, Windows, Linux).
- La porta TCP del server e' la 4444.
- Le password degli utenti sono cifrate nel database con BCrypt
  (estensione pgcrypto di PostgreSQL).
- Per la ricerca come guest si consiglia di provare titoli con
  proiezioni nei tre mesi successivi alla data odierna
  (es. "Star Wars", "Funny Games").