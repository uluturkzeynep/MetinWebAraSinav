
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zeyne
 */
public class NaiveBayes {
//https://www.youtube.com/watch?v=OWGVQfuvNMk
    
    public NaiveBayes() throws FileNotFoundException, IOException{
    d.dosyalariCek();
    d.readStopWords();//bu projede stopwords'lerin çıkarılıp çıkarılmayacağından emin değilim çıkarılabilir.
    d.egitimVerileri();
    sozlukOlustur();
    calcPrior();
    }
    List<String> sozcukler=new ArrayList<String>();
    public void sozlukOlustur() throws IOException{ //tüm kelimelerin bir araya gelmesi
        for (Map<String,Integer> entry : d.olumluEgitimVerisi.values()) {
            for(String key:entry.keySet())
              sozcukler.add(key);
             }
        for (Map<String,Integer> entry : d.olumsuzEgitimVerisi.values()) {
            for(String key:entry.keySet())
              sozcukler.add(key);
             }
        for (Map<String,Integer> entry : d.notrEgitimVerisi.values()) {
            for(String key:entry.keySet())
              sozcukler.add(key);
             }
        d.removeDuplicate(sozcukler);
    }
    Dokuman d=new Dokuman();
    double olumluPrior=0.0,olumsuzPrior=0.0,notrPrior=0.0;
    public void calcPrior(){//Prior olasılıklarını hesaplama
       olumluPrior=d.olumluDokumanSayisi/d.dokumanSayisi;
       olumsuzPrior=d.olumsuzDokumanSayisi/d.dokumanSayisi;
       notrPrior=d.notrDokumanSayisi/d.dokumanSayisi;
        //System.out.println(olumluP);
    }
    
    double condOlumlu=0.0,condOlumsuz=0.0,condNotr=0.0;
    public double conditionalProbabilities(String kelime){//multinomal naive bayes hesabı
        condOlumlu=0.0;condOlumsuz=0.0;condNotr=0.0;
        d.getIDF(kelime); //olumluSınıf,olumsuzSınıf,notrSınıf dediğim değişkenler kelimenin o sınıflardaki bulunma sayısı
        condOlumlu=olumluPrior*((d.olumluSınıf+1)/(d.olumluToplamKelimeSayısı+sozcukler.size()));
        condOlumsuz=olumsuzPrior*((d.olumsuzSınıf+1)/(d.olumsuzToplamKelimeSayısı+sozcukler.size()));
        condNotr=notrPrior*((d.notrSınıf+1)/(d.notrToplamKelimeSayısı+sozcukler.size()));
        return max(condOlumlu, condOlumsuz,condNotr);
    }
    
    public String classification(){
        String sinif="Nötr";//hangi cond sonucu en yüksek çıkarsa atamayı ona yapması..
    if(max(condOlumlu, condOlumsuz,condNotr)==condOlumlu){
    sinif="Olumlu";
    }
    else if(max(condOlumlu, condOlumsuz,condNotr)==condOlumsuz){
    sinif="Olumsuz";
    }
        return sinif;
    }
    
    public double max(double s1,double s2,double s3)
	{
		double max=s1; 
		if (s2>max) max=s2; 
		if (s3>max) max=s3; 
		return max;
	}
    
}
