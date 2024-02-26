package src.PreProcessing;

public class Utility {
    public static String getPartitionString(boolean[] b){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < b.length; ++i){
            if(b[i]){
                sb.append("1");
            }
            else{
                sb.append("0");
            }
        }
        return sb.toString();
    }
}
