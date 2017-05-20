package com.example.almajed.racinggame;

import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
public class Test {

}

class TestBank {
    public final static int numOfQuestion = 0;   // if you choose random, you can choose number of questions
    public final static String Default = "questions.txt";
    public final static String readFile = Default;// this is the file you enter your questions in, it is in the project file
    public final static String writtenFile = "Wrong Answers.txt"; // this is the file where you can see your mistakes, it's cleared when you run the program again
    public final static String editedQuestionsFile = "Edited Questions.txt"; //you can get edited copy but when it's not random
    public static boolean isRandom;
    public static LinkedList<Question> questions;
    public static Scanner s = new Scanner(System.in);              // read from the user
    public static void main(String[] args) {
        save(Action.clear,""); // this will clear the text file
        String q = "";                                   // all the question will be assigned here
        try{
            Scanner reader = new Scanner(new File(readFile));// this allows you to read from the file that contains the question
            try{
                while(true)
                    q += reader.nextLine();                 // Reading File . . .
            }catch(Exception exc){
                System.out.println("    Start\n\n");            // at the end of reading, you can start
                reader.close();
            }
        }catch(Exception ex){
            System.out.println("    ERROR 1: Check the readFile (questions.txt) "+readFile); // check readFile ^^ "questions.txt", does it contain questions?
            return;
        }

        System.out.print("    Do You Want questions to be randomly picked?\n    Enter Y to pick questions randomly or N otherwise: ");
        isRandom = !((""+s.next().charAt(0)).equalsIgnoreCase("N"));
        System.out.println("\n");

        questions = new LinkedList<Question>();
        int start =0;                                                  // --> | 1) ....
        int end = 0;                                                   // --> | Answer: ..
        boolean flag = false;
        for(int d = 0; d < q.length(); d++){
            if(q.charAt(d) == ')' && (d < 2 || q.charAt(d-2) != '(')&& (d < 3|| q.charAt(d-3) != '(')){
                try{
                    Integer.parseInt(""+q.charAt(d-1));
                    flag = true;
                    if(!isRandom){
                        d--;
                        try{
                            Integer.parseInt(""+q.charAt(d-1));
                            d--;
                        }catch(Exception exe){	}
                    }else
                        d = d + 1;     //Whether to show question number or not
                }catch(Exception ex){}
                if(flag){
                    start = d;
                    d = q.length();
                }
            }
        }

        for(int i = 0; i < q.length(); i++){                           // char by char
            if(q.charAt(i) == 'A' && i+7 < q.length())
                if(q.charAt(i+1) == 'n')
                    if(q.charAt(i+2) == 's')
                        if(q.charAt(i+3) == 'w')
                            if(q.charAt(i+4) == 'e')
                                if(q.charAt(i+5) == 'r')
                                    if(q.charAt(i+6) == ':'){
                                        int k = i+7;
                                        while(q.charAt(k) == ' ')      // Answer: . . True  until you find an answer
                                            k++;
                                        char answer = q.charAt(k);     // We FOUND it
                                        end = i-1;
                                        Question question = new Question(q.substring(start, end+1),answer);
                                        if(question.check())
                                            questions.add(question);
                                        boolean done = false;
                                        for(int d = k; d < q.length(); d++)
                                            if(q.charAt(d) == ')' && q.charAt(d-2) != '('&& q.charAt(d-2) != '('){
                                                try{
                                                    Integer.parseInt(""+q.charAt(d-1));
                                                    done = true;
                                                    if(!isRandom){
                                                        d--;
                                                        try{
                                                            Integer.parseInt(""+q.charAt(d-1));
                                                            d--;
                                                        }catch(Exception exe){}
                                                    }else
                                                        d = d + 1;
                                                }catch(Exception ex){}
                                                if(done){
                                                    start = d; // )|
                                                    d = q.length();
                                                }
                                            }
                                    }
        }
        if(questions.isEmpty()){
            System.out.println("    ERROR 2: There is no question, please check the file!");
            return;
        }
        if(!isRandom)
            editedCopy();
        start();
    }
    public static void start(){
        int[] random;
        if(isRandom){
            random = getNonRepeatedArray(0,questions.size()-1,questions.size());
        }else{
            random = new int[questions.size()];
            for(int i = 0; i < random.length; i++)
                random[i] = i;
        }
        int mark = 0;
        String wrongAnswers = "";
        int count = isRandom && numOfQuestion > 0 && numOfQuestion < questions.size()? numOfQuestion: questions.size();

        System.out.println("    Number of questions: "+count+"\n\n");

        for(int i = 0; i < count; i++){
            Question q = questions.get(random[i]);
            String question = "";
            try{
                question = " "+(isRandom?(i+1)+")":"")+format(q.question)+"\n";

                if(q.type == Type.Multiple_Choices){
                    for(int j = 0; j < q.choices.size(); j++)
                        question += "\n    "+format(q.choices.get(j));
                    question +="\n";
                }
                question += "\n    Answer: ";
                System.out.print(question);
                String userAnswer = "";
                do{
                    userAnswer = ""+s.next().charAt(0);
                }while(!q.check(userAnswer));

                if(userAnswer.equalsIgnoreCase(""+q.answer)){
                    System.out.println("\n    Correct!\n");
                    mark++;
                }else{
                    String wrong = "\n    WRONG! \n    The correct answer is "+q.answer+" \n\n";
                    System.out.print(wrong);
                    question += userAnswer+"\n"+ wrong;
                    wrongAnswers += question;
                    save(Action.append,question);
                }
            }catch(Exception ex){
                System.out.println("    ERROR 3: check this question : |\n\n"+q.question+"\n|\n\n");
            }
        }
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n    Result = "+mark+"/"+count);
        if(mark != count){
            System.out.print("\n    Enter 1 to show Wrong Answers: ");
            String c;
            do{
                c = s.next();
                if(c.contains("1"))
                    System.out.println("\n\n"+wrongAnswers);
            }while(!c.contains("1"));
        }
    }
    public static String format(String s){
        String result = "";
        try{
            boolean flag = false;
            for(int i = 0; i < s.length(); i++){
                result += s.charAt(i);
                if(flag && s.charAt(i) == ' '){
                    result += "\n     ";
                    flag = false;
                }
                if((i + 1) % 100 == 0)
                    flag = true;
            }
        }catch(Exception ex){
            System.out.println("    ERROR 3: check this question : |\n\n"+s+"\n|\n\n");
        }
        return result;
    }
    public static void save(Action a ,String s){
        try{
            FileWriter writer = new FileWriter(new File(writtenFile),a == Action.append);
            if(a == Action.append)
                writer.write(s);
            writer.close();
        }catch(Exception e){
            System.out.println("    ERROR 4: check the writtenFile (Wrong Answers.txt) "+writtenFile);
        }
    }
    public static void editedCopy(){
        try{
            FileWriter writer = new FileWriter(new File(editedQuestionsFile),false);
            for(int i = 0; i < questions.size(); i++){
                Question q = questions.get(i);
                String question = q.question+"\n";
                if(q.type == Type.Multiple_Choices){
                    for(int j = 0; j < q.choices.size(); j++)
                        question += q.choices.get(j)+"\n";
                }
                question += "Answer: "+q.answer+"\n";
                writer.write(question);
            }
            writer.close();
            System.out.println("    Check the file for Edited Copy\n");
        }catch(Exception e){
            System.out.println("    ERROR 5: check the file (Edited Questions.txt)"+editedQuestionsFile+"\n");
        }
    }
    public static int[] getNonRepeatedArray(int a,int b,int size){
        if(size > b - a + 1)
            return null;
        boolean flagA_0 = false;
        if(a == 0){
            a++;
            b++;
            flagA_0 = true;
        }
        int n = 0;
        int arr[] = new int[size];
        while(n != size){
            boolean flag = true;
            int c = a + (int)(Math.random()*(b + 1 -a));
            for (int i = 0; i < n+1; i++)
                if (arr[i] == c)
                    flag = false;
            if(flag)
                arr[n++]= c;
        }
        if(flagA_0)
            for(int i = 0; i < arr.length; i++)
                arr[i]--;
        return arr;
    }
}
enum Action {clear,append};
enum Type {True_False, Multiple_Choices}
class Question {
    public Type type;
    public String question;
    public LinkedList<String> choices;
    public char answer;
    public char userAnswer;
    public boolean valid = true;

    public Question(String s, char an){
        try{
            answer = an;
            type = answer == 'T' || answer == 'F'? Type.True_False : Type.Multiple_Choices;
            int start = 0;
            int end = - 1;
            if(type == Type.True_False){
                question = s;
            }else{
                boolean isChoice = false;
                for (int i = 1; i < s.length(); i++) {
                    char ch = s.charAt(i);
                    char ch2 = s.charAt(i-1);
                    if (ch == ')' && (ch2 == 'A' || ch2 == 'B'|| ch2 == 'C'|| ch2 == 'D'|| ch2 == 'E')) {
                        end = i - 1;
                        String part = s.substring(start, end);
                        if(isChoice)
                            choices.add(part);
                        else{
                            question = part;
                            isChoice = true;
                        }
                        start = end;
                    }
                }
                String lastChoice = "";
                for(int i = start; i < s.length(); i++)
                    lastChoice += s.charAt(i);
                choices.add(lastChoice);

            }
            if(type == Type.Multiple_Choices){
                if(TestBank.isRandom)
                    randomize();
            }
        }catch(Exception ex){
            valid = false;
            System.out.println("    ERROR 3: check this question : |\n\n"+question+"\n|\n\n");
        }
    }
    public boolean check(){
        return valid;
    }
    public boolean check(String s){
        s = s.toUpperCase();
        switch(type){
            case Multiple_Choices:
                switch(s){
                    case "A":case "B":case "C":case "D":case "E":
                        userAnswer = s.charAt(0);
                        return true;
                }
                break;
            case True_False:
                switch(s){
                    case "T":case "F":
                        return true;
                }
        }
        System.out.print("    Try Again: ");
        return false;
    }
    private void randomize(){
        try{

            int[] random = TestBank.getNonRepeatedArray(0, choices.size()-1, choices.size());
            int ans = convertAnswer(answer);
            for(int i = 0; i < random.length; i++)
                if(random[i] == ans){
                    ans = i;
                    i = random.length;
                }
            answer = convertAnswer(ans);
            LinkedList<String> ch = new LinkedList<String>();
            char[] abcde = {'A','B','C','D','E'};
            for(int i = 0; i < choices.size(); i++){
                String choice = choices.get(random[i]);
                ch.add(abcde[i]+")"+choice.substring(2,choice.length()));
            }
            choices = ch;
        }catch(Exception ex){
            valid = false;
            System.out.println("    ERROR 3: check this question : |\n\n"+question+"\n|\n\n");
        }
    }
    private int convertAnswer(char c){
        switch(c){
            case 'A': return 0;
            case 'B': return 1;
            case 'C': return 2;
            case 'D': return 3;
            case 'E': return 4;
        }
        return -1;
    }
    private char convertAnswer(int c){
        switch(c){
            case 0 : return 'A';
            case 1 : return 'B';
            case 2 : return 'C';
            case 3 : return 'D';
            case 4 : return 'E';
        }
        return ' ';
    }
}
