import java.io.*;
import java.util.*;
class Stock{
    String name;
    double price;
    Stock(String name, double price){
        this.name=name;
        this.price=price;
    }
    void updatePrice(){
        double change = (Math.random()-0.5)*10;
        price+=change;
        if(price<1)price=1;
    }
}
class Transaction{
    String type;
    String stockName;
    int quantity;
    double price;
    Transaction(String type, String stockName, int quantity, double price){
        this.type=type;
        this.stockName=stockName;
        this.quantity=quantity;
        this.price=price;
    }
}
class Portfolio{
    Map<String, Integer> holdings = new HashMap<>();
    List<Transaction> history = new ArrayList<>();
    void buy(String stock, int qty, double price){
        holdings.put(stock, holdings.getOrDefault(stock,0)+qty);
        history.add(new Transaction("BUY", stock, qty, price));
    } 
    void sell(String stock, int qty, double price){
        if(holdings.containsKey(stock)&&holdings.get(stock)>=qty){
            holdings.put(stock,holdings.get(stock)-qty);
            history.add(new Transaction("SELL",stock,qty,price));
        } else{
        System.out.println("Not enough shares!");
        }
    }
    double getValue(Map<String, Stock> marketStocks){
        double total=0;
        for(String stock: holdings.keySet()){
            total+=holdings.get(stock)*marketStocks.get(stock).price;
        }
        return total;
    }
    void display(){
        System.out.println("Portfolio:");
        for(String s: holdings.keySet()){
            System.out.println(s+" : "+holdings.get(s));
        }
    }
}
class User{
    String name;
    double balance;
    Portfolio portfolio=new Portfolio();
    User(String name, double balance){
        this.name=name;
        this.balance=balance;
    }
}
class Market{
    Map<String, Stock> stocks=new HashMap<>();
    void addStock(Stock s){
        stocks.put(s.name,s);
    }
    void updateMarket(){
        for(Stock s: stocks.values()){
            s.updatePrice();
        }
    }
    void displayMarket(){
        System.out.println("Market Data:");
        for(Stock s: stocks.values()){
            System.out.println(s.name+" : "+String.format("%.2f",s.price));
        }
    }
}
public class TradingSystem{
    static final String FILE_NAME="portfolio.txt";
    public static void main(String args[]) throws Exception{
        Scanner sc=new Scanner(System.in);
        Market market=new Market();
        market.addStock(new Stock("TCS", 3500));
        market.addStock(new Stock("INFY",1500));
        market.addStock(new Stock("RELIANCE",2800));
        User user=new User("Srilakshmi",100000);
        while(true){
            market.updateMarket();
            System.out.println("\n1. View Market");
            System.out.println("2. Buy");
            System.out.println("3. Sell");
            System.out.println("4. Portfolio");
            System.out.println("5. Save Data");
            System.out.println("6. Load Data");
            System.out.println("7. Exit");
            int ch=sc.nextInt();
            switch(ch){
                case 1:
                    market.displayMarket();
                    break;
                case 2:
                    System.out.println("Enter stock name: ");
                    String buyStock = sc.next().toUpperCase();
                    System.out.print("Quantity: ");
                    int bqty = sc.nextInt();
                    if(market.stocks.containsKey(buyStock)){
                        double price = market.stocks.get(buyStock).price;
                        double cost=price*bqty;
                        if(user.balance>=cost){
                            user.balance-=cost;
                            user.portfolio.buy(buyStock,bqty,price);
                            System.out.println("Bought!");                            } else{
                            System.out.println("Insufficient balance");
                            }
                        }
                        break;
                case 3:
                    System.out.print("Enter Stock name: ");
                    String sellStock=sc.next().toUpperCase();
                    System.out.println("Quantity: ");
                    int sqty = sc.nextInt();
                    if(market.stocks.containsKey(sellStock)){
                        double price = market.stocks.get(sellStock).price;
                        user.portfolio.sell(sellStock,sqty,price);
                        user.balance+=price*sqty;
                        System.out.println("Sold!");
                    }
                    break;
                case 4:
                    user.portfolio.display();
                    double total=user.portfolio.getValue(market.stocks);
                    System.out.println("Portfolio Value: "+total);
                    System.out.println("Balance: "+user.balance);
                    System.out.println("Total Wealth: "+(total+user.balance));
                    break;
                case 5:
                    saveData(user);
                    break;
                case 6:
                    loadData(user);
                    break;
                case 7:
                    System.exit(0);
            } 
        }
    }
    static void saveData(User user) throws Exception{
        BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME));
        bw.write(user.balance+"\n");
        for(String s: user.portfolio.holdings.keySet()){        
            bw.write(s+","+user.portfolio.holdings.get(s)+"\n");
        }
        bw.close();
        System.out.println("Data saved!");
    }
    static void loadData(User user) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        user.balance=Double.parseDouble(br.readLine());
        user.portfolio.holdings.clear();
        String line;
        while((line = br.readLine())!=null){
            String[] parts=line.split(",");
            user.portfolio.holdings.put(parts[0], Integer.parseInt(parts[1]));
        }
        br.close();
        System.out.println("Data loaded!");
    }
}
