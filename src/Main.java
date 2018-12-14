import pl.Utils.ScannerHelper;

public class Main {

    public static void main(String[] args) {

        while (true) {
            System.out.println("Cześć, co chcesz dzisiaj zrobić?");
            System.out.println("add u   - dodanie nowego użytkownika");
            System.out.println("get u   - pobranie użytkownika z bazy");
            System.out.println("get u a - pobranie wszystkich użytkowników z bazy");
            System.out.println("exit    - zakończenie działania programu");

            String userChoice = ScannerHelper.getLine("");

            switch (userChoice) {
                case "add u" :
                default :
                    System.out.println("Nie rozpoznano polecenia");
            }



        }
    }
}
