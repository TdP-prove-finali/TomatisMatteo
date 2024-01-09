## Simulazione della Propagazione di un Incendio

Progetto di tesi di Tomatis Matteo. Lo scopo dell'applicativo e' quello di simulare la propagazione di un incendio e mostrarlo visivamente a partire dai dati climatici e ambientali inseriti dall'utente. Il programma si basa sui dati del db Wildfires che raccoglie informazioni sugli incendi in sardegna nel decennio 2010-2019. Il db puo' essere scaricato [qui](https://www.kaggle.com/datasets/christianmolliere/wildfires-and-climate-in-sardinia), tuttavia non e' necessario farlo per far funzionare il programma.

## Utilizzare l'Applicazione

L'applicazione e' stata sviluppata utilizzando Virtual Studio Code, pertanto le segunti istruzioni fanno riferimento a tale IDE. Al fine di utilizzare l'applicazione occorre:
1. Creare un fork del progetto
2. Importare il progetto sul proprio PC
3. Dal proprio IDE selezionare il comando di apertura di una cartella, navigare attraverso i file del proprio PC fino alla cartella "Tesi" contenuta all'interno del progetto e selezionarla
4. Scaricare [qui](https://gluonhq.com/products/javafx/) i file SDK adatti al proprio sistema operativo ed estrarre la cartella zip, poi aprire la cartella e copiare l'indirizzo della sottocartella "lib"
5. All'interno dei file del progetto selezionare il file "launch.json" contenuto in .vscode e sostituire il module path "D:/Downloads/openjfx-21.0.1_windows-x64_bin-sdk/javafx-sdk-21.0.1/lib" con l'indirizzo precedentemente copiato
6. Ora e' possibile runnare il programma direttamente dal file "Main" nella cartella src.javacode.controller
