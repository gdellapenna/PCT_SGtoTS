package org.dellapenna.research.grammars.algorithm.sg2yacc.old;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGrammar;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.json.JSONObject;

/**
 *
 * @author Andrea
 */
public class _Example_SG2Y {

//    public static void main(String[] args) {
//        try {
//
//            FileReader f;
//            f = new FileReader("sggrammar.json");
//
//            BufferedReader b;
//            b = new BufferedReader(f);
//
//            //Inseriamo il file nella stringa che poi trasformiamo in JSON.
//            String s = "";
//            String line = b.readLine();
//            while (line != null) {
//                s = s + line;
//                line = b.readLine();
//            }
//
//            JSONObject jsonObj = new JSONObject(s);
//
//            //Dal JSON otteniamo la grammatica con il metodo statico fromJson.
//            SGGrammar sg = SGGrammar.fromJson(jsonObj);
//
//            System.out.println("Grammatica SG di input: ");
//            System.out.println(sg);
//
//            System.out.println("_______________Normalize_________________");
//            SGGrammar sg_norm = Normalize.execute(sg);
//            System.out.println(sg_norm);
//
//            HashMap<Nonterminal, List<Relation>> relprecede = RelPrecede.execute(sg_norm);
//            System.out.println("** RelPrecede: ");
//            System.out.println(relprecede);
//            System.out.println();
//
//            System.out.println("_______________SGtoG_________________");
//            CFGrammar sg_norm_cfg = SGtoG.execute(sg_norm, relprecede);
//            System.out.println(sg_norm_cfg);
//
//            HashMap<Nonterminal, List<CombinedSymbol>> relfollow = RelFollow.execute(sg_norm_cfg);
//            System.out.println("** RelFollow: ");
//            System.out.println(relfollow);
//            System.out.println();
//
//            HashMap<Nonterminal, List<Terminal>> relinside = RelInside.execute(sg_norm_cfg);
//            System.out.println("** RelInside: ");
//            System.out.println(relinside);
//            System.out.println();
//
//            System.out.println("_______________GtoTS_________________");
//            CFGrammar sg_tschema = GtoTS.execute(sg_norm_cfg, relinside, relfollow);
//            System.out.println(sg_tschema);
//
//            System.out.println("Esempio di sentenza:");
//            System.out.println(sg_tschema.getRandomSentence());
//
//        } catch (GrammarException ex) {
//            Logger.getLogger(_Example_SG2Y.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FileNotFoundException ex) {
//            System.out.println("non trovato");
//        } catch (IOException ex) {
//            System.out.println("errore io");
//        }
//    }
}
