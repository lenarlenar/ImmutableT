import java.util.ArrayList;
import java.util.List;

public class PerfomanceTest {
    public static void main(String[] args) {
        List<ImmutableT<Integer>> immutableArray = initList();

        long beforeWithReflection = System.currentTimeMillis();
        for (ImmutableT<Integer> item: immutableArray){
             Integer i = item.getT(ImmutableT.getMethodType.Reflection);
             if(i == null){
                 sayCloningFailedAndExit();
             }
        }

        long resWithRef = System.currentTimeMillis() - beforeWithReflection;


        long beforeWithSerialization = System.currentTimeMillis();
        for (ImmutableT<Integer> item: immutableArray){
            Integer i = item.getT(ImmutableT.getMethodType.Serialization);
            if(i == null){
                sayCloningFailedAndExit();
            }
        }
        long resWithSer = System.currentTimeMillis() - beforeWithSerialization;

        System.out.println("Cloning perfomance result: ");
        System.out.println("    reflection (ms): " + resWithRef);
        System.out.println("    serialization (ms): " + resWithSer);
    }

     static List<ImmutableT<Integer>> initList(){
        List<ImmutableT<Integer>> res = new ArrayList<>();

        for(int i = 0; i < 100; i++){
            res.add(new ImmutableTImpl<>(i));
        }

        return res;
    }

    static void sayCloningFailedAndExit(){
        System.out.println("Сloning failed");
        System.exit(0);
    }
}
