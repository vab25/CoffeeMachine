public class Outlet implements Runnable {

    final private Integer timeToPrepare = 2000;
    private String beverageMessage;

    public Outlet(String beverageMessage) {
        this.beverageMessage = beverageMessage;
    }

    public void run(){
        try {
            Thread.sleep(timeToPrepare);
            System.out.println(this.beverageMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
