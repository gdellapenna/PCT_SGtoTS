package org.dellapenna.research.grammars.symbols;

/**
 *
 * @author Andrea
 */
public class Action  {

    private final Relation relation;
    private final Symbol arg1;
    private final Symbol arg2;

    public Action(Relation relation, Symbol arg1, Symbol arg2) {
        this.relation = relation;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public static Action createAction(Relation r, Symbol y, Symbol x) {
        return new Action(r, y, x);
    }

    public static Action createEmptyAction() {
        return new Action(null, null, null);
    }

    @Override
    public String toString() {
        if (relation != null) {
            return "{" + relation.toString() + "(" + arg1.toString() + "," + arg2.toString() + ")}";
        } else {
            return "{" + "}";
        }
    }

    

}
