/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;
/**
 *
 * @author kewjielong
 */
public class tmp {
    public static void main(String[]args){
        int x = 256;
        int root = 4;
        
        double pre_guess = 1;
        double next_guess = 2;               
        
        
        while(Math.abs(pre_guess - next_guess) > 0.000000001){
            System.out.println(pre_guess);          
            pre_guess = next_guess;
            next_guess = pre_guess - ((Math.pow(pre_guess, root) - x) / derivative(pre_guess, root));                        
            System.out.println("guess = " + next_guess);
            System.out.println("different = " + (pre_guess - next_guess));
        }
        
        System.out.println(root + " root of " + x + " is " + next_guess);
              
    }
    
    public static double derivative(double x, double power){
        double der = Math.pow(((power) * x),power - 1);
        return der;
    }
}
