package org.dellapenna.research.grammars.examples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.algorithm.sg2yacc.GrammarToSchema;
import org.dellapenna.research.grammars.algorithm.sg2yacc.Normalize;
import org.dellapenna.research.grammars.algorithm.sg2yacc.RelFollow;
import org.dellapenna.research.grammars.algorithm.sg2yacc.RelInside;
import org.dellapenna.research.grammars.algorithm.sg2yacc.RelPrecede;
import org.dellapenna.research.grammars.algorithm.sg2yacc.SpatialToString;
import org.dellapenna.research.grammars.cf.CFGrammar;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.json.JSONObject;

/**
 *
 * @author Andrea
 */
public class Example_SG2Y {

    public static void main(String[] args) {
        try {

            FileReader f;
            f = new FileReader("grammars\\sggrammar_signs.json");

            BufferedReader b;
            b = new BufferedReader(f);

            String s = "";
            String line = b.readLine();
            while (line != null) {
                s = s + line;
                line = b.readLine();
            }

            JSONObject jsonObj = new JSONObject(s);

            SGGrammar sg = SGGrammar.fromJson(jsonObj);

            System.out.println("Input spatial grammar: ");
            System.out.println(sg);

            System.out.println("_______________Normalize_________________");
            SGGrammar sg_norm = Normalize.getInstance().apply(sg);
            System.out.println(sg_norm);

            HashMap<Nonterminal, List<Relation>> relprecede = RelPrecede.getInstance().apply(sg_norm);
            System.out.println("** RelPrecede: ");
            System.out.println(relprecede);
            System.out.println();

            System.out.println("_______________Spatial to String Grammar_________________");
            CFGrammar sg_norm_cfg = SpatialToString.getInstance().apply(sg_norm, relprecede);
            System.out.println(sg_norm_cfg);

            HashMap<Nonterminal, List<Relation>> relfollow = RelFollow.getInstance().apply(sg_norm_cfg);
            System.out.println("** RelFollow: ");
            System.out.println(relfollow);
            System.out.println();

            HashMap<Nonterminal, List<Terminal>> relinside = RelInside.getInstance().apply(sg_norm_cfg);
            System.out.println("** RelInside: ");
            System.out.println(relinside);
            System.out.println();

            
            System.out.println("_______________String Grammar to Translation Scheme_________________");
            CFGrammar sg_tschema = GrammarToSchema.getInstance().apply(sg_norm_cfg, relinside, relfollow);
            
            System.out.println();            
            System.out.println("Output translation scheme: ");            
            System.out.println(sg_tschema);

        } catch (GrammarException ex) {
            Logger.getLogger(Example_SG2Y.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            System.out.println("non trovato");
        } catch (IOException ex) {
            System.out.println("errore io");
        }
    }
}
