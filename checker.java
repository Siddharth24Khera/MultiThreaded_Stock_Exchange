import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class checker
{
    public static void main ( String args [])
    {   
        long startT=System.currentTimeMillis();
        stock r = new stock(startT);
        Thread OrderThread = new Thread(r);
        OrderThread.start();
        Exchange ex = new Exchange(startT);
        Thread ExchangeThread = new Thread(ex);
        ExchangeThread.start();
        test clean = new test(startT);
        Thread CleanUpThread = new Thread(clean);
        CleanUpThread.start();        
    }
}
