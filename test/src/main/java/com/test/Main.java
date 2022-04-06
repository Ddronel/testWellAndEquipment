package com.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {

        Scanner scanner = new Scanner(System.in);
        // Подключение к базе данных SQLite
        Server.openDB();
        // Создание таблиц Well и Equipment
        Server.createTable();
        // Переменная для записи команд, введенные пользователем
        int command = 0;

        // Программа
        while(true) {
            System.out.println("_________________________________________________________");
            System.out.println("1 - Создание оборудования на скважине;");
            System.out.println("2 - Вывод общей информации об оборудовании на скважинах;");
            System.out.println("3 - Экспорт всех данных в xml файл;");
            System.out.println("4 - close().");

            System.out.println("Выберите команду для выполнения:");
            try {
                command = scanner.nextInt();
                if (command == 1) {
                    System.out.println("Введите имя скважины: ");
                    String wellName = scanner.next();
                    System.out.println("Введите количество оборудования: ");
                    int equipmentCount = scanner.nextInt();
                    Server.writeInTable(wellName, equipmentCount);
                } else if (command == 2) {
                    System.out.println("Введите имена скважин, разделяя их пробелами или запятыми:");
                    scanner.nextLine();
                    String[] wellsName = scanner.nextLine().split("( |,)");
                    Server.readFromTable(wellsName);
                } else if (command == 3) {
                    System.out.println("Введите имя файла.xml");
                    String fileName = scanner.next();
                    Server.createFile(fileName);
                }
                else if (command == 4) {
                    break;
                } else {
                    System.out.println("Такой команды не существует.");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Попробуй ввести снова...");
                scanner.next();
            }
        }
        // Закрытие подлючения
        Server.closeDB();
    }
}
