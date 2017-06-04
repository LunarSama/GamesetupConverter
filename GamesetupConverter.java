package gamesetupconverter;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GamesetupConverter {

    /**
     * @param args the command line arguments
     */
    public static int MENU_STATE = 1;

    /*
    0 - MainWindow
    1 - Start Menu
    2 - Second Menu
    3 - OptionMenuOne
     */
    public static void main(String[] args) {
        int Delay = 100;
        String[] gamesetups = new String[70];
        String targetGamesetupName = "";
        BufferedReader br;
        JFrame MW = new JFrame();
        StartMenu SM = new StartMenu();
        JComponent test = new JButton();
        SecondMenu ScM = new SecondMenu();
        OptionMenuOne OMO = new OptionMenuOne();
        LoadingDone LD = new LoadingDone();
        MW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MW.setSize(600, 500);
        MW.setVisible(true);
        boolean runningMenus = true;
//        System.out.println(System.getProperty("user.home"));
//        String SourceFolder = System.getProperty("user.home") + "\\Documents\\StarCraft II\\Accounts";
//        String TargetFolder = System.getProperty("user.home") + "\\Documents";
        File sourceFile = new File(System.getProperty("user.home") + "\\Documents\\StarCraft II");
        File targetFile = new File(System.getProperty("user.home") + "\\Documents");
        SM.getjFileChooser1().setCurrentDirectory(sourceFile);
        ScM.getjFileChooser1().setCurrentDirectory(targetFile);
//        System.out.println(SourceFolder);
        boolean first = false;
        while (runningMenus) {
            MW.getContentPane().removeAll();
            MW.getContentPane().add(SM);
            MW.revalidate();
            MW.repaint();
            while (MENU_STATE == 1) {
                sleep(100);
            }
            sourceFile = SM.getjFileChooser1().getSelectedFile();
            MW.getContentPane().removeAll();
            MW.getContentPane().add(ScM);
            MW.revalidate();
            MW.repaint();
            while (MENU_STATE == 2) {
                sleep(100);
            }
            targetFile = ScM.getjFileChooser1().getSelectedFile();
            MW.getContentPane().removeAll();
            MW.getContentPane().add(OMO);
            MW.revalidate();
            MW.repaint();
            try {
                br = new BufferedReader(new FileReader(sourceFile));
                String currentLine = "";
                while (!(currentLine = br.readLine()).trim().equals("</Bank>")) {
                    if (currentLine.trim().startsWith("<Sec")) {
//                        System.out.println(currentLine.trim().substring(currentLine.trim().length() - 6, currentLine.trim().length()));
                        if (!currentLine.trim().substring(currentLine.trim().length() - 6, currentLine.trim().length()).startsWith("unit")) {
                            for (int i = 0; i < gamesetups.length; i++) {
                                if (gamesetups[i] == null) {
                                    gamesetups[i] = currentLine.trim().substring(15).substring(0, currentLine.trim().substring(15).length() - 9);
//                                    System.out.println(gamesetups[i]);
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                System.out.println("File not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Error reading the file");
                System.exit(0);
            }
            OMO.getjList1().setListData(gamesetups);
            OMO.getjTextField1().setText("100");
            while (MENU_STATE == 3) {
                sleep(100);
            }
            targetGamesetupName = OMO.getjList1().getSelectedValue();
            Delay = Integer.parseInt(OMO.getjTextField1().getText());
            if (MENU_STATE == 4) {
                runningMenus = false;
            }
        }
        MW.getContentPane().removeAll();
        MW.revalidate();
        MW.repaint();
        StringBuilder builder = new StringBuilder();
        Key[] game_Keys = new Key[3300];
        if (!sourceFile.exists()) {
            try {
                sourceFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(GamesetupConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            br = new BufferedReader(new FileReader(sourceFile));
            builder.delete(0, builder.length());
            String line_Key = "";
            String line_Value = "";
            String holding_Value = "";
            float x = 0, y = 0;
            float face = 0;
            float key_Number;
            int char_Count = 0;
            while(!(builder.append(br.readLine()).toString().trim().startsWith("<Section name=\""+targetGamesetupName+"_unit\">"))){
//                System.out.println(builder.append(br.readLine()).toString().trim()+"!="+"<Section name=\""+targetGamesetupName+"_unit\">");
                builder.delete(0, builder.length());
            }
//            System.out.println(builder.toString());
            builder.delete(0, builder.length());
            while (!(builder.append(br.readLine()).toString().trim()).equals("</Section>")) {
                if (builder.toString().equals("null")) {
                    break;
                }
                builder = new StringBuilder(builder.toString().trim());
                if (builder.toString().substring(0, 2).equals("<K")) {
                    if (!line_Key.startsWith("<Key name=\"s")) {
                        line_Key = builder.delete(0, 11).toString();
                        line_Key = (new StringBuilder(line_Key)).delete(line_Key.length() - 2, line_Key.length()).toString();
                        line_Value = (new StringBuilder(br.readLine())).delete(0, 27).toString().trim();
                        if (line_Value.equals("/>")) {
                            break;
                        }
                        line_Value = (new StringBuilder(line_Value)).delete(line_Value.length() - 3, line_Value.length()).toString();
                        if (!line_Value.startsWith("@")) {
                            holding_Value = "var setupvar = @spawn " + string_Up_To_First(line_Value, ' ') + ";";
                            line_Value = line_Value.substring(get_Position_Of_First(line_Value, ' '));
                            holding_Value = holding_Value + "@position " + string_Up_To_First(line_Value, ' ') + " ";
                            line_Value = line_Value.substring(get_Position_Of_First(line_Value, ' '));
                            holding_Value = holding_Value + string_Up_To_First(line_Value, ' ') + ";";
                            line_Value = line_Value.substring(get_Position_Of_First(line_Value, ' '));
                            holding_Value = holding_Value + "@face " + string_Up_To_First(line_Value, ' ');
                            line_Value = holding_Value;
                        } else {
                            line_Value = "ZP;" + line_Value;
                            for (int i = 0; i < line_Value.length(); i++) {
                                if (line_Value.charAt(i) == '+') {
                                    line_Value = (new StringBuilder(line_Value).insert(i + 1, '}')).toString();
                                    line_Value = (new StringBuilder(line_Value).insert(i, '{')).toString();
                                    i++;
                                }
                                if (line_Value.charAt(i) == '%') {
//                                    line_Value = (new StringBuilder(line_Value).insert(i + 1, '}')).toString();
                                    line_Value = (new StringBuilder(line_Value).insert(i, '`')).toString();
                                    i++;
                                }
                                if (line_Value.charAt(i) == '"') {
//                                    line_Value = (new StringBuilder(line_Value).insert(i + 1, '}')).toString();
                                    line_Value = (new StringBuilder(line_Value).insert(i, '`')).toString();
                                    i++;
                                }
                                if (line_Value.charAt(i) == '\'') {
//                                    line_Value = (new StringBuilder(line_Value).insert(i + 1, '}')).toString();
                                    line_Value = (new StringBuilder(line_Value).insert(i, '`')).toString();
                                    i++;
                                }
                                if (line_Value.charAt(i) == '&') {
                                    if(line_Value.substring(i, i+6).equals("&apos;")){
                                        line_Value = (new StringBuilder(line_Value).delete(i, i+9).insert(i, '\'')).toString();
                                        i++;
                                    }
//                                    else if(line_Value.substring(i, i+6).equals("&lt;s&gt;")){
//                                        line_Value = (new StringBuilder(line_Value).delete(i, i+9).insert(i, "<s>")).toString();
//                                        i++;
//                                    }
                                }
                            }
                        }
                        key_Number = Float.parseFloat(line_Key);
                        if (key_Number == (int) key_Number) {
                            if (game_Keys[(int) key_Number] == null) {
                                game_Keys[(int) key_Number] = new Key();
                                game_Keys[(int) key_Number].edit_Cache(0, line_Value);
                            } else {
                                game_Keys[(int) key_Number].edit_Cache(0, line_Value);
                            }
                        } else {
                            if (game_Keys[(int) key_Number] == null) {
                                game_Keys[(int) key_Number] = new Key();
                                char_Count = 0;
                                for (int i = 0; i < 10; i++) {
                                    if (line_Key.charAt(i) == '.') {
                                        char_Count++;
                                        break;
                                    } else {
                                        char_Count++;
                                    }
                                }
                                game_Keys[(int) key_Number].edit_Cache(Integer.parseInt(line_Key.substring(char_Count, line_Key.length())), line_Value);
                            } else {
                                char_Count = 0;
                                for (int i = 0; i < 10; i++) {
                                    if (line_Key.charAt(i) == '.') {
                                        char_Count++;
                                        break;
                                    } else {
                                        char_Count++;
                                    }
                                }
                                game_Keys[(int) key_Number].edit_Cache(Integer.parseInt(line_Key.substring(char_Count, line_Key.length())), line_Value);
                            }
                        }
                    }
                }
                builder.delete(0, builder.length());
            }
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Could not read line.");
        }
        String shuffled_To_Next = "";
        for (int i = 0; i < 3300; i++) {
            for (int c = 0; c < 10000; c++) {
                if (game_Keys[i] != null) {
                    if (game_Keys[i].Data[c] != null) {
                        if (game_Keys[i].Data[c].Content.length() > 250) {
                            shuffled_To_Next = "";
                            for (int semi = game_Keys[i].Data[c].Content.length(); semi > 0; semi--) {
                                if (game_Keys[i].Data[c].Content.charAt(semi - 1) == ';') {
                                    shuffled_To_Next = game_Keys[i].Data[c].Content.substring(semi, game_Keys[i].Data[c].Content.length());
                                    break;
                                }
                            }
                            while (game_Keys[i].Data[c + 1] != null) {
                                game_Keys[i].shuffle_Down_Caches(c + 1);
                            }
                            game_Keys[i].Data[c + 1] = new Cache(shuffled_To_Next);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < game_Keys.length; i++) {
            if (game_Keys[i] != null) {
//                System.out.println(i);
//                game_Keys[i].print_All_Caches();
            }
        }
        try {
            PrintWriter writer = new PrintWriter(targetFile.getPath(), "UTF-8");
            writer.println(";Tram Setup");
            writer.println("^$F7::");
            writer.println("{");
            writer.println("SetKeyDelay, 0");

            for (int i = 0; i < game_Keys.length; i++) {
                if (game_Keys[i] != null) {
                    for (int c = 0; c < game_Keys[i].Data.length; c++) {
                        if (game_Keys[i].Data[c] != null) {
                            writer.println("Send {Enter}" + game_Keys[i].Data[c].Content + " {Enter}");
                            writer.println("Send {Enter}" + i);
                            writer.println("sleep,10");
                            writer.println("Send {Esc}");
                            writer.println("sleep,100");
                        }
                    }
                }
            }
            writer.println("Send {Enter}Setup is done.");
            writer.println("ExitApp");
            writer.println("return");
            writer.println("}");
            writer.println("^$F8::");
            writer.println("{");
            writer.println("Reload");
            writer.println("return");
            writer.println("}");

            writer.close();
        } catch (IOException e) {
            // do something
        }
        MW.add(LD);
        MW.revalidate();
        MW.repaint();
        while(true){
            sleep(1000);
        }
//        System.exit(0);
    }

    public static String string_Up_To_First(String s, char c) {
        String rVal = null;
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                length++;
                break;
            } else {
                length++;
            }
        }
        rVal = s.substring(0, length - 1);
        return rVal;
    }

    public static int get_Position_Of_First(String s, char c) {
        int rVal = 0;
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                length++;
                break;
            } else {
                length++;
            }
        }
        rVal = length;
        return rVal;
    }

    public static void sleep(int i) {
        try {
            TimeUnit.MILLISECONDS.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(GamesetupConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
