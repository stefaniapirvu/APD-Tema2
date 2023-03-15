
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;

public class Tema2 {
   
    public static void main(String[] args) throws InterruptedException, IOException {

        // deschid fisierele de out
        FileWriter file = new FileWriter("orders_out.txt");  
        BufferedWriter writer = new BufferedWriter(file); 
        FileWriter file_prod = new FileWriter("order_products_out.txt");  
        BufferedWriter writer_prod = new BufferedWriter(file_prod); 
        
        String dirName = args[0];
        String fileName = dirName + "/orders.txt";

        Path path = Paths.get(fileName);

        // verific dimensiunea fisierului de input
        // calculez cati bytes trebuie sa citeasca un thread
        long bytes = Files.size(path);
        int NUMBER_OF_THREADS = Integer.parseInt(args[1]);
        long max = bytes/ (long)NUMBER_OF_THREADS;
        // ma asigur ca un thread nu e nevoit sa citeasca 0 bytes
        if (max < 1){
            max  = 1;
        }

        Thread[] t = new Thread[NUMBER_OF_THREADS];
        Semaphore sem = new Semaphore(NUMBER_OF_THREADS+1);

        for (int i = 0; i < NUMBER_OF_THREADS; ++i) {
            t[i] = new MyThread(sem,i, max, dirName,  writer,writer_prod, bytes );
            t[i].start();
        }
 
        for (int i = 0; i < NUMBER_OF_THREADS; ++i) {
            try {
                t[i].join();
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        

        writer.close(); 
        writer_prod.close();  


    }

    
}
