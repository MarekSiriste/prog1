import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

/**
 * Zaznam hash tabulky s nahledy obrazku
 */
class Entry{
    /** Nazev souboru s obrazkem */
    public String fileName;
    /** Index v databazi nahledu obrazku */
    int indexInDatabase;
    /** Dalsi zaznam (pro potreby hash tabulky) */
    public Entry next;

    /**
     * Vyvtori novy zaznam se zadanym nazvem souboru a indexem do databaze
     * @param fn nazev souboru s obrazkem
     * @param index index v databazi nahledu obrazku
     */
    public Entry (String fn, int index) {
        fileName = fn;
        indexInDatabase = index;
    }
}

/**
 * Hash tabulka pro uchovani zaznamu s nahledem obrazku
 */
class HashTable {
    /** Zaznamy tabulky */
    Entry[] data;

    /**
     * Vytvori novou hash tabulku se zadanou kapacitou
     * @param capacity kapacita tabulky
     */
    public HashTable(int capacity) {
        this.data=new Entry[capacity];//Chybelo vytvoreni tabulky o dane kapacite
    }

    /**
     * Prida zaznam do has tabulky (spatnym zpusobem)
     * @param key klic - nazev souboru s obrazkem
     * @param value hodnota - index v databazi nahledu obrazku
     */
    public void add_bad(String key, int value) {
        Entry newEntry = new Entry(key, value);
        int index = getHashCode(key);
        newEntry.next = data[index];//CHYBA - obracene
        data[index] = newEntry;
    }

    /**
     * Vypocte a vrati hash kod pro zadany klic
     * @param s klic, pro ktery se ma vypocitat hash kod
     * @return hash kod pro zadany klic
     */
    int getHashCode(String s){
        return 0;
    }
    /**
     * Prida zaznam do has tabulky (lepsim zpusobem)
     * @param key klic - nazev souboru s obrazkem
     * @param value hodnota - index v databazi nahledu obrazku
     */
    public void add_good(String key, int value) {
        Entry newEntry = new Entry(key, value);
        int index = Hash(key);
        newEntry.next = data[index];//CHYBA - obracene
        data[index] = newEntry;
    }
    /**
     * Vypocte a vrati hash kod pro zadany klic
     * @param s klic, pro ktery se ma vypocitat hash kod
     * @return hash kod pro zadany klic
     */
    int Hash(String s){
        int rozsah=256;//rozsah klicu
        int m=data.length;
       /* int[] a=new int[s.length()];
        for(int i=0;i<s.length();i++){//prevede retezec do ascii kodu
            a[i]= (s.charAt(i));
        }*/
        //int r=a[0];
        //Funkce Hash byla na mem puvodnim zpracovani casove neefektivn, tak jsem ji opravil
        int r=s.charAt(0);
        for(int j=1;j<s.length();j++){
            r=(r*rozsah+s.charAt(j))%m;
        }
        return r;
    }
    /**
     * Pro zadane jmemo souboru zjisti, zda byl pro nej vytvoren nahled a vrati hodnotu indexInDatabase - pro spatnou rozptyl funkci
     * @param key klic, ktery hledame
     * @return hodnotu schovanou pod klicem, pokud klic nebude nalezen, vrati -1
     */
    int get_bad(String key){
        int hash=getHashCode(key);
        Entry current=data [hash];
        while(current!=null){
            if(current.fileName.equals(key))
                return current.indexInDatabase;
            current=current.next;
        }
        return -1;
    }
    /**
     * Pro zadane jmemo souboru zjisti, zda byl pro nej vytvoren nahled a vrati hodnotu indexInDatabase - pro spravnou rozptyl funkci
     * @param key klic, ktery hledame
     * @return hodnotu schovanou pod klicem, pokud klic nebude nalezen, vrati -1
     */
    int get_good(String key){
        int hash=Hash(key);
        Entry current=data [hash];
        while(current!=null){
            if(current.fileName.equals(key))
                return current.indexInDatabase;
            current=current.next;
        }
        return -1;
    }


    int Delka (int index){
        Entry current=data[index];
        int pocet=0;
        while (current!=null){
            pocet++;
            current=current.next;
        }
        return pocet;
    }
}

/**
 * Hlavni trida programu
 */
public class Thumbnails {
    public static void main(String[] args) throws IOException {
        String[] pole=NacteniDat("ImageNames.txt");
        System.out.println("Počet zpracovaných dotazů za 2 sekundy, hloupá rozptylová funkce:");
        Test_bad(1000,pole);
        Test_bad(1009,pole);
        Test_bad(30030,pole);
        Test_bad(100000,pole);
        Test_bad(100003,pole);
        System.out.println("Počet zpracovaných dotazů za 2 sekundy, chytrá rozptylová funkce:");
        Test_good(1000,pole);
        Test_good(1009,pole);
        Test_good(30030,pole);
        Test_good(100000,pole);
        Test_good(100003,pole);
    }
    /**
     * Otestuje rychlost tabulky se spatnou rozpytlovou funkci
     * @param capacity delka pole
     * @param field pole klicu
     */
    public static void Test_bad(int capacity,String[] field){
        Random r = new Random();
        HashTable tabulka=new HashTable(capacity);
        int p=0;
        long start=System.nanoTime();
        long end=System.nanoTime();
        while(((double)(end-start)/1000000000.0)<2.0){
            int i= r.nextInt(field.length);
            if(tabulka.get_bad(field[i])==-1){
                tabulka.add_bad(field[i],0);
            }
            p++;
            end=System.nanoTime();
        }
        System.out.println("Pro C = "+capacity+" bylo zpracováno "+ p+" dotazů");
    }
    /**
     * Otestuje rychlost tabulky s lepsi rozpytlovou funkci
     * @param capacity delka pole
     * @param field pole klicu
     */
    public static void Test_good(int capacity,String[] field){
        Random r = new Random();
        HashTable tabulka=new HashTable(capacity);
        int p=0;
        long start=System.nanoTime();
        long end=System.nanoTime();
        while(((double)(end-start)/1000000000.0)<2.0){
            int i= r.nextInt(field.length);
            if(tabulka.get_good(field[i])==-1){
                tabulka.add_good(field[i],0);
            }
            p++;
            end=System.nanoTime();
        }
        System.out.println("Pro C = "+capacity+" bylo zpracováno "+ p+" dotazů");
    }

    /**
     * Vygeneruje a vrati nahodny nazev obrazku
     *
     * @return nahodny nazev obrazku
     */
    private static String randomImageName() {
        Random r = new Random();
        int year = 2005 + r.nextInt(13);
        int month = 1 + r.nextInt(12);
        int day = 1 + r.nextInt(28);
        int img = 1 + r.nextInt(9999);
        return String.format("c:\\fotky\\%d-%02d-%02d\\IMG_%04d.CR2", year, month, day, img);
    }

    private static String[] NacteniDat(String soubor) throws IOException {
        int delka=0;
        try (Scanner vstup = new Scanner(Paths.get(soubor))) {
            while (vstup.hasNextLine() && !(vstup.equals(""))) {
                delka++;
                vstup.nextLine();
            }
        }
        String [] pole=new String[delka];
        try (Scanner vstup1 = new Scanner(Paths.get(soubor))) {
            for(int i=0;i<pole.length;i++) {
                pole[i]=vstup1.nextLine();
            }
        }
        return pole;
    }
}