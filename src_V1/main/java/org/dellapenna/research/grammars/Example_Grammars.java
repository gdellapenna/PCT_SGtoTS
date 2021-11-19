package org.dellapenna.research.grammars;

import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.dellapenna.research.grammars.symbols.AlphaTerminal;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.cf.CFGGrammar;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Della Penna
 */
public class Example_Grammars {

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
            CFGGrammar g = new CFGGrammar();

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
            CFGGrammar g2 = CFGGrammar.fromJson(new JSONObject(g2s));
            //stampa di test della grammatica ricaricata
            System.out.println(g2);

            //PUOI MANIPOLARE LA GRAMMATICA COSì CARICATA CON I METODI DELLE RISPETTIVE
            //CLASSI, GUARDA IL SORGENTE. IN QUESTO MODO, PUOI IMPLEMENTARCI SOPRA
            //L'ALGORITMO
        } catch (GrammarException ex) {
            Logger.getLogger(Example_Grammars.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //ESEMPIO 2: GRAMMATICHE SG
    public void esempio_sg_base() {
        //CREAZIONE GRAMMATICA VIA CODICE
        try {
            //i simboli vanno dichiarati come al solito
            Nonterminal A = AlphaNonterminal.create("A");
            Nonterminal B = AlphaNonterminal.create("B");
            Nonterminal S = AlphaNonterminal.create("S");
            Terminal x = AlphaTerminal.create("x");
            Terminal y = AlphaTerminal.create("y");
            Terminal z = AlphaTerminal.create("z");

            //le relazioni vanno dichiarate come i simboli, usando la factory AlphaRelation
            //il secondo parametro è l'apice della relazione (1 se omesso)
            Relation L = AlphaRelation.create("LEFT");
            Relation U2 = AlphaRelation.create("UP", 2);
            Relation D = AlphaRelation.create("DOWN");

            //creiamo la grammatica
            SGGrammar g3 = new SGGrammar();

            //impostiamo lo start symbol
            g3.setStartSymbol(S);

            //aggiungiamo le produzioni
            /*
            * il metodo addProduction(l,r1...rn) crea la produzione
            * l -> r1 ... rn
            * in questo caso, però, simboli e relazioni devono essere alternati
            * nel lato sinistro
            *
            * il metodo addProduction(l,{a1,..an},r1...rn) crea la produzione con ancora
            * l ->{a1,..,an} r1 ... rn
            * dove le a sono numeri interi, come di consueto
             */
            // S ->{} A LEFT^1 B UP^2 x 
            //(notare che l'ancora vuota che viene comunque stampata)
            g3.addProduction(S, A, L, B, U2, x);
            //A ->{2} x DOWN^1 y 
            g3.addProduction(A, new Integer[]{2}, x, D, y);
            //A ->{} z
            g3.addProduction(A, z);
            //B ->{} y LEFT^1 x 
            g3.addProduction(B, y, L, x);

            //stampa diagnostica
            System.out.println(g3);
            /* OUTPUT
            * S ->{} A LEFT^1 B UP^2 x 
            * A ->{2} x DOWN^1 y 
            * A ->{} z 
            * B ->{} y LEFT^1 x             
             */

            //versione JSON serializzabile
            System.out.println(g3.toJson());
            /* OUTPUT (grammatica codificata in JSON):
            * {"start":"S","productions":[{"A":[[2],"x",{"DOWN":1},"y"]},{"A":[[],"z"]},{"B":[[],"y",{"LEFT":1},"x"]},{"S":[[],"A",{"LEFT":1},"B",{"UP":2},"x"]}],"relations":[{"UP":2},{"DOWN":1},{"LEFT":1}],"terminals":["x","y","z"],"nonterminals":["A","B","S"]}
            * il formato è un'estensione di quello delle CF standard:
            * {
            *    "start":"S",
            *    "productions":[
            *        {                              <-- inizio produzione
            *           "A":                        <-- simbolo sinistro
            *               [
            *                   [2],                <-- ancora (lista di interi anche vuota)
            *                   "x",{"DOWN":1},"y"  <-- simboli lato destro
            *               ]
            *        },                             <-- fine produzione
            *        {"A":[[],"z"]},
            *        {"B":[[],"y",{"LEFT":1},"x"]},
            *        {"S":[[],"A",{"LEFT":1},"B",{"UP":2},"x"]}],
            *        "relations":[{"UP":2},{"DOWN":1},{"LEFT":1}], <-- lista delle relazioni usate nelle produzioni
            *        "terminals":["x","y","z"],
            *        "nonterminals":["A","B","S"]}
            * 
            * Notare che le relazioni sono serializzate nella forma
            * {"nomerelazione":apice}
            *
             */

            System.out.println();
            //questa chiamata permette di stampare delle stringhe casuali generate
            //dalla grammatica, utili per i test...
            System.out.println(g3.getRandomSentence());
            System.out.println(g3.getRandomSentence());
            System.out.println(g3.getRandomSentence());
            System.out.println();

            /* 
            * caricamento grammatica da stringa JSON. La stringa, ovviamente,
            * può essere letta da un file. Qui l'ho cablata nel codice.
            * attenzione alla sintassi del JSON: non ho inserito procedure
            * di controllo sintattico troppo intelligenti, quindi se il formato
            * è sbagliato può succedere di tutto!
             */
            String g3s = "{\"start\":[\"S\"],\"productions\":[{\"A\":[[1,2],\"x\",{\"DOWN\":1},\"y\"]},{\"A\":[[],\"z\"]},{\"B\":[[],\"y\",{\"LEFT\":1},\"x\"]},{\"S\":[[],\"A\",{\"LEFT\":1},\"B\",{\"UP\":2},\"x\"]}],\"relations\":[{\"LEFT\":1},{\"UP\":2},{\"DOWN\":1}],\"terminals\":[\"x\",\"y\",\"z\"],\"nonterminals\":[\"A\",\"B\",\"S\"]}";
            //la grammatica viene istanziata e popolata dalla stringa JSON con una chiamata statica
            SGGrammar g4 = SGGrammar.fromJson(new JSONObject(g3s));
            //stampa di test della grammatica ricaricata
            System.out.println(g4);

            //PUOI MANIPOLARE LA GRAMMATICA COSì CARICATA CON I METODI DELLE RISPETTIVE
            //CLASSI, GUARDA IL SORGENTE. IN QUESTO MODO, PUOI IMPLEMENTARCI SOPRA
            //L'ALGORITMO
        } catch (GrammarException ex) {
            Logger.getLogger(Example_Grammars.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //ESEMPIO 3: GRAMMATICA CF CON SIMBOLI COMBINATI (relazione+simbolo, come
    //quelli usati nei passi intermedi dell'algoritmo
    //PUOI  ANCHE USARE CODICE TUO A SUPPORTO DEI PASSAGGI INTERMEDI, IGNORANDO
    //LE CLASSI DI QUESTO ESEMPIO: 
    //L'IMPORTANTE E' CHE PARTI DA UNA SGGRAMMAR E ALLA FINE GENERI UNA CFGGRAMMAR
    public void esempio_cfg_combinata() {
        //CREAZIONE GRAMMATICA VIA CODICE
        try {

            //i simboli di base e le relazioni vanno dichiarate come al solito
            Nonterminal A = AlphaNonterminal.create("A");
            Nonterminal S = AlphaNonterminal.create("S");
            Terminal x = AlphaTerminal.create("x");
            Terminal y = AlphaTerminal.create("y");
            Terminal z = AlphaTerminal.create("z");
            Relation L = AlphaRelation.create("LEFT");
            Relation U2 = AlphaRelation.create("UP", 2);

            //i simboli combinati vanno dichiarati  usando la factory CombinedSymbol
            //LEFT_x
            CombinedSymbol Lx = CombinedSymbol.create(new Symbol[]{L, x});
            //LEFT_y
            CombinedSymbol Ly = CombinedSymbol.create(new Symbol[]{L, y});
            //UP^2_A
            CombinedSymbol U2A = CombinedSymbol.create(new Symbol[]{U2, A});

            //creiamo la grammatica
            CFGGrammar g5 = new CFGGrammar();

            //impostiamo lo start symbol
            g5.setStartSymbol(S);

            //aggiungiamo le produzioni
            //lavoriamo come con le grammatiche CF, i simboli utilizzati
            //non modificano il modo di costruire la gramamtica
            //S -> _LEFT^1_x _LEFT^1_y 
            g5.addProduction(S, Lx, Ly);
            //_LEFT^1_x -> _UP^2_A 
            g5.addProduction(Lx, U2A);
            // _UP^2_A -> z
            g5.addProduction(U2A, z);

            //stampa diagnostica
            System.out.println(g5);
            /* OUTPUT
            * S -> _LEFT^1_x _LEFT^1_y 
            * _LEFT^1_x -> _UP^2_A 
            * _UP^2_A -> z
            *
             */

            //LA SERIALIZZAZIONE e DESERIALIZZAZIONE NON SONO 
            //STATE TOTALMENTE IMPLEMENTATE VISTO CHE QUESTO E' UN FORMATO
            //INRERNO
            System.out.println(g5.toJson());
            System.out.println();

        } catch (GrammarException ex) {
            Logger.getLogger(Example_Grammars.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Example_Grammars m = new Example_Grammars();
        m.esempio_cfg_base();
        m.esempio_sg_base();
        //m.esempio_cfg_combinata();
    }

}
