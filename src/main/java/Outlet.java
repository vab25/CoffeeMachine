public class Outlet implements Runnable {
    final int timeToPrepare = 2000;
    String beverageMessage;
    public Outlet(String s) {
        beverageMessage=s;
    }

    public void run(){
        try {
            Thread.sleep(timeToPrepare);
            System.out.println(beverageMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
