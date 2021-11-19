package org.dellapenna.research.grammars.sg2yacc.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.dellapenna.research.grammars.cf.CFGGrammar;
import org.dellapenna.research.grammars.cf.CFGProduction;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Terminal;

/**
 *
 * @author Andrea
 */
public class _RelInside {
    
    public static HashMap<Nonterminal, List<Terminal>> execute(CFGGrammar g){
     
        HashMap<Nonterminal, List<Terminal>> map = new HashMap<Nonterminal, List<Terminal>>();
        
        //Troviamo i nonterminali. 
        Symbol[] arrnt = g.getSymbols("RNT");
        List<Symbol> nonterminals = Arrays.asList(arrnt);
        
        //Per ogni nonterminale costruiamo la RelInside
        
        for (Symbol s : nonterminals){
            
            map.put((Nonterminal) s, SingleRelInside((Nonterminal) s,g));
            
        }
        
        return map;       
    }
    
    
    public static List<Terminal> SingleRelInside(Nonterminal n, CFGGrammar g){
        
        if(n instanceof Terminal){
            
             ArrayList<Terminal> list = new ArrayList<>();
             list.add((Terminal)n);
             return list;
            
        }
        
        List<Terminal> relinside = new ArrayList<>();
        //Prendiamo le produzioni che a sinistra hanno quel nonterminale.
        for(CFGProduction production : (List<CFGProduction>) g.getProductionsByNonterminal(n)){
            
            //Se non ci sono ancore   
            if(production.getAnchor().isEmpty()){

                 for(Symbol s : production.getRHS()){

                    if (s instanceof CombinedSymbol && ((CombinedSymbol)s).isTerminal() ){
                        relinside.add((Terminal) ((CombinedSymbol)s).getSymbol(1));
                    }
                    else{
                        relinside.addAll(SingleRelInside((Nonterminal) s,g));
                    } 
                 }
            }   
            
            else{
                List<Integer> anchors = production.getAnchor();
                
                for(Integer i : anchors){
                   
                    if( ((CombinedSymbol)production.getRHS().get(i-1)).isTerminal() ){
                        
                        relinside.add((Terminal) ((CombinedSymbol)production.getRHS().get(i-1)).getSymbol(1));
                        
                    }
                    else{
                        
                        relinside.addAll(SingleRelInside((Nonterminal)production.getRHS().get(i-1),g));
                        
                    }
                    
                }
                
            }
        }
        
        return relinside;
    }     
}
