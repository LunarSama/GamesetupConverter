
package gamesetupconverter;

public class Key {
    Cache[] Data = new Cache[10000];
    public Key(){
        Data[0] = new Cache("");
    }
    public String get_Cache(int i){
        if(Data[i]!=null){
            return Data[i].Content;
        } else {
            return null;
        }
    }
    public void edit_Cache(int i, String data){
        if(Data[i] != null){
            Data[i].Content = data;
        } else {
            Data[i] = new Cache(data);
        }
    }
    public void print_All_Caches(){
        for(int i = 0; i < 10000; i++){
            if(Data[i] != null){
                System.out.println(Data[i].Content);
            }
        }
    }
    public void shuffle_Down_Caches(int i){
        if(Data[i+1]==null){
            Data[i+1] = new Cache(Data[i].Content);
            Data[i] = null;
        } else {
            if(!Data[i+1].Content.equals("")){
                shuffle_Down_Caches(i+1);
            } else {
                Data[i+1] = new Cache(Data[i].Content);
                Data[i] = null;
            }
        }
    }
}
