import java.util.*;
import java.io.*;

public class Exchange implements Runnable{
    //match orders
    public static LinkedList buyList;
    public static LinkedList sellList;
    LinkedList buffer;
    int profit;
    long startT;
    Order a;    
    static boolean isBuyEmpty;
    static boolean isSellEmpty;    
    
    Exchange(long start)
    {
        startT=start;
        profit=0;
        buyList = new LinkedList();
        sellList= new LinkedList();
        buffer = new LinkedList();
    }
    
    public void run()
    {
        while(System.currentTimeMillis() - startT < 50000){
            try
            {
                a = stock.qq.deque();
                performer(a);
            }
            catch(Exception e){}
        }
        print("",-1.0,profit,null);
       }
       
    private void performer(Order a)
    {
        if((a.inTime + a.expTime)*1000 < System.currentTimeMillis()-startT)
            return;
        Order parser;
        Order bestMatch=null;
        boolean flag=false;
        int maxPrice = a.price;
        int minPrice = a.price;
        buffer.clear();        
        buffer.resetParser();
        buyList.resetParser();
        sellList.resetParser();
        
        if(a.type && a.partial)
        {
            if(!buyList.isEmpty())
            {
                while(buyList.hasSome())
                {
                    parser = buyList.show();
                    if((parser.stock).equals(a.stock))
                        buffer.add(parser);
                }
            }
            if(buyList.isEmpty() || buffer.isEmpty())
            {
                sellList.add(a);
                print("S",(double)(System.currentTimeMillis()-startT)/1000,0,a);
                return;
            }
            if(!buffer.isEmpty())
            {
              while(true)
              {
                 while(buffer.hasSome())
                 {
                    parser = buffer.show();
                    if(parser.qty <= a.qty  && isAlive(parser) && parser.price >= maxPrice && parser.qty>0)
                    {                        
                       maxPrice = parser.price;
                       bestMatch= parser;
                       flag=true;
                    }                    
                 }
                 if(flag == false) {break;}
                 a.qty -= bestMatch.qty;                                 
                 profit += bestMatch.qty*(bestMatch.price-a.price);
                 print("T",(double)(System.currentTimeMillis()-startT)/1000,bestMatch.qty,a);
                 bestMatch.qty=0;                 
                 flag=false;
              }
            }
            if(a.qty ==0) return;
            buffer.resetParser();
            flag=false;
            maxPrice=a.price;
            while(buffer.hasSome())
               {
                 parser=buffer.show();
                 if(parser.qty > a.qty && parser.partial && isAlive(parser)&& parser.price >= maxPrice)
                 {
                    maxPrice= parser.price;
                    bestMatch = parser;
                    flag=true;
                 }                 
               } 
            if(flag == false) {
                sellList.add(a);
                print("S",(double)(System.currentTimeMillis()-startT)/1000,0,a);
                return;
            }
            bestMatch.qty -= a.qty;
            profit= a.qty*(bestMatch.price - a.price);            
            print("T",(double)(System.currentTimeMillis()-startT)/1000,a.qty,a);
            a.qty=0;
            return;
        }
        
        if(a.type && !a.partial)
        {
            if(!buyList.isEmpty())
            {
                while(buyList.hasSome())
                {
                    parser = buyList.show();
                    if((parser.stock).equals(a.stock))
                        buffer.add(parser);
                }
            }
            if(buyList.isEmpty() || buffer.isEmpty())
            {
                sellList.add(a);
                print("S",(double)(System.currentTimeMillis()-startT)/1000,0,a);
                return;
            }
            if(!buffer.isEmpty())
            {
                while(buffer.hasSome())
                {
                  parser=buffer.show();
                  if(((parser.qty>a.qty && parser.partial) || (parser.qty==a.qty)) && parser.price >= maxPrice && isAlive(parser))
                  {
                    maxPrice=parser.price;
                    bestMatch=parser;
                    flag=true;                    
                  }                  
                }
                if(flag==false) {
                    sellList.add(a); 
                    print("S",(double)(System.currentTimeMillis()-startT)/1000,0,a); 
                    return;
                }
                bestMatch.qty -= a.qty;
                profit += (bestMatch.price - a.price)*a.qty;
                print("T",(double)(System.currentTimeMillis()-startT)/1000,a.qty,a);
                a.qty=0;
                return;
            }
        }
        
        if(!a.type && a.partial)
        {
            if(!sellList.isEmpty())
            {
                while(sellList.hasSome())
                {
                    parser = sellList.show();
                    if((parser.stock).equals(a.stock))
                        buffer.add(parser);
                }
            }
            if(sellList.isEmpty() || buffer.isEmpty())
            {
                buyList.add(a);
                print("B",(double)(System.currentTimeMillis()-startT)/1000,0,a);
                return;
            }
            
            if(!buffer.isEmpty())
            {
              while(true)
              {
                 while(buffer.hasSome())
                 {
                    parser = buffer.show();
                    if(parser.qty <= a.qty  && isAlive(parser) && parser.price <= minPrice && parser.qty>0)
                    {                        
                       minPrice = parser.price;
                       bestMatch= parser;
                       flag=true;
                    }                    
                 }
                 if(flag == false) {break;}
                 a.qty -= bestMatch.qty;                                 
                 profit += bestMatch.qty*(a.price-bestMatch.price);
                 print("T",(double)(System.currentTimeMillis()-startT)/1000,bestMatch.qty,a);
                 bestMatch.qty=0;
                 flag=false;
              }
            }
            if(a.qty ==0) return;
            buffer.resetParser();
            flag=false;        
            minPrice=a.price;
            while(buffer.hasSome())
               {
                 parser=buffer.show();
                 if(parser.qty > a.qty && parser.partial && isAlive(parser)&& parser.price <= minPrice)
                 {
                    maxPrice= parser.price;
                    bestMatch = parser;
                    flag=true;
                 }                 
               } 
            if(flag == false) {
                buyList.add(a);
                print("B",(double)(System.currentTimeMillis()-startT)/1000,0,a);
                return;
            }
            bestMatch.qty -= a.qty;
            profit= a.qty*(a.price-bestMatch.price);
            print("T",(double)(System.currentTimeMillis()-startT)/1000,a.qty,a);
            a.qty=0;
            return;
        }
        
        if(!a.type && !a.partial)
        {
            if(!sellList.isEmpty())
            {
                while(sellList.hasSome())
                {
                    parser = sellList.show();
                    if((parser.stock).equals(a.stock))
                        buffer.add(parser);
                }
            }
            if(sellList.isEmpty() || buffer.isEmpty())
            {
                buyList.add(a);
                print("B",(double)(System.currentTimeMillis()-startT)/1000,0,a);
                return;
            }
            if(!buffer.isEmpty())
            {
                while(buffer.hasSome())
                {
                  parser=buffer.show();
                  if(((parser.qty>a.qty && parser.partial) || (parser.qty==a.qty)) && parser.price <= minPrice && isAlive(parser))
                  {
                    minPrice=parser.price;
                    bestMatch=parser;
                    flag=true;                    
                  }                  
                }
                if(flag==false) {
                    buyList.add(a);
                    print("B",(double)(System.currentTimeMillis()-startT)/1000,0,a);
                    return;
                }
                bestMatch.qty -= a.qty;
                profit += (a.price - bestMatch.price)*a.qty;
                print("T",(double)(System.currentTimeMillis()-startT)/1000,a.qty,a);
                a.qty=0;
                return;
            }
        }
    }
    
    private void print(String log,double time,int status,Order a)
    {
         try{
        FileOutputStream fos = new FileOutputStream("exchange.txt",true);
        PrintStream p = new PrintStream(fos);
        if(time==-1.0)
            p.println("Profit = "+status);        
        if(a.type)
            p.println(log+" "+time+" "+status+" "+a.inTime+" "+a.name+" "+a.expTime+" sell "+a.qty+" "+a.stock+" "+a.price+" "+a.partial);
        else  p.println(log+" "+time+" "+status+" "+a.inTime+" "+a.name+" "+a.expTime+" buy "+a.qty+" "+a.stock+" "+a.price+" "+a.partial);
      } catch(FileNotFoundException e)
        {System.out.println("EXCEPTION " + e);}  
    }
    
    private boolean isAlive(Order b)
    {
        if((b.inTime + b.expTime)*1000 < System.currentTimeMillis()-startT)
            return false;
        return true;
    }
}