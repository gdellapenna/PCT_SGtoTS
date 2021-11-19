package org.dellapenna.research.grammars.sg2yacc.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGGrammar;
import org.dellapenna.research.grammars.cf.CFGProduction;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;


/**
 *
 * @author Andrea
 */
public class _RelFollow {
    
    public static HashMap<Nonterminal, List<CombinedSymbol>> execute(CFGGrammar g) throws GrammarException{
        
        //Restituiremo una mappa che lega terminali ai propri insiemi relfollow.
        HashMap<Nonterminal, List<CombinedSymbol>> map = new HashMap<Nonterminal, List<CombinedSymbol>>();
        

        //Troviamo i nonterminali. 
        Symbol[] arrnt = g.getSymbols("RNT");
        List<Symbol> nonterminals = Arrays.asList(arrnt);
        
        //Per ogni nonterminale costruiamo la RFollow
        
        for (Symbol s : nonterminals){
            
            map.put((Nonterminal) s, SingleRelFollow((Nonterminal) s,g));
            
        }
          
        return map;
    }
    
    
    
    private static List<CombinedSymbol> SingleRelFollow(Nonterminal n, CFGGrammar g) throws GrammarException{
        
        List<CombinedSymbol> rellist = new ArrayList<>();
        
        //REGOLA 1: Se è uno Start Symbol, va aggiunto solo ().
        if(g.getStartSymbols().contains(n)){
            
            AlphaRelation r =  AlphaRelation.create("()");
            CombinedSymbol cs = new CombinedSymbol(r,null);
            rellist.add(cs);
            return rellist;
        }
        
        for (CFGProduction production : (List<CFGProduction>) g.getProductions()){
            
            List<Symbol> RightSide = production.getRHS();
            int iteratore = 0;
            
            
            while( iteratore < RightSide.size()){
                
                //Cerchiamo il simbolo.
                if ( RightSide.get(iteratore).equals(n)){
                    
                    //Controlliamo che non sia l'ultimo. In questo caso, REGOLA 2
                    if(iteratore < RightSide.size()-1){
                        
                        //rellist.add( (Relation) ((CombinedSymbol) RightSide.get(iteratore+1)).getSymbol(0));
                        //Prendiamo la parte a destra del simbolo trovato.
                        List<Symbol> remaining = new ArrayList<>();
                        remaining.addAll( RightSide.subList(iteratore,RightSide.size()) );
                        int secondoit = 0;
                        Boolean found = false;
                        
                        for(Symbol s : remaining ){
                            
                            CombinedSymbol cs = (CombinedSymbol) remaining.get(secondoit);
                            AlphaRelation rel = (AlphaRelation) cs.getSymbol(0);
                            //Se non ne abbiamo trovato nessuno applichiamo la regola 2A
                            
                            if(rel.getIndex() == secondoit){
                                
                                rellist.add(cs);
                                found = true;
                                
                            }
                            
                            secondoit++;
                            
                        }
                        
                        if(!found){
                            
                            rellist.addAll(SingleRelFollow(production.getLHSNonterminal(),g));
                            
                        }
                        
                    }
                    //Altrimenti regola 3
                    else{
                        rellist.addAll(SingleRelFollow(production.getLHSNonterminal(),g));
                    }
                }
                
                iteratore++;
            }
            
        }
     
        return rellist;
    }
    

}
