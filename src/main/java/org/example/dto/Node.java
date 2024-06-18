package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Node {
    public int id;
    public Node next;
    public Node previous;
    public int position;
    public Node(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", next=" + next.id +
                ", previous=" + previous.id +
                ", position=" + position +
                '}';
    }
}
