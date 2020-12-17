package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T> {
    private static final class Node<T>
    {
        private T verdi;
        private Node<T> venstre, høyre;
        private Node<T> forelder;

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
            this.verdi = verdi;
            venstre = v;
            høyre = h;
            this.forelder = forelder;

        }

        private Node(T verdi, Node<T> forelder)  // konstruktør
        {
            this(verdi, null, null, forelder);
        }

        @Override
        public String toString() {
            return "" + verdi;
        }

    } // class Node

    private Node<T> rot;
    private int antall;
    private int endringer;

    private final Comparator<? super T> comp;

    public ObligSBinTre(Comparator<? super T> c)
    {
        rot = null;
        antall = 0;
        comp = c;
        endringer = 0;
    }

    @Override
    public boolean leggInn(T verdi) {
        Node<T> p = rot;
        Node<T> q = null;
        int cmp = 0;

        while (p != null) {
            q = p;
            cmp = comp.compare(verdi, p.verdi);
            p = cmp < 0 ? p.venstre : p.høyre;
        }
        p = new Node<>(verdi, q);

        if (q == null) rot = p;
        else if (cmp < 0) q.venstre = p;
        else q.høyre = p;

        antall++;
        endringer++;

        return true;
    }

    @Override
    public boolean inneholder(T verdi) {
        if (verdi == null) return false;
        Node<T> p = rot;
        while (p != null) {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else if (cmp > 0) p = p.høyre;
            else return true;
        }

        return false;
    }

    @Override
    public boolean fjern(T verdi)
    {
        if (verdi == null) return false;
        Node<T> p = rot;
        Node<T> q = null;

        while (p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) {
                q = p;
                p = p.venstre;
            } else if (cmp > 0) {
                q = p;
                p = p.høyre;
            } else break;
        }
        if (p == null) return false;
        endringer--;
        if (p.venstre == null || p.høyre == null)
        {
            Node<T> b = p.venstre != null ? p.venstre : p.høyre;
            if (p == rot) {
                rot = b;
            } else if (p == q.venstre) {
                q.venstre = b;
            } else {
                q.høyre = b;
                if (b != null) b.forelder = q;
            }
        } else
        {
            Node<T> s = p, r = p.høyre;
            while (r.venstre != null) {
                s = r;
                r = r.venstre;
            }

            p.verdi = r.verdi;

            if (s != p) {
                s.venstre = r.høyre;
            } else {
                s.høyre = r.høyre;
                r.høyre.forelder = s;
            }
        }

        antall--;
        return true;
    }

    public int fjernAlle(T verdi) {
        int i = 0;
        boolean fjernet = true;
        while (fjernet != false) {
            if (fjern(verdi))
                i++;
            else
                fjernet = false;
        }
        return i;
    }

    @Override
    public int antall() {
        return antall;
    }

    public int antall(T verdi) {
        if (verdi == null || inneholder(verdi) == false) {
            return 0;
        }
        int antall = 0;
        int cmp = 0;
        Node<T> p = rot;
        while (p != null) {

            cmp = comp.compare(verdi, p.verdi);

            if (cmp < 0) {
                p = p.venstre;
            } else {
                if (cmp == 0) {
                    antall++;
                }
                p = p.høyre;
            }

        }
        return antall;
    }

    @Override
    public boolean tom() {
        return antall == 0;
    }

    @Override
    public void nullstill() {
        if (!tom()) nullstill(rot);
        rot = null;
        antall = 0;
        endringer = 0;
    }

    public void nullstill(Node<T> p) {
        if (p.venstre != null) {
            nullstill(p.venstre);
            p.venstre = null;
        }
        if (p.høyre != null) {
            nullstill(p.høyre);
            p.høyre = null;
        }
        endringer = 0;
        p.verdi = null;
    }


    private static <T> Node<T> nesteInorden(Node<T> temp) {
        if (temp == null)
            return null;
        if (temp.høyre != null) {
            temp = temp.høyre;

            while (temp.venstre != null)
                temp = temp.venstre;

            return temp;
        }
        Node y = temp.forelder;
        Node x = temp;
        while (y != null && x == y.høyre) {
            x = y;
            y = y.forelder;
        }
        return y;

    }

    @Override
    public String toString() {
        if (tom()) return "[]";
        StringBuilder s = new StringBuilder();
        s.append('[');

        Node<T> p = rot;
        while (p.venstre != null) p = p.venstre;
        s.append(p.verdi);
        p = nesteInorden(p);
        while (p != null) {
            s.append(',').append(' ').append(p.verdi);
            p = nesteInorden(p);

        }
        s.append(']');
        return s.toString();
    }

    private static <T> Node<T> finnLaveste(Node<T> p) {
        if (p == null)
            return null;
        if (p.venstre != null)
            return finnLaveste(p.venstre);
        return p;
    }

    public String omvendtString() {
        StringBuilder s = new StringBuilder();
        s.append('[');
        Stack stakk = new Stack();
        Node p = finnLaveste(rot);
        if (p != null) {
            stakk.push(p);
            while (nesteInorden(p) != null) {
                p = nesteInorden(p);
                stakk.push(p);
            }
        }

        while (!stakk.empty()) {
            s.append(stakk.pop());
            if (!stakk.empty())
                s.append(",").append(" ");
        }
        s.append(']');
        return s.toString();
    }

    public String høyreGren() {

        StringBuilder streng = new StringBuilder();
        streng.append("[");

        Node<T> n = rot;


        while (n != null) {
            if (n == rot) {
                streng.append(n.verdi);
            } else {
                streng.append(",").append(" ").append(n.verdi);
            }

            if (n.høyre == null && n.venstre == null) {
                break;
            }

            if (n.høyre != null) {
                n = n.høyre;
            } else {
                n = n.venstre;
            }
        }

        streng.append("]");
        return streng.toString();
    }


    public String lengstGren() {
        if (tom()) return "[]";
        Queue<Node<T>> niva = new ArrayDeque<>();
        niva.add(rot);

        Node<T> p = null;
        while (!niva.isEmpty()) {
            p = niva.remove();
            if (p.høyre != null) niva.add(p.høyre);
            if (p.venstre != null) niva.add(p.venstre);
        }

        List<T> l = new ArrayList<>();

        Node<T> q = rot;
        while (q != null) {
            l.add(q.verdi);
            if (comp.compare(p.verdi, q.verdi) < 0) q = q.venstre;  //hvis p og q ikke er like
            else q = q.høyre;
        }
        return l.toString();
    }

    public String[] grener() {

        if (tom()) return new String[0];

        String[] tabell = new String[1];
        ArrayDeque<Node<T>> a = new ArrayDeque();
        ArrayDeque<Node<T>> b = new ArrayDeque();

        StringJoiner joiner;
        Node<T> p = rot;
        int i = 0;
        boolean listenErTom = false;
        while (!listenErTom) {
            joiner = new StringJoiner(", ", "[", "]");

            while (p.venstre != null || p.høyre != null) {
                if (p.høyre != null) a.add(p.høyre);
                p = p.venstre;
                if (p.høyre != null) {
                    p = p.høyre;
                }
            }
            while (p != null) {
                b.add(p);
                p = p.forelder;
            }

            while (!b.isEmpty())
                joiner.add(b.pollLast().toString());

            if (tabell[tabell.length - 1] != null)
                tabell = Arrays.copyOf(tabell, tabell.length + 1);
            tabell[i++] = joiner.toString();

            if (!a.isEmpty()) {
                p = a.pollLast();
            } else listenErTom = true;
        }

        return tabell;
    }

    public String bladnodeverdier() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node p = rot;
        finnBladnode(p, sb);
        sb.append("]");
        return sb.toString();
    }

    private void finnBladnode(Node p, StringBuilder sb) {
        if (p == null) {
            return;
        }
        if (p.venstre == null && p.høyre == null) {
            if (!sb.toString().equals("[")) {
                sb.append(",").append(" ").append(p);
            } else {
                sb.append(p);
            }
        }
        finnBladnode(p.venstre, sb);
        finnBladnode(p.høyre, sb);
    }

    public String postString() {
        if (tom()) return "[]";
        StringJoiner s = new StringJoiner(", ", "[", "]");

        Stack<Node<T>> stakk = new Stack<>();
        Stack<Node<T>> stakk2 = new Stack<>();
        Node<T> p = rot;

        while (true) {
            stakk2.add(p);

            if (p.høyre != null) {
                if (p.venstre != null) stakk.add(p.venstre);
                p = p.høyre;
            } else if (p.venstre != null)
            {
                p = p.venstre;
            } else if (!stakk.empty()) {
                p = stakk.pop();
            } else
                break;
        }
        while (!stakk2.empty()) {
            p = stakk2.pop();
            s.add(p.verdi.toString());
        }
        return s.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new BladnodeIterator();
    }

    private class BladnodeIterator implements Iterator<T> {
        private Node<T> p = rot, q = null;
        private boolean removeOK = false;
        private int iteratorendringer = endringer;

        private BladnodeIterator() // konstruktør
        {
            if (p == null) {
                return;
            }
            while (p != null && (p.venstre != null || p.høyre != null)) {
                if (p.venstre == null) {
                    p = p.høyre;
                } else {
                    p = p.venstre;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return p != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Ikke flere noder igjen!");
            }
            if (iteratorendringer != endringer) {
                throw new ConcurrentModificationException("Iteratorendringer er " + iteratorendringer + " og " + endringer);
            }
            T a = p.verdi;
            q = p;
            p = nesteInorden(p);

            while (p != null && (p.venstre != null || p.høyre != null)) {
                p = nesteInorden(p);
            }
            removeOK = true;
            return a;

        }

        @Override
        public void remove() {
            if (!removeOK) {
                throw new IllegalStateException("Noden kan ikke fjernes");
            }
            if (q.forelder == null) {
                q = null;
            } else if (q == q.forelder.venstre) {
                q.forelder.venstre = null;
            } else if (q == q.forelder.høyre) {
                q.forelder.høyre = null;
            }
            removeOK = false;
            antall--;
            endringer--;
            iteratorendringer--;
        }

    }

} 