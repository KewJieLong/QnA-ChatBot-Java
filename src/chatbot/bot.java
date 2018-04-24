/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
/**
 *
 * @author kewjielong
 */

public class bot {
    public static String db_name = "text_pool.db";
    public static boolean create_db = false; 
    
    public bot(){
        Connection c = null;
        System.out.println("bot is initized");
        create_db = !db_exist();
        
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + db_name);             
        } catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        
//        if(create_db)
        setting_up_words_table(c);
        setting_up_words_tagging_table(c);
        select_all(c);
        
        
        System.out.println("Opened database successfully");
        
    }
    
    public static boolean db_exist(){
        File f = new File(db_name);   
        System.out.println(f.exists());
        return f.exists();
    }
    
    public static void setting_up_words_table(Connection conn){
        String sql = "CREATE TABLE IF NOT EXISTS word(\n" +
                     " sentence_id integer, \n" +
                     " token_id integer PRIMARY_KEY, \n" +
                     " after_norm String\n" + 
                     ");";               
        try{
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }                
    }
    
    public static void setting_up_words_tagging_table(Connection conn){
        String sql = "CREATE TABLE IF NOT EXISTS tagging(\n" +
                     " sentence_id integer, \n" +
                     " token_id integer PRIMARY_KEY, \n" +
                     " pos string NOT NULL\n" +
                     ");";
        
        try{
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }        
    }
    
    public static void insert_data_to_words_table(HashMap data){
        
    }
    
    public static void insert_data_to_tagging_table(HashMap data){
        
    }
    
    public static void select_all(Connection conn){
        String sql  = "SELECT sentence_id FROM words";
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()){
                System.out.println(rs.getInt("sentence_id"));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }          
    }
    
    
}
