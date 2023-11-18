package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;
import java.util.List;

import static com.company.Main.dicTree;

public class Main {
    static Trie dicTree = new Trie();
    public static void main(String[] args) throws IOException {
        List<List<String>> records = new ArrayList<>();//Open CSV file with NotePad++ and go to Encoding and change "UTF-8-BOM" to "UTF-8"
        try (BufferedReader br = new BufferedReader(new FileReader("src\\com\\company\\EnglishPersianDatabase.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        }//Read all the lines from the CSV file and put them in a ArrayList
        for (List<String> record : records) {//Use a for each loop to go through ArrayList
            dicTree.insert(record.get(0).toLowerCase());//Add English Words to tree
            if (dicTree.search(record.get(0).toLowerCase())) {//Check if English Word exist to add the definition
                if (dicTree.getNode(record.get(0).toLowerCase()).mean.isEmpty()) {//Adding all the meanings (Persian words)
                    dicTree.getNode(record.get(0).toLowerCase()).mean.insertFirst(record.get(1));
                } else {
                    dicTree.getNode(record.get(0).toLowerCase()).mean.insertFirst(record.get(1));
                }
            }
        }
        System.out.println("test");
        MyFrame myFrame = new MyFrame();//Use GUI
    }
}

class Trie {//Trie Structure
    public Node root;
    public Node traverseCurr;
    public Trie() {
        root = new Node('\0',root);
        traverseCurr = root;
    }

    private boolean legalInsert(String word) {//Check if word has any hyphens or dots
        int count = 0;
        for (int i = 0; i< word.length(); i++) {
            char c = word.charAt(i);

            if (c == ' ' || (c - 'a' >= 0 && c - 'a' <= 25)) {
                count++;
            }
        }
        return count == word.length();
    }

    public void insert(String word) {//Insert word into Trie
        if (legalInsert(word)) {
            Node curr = root;
            for (int i = 0 ; i < word.length(); i++) {
                char c = word.charAt(i);
                if (c == ' ') {
                    if (curr.children[26] == null) {
                        curr.children[26] = new Node(c,curr);
                    }
                    curr = curr.children[26];
                } else {
                    if (curr.children[c - 'a'] == null) {
                        curr.children[c - 'a'] = new Node(c,curr);
                    }
                    curr = curr.children[c - 'a'];
                }
            }
            curr.isWord = true;
        }
    }
    public boolean search(String word) {//Search for a word to see if it exists or not
        Node node = getNode(word);
        return node!= null && node.isWord;
    }

    public Node getNode(String word) {//Returns the last letter's Node in Trie
        if (legalInsert(word)) {
            Node curr = root;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (c == ' ') {
                    if (curr.children[26] == null) return null;
                    curr = curr.children[26];
                } else {
                    if (curr.children[c - 'a'] == null) return null;
                    curr =  curr.children[c - 'a'];
                }
            }
            return curr;
        } else {
            Node curr = null;
            return curr;
        }
    }


}
class Node {//Node Structure
    public char c;
    public boolean isWord;
    public Node[] children;
    public Node parent;
    public LinkedList mean;
    public Node(char c, Node parent) {
        this.c = c;
        isWord = false;
        children = new Node[27];//For a-z and WhiteSpace
        mean = new LinkedList();//For the definition
        this.parent = parent;//For parent
    }
}
///////////////////////////////////Adding meaning
class meaning{
    private String element;
    private meaning next;
    meaning(){
        this(null,null);
    }
    meaning(String e, meaning n){
        element=e;
        next=n;
    }
    void setElement(String e) {element=e;}
    void setNext(meaning n) {next=n;}
    String getElement(){return element;}
    meaning getNext(){return next;}
}

class LinkedList extends meaning{//LinkedList is made of "meaning"
    meaning head = null;
    int length = 0;

    int size() {
        return length;
    }

    meaning first() {
        if (head == null)
            System.out.println("ERROR!");
        return head;
    }

    boolean isEmpty() {
        return size() == 0;
    }

    void insertFirst(String x) {
        head = new meaning(x, head);
        length++;
    }

    void insertAfter(String x, meaning n) {
        if (n == null) System.out.println("ERROR!");
        else {
            n.setNext(new meaning(x, n.getNext()));
            length++;
        }
    }

    void deleteFirst() {
        if (isEmpty()) System.out.println("ERROR!");
        else {
            head = head.getNext();
            length--;
        }
    }

    void deleteAfter(meaning n) {
        if (n == null || n.getNext() == null) System.out.println("ERROR!");
        else {
            n.setNext(n.getNext().getNext());
            length--;
        }
    }

    String listToString() {//Changed the printList to ListToString to be used in JLable
        meaning q = head;
        ArrayList<String> al = new ArrayList<>();
        while (q != null) {
            //System.out.print(q.getElement() + " -> ");
            al.add(q.getElement());
            q = q.getNext();
        }
        //System.out.println();
        return String.join(" , ", al);
    }

    meaning find(String data) {
        meaning q = head;
        while (q != null) {
            if (Objects.equals(q.getElement(), data)) return q;
            q = q.getNext();
        }
        System.out.println("Data not found!");
        return null;
    }
}

class MyFrame extends JFrame{//Coding our GUI here
    MyFrame() {
        this.setTitle("English To Persian Dictionary");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(1500,400);
        this.setVisible(true);
        JTextField textField = new JTextField();//Setting up the JTextField
        textField.setPreferredSize(new Dimension(250,40));//Setting up the JTextField
        textField.setBounds(625,150,250,40);//Setting up the JTextField
        this.add(textField);//adding the JTextField to JFrame
        textField.setBackground(new Color(189, 195, 199));//Setting up the JTextField
        textField.setCaretColor(Color.black);//Setting up the JTextField
        this.setResizable(false);//If you want to resize then change this to true
        JLabel label = new JLabel("Translation will appear here",SwingConstants.CENTER);
        this.add(label);
        label.setBounds(0,250,1500,80);
        label.setOpaque(true);
        label.setBackground(new Color(189, 195, 199));
        textField.addKeyListener(new KeyListener() {//Defining a KeyListener for our JTexField
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {//What to do if user hits BackSpace
                    if (dicTree.traverseCurr != dicTree.root) {
                        dicTree.traverseCurr = dicTree.traverseCurr.parent;
                        if (dicTree.traverseCurr.isWord) {
                            meaning definition =  dicTree.traverseCurr.mean.head;
                            label.setText(dicTree.traverseCurr.mean.listToString());
                            for (int i = 0; i < dicTree.traverseCurr.mean.size(); i++) {
                                System.out.println(definition.getElement());
                                definition = definition.getNext();
                            }
                        } else {
                            label.setText("Translation will appear here");
                        }
                    }
                } else {//If user is typing letters a-z plus WhiteSpace
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (dicTree.traverseCurr.children[26] == null) {//What to do if user uses extra whiteSpaces exit Code 25
                            System.out.println("Not found");
                            System.exit(25);
                        } else {
                            dicTree.traverseCurr = dicTree.traverseCurr.children[26];
                        }
                    } else {
                        if (dicTree.traverseCurr.children[e.getKeyChar() - 'a'] == null) {
                            System.out.println("Not found");
                            System.exit(69);
                        } else {
                            dicTree.traverseCurr = dicTree.traverseCurr.children[e.getKeyChar() - 'a'];
                            if (dicTree.traverseCurr.isWord) {
                                meaning definition =  dicTree.traverseCurr.mean.head;
                                label.setText(dicTree.traverseCurr.mean.listToString());
                                for (int i = 0; i < dicTree.traverseCurr.mean.size(); i++) {
                                    System.out.println(definition.getElement());
                                    definition = definition.getNext();
                                }
                                System.out.println("////////////////////////////////////////");
                            } else {
                                label.setText("Translation will appear here");
                            }
                        }
                    }
                }
            }
        });
    }
}