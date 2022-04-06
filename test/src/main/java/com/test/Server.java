package com.test;

import com.thoughtworks.xstream.XStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static Connection co;

    public static void openDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        co = DriverManager.getConnection("jdbc:sqlite:test.db");
    }

    public static void createTable() throws SQLException {
        Statement createTable = co.createStatement();

        createTable.executeUpdate("create table if not exists Well " +
                "(id integer primary key autoincrement, " +
                "name varchar(32) not null unique);");
        createTable.executeUpdate("create table if not exists Equipment " +
                "(id integer primary key autoincrement, name VARCHAR(32) NOT NULL UNIQUE, " +
                "Well_id INTEGER, FOREIGN KEY(Well_id) REFERENCES Well(id));");

        createTable.close();
    }

    // Заполнение таблиц
    public static void writeInTable(String wellName, int equipmentCount) throws SQLException {
        // Запрос на поиск id c таблица well с именем скважины wellName
        PreparedStatement request = co.prepareStatement("SELECT id FROM Well WHERE name = ?");
        request.setString(1, wellName);
        ResultSet resultSet = request.executeQuery();
        int wellId = 0;
        //полученный результат записываем в переменную wellId
        while (resultSet.next())
            wellId = resultSet.getInt("id");
        // Если wellId = 0, то скважины wellName нет в нашей таблице Well и нужно её создать
        if (wellId == 0) {
            // Создаём новую скважину wellName в таблице Well
            PreparedStatement request1 = co.prepareStatement("INSERT INTO Well (name) VALUES (?)");
            request1.setString(1, wellName);
            request1.executeUpdate();
            // Извлекаем id только что созданной скважины wellName и сохраняем в переменную wellId
            PreparedStatement request2 = co.prepareStatement("SELECT id FROM Well WHERE name = ?");
            request2.setString(1, wellName);
            resultSet = request2.executeQuery();
            while(resultSet.next())
                wellId = resultSet.getInt("id");

            request1.close();
            request2.close();
        }
        // Считаем кол-во оборудования в таблице Equipment, для записи новых
        Statement statement = co.createStatement();
        resultSet = statement.executeQuery("SELECT COUNT(id) AS 'count' FROM Equipment");
        int countEq = resultSet.getInt("count");
        // Добавляем новое оборудование в таблицу Equipment
        PreparedStatement request3 = co.prepareStatement("INSERT INTO Equipment (name, Well_id) VALUES (?, ?)");
        // используем цикл, чтобы название оборудовния было индивидуальным и было записано equipmentCount кол-во раз
        for (int i = 1; i <= equipmentCount; i++) {
            String eqName = "EQ" + String.format("%04d", countEq + i);
            request3.setString(1, eqName);
            request3.setInt(2, wellId);
            request3.executeUpdate();
        }
        System.out.println("Создано " + equipmentCount + " оборудования на скважине " + wellName);

        resultSet.close();
        statement.close();
        request3.close();
        request.close();
    }

    // Вывод с таблиц: название скважины и кол-во оборудования в ней
    public static void readFromTable(String[] wellsName) throws SQLException {
        PreparedStatement findId = co.prepareStatement("SELECT id FROM Well WHERE name = ?");
        PreparedStatement countEquip = co.prepareStatement("SELECT COUNT(id) AS 'count' FROM Equipment WHERE Well_id = ?");

        // цикл for each для скважин
        for (String well : wellsName) {
            // Вычисляем id в таблице Well для скважины wellName
            findId.setString(1, well);
            ResultSet resultSet = findId.executeQuery();
            int wellId = 0;
            while (resultSet.next())
                wellId = resultSet.getInt("id");
            // Считаем кол-во оборудования на скважине из таблицы Equipment по Well_id
            countEquip.setInt(1, wellId);
            resultSet = countEquip.executeQuery();
            while (resultSet.next()) {
                int count = resultSet.getInt("count");
                System.out.println("Скважина \"" + well + "\" имеет " + count + " оборудования.");
            }
            resultSet.close();
        }
        findId.close();
        countEquip.close();
    }

    // Создаем файл XML
    public static void createFile(String fileName) throws SQLException, IOException {
        // Извлекаем id, name с таблицы Equipment по каждой скважине Well_id
        Statement statement = co.createStatement();
        PreparedStatement preparedStatement = co.prepareStatement("SELECT id, name FROM Equipment WHERE Well_id = ?");

        DbInfo dbInfo = new DbInfo();
        List<Well> wellList = new ArrayList<>();
        // вызов XStream, который кодирует в xml файл
        XStream xStream = new XStream();
        xStream.autodetectAnnotations(true);

        // извлекаем все поля из таблицы Well и заполняем объекты Well и Equipment
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Well");

        while (resultSet.next()) {
            // извлекаем id и название оборудования
            int wellId = resultSet.getInt("id");
            String wellName = resultSet.getString("name");
            // Создаём новую скважину well с текущими id и именем
            Well well = new Well();
            well.setWelId(wellId);
            well.setWellName(wellName);

            // Находим всё оборудование из таблицы Equipment, которое установлено на скважине well
            preparedStatement.setInt(1, wellId);
            ResultSet resultSet1 = preparedStatement.executeQuery();
            // Создаём список оборудования, где будем сохранять их для каждой скважины well
            List<Equipment> equipmentList = new ArrayList<>();

            while (resultSet1.next()) {
                // Извлекаем id и имя оборудования
                int idEquipment = resultSet1.getInt("id");
                String nameEquipment = resultSet1.getString("name");

                // Создаём новое оборудование с текущими idEquipment и именем nameEquipment
                Equipment equipment = new Equipment();
                equipment.setEquipmentId(idEquipment);
                equipment.setEquipmentName(nameEquipment);
                // Добавляем текущее оборудование в список
                equipmentList.add(equipment);
            }
            // Добавляем полученный список оборудования в текущий объект well
            well.setEquipmentList(equipmentList);
            // Добавляем текущий объект well в список скважин
            wellList.add(well);
            resultSet1.close();
        }
        // Добавляем список скважин в объект класса dbInfo
        dbInfo.setWellList(wellList);
        // Сериализуем объект класса dbInfo в XML строку
        String xml = xStream.toXML(dbInfo);
        // Сохраняем строку xml в файл XML с именем xmlFileName
        Path pathXmlFile = Paths.get("" + fileName + ".xml");
        Files.write(pathXmlFile, xml.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        System.out.println("Файл " + fileName + ".xml успешно создан.\n");

        statement.close();
        preparedStatement.close();
        resultSet.close();
    }

    public static void closeDB() throws SQLException
    {
        co.close();
    }
}
