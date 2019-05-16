package messaging;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;

public class App 
{
//    public static void main(String[] args) {
//
//    	String str="string";
////        if (str.length < 1) {
////            System.err.println("Please provide an input!");
////            System.exit(0);
////        }
//        System.out.println(sha256hex(str));
//
//    }

	  public static void main(String[] args) {

//	        if (args.length < 1) {
//	            System.err.println("Please provide an input!");
//	            System.exit(0);
//	        }
//	        System.out.println(sha256hex(args[0]));
		  try {
			  
			byte src = 1;
			PackageCreator packege1 = new PackageCreator( src, 1, 145, "someting");
			byte[] bytes1 = packege1.getWholePackage();
			for(int i=0; i<bytes1.length; i++)
				System.out.println((i)+" - "+bytes1[i]);
			
			PackageCreator p2 = new PackageCreator(src, 2, 333, "fghjk");
			byte[] bytes2 = p2.getWholePackage();
			for(int i=0; i<bytes2.length; i++)
				System.out.println((i)+" - "+bytes2[i]);
			
			PackageChecker pCh = new PackageChecker(bytes2);
			System.out.println(pCh.isCorrectContent());
			System.out.println(pCh.isCorrectDescription());
			
			PackageGetter pG = new PackageGetter(bytes1);
			System.out.println("begin = "+pG.getbMagic());
			System.out.println("src = "+pG.getbSrc());
			System.out.println("number of message = "+pG.getbPktId());
			System.out.println("length of message = "+pG.getwLen());
			System.out.println("type of command = "+pG.getcType());
			System.out.println("userId = "+pG.getbUserId());
			System.out.println("message = "+pG.getMessage());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	    }
	  
	
	  
    public static String sha256hex(String input) {
        return DigestUtils.sha256Hex(input);
    }
}
