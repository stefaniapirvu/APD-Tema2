# APD-Tema2
 Manager de comenzi de Black Friday ˆın Java



Pentru rezolvarea temei am folosit 2 tipuri de threaduri, 
de nivel 1 "MyThread" si nivel 2 "Workers". Am utilizat
si un semafor pentru a permite a maxim N threaduri Workers
sa lucreze simultan.

In clasa Tema2.java:
       Am verificat dimensiunea fisierului de intrare,
    "orders" si am impartit acest numar la numarul de threaduri 
    pentru a vedea cam cati bytes trebuie sa citeasca fiecare 
    thread. "max" = nr de bytes cititi de fiecare thread.
    Am creeat threadurile de nivel 1.
    Am prezentat detaliat modul in care threadurile citesc din
    fisier in README_BONUS.

In clasa  MyThread:
    Fiecrae thread deschide fisierul de input.    
    Pe scurt fiecare thread citeste "max" bytes, dar daca nu a 
    ajuns la capat de linie, mai citeste cativa pana ajunge la capat.
    Primul thread citeste de la inceput, restul dau Skip peste aproximativ
    max*id_thread bytes. 

    Cand un thread citeste o linie, de forma nume_comada,nr_produse ,
    daca nr_produse >0, atunci retine aceasta comanda intr-un vector 
    de perechi (MyPair<String, Integer>);
    
    Pe rand, ia fiecare dintre aceste comenzi si le proceseaza.
    Creeaza pentru fiecare comanda un nr de threaduri de nivel 2 , 
    workers egal cu numarul de produse din comanda.

    Pentru a avea maxim N workerii care lucreaza simultan am folosit 
    un semafor, initializat in clasa principala "Tema2".

    Cand workerii termina, returneaza numarul de produse procesate.
    Daca numarul de produse procesate este egal cu numarul de produse 
    din comanda, comanda se marcheaza ca si procesata si se trece la 
    urmatoarea.

In clasa Workers:
    Fiecrae worker deschide fisierul de input.
    Identic cu metoda prezentata mai sus si in README_BONUS, workerii
    citesc un numar aproximativ egal de bytes.
    
    Cand un worker citeste o linie, verifica faca numele citit este
    al comenzii la care el lucreaza, daca da, atunci o bifeaza ca si 
    procesata si creste numarul de comenzi pe care el le-a procesat,
    altfel trece mai departe la urmatoarea linie.
