import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import zemberek.langid.LanguageIdentifier;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.normalization.TurkishSentenceNormalizer;
import zemberek.normalization.TurkishSpellChecker;
import zemberek.tokenization.Token;
import zemberek.tokenization.TurkishTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zeyne
 */
public class Dokuman {

    public Dokuman() throws FileNotFoundException, IOException{
    }
    
    private String sınıf;
    static Path lookupRoot = Paths.get("src/main/java/normalization");
    static Path lmFile = Paths.get("src/main/java/lm/lm.2gram.slm");
    static  TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
    static TurkishSentenceNormalizer normalizer = new TurkishSentenceNormalizer(morphology, lookupRoot, lmFile);
    TurkishSpellChecker spellChecker = new TurkishSpellChecker(morphology);
    List<String> olumlu=new ArrayList<String>();
    List<String> olumsuz=new ArrayList<String>();
    List<String> notr=new ArrayList<String>();
    double dokumanSayisi=0.0,olumluDokumanSayisi=0.0,olumsuzDokumanSayisi=0.0,notrDokumanSayisi=0.0;
    public void dosyalariCek() throws FileNotFoundException, IOException{ //Normalize edilmiş halde dosyaları çekme
   
     File dir = new File("src/main/java/metinler");
     
     File[] folder = dir.listFiles(); //Cümleleri çekme kısmı
        for(File table : folder) {
                    sınıf=table.getName();
            //System.out.println(table);
            File[] filenames = table.listFiles();
                for (File file : filenames) {
                    if(file.getParentFile().getName().equals("Olumlu")){ 
                       Scanner scanner = new Scanner(file); 
                       StringBuffer buffer=new StringBuffer();
			while (scanner.hasNextLine()) {
                             buffer.append(scanner.nextLine().replaceAll("\\d","").replaceAll("\\p{Punct}", "").toLowerCase(new Locale("tr-TR")));
                               }
                        olumlu.add(normalizer.normalize(new String(buffer)));
                    }
                    else if (file.getParentFile().getName().equals("Olumsuz")) {
                        Scanner scanner = new Scanner(file); 
                        StringBuffer buffer=new StringBuffer();
			while (scanner.hasNextLine()) {
                             buffer.append(scanner.nextLine().replaceAll("\\d","").replaceAll("\\p{Punct}", "").toLowerCase(new Locale("tr-TR")));
                               }
                        olumsuz.add(normalizer.normalize(new String(buffer)));
                    }
                    else if (file.getParentFile().getName().equals("Nötr")) { 
                       Scanner scanner = new Scanner(file); 
                       StringBuffer buffer=new StringBuffer();
			while (scanner.hasNextLine()) {
                             buffer.append(scanner.nextLine().replaceAll("\\d","").replaceAll("\\p{Punct}", "").toLowerCase(new Locale("tr-TR")));
                               }
                        notr.add(normalizer.normalize(new String(buffer)));
                    }
            }
    }
        olumluDokumanSayisi=olumlu.size();
        olumsuzDokumanSayisi=olumsuz.size();
        notrDokumanSayisi=notr.size();
        dokumanSayisi=olumlu.size()+olumsuz.size()+notr.size();
    }
    public static String normalize(String kelime){
    kelime=normalizer.normalize(kelime);
        return kelime;
    }
    List<String> stopWord = new ArrayList<String>();
     public void readStopWords() throws FileNotFoundException{
     BufferedReader reader;//stopwordsleri okuma
     File file = new File("src/main/java/stop-words-turkish-github.txt");
     reader = new BufferedReader(new FileReader(file));
            Scanner scanner = new Scanner(file); 
		while (scanner.hasNextLine()) {
                    stopWord.add(scanner.nextLine().toLowerCase());
                }
               
        }
     static TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;
     LanguageIdentifier lid = LanguageIdentifier.fromInternalModelGroup("tr_group");
     public List<String> tokenize(String cumle){
         List<String> tokens=new ArrayList<String>();
         Iterator<Token> tokenIterator = tokenizer.getTokenIterator(cumle);
    while (tokenIterator.hasNext()) {
        tokens.add(tokenIterator.next().getText());
    }
    tokens.removeAll(stopWord);
    Dokuman.removeDuplicate(tokens);
    return tokens;
     }
     
     Map<String, Map<String,Integer>> olumluEgitimVerisi = new HashMap<>();
     Map<String, Map<String,Integer>> olumsuzEgitimVerisi = new HashMap<>();
     Map<String, Map<String,Integer>> notrEgitimVerisi = new HashMap<>();
    
    
     public Map<String,Integer> getTF(String cumle){//cümlede geçen kelime kökünün kaç kere bulunduğu 
     Map<String,Integer> tF=new HashMap<>();
     List<String> stems=new ArrayList<String>();
     for(String b:tokenize(cumle)){
     WordAnalysis result = morphology.analyze(b);
        for (SingleAnalysis analysis : result) {
            if(!stems.contains(analysis.getStem())){
            stems.add(analysis.getStem());
            }
     }
     }
         for(String c:stems) {
           int count=(cumle.split(c,-1).length)-1; //https://www.javacodeexamples.com/java-count-occurrences-of-substring-in-string-example/724
           tF.put(c, count);
         }
         return tF;
     }
     
     public void egitimVerileri(){
      for(String cumle:olumlu){
          olumluEgitimVerisi.put(cumle, getTF(cumle));
      }
      for(String cumle:olumsuz){
          olumsuzEgitimVerisi.put(cumle, getTF(cumle));
      }
      for(String cumle:notr){
          notrEgitimVerisi.put(cumle, getTF(cumle));
      }
      for (Map<String,Integer> entry : olumluEgitimVerisi.values()) {
          for(Integer in:entry.values()){
                 olumluToplamKelimeSayısı+=in;
             }
      }
       for (Map<String,Integer> entry : olumsuzEgitimVerisi.values()) {
           for(Integer in:entry.values()){
                 olumsuzToplamKelimeSayısı+=in;
             }
      }
        for (Map<String,Integer> entry : notrEgitimVerisi.values()) {
          for(Integer in:entry.values()){
                 notrToplamKelimeSayısı+=in;
             }
      }
     }
     public double olumluSınıf=0.0,olumsuzSınıf=0.0,notrSınıf=0.0;
     Integer olumluToplamKelimeSayısı=0,notrToplamKelimeSayısı=0,olumsuzToplamKelimeSayısı=0;
     public void getIDF(String kelime){
         olumluSınıf=0.0;olumsuzSınıf=0.0;notrSınıf=0.0;
         for (Map<String,Integer> entry : olumluEgitimVerisi.values()) {
                if(entry.containsKey(kelime)){
              olumluSınıf+=entry.get(kelime);
             }}
         for (Map<String,Integer> entry : olumsuzEgitimVerisi.values()) {
                if(entry.containsKey(kelime)){
              olumsuzSınıf+=entry.get(kelime);
            
             }}
         for (Map<String,Integer> entry : notrEgitimVerisi.values()) {
                if(entry.containsKey(kelime)){
              notrSınıf+=entry.get(kelime);
             }}
         
         }
     public static <T> void removeDuplicate(List <T> list) {
Set <T> set = new HashSet <T>();
List <T> newList = new ArrayList <T>();
for (Iterator <T>iter = list.iterator();    iter.hasNext(); ) {
   Object element = iter.next();
   if (set.add((T) element))
      newList.add((T) element);
   }
   list.clear();
   list.addAll(newList);
} //https://stackoverflow.com/questions/203984/how-do-i-remove-repeated-elements-from-arraylist?page=2&tab=votes#tab-top
     
     }
     
      
     


