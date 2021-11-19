package org.dellapenna.research.grammars.cf;

import org.dellapenna.research.grammars.symbols.Terminal;
import org.dellapenna.research.grammars.symbols.AlphaTerminal;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dellapenna.research.grammars.GrammarException;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Della Penna
 */
public class Example_Grammars_CFG {

    //ESEMPIO 1: GRAMMATICHE CF STANDARD
    public void esempio_cfg_base() {
        try {
            //CREAZIONE GRAMMATICA VIA CODICE
            /* 
            * i simboli da usare nelle produzioni devono essere generati
            * dalle factory AlphaTerminal e AlphaNonterminal
            * qui li creaimo tutti all'inizio e li assegniamo a delle variabili,
            * ma non è strettamente necessario. Chiamate successive alla stessa
            * factory per lo stesso simbolo ritornano comunque lo stesso oggetto
             */
            Nonterminal A = AlphaNonterminal.create("A");
            Nonterminal B = AlphaNonterminal.create("B");
            Nonterminal S = AlphaNonterminal.create("S");
            Terminal x = AlphaTerminal.create("x");
            Terminal y = AlphaTerminal.create("y");
            Terminal z = AlphaTerminal.create("z");

            //creiamo la grammatica
            CFGrammar g = new CFGrammar();

            //impostiamo lo start symbol
            g.setStartSymbol(S);

            //aggiungiamo le produzioni
            /*
            * il metodo addProduction(l,r1...rn) crea la produzione
            * l -> r1 ... rn
             */
            // S -> A y B
            g.addProduction(S, A, y, B);
            // A -> x A
            g.addProduction(A, x, A);
            // A -> z
            g.addProduction(A, z);
            // B -> y y
            g.addProduction(B, y, y);

            //stampa disgnostica con pretty printing
            System.out.println(g);
            /* GENERA l'output
            * S -> A y B 
            * A -> x A 
            * A -> z 
            * B -> y y 
             */

            //genera la rappresentazione JSON della grammatica, che puoi salvare 
            //su un file come fosse un normale testo, ad esempio con un fileWriter
            System.out.println(g.toJson());
            /* OUTPUT (grammatica codificata in JSON):
            * {"start":"S","productions":[{"A":["x","A"]},{"A":["z"]},{"B":["y","y"]},{"S":["A","y","B"]}],"terminals":["x","y","z"],"nonterminals":["A","B","S"]}
            * 
            * Puoi usare questo formato per definire le grammatiche SENZA bisogno 
            * di costruirle via codice come fatto all'inizio di questo esempio
            * il formato dell'oggetto JSON è 
            * {
            *   "start":"S",                        <-- nome dello start symbol
            *   "productions":[                     <-- lista produzioni
            *       {                               <-- inizio produzione
            *           "A":                        <-- simbolo sinistro
            *               ["x","A"]               <-- lista simboli lato destro
            *       },                              <-- fine produzione
            *       {"A":["z"]},
            *       {"B":["y","y"]},
            *       {"S":["A","y","B"]}
            *    ],
            *    "terminals":["x","y","z"],         <-- lista dei terminali usati nelle produzioni
            *    "nonterminals":["A","B","S"]       <-- lista dei non terminali usati nelle produzioni
            * }
             */

            System.out.println();
            //questa chiamata permette di stampare delle stringhe casuali generate
            //dalla grammatica, utili per i test...
            System.out.println(g.getRandomSentence());
            System.out.println(g.getRandomSentence());
            System.out.println(g.getRandomSentence());
            System.out.println();

            /* 
            * caricamento grammatica da stringa JSON. La stringa, ovviamente,
            * può essere letta da un file. Qui l'ho cablata nel codice.
            * attenzione alla sintassi del JSON: non ho inserito procedure
            * di controllo sintattico troppo intelligenti, quindi se il formato
            * è sbagliato può succedere di tutto!
             */
            String g2s = "{\"start\":[\"S\"],\"productions\":[{\"A\":[\"x\",\"x\",\"A\"]},{\"A\":[\"z\"]},{\"B\":[\"y\",\"y\"]},{\"S\":[\"A\",\"y\",\"B\"]}],\"terminals\":[\"x\",\"y\",\"z\"],\"nonterminals\":[\"A\",\"B\",\"S\"]}";
            //la grammatica viene istanziata e popolata dalla stringa JSON con una chiamata statica
            CFGrammar g2 = CFGrammar.fromJson(new JSONObject(g2s));
            //stampa di test della grammatica ricaricata
            System.out.println(g2);

            //PUOI MANIPOLARE LA GRAMMATICA COSì CARICATA CON I METODI DELLE RISPETTIVE
            //CLASSI, GUARDA IL SORGENTE. 
        } catch (GrammarException ex) {
            Logger.getLogger(Example_Grammars_CFG.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Example_Grammars_CFG m = new Example_Grammars_CFG();
        m.esempio_cfg_base();
    }

}
