package org.example.helper;

import org.example.dto.Node;

public class NodeHalpers {
    public NodeHalpers() {
    }
    public boolean isConexa(Node[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return false;
        }

        Node inicio = nodes[0];
        Node actual = inicio.getNext();
        int cont = 1;
        int pos = inicio.getPosition();
        pos++;
        if (pos == nodes.length) pos = 0;

        while (actual != inicio) {
            if (pos != actual.getPosition()) {
                System.out.println("esperada : " + pos + ", encontrada :" + actual.getPosition() + ", en nodo " + actual.getId());
                return false;
            }
            pos++;
            if (pos == nodes.length) pos = 0;
            cont++;
            if (actual.getNext().getPrevious() != actual) {
                System.out.println("anterior mal configurado en nodo: " + actual.getNext().getId());
                return false;
            }
            actual = actual.getNext();
            if (cont > nodes.length) return false;
        }
        return cont == nodes.length;
    }
}
