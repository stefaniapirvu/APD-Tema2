import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.concurrent.Semaphore;


public class MyThread extends Thread {

    private int id;
    private long max;

    Vector<MyPair> my_orders = new Vector<>();
    String dirName;
    String fileName;
    BufferedWriter writer;
    BufferedWriter writer_prod;
    long bytes_prod;
    long bytes;
    Semaphore sem;

    public MyThread(Semaphore sem,int id, long max, String dirName, BufferedWriter writer, BufferedWriter writer_prod, long bytes) throws IOException {
        this.id = id;
        this.max = max;
        this.fileName = dirName + "/orders.txt";
        this.dirName = dirName;
        this.writer = writer;
        this.writer_prod = writer_prod;
        this.bytes_prod = Files.size(Paths.get(dirName + "/order_products.txt"));
        this.bytes = bytes;
        this.sem = sem;

    }

    public void func() throws IOException {
        FileReader fr = null;
        fr = new FileReader(fileName);
        int i = 0;
        StringBuilder str = new StringBuilder();
        boolean ok = true;
        int count = 0;

        int repeat = id;
        int sum = 0;

        // daca nu sunt la primul thread sar peste max*id bytes
        while (repeat > 0 && sum < bytes) {

            fr.skip(max - 1);
            sum += max - 1;
            i = fr.read();
            while (i != '\n' && sum < bytes) {
                i = fr.read();
                sum++;
            }
            repeat--;
        }
        // daca am ajuns la finalul fisierului ma opresc
        if (sum >= bytes) {
            ok = false;
        }


        while (ok && ((i = fr.read()) != -1)) {
            count++;

            // daca am ajuns la final de linie, retin datele comenzii
            if (i == '\n') {

                String[] arrOfStr = str.toString().split(",", 2);
                if (Integer.parseInt(arrOfStr[1]) > 0 ) {
                    my_orders.add(new MyPair(arrOfStr[0], Integer.parseInt(arrOfStr[1])));
                }

                str = new StringBuilder();
            } else {
                str.append((char) i);
            }

            if (count >= max && i == '\n') {
                ok = false;
            }

        }
        fr.close();
    }

    public void work() throws InterruptedException, IOException {
        //iau pe rand fiecare comanda atribuita thread-ului curent
        for (int k = 0; k < my_orders.size(); k++) {

            int nr_th2 = my_orders.elementAt(k).quantity;

            long max_prod = bytes_prod / (long) nr_th2;
            int ready = 0;
            //creez atatea thread-uri workers cate produse am in comanda
            Thread[] t = new Thread[nr_th2];

            for (int j = 0; j < nr_th2; ++j) {
                
                t[j] = new Workers(j, dirName, writer_prod, max_prod,bytes_prod, my_orders.elementAt(k).order_name);

                // folosesc un semafor initializat in clasa principala cu N
                // pornesc simultan maxim N workers (N dat ca parametru)
                sem.acquire();
                t[j].start();
                sem.release();
            }

            for (int j = 0; j < nr_th2; ++j) {
                t[j].join();

                // workerii returneaza numarul de prosude procesate
                ready += ((Workers) t[j]).found; 
                //cand sunt procesate toate produsele, marchez comanda si trec la urmatoarea
                if (ready == my_orders.elementAt(k).quantity) {
                    writer.append(my_orders.elementAt(k).order_name + ',' + my_orders.elementAt(k).quantity + ",shipped\n");

                    break;
                }

            }
        }
    }


    public void run() {
        // citesc comnezile
        try {
            func();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // asignez workerilor procesarea comenzilor
        try {
            work();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

