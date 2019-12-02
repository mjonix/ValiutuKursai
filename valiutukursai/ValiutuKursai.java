/**
* @Programos tikslas - gauti interneto puslapio lb.lt HTML kodą bei atrinkti iš jo reikiamą
* @informaciją apie įvairių užsienio valiutų santykius su euru bei šių santykių pokyčius
* @naudotojo pasirinktu laikotarpiu.
* 
* @autorius Marius Jonikas
*/
package valiutukursai;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ValiutuKursai {

    private String dataNuo, dataIki;

    ValiutuKursai() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String htmlKodas, valiutosKodas = "";
        Document dokumentas;

        System.out.println("Jei norite gauti informaciją apie valiutą konkrečios dienos metu, įveskite '1', o "
                + "jeigu norite stebėti valiutos kurso pokytį tam tikru laikotarpiu, įveskite '2'.");

        String pasirinkimas = scanner.nextLine();

        while (!pasirinkimas.equals("1") && !pasirinkimas.equals("2")) {
            System.out.println("Klaida! Įveskite '1' (konkrečiai datai) arba '2' (periodui).");
            pasirinkimas = scanner.nextLine();
        }

        try {
            if (pasirinkimas.equals("1")) {
                while (true) {
                    System.out.println("Data, kurios informaciją norite gauti:");
                    dataNuo = scanner.nextLine();
                    if (atitinkaDatosFormatas(dataNuo, dataNuo)) {
                        break;
                    }
                }
                System.out.println("Prašome įvesti valiutos, kurios informaciją norite gauti, kodą:");
                valiutosKodas = scanner.nextLine().toUpperCase();
                System.out.println("\nPrašome palaukti...\n");
                dokumentas = Jsoup.connect("https://www.lb.lt/lt/kasdien-skelbiami-euro-ir-uzsienio-valiutu-santykiai-skelbia-europos-centrinis-bankas?class=Eu&type=day&selected_curr=&date_day="
                        + dataNuo).get();

                htmlKodas = dokumentas.toString().split("title=\"" + valiutosKodas + "\">")[1];

                String santykis = htmlKodas.substring(htmlKodas.indexOf("<span>") + 6)
                        .substring(0, htmlKodas.substring(htmlKodas.indexOf("<span>") + 6).indexOf("<"));
                String pokytisVnt = htmlKodas.substring(htmlKodas.indexOf("<td align=\"right\">") + 19)
                        .substring(0, htmlKodas.substring(htmlKodas.indexOf("<td align=\"right\">") + 19).indexOf(" "));
                String pokytisProcentinis = htmlKodas.substring(htmlKodas.indexOf("%") - 8)
                        .substring(0, htmlKodas.substring(htmlKodas.indexOf("%") - 8).indexOf(" <"));

                System.out.println("Valiutos " + valiutosKodas + " informacija (" + dataNuo + "):\n");
                System.out.println("Santykis: " + santykis);
                System.out.println("\nPokyčiai (lyginant su diena prieš):\n");
                System.out.println("Pokytis vnt.: " + pokytisVnt);
                System.out.println("Pokytis %: " + pokytisProcentinis);
                
                return;
            }

            while (true) {
                System.out.println("Data, nuo kurios norite stebėti valiutos pokytį:");
                dataNuo = scanner.nextLine();
                if (atitinkaDatosFormatas(dataNuo, dataNuo)) {
                    while (true) {
                        System.out.println("Data, iki kurios norite stebėti valiutos pokytį:");
                        dataIki = scanner.nextLine();
                        if (atitinkaDatosFormatas(dataNuo, dataIki)) {
                            break;
                        }
                    }
                    break;
                }
            }
            System.out.println("Prašome įvesti valiutos, kurios kursą norite stebėti, kodą:");
            valiutosKodas = scanner.nextLine().toUpperCase();
            System.out.println("\nPrašome palaukti...\n");
            dokumentas = Jsoup.connect("https://www.lb.lt/lt/kasdien-skelbiami-euro-ir-uzsienio-valiutu-santykiai-skelbia-europos-centrinis-bankas?class=Eu&type=day&selected_curr=&date_day="
                    + dataNuo).get();

            htmlKodas = dokumentas.toString().split("title=\"" + valiutosKodas + "\">")[1];
            double pradinisSantykis = Double.parseDouble(htmlKodas.substring(htmlKodas.indexOf("<span>") + 6)
                    .substring(0, htmlKodas.substring(htmlKodas.indexOf("<span>") + 6).indexOf("<")).replace(",", "."));

            dokumentas = Jsoup.connect("https://www.lb.lt/lt/kasdien-skelbiami-euro-ir-uzsienio-valiutu-santykiai-skelbia-europos-centrinis-bankas?class=Eu&type=day&selected_curr=&date_day="
                    + dataIki).get();

            htmlKodas = dokumentas.toString().split("title=\"" + valiutosKodas + "\">")[1];
            double galutinisSantykis = Double.parseDouble(htmlKodas.substring(htmlKodas.indexOf("<span>") + 6)
                    .substring(0, htmlKodas.substring(htmlKodas.indexOf("<span>") + 6).indexOf("<")).replace(",", "."));

            System.out.println("Valiutos (" + valiutosKodas + ") santykis " + dataNuo + ": " + pradinisSantykis);
            System.out.println("Valiutos (" + valiutosKodas + ") santykis " + dataIki + ": " + galutinisSantykis);
            System.out.println("Pokytis vnt.: " + suapvalinti((galutinisSantykis - pradinisSantykis), 5));
            System.out.println("Pokytis %: " + suapvalinti(((galutinisSantykis - pradinisSantykis) / pradinisSantykis) * 100, 4) + " %");
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Klaida! Valiutos su kodu '" + valiutosKodas + "' rasti nepavyko.");
        }
    }
    
    /**
    * @Metodas, skirtas patikrinti, ar naudotojas įvedė tinkamas datas. Jeigu reikia patikrinti ne
    * @periodą, o vieną, konkrečią datą, metodas vistiek grąžina tesingą rezultatą (paduodant tą
    * @pačią datą kaip abu parametrus).
    */
    private boolean atitinkaDatosFormatas(String nuo, String iki) {
        DateFormat formatas;
        Date data;

        try {
            formatas = new SimpleDateFormat("yyyy-MM-dd");
            
            /**
            * @Užtikrinama, jog datos būtų pateikiamos tinkamu formatu nuorodos sudarymui. Pavyzdžiui,
            * @data '2020-1-9' atitinka JAVA datos formatavimo reikalavimus, tačiau panaudojus tokią
            * @įvestį nuorodoje į lb.lt svetainę, nebūtų gautas norimas rezultatas. Vietoje to, turi
            * @būti pateikiama '2020-01-09'.
            */
            dataNuo = formatas.format(formatas.parse(nuo));
            dataIki = formatas.format(formatas.parse(iki));
            data = formatas.parse(iki);
            if (data.before(formatas.parse("2014-09-30"))) {
                System.out.println("Klaida! Kasdien skelbiamų euro ir užsienio valiutų santykių įrašai duomenų bazėje yra nuo 2014 m. rugsėjo 30 d.");
                return false;
            }
            if (data.before(formatas.parse(nuo))) {
                System.out.println("Klaida! Periodo pabaigos data negali būti ankstesnė už pradžios datą.");
                return false;
            }
            if (data.after(Calendar.getInstance().getTime())) {
                System.out.println("Klaida! Negalima įvesti vėlesnės negu šiandiena datos.");
                return false;
            }
        } catch (ParseException ex) {
            System.out.println("Klaida! Prašome įvesti datą formatu 'YYYY-MM-DD'.");
            return false;
        }
        return true;
    }

    private double suapvalinti(double skaicius, int skaitmenysPoKablelio) {
        long daugiklis = (long) Math.pow(10, skaitmenysPoKablelio);
        skaicius = skaicius * daugiklis;
        return (double) Math.round(skaicius) / daugiklis;
    }

    public static void main(String[] args) throws IOException {
        new ValiutuKursai();
    }
}
