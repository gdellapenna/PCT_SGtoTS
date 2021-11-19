package org.dellapenna.research.grammars.algorithm.sg2yacc.old;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashSet;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.sg.SGProduction;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Symbol;

/**
 *
 * @author Andrea
 */
public class _RelPrecede {
       
    public static HashMap<Nonterminal, List<Relation>> execute(SGGrammar sg) throws GrammarException{

        //Restituiremo una mappa che lega terminali ai propri insiemi execute.
        HashMap<Nonterminal, List<Relation>> map = new HashMap<Nonterminal, List<Relation>>();
       
          Symbol[] nonterminals = sg.getSymbols("NT");
        //Per ogni nonterminale, costruiamo la sua execute.
        
        for(Symbol nt : nonterminals){
            
            map.put((Nonterminal)nt,singleRelPrecede(sg,nt));

            } 
        
//        System.out.println("_____________");
//        System.out.println("La mappa è " + map);
//        System.out.println("_____________");
        return map;
        
        
    }
    
    
    private static List<Relation> singleRelPrecede(SGGrammar sg, Symbol nt) throws GrammarException{
        
      
        List<SGProduction> productions = (List<SGProduction>) sg.getProductions();
        Symbol start = sg.getStartSymbol();
        List<Relation> listsp = new ArrayList<Relation>();
        
        //Come da algoritmo, per lo start symbol inseriamo tanti Sp quante sono le produzioni con esso in testa.
            if (nt.equals(start)){
                
                int count = 0, i = 0;
                
                //Contiamo le produzioni con start in testa.
                for (SGProduction sgp : productions){
                    
                    if(sgp.getLHSNonterminal().equals(start)){
                        
                        count++;
                        
                    }
                }
                    
                //Per ogni produzione di questo tipo, aggiungiamo un Sp.    
                 while ( i < count ){
                     
                     AlphaRelation sp = AlphaRelation.create("Sp"+i);
                     listsp.add(sp);
                     i++;
                     
                 }
     
            }
            
        
            else{
                int i = 0;
                
            //Controlliamo le produzioni dove quel non-terminale appare a destra.
            for (SGProduction sgp : productions){
                
                i=0;
                
                if(sgp.getRHS().contains(nt)){
                    
                    
                    while(i < sgp.getRHS().size()){

                        //Se il simbolo si trova in prima posizione, come da algorimo, inseriamo in listsp tutto quello
                        //che c'è nella Relprecede del simbolo a sinistra.
                        if(sgp.getRHS().get(i).equals(nt) && i== 0){
                            
                            listsp.addAll(singleRelPrecede(sg,sgp.getLHSNonterminal()));
                            
                        }
                        
                        //Altrimenti aggiungiamo semplicemente la relazione che si trova appena prima. 
                        if(sgp.getRHS().get(i).equals(nt) && i>0){
                            
                            listsp.add((Relation) sgp.getRHS().get(i-1));
                            
                        }
                        i++;
                        
                    }
                }
                
            }
        }
       
            
    //Facciamo questo passaggio al solo scopo di eliminare eventuali duplicati nelle Relprecede.        
    LinkedHashSet<Relation> hashSet = new LinkedHashSet<>(listsp);
         
    ArrayList<Relation> listNoDup = new ArrayList<>(hashSet);
         
     return listNoDup;     
            
    }
    
       /* public static Boolean checkIfRecursive(SGGrammar sg){
        
        List<SGProduction> prods = (List<SGProduction>) sg.getProductions();
        List<SGProduction> prods2 = (List<SGProduction>) sg.getProductions();
        
        for (Symbol n : sg.getSymbols("NT")){
            
            List<Nonterminal> ntadestra = new ArrayList<>();
            
            for(SGProduction sgprod : sg.getProductionsByNonterminal((Nonterminal) n)){
                
                for(Symbol s : sgprod.getRHS()){
                    
                    if(s instanceof Nonterminal && !(ntadestra.contains(s))){
                        
                        ntadestra.add((Nonterminal) s);
                        
                    }
                    
                }
                
            }
            
            for (Nonterminal nt : ntadestra){
                
                for(SGProduction prodsg : sg.getProductionsByNonterminal(nt)){
                    
                    for(Symbol simbolo : prodsg.getRHS()){
                        
                        if(simbolo.equals(n)){
                            return true;
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
        
        return false;
        /* for (SGProduction prod : prods){
            
            if(prod.getRHS().get(0) instanceof Nonterminal){
                
            Nonterminal A = prod.getLHSNonterminal();
            Nonterminal B = (Nonterminal) prod.getRHS().get(0);
            
                for (SGProduction prod2 : prods2){
                    
                    if(prod2.getLHSNonterminal().equals(B) && prod2.getRHS().get(0).equals(A)){
                        return true;
                    }    

                }

            }
        } 

    } */
    
        public static boolean checkIfRecursive(SGGrammar sg) throws GrammarException{
            
         
            Boolean rec = false;
            List<SGProduction> prods = (List<SGProduction>) sg.getProductions();
            List<SGProduction> sprods = (List<SGProduction>)sg.getProductionsByLHS(sg.getStartSymbol());
            for(int i=0; i<sprods.size(); i++){
                prods.add(i, sprods.get(i));
            }
     
            for(SGProduction prod : (List<SGProduction>)sg.getProductionsByLHS(sg.getStartSymbol())){
                
                List<Nonterminal> list = new ArrayList<>();
                if(checkIfRecursiveProd(sg,prod,list)){
                    rec = true;
                }
                
            }
            return rec;
        }
        
        private static boolean checkIfRecursiveProd(SGGrammar sg,SGProduction prod, List<Nonterminal> list){

  
            if(prod.getRHS().contains(prod.getLHSNonterminal())){
                return true;
            }
            
            for(Symbol s: prod.getRHS()){
                if (s instanceof Nonterminal){
                    
                    if(list.contains((Nonterminal)s)){
                        
                        return true;
                    }
                    else{
                        list.add((Nonterminal)s);
                        for(SGProduction sgprod : (List<SGProduction>) sg.getProductionsByLHS((Nonterminal)s)){
                            return checkIfRecursiveProd(sg, sgprod, list);
                        }
                    }
                    
                }
            }
            
            return false;
            
        }
        
       
    
}
