================================================================
CineMax - Sistema di Gestione Cinema
Laboratorio Interdisciplinare A - a.a. 2025/2026
Università degli Studi dell'Insubria
================================================================

REQUISITI
---------
- Java JDK 17 o superiore
  Download: https://adoptium.net/

VERIFICA INSTALLAZIONE JAVA
----------------------------
Aprire il terminale e digitare:
  java -version
Deve apparire una versione 17 o superiore.

STRUTTURA DEL PROGETTO
-----------------------
  CineMax/
  ├── autori.txt
  ├── README.txt
  ├── bin/          -> file eseguibile (.jar)
  ├── data/         -> file dati CSV
  ├── doc/          -> manuali e javadoc
  └── src/          -> codice sorgente

AVVIO TRAMITE JAR (consigliato)
--------------------------------
1. Aprire il terminale
2. Spostarsi nella cartella del progetto:
   cd percorso/CineMax
3. Avviare il programma:
   java -jar bin/CineMax.jar

COMPILAZIONE DAI SORGENTI
--------------------------
1. Aprire il terminale
2. Spostarsi nella cartella del progetto:
   cd percorso/CineMax
3. Compilare:
   javac -d bin/classes src/cinemax/*.java src/cinemax/**/*.java
4. Avviare:
   java -cp bin/classes cinemax.CineMax

NOTE
----
- I file dati (proiezioni.csv, utenti.csv, prenotazioni.csv)
  devono trovarsi nella cartella data/
- Il programma deve essere avviato dalla cartella radice
  del progetto, altrimenti non trova i file dati
================================================================