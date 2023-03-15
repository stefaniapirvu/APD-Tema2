import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;

public class Workers extends Thread{
    private int id;
    String fileName;
    BufferedWriter writer_prod;
    long max_prod;
    String find;
    long bytes_prod;
    int found;

    public Workers( int id, String dirName, BufferedWriter writer_prod,long max_prod ,long bytes_prod, String find) {
        this.id = id;
        this.fileName = dirName + "/order_products.txt";
        this.writer_prod = writer_prod;
        this.max_prod = max_prod;
        this.find = find;
        this.bytes_prod = bytes_prod;
        found =0;
       
    }
 

    public int search() throws IOException {
        FileReader fr = null;
        fr = new FileReader(fileName);
        int i = 0;
        StringBuilder str = new StringBuilder();
        boolean ok = true;
        int count = 0;

        int repeat = id;
        int sum = 0;
        // daca nu sunt la primul thread sar peste max_prod*id bytes
        while (repeat > 0 && sum < bytes_prod) {

            fr.skip(max_prod - 1);
            sum += max_prod - 1;
            i = fr.read();
            while (i != '\n' && sum < bytes_prod) {
                i = fr.read();
                sum++;
            }
            repeat--;
        }
        if (sum >= bytes_prod) {
            ok = false;
        }


        while (ok && ((i = fr.read()) != -1)) {
            count++;
            // dupa ce citesc o linie verific daca corespunde comenzii mele
            // daca corespunde o procesez, altfel trec peste
            if (i == '\n') {

                String[] arrOfStr = str.toString().split(",", 2);
                if(find.equals(arrOfStr[0])){
                    found ++;
                    synchronized (this){
                        writer_prod.append(str+",shipped\n");
                    }

                }

                str = new StringBuilder();
            } else {
                str.append((char) i);
            }

            if (count >= max_prod && i == '\n') {
                ok = false;
            }

        }
        fr.close();
        return found;
    }
    public void run() {

        try {
            search();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
