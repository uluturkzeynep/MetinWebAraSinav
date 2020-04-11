
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zeyne
 */

public class Main {
     public static void main(String[] args) throws IOException {
         Scanner scan = new Scanner(System.in,"ISO-8859-9");
         NaiveBayes nb=new NaiveBayes();
         int secim;
         int cikis=0;
         while(cikis==0){
         System.out.println("1-Duygu analizi yap"+"\n"+"2.Çıkış");
         secim=scan.nextInt();
         scan.nextLine();
         switch (secim) {
             case 1:
                 System.out.println("Analiz yapılacak kelimeyi giriniz");
                 String kelime=scan.nextLine(); 
                 //String normalize=Normalizer.normalize(kelime, Normalizer.Form.NFD);
                 System.out.println("Kelime "+kelime+" p "+nb.conditionalProbabilities(kelime)+" "+nb.classification());
                 break;
                 
             case 2:
                 cikis=1;
                 break;
             default:
                 throw new AssertionError();
         }
         }
         
        
        
    }
      
}
