package pl.Utils;

import java.util.Scanner;

    public class ScannerHelper {

        public static int getInt() {
            return getInt("");
        }

        public static int getInt(String msg) {
            Scanner scan = new Scanner(System.in);
            System.out.print(msg);
            while (!scan.hasNextInt()) {
                scan.next();
                System.out.print("Nieprawidłowe dane, podaj jeszcze raz: ");
            }
            return scan.nextInt();
        }

        public static double getDouble(String msg) {
            Scanner scan = new Scanner(System.in);
            System.out.print(msg);
            while (!scan.hasNextDouble()) {
                scan.next();
                System.out.print("Nieprawidłowe dane, podaj jeszcze raz: ");
            }
            return scan.nextDouble();
        }

        public static String getLine(String msg) {
            Scanner scan = new Scanner(System.in);
            System.out.print(msg);
            return scan.nextLine();
        }

}
