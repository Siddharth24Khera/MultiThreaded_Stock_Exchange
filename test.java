import java.util.*;
import java.io.*;

public class test implements Runnable{
    //Cleans up the lists    
    private long startT;
    LinkedList buyList;
    LinkedList sellList;    
    test(long start){
        startT = start;
        this.buyList=Exchange.buyList;
        this.sellList=Exchange.sellList;
    }
    
    public void run(){
        for(long i =0;i<100000;i++){}
        while(System.currentTimeMillis()-startT<50000){
            checkSell();
            checkBuy();
        }        
    }
    
    private void checkSell(){
        Order parser =null;
        try{parser = sellList.getHead();}catch(Exception e){}
        if(parser==null) return;
        while(parser.next != null){
            
            if ((!isAlivee(parser) || !isQty(parser)) && !parser.dead){
                  sellList.remove(parser);
                  parser.dead=true;
                  print(((double)(System.currentTimeMillis() - startT))/1000,parser);
                }
                parser=parser.next;
        }
    }
    
    private void checkBuy(){
        Order parser =null;
        try{parser = buyList.getHead();}catch(Exception e){}
        if(parser==null) return;
        while(parser.next != null){
            
            if ((!isAlivee(parser) || !isQty(parser)) && !parser.dead){
                  buyList.remove(parser);
                  parser.dead=true;
                  print(((double)(System.currentTimeMillis() - startT))/1000,parser);
                }
                parser=parser.next;
        }          
    }
    
    private boolean isAlivee(Order b)
    {
        if((b.inTime + b.expTime)*1000 < System.currentTimeMillis()-startT)
            return false;
        return true;
    }
    
    private boolean isQty(Order b){
        if(b.qty==0) return false;
        return true;
    }
    
    private void print(double time,Order a)
    {
         try{
        FileOutputStream fos = new FileOutputStream("cleanup.txt",true);
        PrintStream p = new PrintStream(fos);
        if(a.type)
            p.println(time+" "+a.inTime+" "+a.name+" "+a.expTime+" sell "+a.qty+" "+a.stock+" "+a.price+" "+a.partial);
        else  p.println(time+" "+a.inTime+" "+a.name+" "+a.expTime+" buy "+a.qty+" "+a.stock+" "+a.price+" "+a.partial);
      } catch(FileNotFoundException e)
        {System.out.println("EXCEPTION " + e);}  
    }
}
